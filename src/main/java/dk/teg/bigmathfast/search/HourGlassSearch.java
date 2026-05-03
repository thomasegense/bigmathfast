package dk.teg.bigmathfast.search;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import dk.teg.bigmathfast.BigMathFast;

/**
 * High-performance parallel search for arithmetic progressions of squares.
 *
 * For each candidate middle number n ≡ 1 (mod 4):
 *  1. Factorise n — skip immediately if any prime ≡ 3 (mod 4)
 *  2. Build Gaussian integer decomposition for each prime factor
 *  3. Compute all Gaussian products → all AP step values (BigInteger[])
 *  4. O(n²) two-pointer 3-sum to find minimum |d1 ± d2 ± d3|
 *  5. Log if primitive quality r = log(n)/log(|e|) > THRESHOLD
 *
 * Factor pairs are kept as BigInteger[] throughout to avoid long overflow
 * for large prime factors.  The squarefree filter has been removed —
 * numbers like 5²×13×... are valid candidates distinct from 5×13×...
 */
public class HourGlassSearch {

    // =========================================================================
    // Constants
    // =========================================================================
    private static final BigInteger MOD4 = BigInteger.valueOf(4);
    private static final BigInteger ONE  = BigInteger.ONE;
    private static final BigInteger TWO  = BigInteger.TWO;

    // =========================================================================
    // Configuration
    // =========================================================================
    private final BigInteger start;
    private final int        numThreads;
    private final double     threshold;

    private final AtomicReference<Double> globalBest = new AtomicReference<>(0.5);

    // Gaussian integer cache: prime (as long) → int[]{a, b}  where a²+b²=prime
    private static final HashMap<Long, int[]> GAUSS_CACHE = new HashMap<>(16384);

    // =========================================================================
    // main
    // =========================================================================
    public static void main(String[] args) {
        HourGlassSearch search = new HourGlassSearch(
            new BigInteger("13325"),
            1,
            0.00001
        );
        search.run();
    }

    public HourGlassSearch(BigInteger start, int numThreads, double threshold) {
        BigInteger rem = start.mod(MOD4);
        if (!rem.equals(ONE)) {
            start = start.subtract(rem).add(ONE);
            if (start.mod(MOD4).signum() < 0) start = start.add(MOD4);
        }
        this.start      = start;
        this.numThreads = numThreads;
        this.threshold  = threshold;
    }

