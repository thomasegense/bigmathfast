package dk.teg.bigmathfast.search;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * HourglassSearchK3 — Algebraic Surface Search for Perfect Hourglass Solutions
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 * THE ALGEBRAIC SURFACE (K3-type)
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * A perfect hourglass (e=0) requires three APs of squares sharing a middle M:
 *   (A₁², M², B₁²),  (A₂², M², B₂²),  (A₃², M², B₃²)
 *   with steps D₁ + D₃ = D₂  (exactly, not approximately)
 *
 * Using the rational angle parametrisation of Pythagorean pairs:
 *   t = v/(m+u)  for pair (u,v,m) with u²+v²=m²
 *   f(t) = 4t(1−t²)/(1+t²)²  =  D/m²  (normalised AP step)
 *
 * The surface S: f(t₁) + f(t₃) = f(t₂) over ℚ is what we need rational points on.
 *
 * Champion near-miss: t₁=1/73, t₃=7/22, t₂=19/48 miss by e=120/2665²≈1.7×10⁻⁸.
 * No perfect solution is known. Its existence is an OPEN PROBLEM.
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 * THE CROSS-SCALE ALGORITHM
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * Given two Pythagorean primitives (u₁,v₁,m₁) and (u₃,v₃,m₃) — possibly
 * with DIFFERENT m values — scale both to a common middle M = m₁·m₃:
 *
 *   AP₁ at M:  ( m₃(u₁−v₁) )² , M² , ( m₃(u₁+v₁) )²    step D₁' = D₁·m₃²
 *   AP₃ at M:  ( m₁(u₃−v₃) )² , M² , ( m₁(u₃+v₃) )²    step D₃' = D₃·m₁²
 *
 *   Candidate AP₂ step:  D₂' = D₁' + D₃'  (exact, by construction)
 *
 * This is a PERFECT hourglass iff  D₂'  is a valid AP step at M, i.e.:
 *
 *   isSquare( M² − D₂' )   AND   isSquare( M² + D₂' )
 *
 * Both checks are O(log M) BigInteger operations — NO Gaussian enumeration at M needed.
 * The cross-scale AP₂ is: A² , M² , B²  with A=√(M²−D₂'), B=√(M²+D₂').
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 * NEAR-MISS QUALITY MEASURE  r_K3
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * For a cross-scale pair to be a near-miss, BOTH AP₂ endpoints must
 * simultaneously be close to perfect squares.  Define:
 *
 *   A  = ⌊√(M²−D₂')⌋
 *   e⁻ = min( (M²−D₂') − A² ,  (A+1)² − (M²−D₂') )   ← dist to nearest sq
 *
 *   B  = ⌊√(M²+D₂')⌋
 *   e⁺ = min( (M²+D₂') − B² ,  (B+1)² − (M²+D₂') )   ← dist to nearest sq
 *
 *   e_max = max(e⁻, e⁺)     ← both endpoints must be close
 *
 *   r_K3 = log(M) / log(e_max)
 *
 * Interpretation (for M ≈ 2.5×10¹³):
 *   Random pair       →  e_max ≈ M/2,  r_K3 ≈ 1.0
 *   Interesting       →  r_K3 ≥ 1.5   (rarer than 1 in 10⁹ random pairs)
 *   Champion-quality  →  r_K3 ≥ 1.648 (expected every ~2 hrs at 5M pairs/sec)
 *   Perfect solution  →  e_max = 0,    r_K3 = ∞
 *
 * Note: finding the nearest VALID AP₂ step (as brute-force does) still
 * requires Gaussian enumeration at M.  r_K3 measures proximity of D₂'
 * to making both endpoints squares — a necessary condition for a near-miss.
 *
 * ═══════════════════════════════════════════════════════════════════════════════
 * REACHING NEW TERRITORY
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 * Brute force covered all M ≤ 21,242,099,354,125.
 * By using primitives with m ≥ √BRUTE_LIMIT ≈ 4,608,916, every pair gives
 * M = m₁·m₃ > BRUTE_LIMIT — genuinely new.  The key: M need not be smooth
 * (product of primes ≡ 1 mod 4); any product of two primitives works.
 */

/*Best hit
 * 
r_K3=1.6261  M=95783950072225 (14 dig)  e_max=396349215
  AP1(exact): 77389160620975² , 95783950072225² , 111175752757775²   D=3185482909819423935106500000  ✓
  AP2(approx): 76629919677331² , 95783950072225² , 111700427900356²   e⁻=396349215  e⁺=169685505
  AP3(exact): 95171568756503² , 95783950072225² , 96392441007079²   D=116937591864713597011661616  ✓
  D1=3185482909819423935106500000
  D2_eff=3302420501684137532118161616
  D3=116937591864713597011661616
  Seeds: AP1(u=7911975,v=1417648,m=8037977)  AP3(u=11916183,v=75944,m=11916425)
  
r_K3=1.6343  M=65451619928621 (14 dig)  e_max=284128355
  AP1(exact): 62734651408321² , 65451619928621² , 68060213158921²   D=348278063957105608466922600  ✓
  AP2(approx): 21349619086613² , 65451619928621² , 90066768940701²   e⁻=284128355  e⁺=272186163
  AP3(exact): 28356380218579² , 65451619928621² , 88112114964179²   D=3479830252180039221318182400  ✓
  D1=348278063957105608466922600
  D2_eff=3828108316137144829785105000
  D3=3479830252180039221318182400
  Seeds: AP1(u=6034349,v=245700,m=6039349)  AP3(u=9642471,v=4947200,m=10837529)
 * 
 * 
 */

public class HourglassSearchK3 {

    /* ── CONFIGURATION ─────────────────────────────────────────────────── */
    static final int THREADS = 2;// Runtime.getRuntime().availableProcessors();

    /** Only report/process M above this (already brute-forced). */
    static final BigInteger BRUTE_LIMIT = new BigInteger("21242099354125");

    /**
     * Upper bound on primitive hypotenuse m.
     *
     * Build time scales linearly with MAX_PRIM_M (pool uses packed int[], 4 bytes/entry):
     *
     *   MAX_PRIM_M =     7_000_000  →  ~1.1 M entries,    4 MB,    0.3 s
     *   MAX_PRIM_M =    50_000_000  →  ~8.0 M entries,   32 MB,    2   s
     *   MAX_PRIM_M =   200_000_000  →  ~32  M entries,  127 MB,   10   s
     *   MAX_PRIM_M = 1_200_000_000  →  ~191 M entries,  764 MB,  ~60   s  ← default
     *   MAX_PRIM_M = 2_000_000_000  →  ~318 M entries, 1272 MB,  ~95   s
     *
     * Run with -Xmx2g for the default.  All primitives from m=5 upward are
     * included; pairs where M = m₁·m₃ ≤ BRUTE_LIMIT are skipped in checkPair.
     */
    static final long MAX_PRIM_M = 1_800_000_0L;

    static final long HB = 2_000_00000L;

    /**
     * Report near-misses with r_K3 ≥ THRESHOLD.
     * Random baseline: r_K3 ≈ 1.0.  Recommend ≥ 1.5 to avoid noise.
     * Champion-equivalent quality is r_K3 ≈ 1.648.
     */
    static final double THRESHOLD = 1.5;

    static final BigInteger BI1 = BigInteger.ONE;
    static final BigInteger BI2 = BigInteger.TWO;

    /**
     * Packed pool: each int has high 16 bits = a, low 16 bits = b,
     * where the primitive triple is u=a²−b², v=2ab, m=a²+b².
     * a,b ≤ √MAX_PRIM_M ≤ √2e9 ≈ 44721 < 65536 so both fit in 16 bits.
     */
    static int[] POOL;

    static final AtomicLong PAIRS   = new AtomicLong();
    static final AtomicLong PERFECT = new AtomicLong();
    static final AtomicLong LOGGED  = new AtomicLong();
    static volatile double  BEST_R  = 0.0;

    /* ════════════════════════════════════════════════════════════════════ */
    public static void main(String[] args) throws Exception {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  HourglassSearchK3 — Cross-Scale Perfect Hourglass Search");
        System.out.printf ("  threads=%d  threshold=r_K3≥%.2f%n", THREADS, THRESHOLD);
        System.out.printf ("  Primitives: m ≤ %,d%n", MAX_PRIM_M);
        System.out.printf ("  Skip M ≤ %s (already brute-forced)%n", BRUTE_LIMIT);
        System.out.println("  Run with: java -Xmx2g HourglassSearchK3");
        System.out.println("═══════════════════════════════════════════════════════════\n");

        System.out.println("Building primitive pool (target ~60s for MAX_PRIM_M=2B)...");
        long t0 = System.currentTimeMillis();
        POOL = buildPool(MAX_PRIM_M);
        long buildMs = System.currentTimeMillis() - t0;
        System.out.printf("Pool: %,d primitives  (built in %.1fs)  ~%.0f MB%n",
                POOL.length, buildMs / 1000.0, POOL.length * 4.0 / 1e6);
        System.out.printf("M values checked: up to ~%s%n\n",
                BigInteger.valueOf(MAX_PRIM_M).pow(2));

        verifyChampion();
        System.out.println();

        // Each thread draws random pairs from POOL indefinitely
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) pool.submit(HourglassSearchK3::workerLoop);
        try { Thread.currentThread().join(); } catch (InterruptedException ignored) {}
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* WORKER                                                                */
    /* ════════════════════════════════════════════════════════════════════ */
    static void workerLoop() {
        Random rng = new Random();
        int n = POOL.length;
        while (!Thread.currentThread().isInterrupted()) {
            int i = rng.nextInt(n), j = rng.nextInt(n);
            if (i == j) continue;
            checkPair(POOL[i], POOL[j]);
            long total = PAIRS.incrementAndGet();
            if (total % HB == 0)
                System.out.printf("♥  pairs=%,d  near_miss=%,d  perfect=%,d  best_r=%.4f%n",
                        total, LOGGED.get(), PERFECT.get(), BEST_R);
        }
    }

    /** Unpack a pool entry into {u, v, m}. */
    static long[] unpack(int packed) {
        long a = (packed >>> 16) & 0xFFFFL;
        long b =  packed         & 0xFFFFL;
        long u = a * a - b * b;
        long v = 2 * a * b;
        if (u < v) { long t = u; u = v; v = t; }
        long m = a * a + b * b;
        return new long[]{u, v, m};
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* CHECK ONE CROSS-SCALE PAIR                                            */
    /* ════════════════════════════════════════════════════════════════════ */
    static void checkPair(int packed1, int packed3) {
        long[] tr1 = unpack(packed1);
        long[] tr3 = unpack(packed3);
        long u1=tr1[0], v1=tr1[1], m1=tr1[2];
        long u3=tr3[0], v3=tr3[1], m3=tr3[2];

        BigInteger bm1 = BigInteger.valueOf(m1);
        BigInteger bm3 = BigInteger.valueOf(m3);
        BigInteger M   = bm1.multiply(bm3);

        // Skip M values already covered by brute force
        if (M.compareTo(BRUTE_LIMIT) <= 0) return;

        BigInteger D1 = BigInteger.valueOf(2L * u1 * v1);
        BigInteger D3 = BigInteger.valueOf(2L * u3 * v3);

        BigInteger D1_eff = D1.multiply(bm3).multiply(bm3);  // D1·m3²
        BigInteger D3_eff = D3.multiply(bm1).multiply(bm1);  // D3·m1²
        BigInteger D2_eff = D1_eff.add(D3_eff);

        BigInteger M2 = M.multiply(M);
        if (D2_eff.compareTo(M2) >= 0) return;

        // ── AP₂ lower endpoint: check_minus = M²−D₂' ───────────────────
        BigInteger check_minus = M2.subtract(D2_eff);
        BigInteger A = check_minus.sqrt();
        BigInteger A2 = A.multiply(A);

        boolean perfectMinus = A2.equals(check_minus);

        // Distance of check_minus from its nearest perfect square
        // e⁻ = min(check_minus − A², (A+1)² − check_minus)
        BigInteger e_minus;
        if (perfectMinus) {
            e_minus = BigInteger.ZERO;
        } else {
            BigInteger below = check_minus.subtract(A2);                      // ≥ 1
            BigInteger above = A.add(BI1).multiply(A.add(BI1)).subtract(check_minus); // ≥ 1
            e_minus = below.min(above);
        }

        // ── AP₂ upper endpoint: check_plus = M²+D₂' ────────────────────
        BigInteger check_plus = M2.add(D2_eff);
        BigInteger B = check_plus.sqrt();
        BigInteger B2 = B.multiply(B);

        boolean perfectPlus = B2.equals(check_plus);

        BigInteger e_plus;
        if (perfectPlus) {
            e_plus = BigInteger.ZERO;
        } else {
            BigInteger below = check_plus.subtract(B2);
            BigInteger above = B.add(BI1).multiply(B.add(BI1)).subtract(check_plus);
            e_plus = below.min(above);
        }

        // ── Perfect solution ─────────────────────────────────────────────
        if (perfectMinus && perfectPlus) {
            PERFECT.incrementAndGet();
            logPerfect(M, u1, v1, m1, A, B, u3, v3, m3, D1_eff, D2_eff, D3_eff, M2);
            return;
        }

        // ── Near-miss quality: r_K3 = log(M) / log(e_max) ───────────────
        // e_max = max(e⁻, e⁺): both endpoints must be close to squares.
        BigInteger e_max = e_minus.max(e_plus);
        if (e_max.signum() == 0) e_max = BI1;  // safety (shouldn't happen here)

        double r = blog(M) / blog(e_max);
        if (r >= THRESHOLD) {
            synchronized (HourglassSearchK3.class) { if (r > BEST_R) BEST_R = r; }
            LOGGED.incrementAndGet();
            logNearMiss(M, u1, v1, m1, A, B, u3, v3, m3,
                        D1_eff, D2_eff, D3_eff, M2,
                        e_minus, e_plus, e_max, r);
        }
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* BUILD POOL                                                             */
    /* Every primitive triple: u=a²−b², v=2ab, m=a²+b²  (a>b, gcd=1, a−b odd) */
    /* Packed as int: high 16 bits = a, low 16 bits = b  (both < 65536)     */
    /* O(MAX_PRIM_M) total — iterates a from 2 to √MAX_PRIM_M only.        */
    /* ════════════════════════════════════════════════════════════════════ */
    static int[] buildPool(long maxM) {
        // First pass: count entries for exact array allocation
        long aMax = (long) Math.sqrt(maxM) + 1;
        int count = 0;
        for (long a = 2; a <= aMax; a++) {
            long bStart = (a % 2 == 0) ? 1 : 2;
            for (long b = bStart; b < a; b += 2) {
                if (gcd(a, b) != 1) continue;
                long m = a * a + b * b;
                if (m > maxM) break;
                count++;
            }
        }
        int[] pool = new int[count];
        int idx = 0;
        for (long a = 2; a <= aMax; a++) {
            long bStart = (a % 2 == 0) ? 1 : 2;
            for (long b = bStart; b < a; b += 2) {
                if (gcd(a, b) != 1) continue;
                long m = a * a + b * b;
                if (m > maxM) break;
                pool[idx++] = (int)((a << 16) | b);
            }
        }
        return pool;
    }

    static long gcd(long a, long b) {
        while (b != 0) { long t = b; b = a % b; a = t; }
        return a;
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* LOGGING                                                               */
    /* ════════════════════════════════════════════════════════════════════ */
    static synchronized void logNearMiss(BigInteger M,
            long u1, long v1, long m1, BigInteger A, BigInteger B,
            long u3, long v3, long m3,
            BigInteger D1, BigInteger D2, BigInteger D3, BigInteger M2,
            BigInteger e_minus, BigInteger e_plus, BigInteger e_max, double r) {
        BigInteger bm3 = BigInteger.valueOf(m3), bm1 = BigInteger.valueOf(m1);
        BigInteger a1 = bm3.multiply(BigInteger.valueOf(u1 - v1));
        BigInteger b1 = bm3.multiply(BigInteger.valueOf(u1 + v1));
        BigInteger a3 = bm1.multiply(BigInteger.valueOf(u3 - v3));
        BigInteger b3 = bm1.multiply(BigInteger.valueOf(u3 + v3));

        System.out.printf("%nr_K3=%.4f  M=%s (%d dig)  e_max=%s%n",
                r, M, M.toString().length(), e_max);
        printAP("  AP1(exact)", a1, M, b1, D1, M2);
        System.out.printf("  AP2(approx): %s² , %s² , %s²   e⁻=%s  e⁺=%s%n",
                A, M, B, e_minus, e_plus);
        printAP("  AP3(exact)", a3, M, b3, D3, M2);
        System.out.printf("  D1=%s%n  D2_eff=%s%n  D3=%s%n", D1, D2, D3);
        System.out.printf("  Seeds: AP1(u=%d,v=%d,m=%d)  AP3(u=%d,v=%d,m=%d)%n%n",
                u1, v1, m1, u3, v3, m3);
    }

    static synchronized void logPerfect(BigInteger M,
            long u1, long v1, long m1, BigInteger A, BigInteger B,
            long u3, long v3, long m3,
            BigInteger D1, BigInteger D2, BigInteger D3, BigInteger M2) {
        System.out.println("\n★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★");
        System.out.println("  ★★★  PERFECT SOLUTION  e = 0  ★★★");
        System.out.printf ("  M = %s  (%d digits)%n", M, M.toString().length());
        System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★\n");

        BigInteger bm3 = BigInteger.valueOf(m3), bm1 = BigInteger.valueOf(m1);
        BigInteger a1 = bm3.multiply(BigInteger.valueOf(u1 - v1));
        BigInteger b1 = bm3.multiply(BigInteger.valueOf(u1 + v1));
        BigInteger a3 = bm1.multiply(BigInteger.valueOf(u3 - v3));
        BigInteger b3 = bm1.multiply(BigInteger.valueOf(u3 + v3));

        printAP("  AP1", a1, M, b1, D1, M2);
        printAP("  AP2", A,  M, B,  D2, M2);
        printAP("  AP3", a3, M, b3, D3, M2);
        System.out.printf("  D1=%s%n  D2=%s%n  D3=%s%n", D1, D2, D3);
        System.out.printf("  e=D1−D2+D3=%s%n", D1.subtract(D2).add(D3));
        System.out.printf("  Seed AP1: (u=%d,v=%d,m=%d)%n", u1, v1, m1);
        System.out.printf("  Seed AP3: (u=%d,v=%d,m=%d)%n\n", u3, v3, m3);
    }

    static void printAP(String lbl, BigInteger a, BigInteger M, BigInteger b,
                        BigInteger D, BigInteger M2) {
        boolean ok = M2.subtract(a.multiply(a)).equals(D)
                  && b.multiply(b).subtract(M2).equals(D);
        System.out.printf("%s: %s² , %s² , %s²   D=%s  %s%n",
                lbl, a, M, b, D, ok ? "✓" : "BUG");
    }

    static double blog(BigInteger v) {
        String s = v.abs().toString(); int len = s.length();
        if (len <= 15) return Math.log10(v.abs().doubleValue());
        return (len - 1) + Math.log10(Double.parseDouble(s.substring(0, 15)) / 1e14);
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* CHAMPION VERIFICATION                                                 */
    /* ════════════════════════════════════════════════════════════════════ */
    static void verifyChampion() {
        System.out.println("── Cross-scale formula check on champion (m=2665, e=120) ───");
        System.out.println("   The champion is a NEAR-MISS at m=2665 < BRUTE_LIMIT.");
        System.out.println("   Cross-scale with m1=m3=2665 gives M=2665²=7102225 (not m=2665).");
        System.out.println("   This illustrates why near-misses can't be recovered cross-scale:");

        // Same-m cross-scale: M=2665^2, both APs scaled
        BigInteger m = BigInteger.valueOf(2665);
        BigInteger M = m.multiply(m);
        BigInteger D1 = BigInteger.valueOf(388944);   // champion AP1 step
        BigInteger D3 = BigInteger.valueOf(6699000);  // champion AP3 step

        BigInteger D1_eff = D1.multiply(m).multiply(m);
        BigInteger D3_eff = D3.multiply(m).multiply(m);
        BigInteger D2_eff = D1_eff.add(D3_eff);
        BigInteger M2 = M.multiply(M);

        System.out.printf("   M=%s, D1_eff/M²+D3_eff/M² = %.6f + %.6f = %.6f (should ≤ 1)%n",
                M, D1.doubleValue()/2665/2665, D3.doubleValue()/2665/2665,
                (D1.doubleValue()+D3.doubleValue())/2665/2665);

        if (D2_eff.compareTo(M2) < 0) {
            BigInteger A = M2.subtract(D2_eff).sqrt();
            BigInteger e_sq = M2.subtract(D2_eff).subtract(A.multiply(A));
            System.out.printf("   check_minus dist from sq: %s  (non-zero → not perfect)%n", e_sq);
        } else {
            System.out.println("   D2_eff > M²  (AP steps too large for cross-scale)");
        }

        System.out.println();
        System.out.println("   For the ACTUAL champion, all 3 APs are at m=2665 directly");
        System.out.println("   (same-m case, found by brute force). D1+D3=7087944≠7088064=D2.");
        System.out.println("   Finding a TRUE e=0 solution is what this program targets.");
    }
}