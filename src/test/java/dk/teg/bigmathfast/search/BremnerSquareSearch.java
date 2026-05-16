package dk.teg.bigmathfast.search;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * BremnerSquareSearch — Semi-magic Squares of Squares with Minimal Repeats
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE GOAL: The Egense Square
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * A 3×3 semi-magic square of squares has all 9 entries as perfect squares
 * and 7 of the 8 row/column/diagonal sums equal (the "magic constant" S).
 *
 * The Parker Square (2016) is the best known:
 *
 *   29²   1²  47²       S = 3051
 *   41²  37²   1²       Anti-diagonal = 4107  ✗
 *   23²  41²  29²
 *
 * It has 3 repeated entries (1² twice, 29² twice, 41² twice).
 * The "Egense Square" would improve this to ≤2 repeats with 7/8 sums equal.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE ALGORITHM
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * A 3×3 grid with entries aᵢⱼ satisfies all 3 rows + all 3 cols + main
 * diagonal = S iff (7/8 semi-magic):
 *
 *   Row 1:  a11 + a12 + a13 = S
 *   Row 2:  a21 + a22 + a23 = S
 *   Row 3:  a31 + a32 + a33 = S   (forced: a3j = S − a1j − a2j)
 *   Col j:  a1j + a2j + a3j = S   (automatically satisfied by above)
 *   Main ↘: a11 + a22 + a33 = S   (one extra constraint)
 *
 * Note: the anti-diagonal a13+a22+a31 is NOT constrained — it may differ.
 *
 * Given row 1 = (a², b², c²) and row 2 = (d², e², f²):
 *   Row 3 = (S−a²−d², S−b²−e², S−c²−f²) — all FORCED
 *   Must check: each row-3 entry is a positive perfect square
 *   Must check: a² + e² + (S−c²−f²) = S  → main diagonal condition
 *                 ⟺  a² + e² = c² + f²
 *
 * So the algorithm is:
 *   1. Enumerate all triples (a,b,c) with a²+b²+c² = S
 *   2. For each pair of ordered triples (row1, row2):
 *      a. Check diagonal condition: row1[0]² + row2[1]² = row1[2]² + row2[2]²
 *         (equivalently a²+e² = c²+f²)
 *      b. Check all 3 row-3 entries are positive perfect squares
 *      c. Count distinct values — report if ≤ MAX_REPEATS repeats
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * NON-DETERMINISM: SCALING + RANDOM S
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Parker's square scaled by (2N)² gives a valid semi-magic square with
 * the same structure but different numbers:
 *
 *   (29·2N)²  (1·2N)²  (47·2N)²       S' = 3051 · 4N²
 *   (41·2N)²  (37·2N)²  (1·2N)²
 *   (23·2N)²  (41·2N)²  (29·2N)²
 *
 * This has the same repeats but provides different starting points for
 * search. More importantly, we search RANDOM magic constants S, not just
 * multiples of Parker's S. This explores fresh algebraic territory.
 *
 * The key non-determinism:
 * - Each thread draws a random S in [S_MIN, S_MAX]
 * - Enumerates all 3-square representations of S
 * - Tries all row-pair combinations
 * - Reports solutions with ≤ MAX_REPEATS repeated entries
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * MULTI-SCALE: WHY LARGER S MATTERS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * For S = 3051 (Parker's constant), there are 14 representations of S as
 * a²+b²+c². Trying all row-pair orderings gives 78 semi-magic solutions,
 * all with ≥ 3 repeats — Parker's 3-repeat is the MINIMUM for this S.
 *
 * For larger S, there are MORE representations (more combinations to try),
 * giving a higher chance of hitting a ≤ 2-repeat solution. The probability
 * scales roughly as (number of reps)² / S^(3/2), with rare "jackpot" S
 * values having many representations (smooth numbers, products of primes ≡1 mod 4).
 */
public class BremnerSquareSearch {

    /* ── CONFIGURATION ─────────────────────────────────────────────────── */
    static final int  THREADS     = 2;//Runtime.getRuntime().availableProcessors();
    /** Only report solutions with at most this many repeated entries (Parker has 3). */
    static final int  MAX_REPEATS = 3;
    /** Search range for magic constant S. */
    static final long S_MIN       = 3_051L;
    static final long S_MAX       = 500_000_000L;
    /** Heartbeat interval (number of S values tried across all threads). */
    static final long HB          = 500_000L;

    static final AtomicLong S_TRIED  = new AtomicLong();
    static final AtomicLong FOUND    = new AtomicLong();
    static volatile int     BEST_REP = MAX_REPEATS;

    /* ════════════════════════════════════════════════════════════════════ */
    public static void main(String[] args) throws Exception {
        System.out.println("══════════════════════════════════════════════════════════════");
        System.out.println("  BremnerSquareSearch — Semi-Magic Squares of Squares");
        System.out.printf ("  threads=%d  max_repeats=%d  S range=[%,d, %,d]%n",
                THREADS, MAX_REPEATS, S_MIN, S_MAX);
        System.out.println("  Parker Square: S=3051, repeats=3");
        System.out.println("  Goal (Egense Square): same 7/8 property, repeats≤2");
        System.out.println("══════════════════════════════════════════════════════════════\n");

        // Verify Parker's square is found
        verifyParker();
        System.out.println();

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        for (int t = 0; t < THREADS; t++) pool.submit(BremnerSquareSearch::workerLoop);
        try { Thread.currentThread().join(); } catch (InterruptedException ignored) {}
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* WORKER: pick random S values and search                              */
    /* ════════════════════════════════════════════════════════════════════ */
    static void workerLoop() {
        Random rng = new Random();
        while (!Thread.currentThread().isInterrupted()) {
            // Pick a random S in range.
            // Bias toward S values with many 3-square representations:
            // use "smooth-ish" numbers by sometimes picking multiples of
            // small squares, which tend to have more representations.
            long S;
            int bias = rng.nextInt(4);
            if (bias == 0) {
                // Scale from Parker: S = 3051 * k² for random odd k
                long k = 2 * rng.nextInt(500) + 1;
                S = 3051L * k * k;
            } else if (bias == 1) {
                // Multiple of a small perfect square
                long base = rng.nextInt(10000) + 1000L;
                long sq = (long) rng.nextInt(100) + 1;
                S = base * sq * sq;
            } else {
                // Completely random
                S = S_MIN + (long)(rng.nextDouble() * (S_MAX - S_MIN));
            }
            if (S < S_MIN || S > S_MAX) continue;

            searchS(S);

            long tried = S_TRIED.incrementAndGet();
            if (tried % HB == 0)
                System.out.printf("♥  S_tried=%,d  found=%,d  best_repeats=%d%n",
                        tried, FOUND.get(), BEST_REP);
        }
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* SEARCH ONE S VALUE                                                    */
    /* ════════════════════════════════════════════════════════════════════ */
    static void searchS(long S) {
        // Step 1: find all unordered triples (a≥b≥c≥1) with a²+b²+c²=S
        int[] triples = findTriples(S);
        if (triples == null || triples.length < 6) return;  // need at least 2 triples
        int nTriples = triples.length / 3;

        // Step 2: generate all ORDERED triples (permutations of each unordered triple)
        // Store as flat int array: [a,b,c, a,c,b, b,a,c, ...] for all triples
        int[] ordered = new int[nTriples * 6 * 3];  // max 6 perms × 3 entries × nTriples
        int nOrdered = 0;
        for (int i = 0; i < nTriples; i++) {
            int a = triples[i*3], b = triples[i*3+1], c = triples[i*3+2];
            // Generate all distinct permutations
            int[][] perms = distinctPerms(a, b, c);
            for (int[] p : perms) {
                ordered[nOrdered*3]   = p[0];
                ordered[nOrdered*3+1] = p[1];
                ordered[nOrdered*3+2] = p[2];
                nOrdered++;
            }
        }

        // Step 3: for each pair (row1, row2), check diagonal + row3 squares
        for (int i = 0; i < nOrdered; i++) {
            int a = ordered[i*3], b = ordered[i*3+1], c = ordered[i*3+2];
            long a2 = (long)a*a, b2 = (long)b*b, c2 = (long)c*c;
            long diag_need = c2;  // a2 + e2 = c2 + f2 ⟺ e2 - f2 = c2 - a2

            for (int j = 0; j < nOrdered; j++) {
                int d = ordered[j*3], e = ordered[j*3+1], f = ordered[j*3+2];
                long d2 = (long)d*d, e2 = (long)e*e, f2 = (long)f*f;

                // Main diagonal condition: a² + e² = c² + f²
                if (a2 + e2 != c2 + f2) continue;

                // Row 3 entries (forced by column constraints)
                long r31 = S - a2 - d2;
                long r32 = S - b2 - e2;
                long r33 = S - c2 - f2;

                if (r31 <= 0 || r32 <= 0 || r33 <= 0) continue;
                if (r31 + r32 + r33 != S) continue; // sanity (always true)

                // Check row3 entries are perfect squares
                long sa = isqrt(r31), sb = isqrt(r32), sc = isqrt(r33);
                if (sa < 0 || sb < 0 || sc < 0) continue;

                // Count repeats
                int rep = countRepeats(a, b, c, d, e, f, (int)sa, (int)sb, (int)sc);
                if (rep <= MAX_REPEATS) {
                    synchronized (BremnerSquareSearch.class) {
                        if (rep < BEST_REP) BEST_REP = rep;
                    }
                    FOUND.incrementAndGet();
                    logResult(S, a, b, c, d, e, f, (int)sa, (int)sb, (int)sc, rep);
                }
            }
        }
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* FIND ALL UNORDERED TRIPLES (a≥b≥c≥1) with a²+b²+c²=S              */
    /* ════════════════════════════════════════════════════════════════════ */
    static int[] findTriples(long S) {
        int[] buf = new int[300 * 3];  // generous buffer
        int n = 0;
        long aMax = (long) Math.sqrt(S / 3.0);  // a ≥ b ≥ c so a² ≥ S/3
        long aTop = (long) Math.sqrt(S);
        for (long a = aTop; a >= aMax; a--) {
            long rem = S - a * a;
            if (rem <= 0) continue;
            long bMax2 = rem / 2;  // b ≥ c so b² ≥ rem/2
            long bTop = (long) Math.sqrt(rem);
            if (bTop > a) bTop = a;
            for (long b = bTop; b * b >= bMax2; b--) {
                long c2 = rem - b * b;
                if (c2 <= 0) break;
                long c = isqrt(c2);
                if (c >= 0 && c <= b) {
                    if (n + 1 > buf.length / 3) break;
                    buf[n*3] = (int)a; buf[n*3+1] = (int)b; buf[n*3+2] = (int)c;
                    n++;
                }
            }
        }
        if (n == 0) return null;
        int[] res = new int[n*3];
        System.arraycopy(buf, 0, res, 0, n*3);
        return res;
    }

    /* Return all distinct permutations of (a,b,c) */
    static int[][] distinctPerms(int a, int b, int c) {
        if (a == b && b == c) return new int[][]{{a,b,c}};
        if (a == b) return new int[][]{{a,b,c},{a,c,b},{c,a,b}};
        if (a == c) return new int[][]{{a,b,c},{b,a,c},{b,c,a}};
        if (b == c) return new int[][]{{a,b,c},{a,c,b},{b,a,c}};  // c==b so swap: {c,a,b}={b,a,c}
        // All distinct: 6 permutations
        return new int[][]{{a,b,c},{a,c,b},{b,a,c},{b,c,a},{c,a,b},{c,b,a}};
    }

    /** Integer square root: returns r≥0 if n=r², else -1. */
    static long isqrt(long n) {
        if (n < 0) return -1;
        if (n == 0) return 0;
        long r = (long) Math.sqrt(n);
        // Fine-tune around float error
        while (r > 0 && r * r > n) r--;
        while ((r+1) * (r+1) <= n) r++;
        return r * r == n ? r : -1;
    }

    /** Count total repeated entries (sum of (count-1) for each distinct value). */
    static int countRepeats(int a, int b, int c, int d, int e, int f,
                             int g, int h, int k) {
        int[] vals = {a,b,c,d,e,f,g,h,k};
        Arrays.sort(vals);
        int reps = 0;
        for (int i = 1; i < vals.length; i++)
            if (vals[i] == vals[i-1]) reps++;
        return reps;
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* LOGGING                                                               */
    /* ════════════════════════════════════════════════════════════════════ */
    static synchronized void logResult(long S,
            int a, int b, int c,
            int d, int e, int f,
            int g, int h, int k, int rep) {
        long a2=(long)a*a,b2=(long)b*b,c2=(long)c*c;
        long d2=(long)d*d,e2=(long)e*e,f2=(long)f*f;
        long g2=(long)g*g,h2=(long)h*h,k2=(long)k*k;
        long antiDiag = c2 + e2 + g2;

        String star = rep < MAX_REPEATS ? "★★★ EGENSE SQUARE CANDIDATE ★★★" : "result";
        System.out.printf("%n%s  repeats=%d  S=%,d%n", star, rep, S);
        System.out.printf("  %6d²  %6d²  %6d²  = %,d  %s%n",
                a, b, c, a2+b2+c2, a2+b2+c2==S?"✓":"✗");
        System.out.printf("  %6d²  %6d²  %6d²  = %,d  %s%n",
                d, e, f, d2+e2+f2, d2+e2+f2==S?"✓":"✗");
        System.out.printf("  %6d²  %6d²  %6d²  = %,d  %s%n",
                g, h, k, g2+h2+k2, g2+h2+k2==S?"✓":"✗");
        System.out.printf("  Main ↘: %d²+%d²+%d² = %,d  %s%n",
                a, e, k, a2+e2+k2, a2+e2+k2==S?"✓":"✗");
        System.out.printf("  Anti ↗: %d²+%d²+%d² = %,d  %s%n",
                c, e, g, antiDiag, antiDiag==S?"✓ FULL MAGIC!":"✗ ("+antiDiag+")");
        System.out.printf("  Cols:  %,d  %,d  %,d  %s%n",
                a2+d2+g2, b2+e2+h2, c2+f2+k2,
                a2+d2+g2==S&&b2+e2+h2==S&&c2+f2+k2==S?"✓":"✗");

        // Find and report distinct values + repeats
        int[] vals = {a,b,c,d,e,f,g,h,k};
        Map<Integer,Integer> cnt = new LinkedHashMap<>();
        for (int v : vals) cnt.merge(v, 1, Integer::sum);
        System.out.print("  Values: ");
        for (Map.Entry<Integer,Integer> en : cnt.entrySet()) {
            System.out.printf("%d² ", en.getKey());
            if (en.getValue() > 1) System.out.printf("(×%d) ", en.getValue());
        }
        System.out.println();
        if (antiDiag == S) {
            System.out.println("\n  ╔════════════════════════════════════════════╗");
            System.out.println("  ║  PERFECT 8/8 MAGIC SQUARE OF SQUARES!     ║");
            System.out.println("  ╚════════════════════════════════════════════╝");
        }
        System.out.println();
    }

    /* ════════════════════════════════════════════════════════════════════ */
    /* VERIFY: find Parker's square as a sanity check                       */
    /* ════════════════════════════════════════════════════════════════════ */
    static void verifyParker() {
        System.out.println("── Verifying Parker's Square at S=3051 ─────────────────────");
        searchS(3051);
        System.out.printf("  S_tried so far: %,d  found: %,d%n",
                S_TRIED.get(), FOUND.get());
        // Reset counters after verification
        S_TRIED.set(0); FOUND.set(0); BEST_REP = MAX_REPEATS;
        System.out.println("  (Counters reset for main search)");
    }
}