    // =========================================================================
    // Entry point
    // =========================================================================
    public void run() {
        System.out.println("Starting search from n = " + start);
        System.out.println("Threads: " + numThreads + "  Threshold: " + threshold);
        System.out.println();

        ExecutorService pool   = Executors.newFixedThreadPool(numThreads);
        BigInteger      stride = BigInteger.valueOf((long) numThreads * 4);

        for (int t = 0; t < numThreads; t++) {
            final BigInteger threadStart = start.add(BigInteger.valueOf((long) t * 4));
            pool.submit(() -> {
                try {
                    searchThread(threadStart, stride);
                } catch (Exception ex) {
                    System.err.println("Thread crashed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        }
    }

    // =========================================================================
    // Per-thread search loop
    // =========================================================================
   private void searchThread(BigInteger n, BigInteger stride) {
    long localCount = 0;
    while (true) {
        try {
            testCandidate(n);
        } catch (Exception ex) {
            System.err.println("Error on n=" + n + ": " + ex.getMessage());
        }
        n = n.add(stride);
        if (++localCount % 1_000_000 == 0) {
            System.out.println(n);
            localCount = 0;
        }
    }
}
    // =========================================================================
    // Test a single candidate middle number n
    // =========================================================================
    void testCandidate(BigInteger n) {        

        // Step 1: Factorise — returns null if any prime ≡ 3 (mod 4)
        ArrayList<BigInteger> rawFactors = factorize(n);
        if (rawFactors == null) return;

        // Step 2: Deduplicate into (prime, exponent) pairs as BigInteger[]
        //         No squarefree filter — 5²×13×... is distinct from 5×13×...
        List<BigInteger[]> factors = toExponentPairs(rawFactors);
        if (factors == null) return;

        // Step 3: Gaussian decomposition for each prime factor
        int[][] gauss = new int[factors.size()][3];
        for (int i = 0; i < factors.size(); i++) {
            BigInteger[] pe = factors.get(i);
            int[]        ab = gaussianDecompose(pe[0]);
            if (ab == null) return;
            gauss[i][0] = ab[0];
            gauss[i][1] = ab[1];
            gauss[i][2] = pe[1].intValueExact();
        }

        // Step 4: Compute all AP step values as sorted BigInteger[]
        BigInteger[] steps = computeAllSteps(gauss);
        if (steps == null || steps.length < 3) return;

        // Step 5: 3-sum — returns BigInteger[4]: {bestE, d1, d2, d3}
        BigInteger[] sumResult = threeSum(steps);
        BigInteger   bestE     = sumResult[0];
        BigInteger[] triple    = {sumResult[1], sumResult[2], sumResult[3]};

        if (bestE.signum() == 0) {
            printPerfect(n);
            return;
        }

        // Step 6: Skip non-primitive results
        if (!isPrimitive(factors, bestE)) return;

        // Step 7: Quality and logging
        double r = calculateR(n, bestE);
        if (r > threshold) {
            log(n, r, bestE, steps.length, triple);
        }

        double curr = globalBest.get();
        if (r > curr) globalBest.compareAndSet(curr, r);
    }

    // =========================================================================
    // Compute all AP step values as sorted BigInteger[].
    //
    // For each prime p^e, precompute contributions π^j * π̄^(2e-j) as long[]{Re,Im}.
    // Accumulate products across primes using BigInteger to avoid overflow.
    // Final step = 2 * Re * Im as BigInteger.
    // =========================================================================
    private BigInteger[] computeAllSteps(int[][] gauss) {
        int   k     = gauss.length;
        int[] sizes = new int[k];
        int   total = 1;
        for (int i = 0; i < k; i++) {
            sizes[i] = 2 * gauss[i][2] + 1;
            total   *= sizes[i];
        }

        // Precompute: pows[i][j] = π^j * π̄^(2e-j) as long[]{Re, Im}
        // Safe as long since each entry is bounded by p^e which is < n^(1/k)
        long[][][] pows = new long[k][][];
        for (int i = 0; i < k; i++) {
            int  a    = gauss[i][0], b = gauss[i][1], e = gauss[i][2];
            int  maxK = 2 * e;
            long p    = (long) a * a + (long) b * b;
            long[] piUnit = {a, b};

            pows[i]    = new long[maxK + 1][2];
            long[] piPow    = {1, 0};
            long[] pibarPow = gaussPow(a, -b, maxK);
            pows[i][0]      = pibarPow.clone();

            for (int j = 1; j <= maxK; j++) {
                piPow = gaussMulL(piPow, piUnit);
                long[] prev = pibarPow;
                pibarPow = new long[]{
                    (prev[0] * piUnit[0] - prev[1] * piUnit[1]) / p,
                    (prev[0] * piUnit[1] + prev[1] * piUnit[0]) / p
                };
                pows[i][j] = gaussMulL(piPow, pibarPow);
            }
        }

        TreeSet<BigInteger> stepSet = new TreeSet<>();
        int[] idx = new int[k];

for (int combo = 0; combo < total; combo++) {
    // Long arithmetic throughout — safe since |Re|,|Im| <= n <= 10^13
    long re = 1, im = 0;
    for (int i = 0; i < k; i++) {
        long[] c = pows[i][idx[i]];
        long nr  = re * c[0] - im * c[1];
        long ni  = re * c[1] + im * c[0];
        re = nr; im = ni;
    }
    // Only ONE BigInteger multiply needed for the step
    BigInteger absStep = BigInteger.valueOf(re)
                                   .multiply(BigInteger.valueOf(im))
                                   .multiply(TWO).abs();
    if (absStep.signum() > 0) stepSet.add(absStep);

    for (int i = k - 1; i >= 0; i--) {
        if (++idx[i] < sizes[i]) break;
        idx[i] = 0;
    }
}

        return stepSet.toArray(new BigInteger[0]);  // sorted ascending
    }

    // =========================================================================
    // O(n²) two-pointer 3-sum over sorted BigInteger[].
    // Returns BigInteger[4]: {bestE, d1, d2, d3}  where e = d1 + d3 - d2
    // =========================================================================
BigInteger[] threeSum(BigInteger[] d) {
    int n = d.length;
    double[] dd = new double[n];
    for (int i = 0; i < n; i++) dd[i] = d[i].doubleValue();

    BigInteger   best       = null;
    BigInteger[] bestTriple = {BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};

    // Pattern 1: d[i] + d[j] - d[k]
    for (int i = 0; i < n - 2; i++) {
        int j = i + 1, k = n - 1;
        while (j < k) {
            double sum = dd[i] + dd[j] - dd[k];
            double abs = Math.abs(sum);
            if (best == null || abs < best.doubleValue()) {
                BigInteger exact = d[i].add(d[j]).subtract(d[k]);
                BigInteger eabs  = exact.abs();
                if (best == null || eabs.compareTo(best) < 0) {
                    best       = eabs;
                    bestTriple = new BigInteger[]{d[i], d[k], d[j]};
                    if (best.signum() == 0)
                        return new BigInteger[]{BigInteger.ZERO, d[i], d[k], d[j]};
                }
            }
            if      (sum < 0) j++;
            else if (sum > 0) k--;
            else { j++; k--; }
        }
    }

    // Pattern 2: d[lo] + d[hi] - d[j]
    for (int j = 1; j < n - 1; j++) {
        int lo = 0, hi = n - 1;
        while (lo < hi) {
            if (lo == j) { lo++; continue; }
            if (hi == j) { hi--; continue; }
            double sum = dd[lo] + dd[hi] - dd[j];
            double abs = Math.abs(sum);
            if (best == null || abs < best.doubleValue()) {
                BigInteger exact = d[lo].add(d[hi]).subtract(d[j]);
                BigInteger eabs  = exact.abs();
                if (best == null || eabs.compareTo(best) < 0) {
                    best       = eabs;
                    bestTriple = new BigInteger[]{d[lo], d[hi], d[j]};
                    if (best.signum() == 0)
                        return new BigInteger[]{BigInteger.ZERO, d[lo], d[hi], d[j]};
                }
            }
            if      (sum < 0) lo++;
            else if (sum > 0) hi--;
            else { lo++; hi--; }
        }
    }

    if (best == null) return new BigInteger[]{BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO};
    return new BigInteger[]{best, bestTriple[0], bestTriple[1], bestTriple[2]};
}
    // =========================================================================
    // Primitive check: skip if any prime p of n satisfies p² | bestE.
    // Uses BigInteger multiply to avoid long overflow for large primes.
    // =========================================================================
    private boolean isPrimitive(List<BigInteger[]> factors, BigInteger bestE) {
        for (BigInteger[] pe : factors) {
            BigInteger psq = pe[0].multiply(pe[0]);
            if (bestE.mod(psq).signum() == 0) return false;
        }
        return true;
    }

    // =========================================================================
    // Deduplication: rawFactors → (prime, exponent) pairs as BigInteger[].
    // Returns null if any prime ≡ 3 (mod 4).
    // No squarefree filter — prime powers are valid distinct candidates.
    // =========================================================================
    private List<BigInteger[]> toExponentPairs(ArrayList<BigInteger> rawFactors) {
        HashMap<BigInteger, Integer> map = new HashMap<>();
        for (BigInteger p : rawFactors) {
            if (!p.mod(MOD4).equals(ONE)) return null;  // prime ≡ 3 (mod 4) → skip
            map.merge(p, 1, Integer::sum);
        }
        List<BigInteger[]> result = new ArrayList<>(map.size());
        for (var entry : map.entrySet()) {
            result.add(new BigInteger[]{entry.getKey(),
                                        BigInteger.valueOf(entry.getValue())});
        }
        return result;
    }

    // =========================================================================
    // Gaussian integer decomposition: a²+b²=p, a even, b odd.
    // Cached for p < 100_000.  Accepts BigInteger; converts to long internally.
    // Returns null if p > Long.MAX_VALUE (extremely large primes — skip).
    // =========================================================================
    private int[] gaussianDecompose(BigInteger bigP) {
        if (bigP.bitLength() > 62) return null;   // too large to handle as int a,b
        long p = bigP.longValueExact();
        if (p == 2)     return null;               // 2 ≡ 2 mod 4, skip
        if (p % 4 != 1) return null;

        if (p < 100_000L) {
            int[] cached = GAUSS_CACHE.get(p);
            if (cached != null) return cached;
        }

        int sq = (int) Math.sqrt(p);
        for (int b = 1; b <= sq; b += 2) {
            long r = p - (long) b * b;
            if (r <= 0) break;
            int a = (int) Math.round(Math.sqrt(r));
            if (a % 2 == 0 && (long) a * a + (long) b * b == p) {
                int[] result = {a, b};
                if (p < 100_000L) GAUSS_CACHE.put(p, result);
                return result;
            }
        }
        // Try swapped parity
        for (int a = 1; a <= sq; a++) {
            long r = p - (long) a * a;
            if (r <= 0) break;
            int b = (int) Math.round(Math.sqrt(r));
            if ((long) a * a + (long) b * b == p) {
                int[] result = (a % 2 == 0) ? new int[]{a, b} : new int[]{b, a};
                if (p < 100_000L) GAUSS_CACHE.put(p, result);
                return result;
            }
        }
        return null;
    }

    // =========================================================================
    // Gaussian helpers — long arithmetic for single-prime Re/Im values
    // =========================================================================
    private static long[] gaussMulL(long[] z1, long[] z2) {
        return new long[]{
            z1[0] * z2[0] - z1[1] * z2[1],
            z1[0] * z2[1] + z1[1] * z2[0]
        };
    }

    private static long[] gaussPow(int a, int b, int n) {
        long[] result = {1, 0};
        long[] base   = {a, b};
        while (n > 0) {
            if ((n & 1) == 1) result = gaussMulL(result, base);
            base = gaussMulL(base, base);
            n >>= 1;
        }
        return result;
    }

    // =========================================================================
    // Quality: r = log(n) / log(|e|)
    // =========================================================================
    public static double calculateR(BigInteger n, BigInteger e) {
        if (e.signum() == 0) return Double.MAX_VALUE;
        return bigIntLog(n) / bigIntLog(e.abs());
    }

    private static double bigIntLog(BigInteger v) {
        String s   = v.toString();
        int    len = s.length();
        if (len <= 15) {
            return Math.log10(v.doubleValue());
        }
        double mantissa = Double.parseDouble(s.substring(0, 15));
        return (len - 1) + Math.log10(mantissa / 1e14);
    }

    // =========================================================================
    // Logging
    // =========================================================================
    private synchronized void log(BigInteger n, double r, BigInteger bestE,
                                   int numAPs, BigInteger[] triple) {
        String aps = formatAP(n, triple[0]) + " , "
                   + formatAP(n, triple[1]) + " , "
                   + formatAP(n, triple[2]);
        System.out.printf("r=%.4f  n=%-22s  |e|=%-20s  APs=%d  %s%n",
            r, n, bestE, numAPs, aps);
        
        // Log primitive version on second line if reducible
    BigInteger g = primitiveGcd(n, triple);
    //System.out.println(n+":"+g);
    if (!g.equals(ONE)) {
        BigInteger nP = n.divide(g);
        BigInteger g2 = g.multiply(g);
        BigInteger[] tripleP = {
            triple[0].divide(g2),
            triple[1].divide(g2),
            triple[2].divide(g2)
        };
        BigInteger bestEP = bestE.divide(g2);
        String apsP = formatAP(nP, tripleP[0]) + " , "
                    + formatAP(nP, tripleP[1]) + " , "
                    + formatAP(nP, tripleP[2]);
        double rP = calculateR(nP, bestEP);
        System.out.printf("  prim r=%.4f  n=%-22s  |e|=%-20s  %s%n",
            rP, nP, bestEP, apsP);
    }
        
    }

    private String formatAP(BigInteger n, BigInteger d) {
        BigInteger n2 = n.multiply(n);
        BigInteger x  = n2.subtract(d).sqrt();
        BigInteger y  = n2.add(d).sqrt();
        return "(" + x + "," + n + "," + y + ":" + d + ")";
    }

    private synchronized void printPerfect(BigInteger n) {
        System.out.println();
        System.out.println("================================================");
        System.out.println("  *** PERFECT SOLUTION: e = 0 ***");
        System.out.println("  n = " + n);
        System.out.println("================================================");
        System.out.println();
    }

    // =========================================================================
    // Factorizer — override with your implementation.
    // Must return null if any prime factor ≡ 3 (mod 4).
    // =========================================================================
    protected ArrayList<BigInteger> factorize(BigInteger n) {
        ArrayList<BigInteger> factors = BigMathFast.factorize(n);
        if (factors == null) return null;
        for (BigInteger f : factors) {
            if (!f.mod(MOD4).equals(ONE)) return null;
        }
        return factors;
    }
    
    /**
 * GCD of n and all 6 AP endpoints (x and y for each of the 3 APs).
 * The step values are NOT included — we want the spatial GCD only.
 */
private BigInteger primitiveGcd(BigInteger n, BigInteger[] triple) {
    BigInteger n2 = n.multiply(n);
    BigInteger g  = n;
    for (BigInteger d : triple) {
        BigInteger x = n2.subtract(d).sqrt();
        BigInteger y = n2.add(d).sqrt();
        g = g.gcd(x).gcd(y);
        if (g.equals(ONE)) return ONE;  // early exit
    }
    return g;
}
}