package dk.teg.bigmathfast.squares;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * 4-prime chain parameterization:
 *   p1 = a1^2 + b1^2   (a1,b1)
 *   p2 = a2^2 + b1^2   (a2,b1)  shares b1 with p1
 *   p3 = a2^2 + b2^2   (a2,b2)  shares a2 with p2
 *   p4 = a3^2 + b2^2   (a3,b2)  shares b2 with p3
 *   n  = p1 * p2 * p3 * p4
 *
 * Parity: a1,a2,a3 even; b1,b2 odd -> all four primes odd.
 * a1 != a2 required (otherwise d2=0).
 *
 * Three winning APs use Gaussian products:
 *   d1: alphas (2,0,2,0) - all active
 *   d2: alphas (2,0,1,1) - p3,p4 scalar; p1,p2 active
 *   d3: alphas (1,0,2,0) - p1 scalar; p2,p3,p4 active
 *   e  = d1 - d2 + d3
 *
 * KEY FEATURES:
 *   - Primitive reduction: divides out GCD of all AP endpoints before quality
 *     calculation. This correctly handles cases where pi values share common
 *     factors, which would otherwise inflate n and deflate quality.
 *     A non-primitive n with r=0.70 may reduce to a primitive with r=0.87+.
 *   - e=0 detection: if the difference is exactly zero, we have found a
 *     3x3 Magic Square of Squares candidate -- the holy grail!
 *
 * Verified: a1=2, a2=4, a3=18, b1=1, b2=5 -> n=1216265, |e|=7680, r=1.5661 ✓
 */
public class SearchHourGlass4PrimesChain {

    private static final double CLOSE_TO_INTEGER_THRESHOLD = 0.005;
    private static final int MAX_GAP_B = 500;
    private static final int MAX_A2    = 200;
    private static final int MAX_B1    = 500;

    public static void main(String[] args) {

        double bestQuality = 0.001d;
        long start = System.currentTimeMillis();

        for (long a1 = 2; a1 <= Long.MAX_VALUE; a1 += 2) {
            for (long a2 = 2; a2 <= MAX_A2; a2 += 2) {
                if (a2 == a1) continue;
                for (long b1 = 1; b1 <= MAX_B1; b1 += 2) {
                    for (int gapB = 2; gapB <= MAX_GAP_B; gapB += 2) {
                        long b2 = b1 + gapB;

                        double[] result = calculateBestR(a1, a2, b1, b2, start);
                        double quality = result[0];
                        long   a3best  = (long) result[1];

                        if (quality >= 0.8 && a3best > 0) {
                            double[] a3cands = calculateOptimalA3(a1, a2, b1, b2);
                            BigInteger[] steps = computeThreeSteps(a1, a2, a3best, b1, b2);
                            BigInteger n    = getMiddleN(a1, a2, a3best, b1, b2);
                            BigInteger diff = steps[0].subtract(steps[1]).add(steps[2]);
                            BigInteger[] prim = toPrimitive(n, steps);
                            BigInteger n_prim = prim[0], e_prim = prim[1];

                            System.out.println(Arrays.toString(a3cands));
                            System.out.println("frac*b2=" + fracTimesB2(a3cands[0], b2));
                            System.out.println("q=" + quality
                                    + "  a1=" + a1 + ", a2=" + a2 + ", a3=" + a3best
                                    + ", b1=" + b1 + ", b2=" + b2
                                    + ", diff=" + diff.abs()
                                    + " (primitive diff=" + e_prim.abs()
                                    + ", primitive n=" + n_prim + ")");
                            printThreeAPs(a1, a2, a3best, b1, b2);
                            bestQuality = quality;
                            System.out.println("time:" + (System.currentTimeMillis() - start));
                        }
                    }
                }
            }

            if (a1 % 1000 == 0) {
                System.out.println("Progress: a1=" + a1
                        + "  time:" + (System.currentTimeMillis() - start));
            }
        }
    }

    // =========================================================================
    // Calculate best primitive quality r for given (a1, a2, b1, b2).
    // Finds optimal a3 analytically, then applies GCD primitive reduction.
    // Also detects e=0 (the holy grail).
    //
    // @return double[2]: [0]=best primitive r, [1]=best a3 as long
    // =========================================================================
    public static double[] calculateBestR(long a1, long a2, long b1, long b2, long start) {

        double bestR  = 0.0;
        long   bestA3 = 0;

        double[] a3cands = calculateOptimalA3(a1, a2, b1, b2);

        for (double a3d : a3cands) {
            if (Double.isNaN(a3d) || nearestEvenLong(a3d) < 2) continue;
            if (!isWorthChecking(a3d, b2)) continue;

            long a3base = nearestEvenLong(a3d);
            for (long a3 : new long[]{a3base - 2, a3base, a3base + 2}) {
                if (a3 < 2) continue;

                BigInteger[] steps = computeThreeSteps(a1, a2, a3, b1, b2);
                BigInteger   diff  = steps[0].subtract(steps[1]).add(steps[2]);
                BigInteger   n     = getMiddleN(a1, a2, a3, b1, b2);

                // *** e = 0: HOLY GRAIL ***
                if (diff.signum() == 0) {
                    System.out.println();
                    System.out.println("=================================================");
                    System.out.println("  *** PERFECT SOLUTION FOUND: e = 0 ***");
                    System.out.println("=================================================");
                    System.out.println("  a1=" + a1 + ", a2=" + a2 + ", a3=" + a3
                                     + ", b1=" + b1 + ", b2=" + b2);
                    System.out.println("  n = " + n);
                    System.out.println("  This is a 3x3 Magic Square of Squares candidate!");
                    printThreeAPs(a1, a2, a3, b1, b2);
                    System.out.println("  time:" + (System.currentTimeMillis() - start));
                    System.out.println("=================================================");
                    System.out.println();
                    // Do NOT skip — record it but continue searching for more
                    bestR  = Double.MAX_VALUE;
                    bestA3 = a3;
                    continue;
                }

                // Reduce to primitive by dividing out GCD of all AP endpoints
                BigInteger[] prim  = toPrimitive(n, steps);
                BigInteger n_prim  = prim[0];
                BigInteger e_prim  = prim[1];

                double r = calculateQuality(e_prim.abs(), n_prim);

                if (r > bestR) {
                    bestR  = r;
                    bestA3 = a3;
                }
            }
        }
        return new double[]{bestR, (double) bestA3};
    }

    // =========================================================================
    // Compute the three individual step values d1, d2, d3 (signed).
    //   e = d1 - d2 + d3
    // Returns BigInteger[]{d1, d2, d3}
    // =========================================================================
    public static BigInteger[] computeThreeSteps(long a1, long a2, long a3, long b1, long b2) {
        BigInteger ba1=BigInteger.valueOf(a1), ba2=BigInteger.valueOf(a2), ba3=BigInteger.valueOf(a3);
        BigInteger bb1=BigInteger.valueOf(b1), bb2=BigInteger.valueOf(b2);
        return computeThreeSteps(ba1, ba2, ba3, bb1, bb2);
    }

    public static BigInteger[] computeThreeSteps(BigInteger a1, BigInteger a2, BigInteger a3,
                                                   BigInteger b1, BigInteger b2) {
        BigInteger a1sq=a1.multiply(a1), a2sq=a2.multiply(a2), a3sq=a3.multiply(a3);
        BigInteger b1sq=b1.multiply(b1), b2sq=b2.multiply(b2);
        BigInteger p1=a1sq.add(b1sq), p3=a2sq.add(b2sq), p4=a3sq.add(b2sq);

        BigInteger[] pi1sq  = {a1sq.subtract(b1sq), BigInteger.TWO.multiply(a1).multiply(b1)};
        BigInteger[] pi2bsq = {a2sq.subtract(b1sq), BigInteger.TWO.multiply(a2).multiply(b1).negate()};
        BigInteger[] pi3sq  = {a2sq.subtract(b2sq), BigInteger.TWO.multiply(a2).multiply(b2)};
        BigInteger[] pi4bsq = {a3sq.subtract(b2sq), BigInteger.TWO.multiply(a3).multiply(b2).negate()};

        // d1: (2,0,2,0) — all active
        BigInteger[] w1 = gaussMult(gaussMult(gaussMult(pi1sq, pi2bsq), pi3sq), pi4bsq);
        BigInteger d1   = stepFromGauss(w1);

        // d2: (2,0,1,1) — p3,p4 scalar
        BigInteger[] w2b = gaussMult(pi1sq, pi2bsq);
        BigInteger   sc  = p3.multiply(p4);
        BigInteger[] w2  = {w2b[0].multiply(sc), w2b[1].multiply(sc)};
        BigInteger d2    = stepFromGauss(w2);

        // d3: (1,0,2,0) — p1 scalar
        BigInteger[] w3b = gaussMult(gaussMult(pi2bsq, pi3sq), pi4bsq);
        BigInteger[] w3  = {w3b[0].multiply(p1), w3b[1].multiply(p1)};
        BigInteger d3    = stepFromGauss(w3);

        return new BigInteger[]{d1, d2, d3};
    }

    // =========================================================================
    // e = d1 - d2 + d3
    // =========================================================================
    public static BigInteger calculateDifference(BigInteger a1, BigInteger a2, BigInteger a3,
                                                  BigInteger b1, BigInteger b2) {
        BigInteger[] steps = computeThreeSteps(a1, a2, a3, b1, b2);
        return steps[0].subtract(steps[1]).add(steps[2]);
    }

    // =========================================================================
    // Primitive reduction.
    //
    // If all AP endpoints share a common factor g, then n'=n/g and e'=e/g^2
    // give the primitive (irreducible) solution, which always has higher quality.
    //
    // g = gcd(n, x1, y1, x2, y2, x3, y3)
    // where xi = sqrt(n^2 - |di|), yi = sqrt(n^2 + |di|)
    //
    // @return BigInteger[]{n_primitive, e_primitive}
    // =========================================================================
    public static BigInteger[] toPrimitive(BigInteger n, BigInteger[] steps) {
        BigInteger d1 = steps[0], d2 = steps[1], d3 = steps[2];
        BigInteger e  = d1.subtract(d2).add(d3);
        BigInteger n2 = n.multiply(n);

        // g starts at n then intersects with each endpoint
        BigInteger g = n;
        for (BigInteger d : new BigInteger[]{d1.abs(), d2.abs(), d3.abs()}) {
            BigInteger x = n2.subtract(d).sqrt();
            BigInteger y = n2.add(d).sqrt();
            g = g.gcd(x).gcd(y);
            if (g.equals(BigInteger.ONE)) break;  // can't reduce further
        }

        if (g.equals(BigInteger.ONE)) {
            return new BigInteger[]{n, e};
        }
        BigInteger g2 = g.multiply(g);
        return new BigInteger[]{n.divide(g), e.divide(g2)};
    }

    // =========================================================================
    // Optimal a3: C4*u^2 + C3*u + C2_new = 0,  u = a3 - b2^2/a3
    //   a3 = (u + sqrt(u^2 + 4*b2^2)) / 2
    // =========================================================================
    public static double[] calculateOptimalA3(long a1, long a2, long b1, long b2) {

        BigInteger ba1=BigInteger.valueOf(a1), ba2=BigInteger.valueOf(a2);
        BigInteger bb1=BigInteger.valueOf(b1), bb2=BigInteger.valueOf(b2);

        BigInteger a1sq=ba1.multiply(ba1), a2sq=ba2.multiply(ba2);
        BigInteger b1sq=bb1.multiply(bb1), b2sq=bb2.multiply(bb2);
        BigInteger a1p4=a1sq.pow(2), a1p6=a1sq.multiply(a1p4), a1p8=a1p4.pow(2);
        BigInteger a2p3=a2sq.multiply(ba2), a2p4=a2sq.pow(2), a2p5=a2p4.multiply(ba2);
        BigInteger a2p6=a2p4.multiply(a2sq), a2p7=a2p6.multiply(ba2), a2p8=a2p4.pow(2);
        BigInteger b1p3=b1sq.multiply(bb1), b1p4=b1sq.pow(2);
        BigInteger b2p3=b2sq.multiply(bb2), b2p4=b2sq.pow(2);
        BigInteger a1p3=a1sq.multiply(ba1), a2p2=a2sq;
        BigInteger a1p5=a1p4.multiply(ba1), a1p7=a1p6.multiply(ba1);
        BigInteger b1p5=b1p4.multiply(bb1), b1p6=b1p4.multiply(b1sq);
        BigInteger b1p7=b1p6.multiply(bb1), b1p8=b1p4.pow(2);
        BigInteger b2p5=b2p4.multiply(bb2), b2p6=b2p4.multiply(b2sq);
        BigInteger b2p7=b2p6.multiply(bb2);

        // C3 = -8*b2*(a1^2-b1^2)*F1*F2
        BigInteger F1 = ba1.multiply(a2p4)
                .subtract(BigInteger.TWO.multiply(ba1).multiply(a2p3).multiply(bb1))
                .add(BigInteger.TWO.multiply(ba1).multiply(a2p3).multiply(bb2))
                .subtract(ba1.multiply(a2sq).multiply(b1sq))
                .add(BigInteger.valueOf(4).multiply(ba1).multiply(a2sq).multiply(bb1).multiply(bb2))
                .subtract(ba1.multiply(a2sq).multiply(b2sq))
                .subtract(BigInteger.TWO.multiply(ba1).multiply(ba2).multiply(b1sq).multiply(bb2))
                .add(BigInteger.TWO.multiply(ba1).multiply(ba2).multiply(bb1).multiply(b2sq))
                .add(ba1.multiply(b1sq).multiply(b2sq))
                .add(a2p4.multiply(bb1))
                .add(BigInteger.TWO.multiply(a2p3).multiply(b1sq))
                .subtract(BigInteger.TWO.multiply(a2p3).multiply(bb1).multiply(bb2))
                .subtract(a2sq.multiply(b1p3))
                .add(BigInteger.valueOf(4).multiply(a2sq).multiply(b1sq).multiply(bb2))
                .subtract(a2sq.multiply(bb1).multiply(b2sq))
                .add(BigInteger.TWO.multiply(ba2).multiply(b1p3).multiply(bb2))
                .subtract(BigInteger.TWO.multiply(ba2).multiply(b1sq).multiply(b2sq))
                .add(b1p3.multiply(b2sq));

        BigInteger F2 = ba1.multiply(a2p4)
                .add(BigInteger.TWO.multiply(ba1).multiply(a2p3).multiply(bb1))
                .subtract(BigInteger.TWO.multiply(ba1).multiply(a2p3).multiply(bb2))
                .subtract(ba1.multiply(a2sq).multiply(b1sq))
                .add(BigInteger.valueOf(4).multiply(ba1).multiply(a2sq).multiply(bb1).multiply(bb2))
                .subtract(ba1.multiply(a2sq).multiply(b2sq))
                .add(BigInteger.TWO.multiply(ba1).multiply(ba2).multiply(b1sq).multiply(bb2))
                .subtract(BigInteger.TWO.multiply(ba1).multiply(ba2).multiply(bb1).multiply(b2sq))
                .add(ba1.multiply(b1sq).multiply(b2sq))
                .subtract(a2p4.multiply(bb1))
                .add(BigInteger.TWO.multiply(a2p3).multiply(b1sq))
                .subtract(BigInteger.TWO.multiply(a2p3).multiply(bb1).multiply(bb2))
                .add(a2sq.multiply(b1p3))
                .subtract(BigInteger.valueOf(4).multiply(a2sq).multiply(b1sq).multiply(bb2))
                .add(a2sq.multiply(bb1).multiply(b2sq))
                .add(BigInteger.TWO.multiply(ba2).multiply(b1p3).multiply(bb2))
                .subtract(BigInteger.TWO.multiply(ba2).multiply(b1sq).multiply(b2sq))
                .subtract(b1p3.multiply(b2sq));

        BigInteger C3 = BigInteger.valueOf(-8).multiply(bb2)
                .multiply(a1sq.subtract(b1sq))
                .multiply(F1).multiply(F2);

        // C4 (term-by-term from sympy expansion)
        BigInteger C4 = computeC4(ba1,ba2,bb1,bb2,a1sq,a2sq,b1sq,b2sq,
                a1p3,a2p3,a1p4,a2p4,b1p3,b2p3,b1p4,b2p4,
                a1p5,a2p5,a1p6,a2p6,a1p7,a2p7,b1p5,b1p6,b1p7,b1p8);

        // C2_new: full polynomial from sympy (NOT the 3-prime shortcut -4*b2^2*C4)
        BigInteger C2_new = computeC2new(ba1,ba2,bb1,bb2,a1sq,a2sq,b1sq,b2sq,
                a1p3,a2p3,a1p4,a2p4,b1p3,b2p3,b1p4,b2p4,
                a1p5,a2p5,a1p6,a2p6,a1p7,a2p7,b1p5,b1p6,b1p7,b1p8,
                b2p5,b2p6,b2p7,a2p8);

        // D = C3^2 - 4*C4*C2_new
        BigInteger discriminant = C3.multiply(C3)
                .subtract(BigInteger.valueOf(4).multiply(C4).multiply(C2_new));

        if (discriminant.signum() < 0) return new double[]{Double.NaN, Double.NaN};

        double C4d   = C4.doubleValue();
        double C3d   = C3.doubleValue();
        double sqrtD = Math.sqrt(discriminant.doubleValue());
        double u1    = (-C3d + sqrtD) / (2 * C4d);
        double u2    = (-C3d - sqrtD) / (2 * C4d);
        double b2sq_d = (double) b2 * b2;

        double a3_1 = (u1 + Math.sqrt(u1 * u1 + 4 * b2sq_d)) / 2.0;
        double a3_2 = (u2 + Math.sqrt(u2 * u2 + 4 * b2sq_d)) / 2.0;
        return new double[]{a3_1, a3_2};
    }

    // C4 coefficient of a3^4 in e (from sympy expansion)
    private static BigInteger computeC4(BigInteger a1, BigInteger a2, BigInteger b1, BigInteger b2,
            BigInteger a1sq, BigInteger a2sq, BigInteger b1sq, BigInteger b2sq,
            BigInteger a1p3, BigInteger a2p3, BigInteger a1p4, BigInteger a2p4,
            BigInteger b1p3, BigInteger b2p3, BigInteger b1p4, BigInteger b2p4,
            BigInteger a1p5, BigInteger a2p5, BigInteger a1p6, BigInteger a2p6,
            BigInteger a1p7, BigInteger a2p7, BigInteger b1p5, BigInteger b1p6,
            BigInteger b1p7, BigInteger b1p8) {
        BigInteger r = BigInteger.ZERO;
        r = r.subtract(n(4).multiply(a1p4).multiply(a2p7).multiply(b1));
        r = r.add(n(8).multiply(a1p4).multiply(a2p7).multiply(b2));
        r = r.add(n(4).multiply(a1p4).multiply(a2p5).multiply(b1p3));
        r = r.subtract(n(48).multiply(a1p4).multiply(a2p5).multiply(b1sq).multiply(b2));
        r = r.add(n(56).multiply(a1p4).multiply(a2p5).multiply(b1).multiply(b2sq));
        r = r.subtract(n(8).multiply(a1p4).multiply(a2p5).multiply(b2p3));
        r = r.add(n(8).multiply(a1p4).multiply(a2p3).multiply(b1p4).multiply(b2));
        r = r.subtract(n(56).multiply(a1p4).multiply(a2p3).multiply(b1p3).multiply(b2sq));
        r = r.add(n(48).multiply(a1p4).multiply(a2p3).multiply(b1sq).multiply(b2p3));
        r = r.subtract(n(4).multiply(a1p4).multiply(a2p3).multiply(b1).multiply(b2p4));
        r = r.subtract(n(8).multiply(a1p4).multiply(a2).multiply(b1p4).multiply(b2p3));
        r = r.add(n(4).multiply(a1p4).multiply(a2).multiply(b1p3).multiply(b2p4));
        r = r.add(n(64).multiply(a1p3).multiply(a2p6).multiply(b1sq).multiply(b2));
        r = r.subtract(n(32).multiply(a1p3).multiply(a2p6).multiply(b1).multiply(b2sq));
        r = r.subtract(n(64).multiply(a1p3).multiply(a2p4).multiply(b1p4).multiply(b2));
        r = r.add(n(192).multiply(a1p3).multiply(a2p4).multiply(b1p3).multiply(b2sq));
        r = r.subtract(n(64).multiply(a1p3).multiply(a2p4).multiply(b1sq).multiply(b2p3));
        r = r.subtract(n(32).multiply(a1p3).multiply(a2sq).multiply(b1p5).multiply(b2sq));
        r = r.add(n(64).multiply(a1p3).multiply(a2sq).multiply(b1p4).multiply(b2p3));
        r = r.subtract(n(8).multiply(a1sq).multiply(a2p7).multiply(b1p3));
        r = r.subtract(n(16).multiply(a1sq).multiply(a2p7).multiply(b1sq).multiply(b2));
        r = r.add(n(8).multiply(a1sq).multiply(a2p5).multiply(b1p5));
        r = r.add(n(96).multiply(a1sq).multiply(a2p5).multiply(b1p4).multiply(b2));
        r = r.subtract(n(144).multiply(a1sq).multiply(a2p5).multiply(b1p3).multiply(b2sq));
        r = r.add(n(16).multiply(a1sq).multiply(a2p5).multiply(b1sq).multiply(b2p3));
        r = r.subtract(n(16).multiply(a1sq).multiply(a2p3).multiply(b1p6).multiply(b2));
        r = r.add(n(144).multiply(a1sq).multiply(a2p3).multiply(b1p5).multiply(b2sq));
        r = r.subtract(n(96).multiply(a1sq).multiply(a2p3).multiply(b1p4).multiply(b2p3));
        r = r.subtract(n(8).multiply(a1sq).multiply(a2p3).multiply(b1p3).multiply(b2p4));
        r = r.add(n(16).multiply(a1sq).multiply(a2).multiply(b1p6).multiply(b2p3));
        r = r.add(n(8).multiply(a1sq).multiply(a2).multiply(b1p5).multiply(b2p4));
        r = r.subtract(n(64).multiply(a1).multiply(a2p6).multiply(b1p4).multiply(b2));
        r = r.add(n(32).multiply(a1).multiply(a2p6).multiply(b1p3).multiply(b2sq));
        r = r.add(n(64).multiply(a1).multiply(a2p4).multiply(b1p6).multiply(b2));
        r = r.subtract(n(192).multiply(a1).multiply(a2p4).multiply(b1p5).multiply(b2sq));
        r = r.add(n(64).multiply(a1).multiply(a2p4).multiply(b1p4).multiply(b2p3));
        r = r.add(n(32).multiply(a1).multiply(a2sq).multiply(b1p7).multiply(b2sq));
        r = r.subtract(n(64).multiply(a1).multiply(a2sq).multiply(b1p6).multiply(b2p3));
        r = r.subtract(n(4).multiply(a2p7).multiply(b1p5));
        r = r.add(n(8).multiply(a2p7).multiply(b1p4).multiply(b2));
        r = r.add(n(4).multiply(a2p5).multiply(b1p7));
        r = r.subtract(n(48).multiply(a2p5).multiply(b1p6).multiply(b2));
        r = r.add(n(56).multiply(a2p5).multiply(b1p5).multiply(b2sq));
        r = r.subtract(n(8).multiply(a2p5).multiply(b1p4).multiply(b2p3));
        r = r.add(n(8).multiply(a2p3).multiply(b1p8).multiply(b2));
        r = r.subtract(n(56).multiply(a2p3).multiply(b1p7).multiply(b2sq));
        r = r.add(n(48).multiply(a2p3).multiply(b1p6).multiply(b2p3));
        r = r.subtract(n(4).multiply(a2p3).multiply(b1p5).multiply(b2p4));
        r = r.subtract(n(8).multiply(a2).multiply(b1p8).multiply(b2p3));
        r = r.add(n(4).multiply(a2).multiply(b1p7).multiply(b2p4));
        return r;
    }

    // =========================================================================
    // Middle number n = p1 * p2 * p3 * p4
    // =========================================================================
    public static BigInteger getMiddleN(long a1, long a2, long a3, long b1, long b2) {
        BigInteger ba1=BigInteger.valueOf(a1), ba2=BigInteger.valueOf(a2);
        BigInteger ba3=BigInteger.valueOf(a3), bb1=BigInteger.valueOf(b1), bb2=BigInteger.valueOf(b2);
        BigInteger a1sq=ba1.multiply(ba1), a2sq=ba2.multiply(ba2), a3sq=ba3.multiply(ba3);
        BigInteger b1sq=bb1.multiply(bb1), b2sq=bb2.multiply(bb2);
        return a1sq.add(b1sq).multiply(a2sq.add(b1sq))
                              .multiply(a2sq.add(b2sq))
                              .multiply(a3sq.add(b2sq));
    }

    // =========================================================================
    // Print three APs — also shows primitive reduction if g > 1
    // =========================================================================
    public static void printThreeAPs(long a1, long a2, long a3, long b1, long b2) {
        BigInteger[] steps = computeThreeSteps(a1, a2, a3, b1, b2);
        BigInteger   n     = getMiddleN(a1, a2, a3, b1, b2);
        BigInteger   n2    = n.multiply(n);
        BigInteger   d1    = steps[0].abs();
        BigInteger   d2    = steps[1].abs();
        BigInteger   d3    = steps[2].abs();

        printAP(n, n2, d1); System.out.print(" , ");
        printAP(n, n2, d2); System.out.print(" , ");
        printAP(n, n2, d3); System.out.println();

        // Show primitive reduction if applicable
        BigInteger[] prim = toPrimitive(n, steps);
        BigInteger n_prim = prim[0];
        if (!n_prim.equals(n)) {
            BigInteger g  = n.divide(n_prim);
            BigInteger n2p = n_prim.multiply(n_prim);
            System.out.println("  [Primitive g=" + g + ", n'=" + n_prim + "]");
            printAP(n_prim, n2p, d1.divide(g.multiply(g))); System.out.print(" , ");
            printAP(n_prim, n2p, d2.divide(g.multiply(g))); System.out.print(" , ");
            printAP(n_prim, n2p, d3.divide(g.multiply(g))); System.out.println();
        }
    }

    private static void printAP(BigInteger n, BigInteger n2, BigInteger d) {
        BigInteger x = n2.subtract(d).sqrt();
        BigInteger y = n2.add(d).sqrt();
        System.out.print("(" + x + "," + n + "," + y + ":" + d + ")");
    }

    // =========================================================================
    // Gaussian integer helpers
    // =========================================================================
    private static BigInteger[] gaussMult(BigInteger[] z1, BigInteger[] z2) {
        return new BigInteger[]{
            z1[0].multiply(z2[0]).subtract(z1[1].multiply(z2[1])),
            z1[0].multiply(z2[1]).add(z1[1].multiply(z2[0]))
        };
    }

    /** step = 2*w[0]*w[1]  (from (1+i)*w structure) */
    private static BigInteger stepFromGauss(BigInteger[] w) {
        return BigInteger.TWO.multiply(w[0]).multiply(w[1]);
    }

    private static BigInteger n(long v) { return BigInteger.valueOf(v); }

    // =========================================================================
    // Search helpers
    // =========================================================================
    public static long nearestEvenLong(double value) {
        long r = Math.round(value);
        if (r % 2 != 0) {
            long lo = r - 1, hi = r + 1;
            return (Math.abs(value - lo) <= Math.abs(value - hi)) ? lo : hi;
        }
        return r;
    }

    public static boolean isCloseToInteger(double value) {
        return Math.abs(value - Math.round(value)) < CLOSE_TO_INTEGER_THRESHOLD;
    }

    public static boolean isWorthChecking(double a3double, long b2) {
        if (Double.isNaN(a3double)) return false;
        if (isCloseToInteger(a3double)) return true;
        double frac = Math.abs(a3double / b2 - Math.round(a3double / b2));
        return Math.abs(frac * b2 - Math.round(frac * b2)) < CLOSE_TO_INTEGER_THRESHOLD * b2;
    }

    public static double fracTimesB2(double a3double, long b2) {
        double frac = Math.abs(a3double / b2 - Math.round(a3double / b2));
        return frac * b2;
    }

    // =========================================================================
    // Quality and logging — uses PRIMITIVE n and e
    // =========================================================================
    public static double calculateQuality(BigInteger diff, BigInteger middleNumber) {
        double a = bigIntLog(middleNumber, 2);
        double b = bigIntLog(diff, 2);
        String s = String.format("%.4f", a / b);
        return Double.valueOf(s.replaceAll(",", "."));
    }

    public static double bigIntLog(BigInteger number, double base) {
        BigDecimal bd  = new BigDecimal(number);
        BigDecimal div = new BigDecimal(10).pow(number.toString().length() - 1);
        double bd_dbl  = bd.divide(div).doubleValue();
        return (Math.log10(bd_dbl) + number.toString().length() - 1) / Math.log10(base);
    }
    // C2_new coefficient of a3^2 in e, from sympy expansion
    private static BigInteger computeC2new(BigInteger a1, BigInteger a2, BigInteger b1, BigInteger b2,
            BigInteger a1sq, BigInteger a2sq, BigInteger b1sq, BigInteger b2sq,
            BigInteger a1p3, BigInteger a2p3, BigInteger a1p4, BigInteger a2p4,
            BigInteger b1p3, BigInteger b2p3, BigInteger b1p4, BigInteger b2p4,
            BigInteger a1p5, BigInteger a2p5, BigInteger a1p6, BigInteger a2p6,
            BigInteger a1p7, BigInteger a2p7, BigInteger b1p5, BigInteger b1p6,
            BigInteger b1p7, BigInteger b1p8,
            BigInteger b2p5, BigInteger b2p6, BigInteger b2p7, BigInteger a2p8) {
        BigInteger r = BigInteger.ZERO;
        r = r.add(n(48).multiply(a1p4).multiply(a2p7).multiply(b1).multiply(b2sq));
        r = r.subtract(n(32).multiply(a1p4).multiply(a2p7).multiply(b2p3));
        r = r.subtract(n(48).multiply(a1p4).multiply(a2p5).multiply(b1p3).multiply(b2sq));
        r = r.add(n(192).multiply(a1p4).multiply(a2p5).multiply(b1sq).multiply(b2p3));
        r = r.subtract(n(160).multiply(a1p4).multiply(a2p5).multiply(b1).multiply(b2p4));
        r = r.add(n(32).multiply(a1p4).multiply(a2p5).multiply(b2p5));
        r = r.subtract(n(32).multiply(a1p4).multiply(a2p3).multiply(b1p4).multiply(b2p3));
        r = r.add(n(160).multiply(a1p4).multiply(a2p3).multiply(b1p3).multiply(b2p4));
        r = r.subtract(n(192).multiply(a1p4).multiply(a2p3).multiply(b1sq).multiply(b2p5));
        r = r.add(n(48).multiply(a1p4).multiply(a2p3).multiply(b1).multiply(b2p6));
        r = r.add(n(32).multiply(a1p4).multiply(a2).multiply(b1p4).multiply(b2p5));
        r = r.subtract(n(48).multiply(a1p4).multiply(a2).multiply(b1p3).multiply(b2p6));
        r = r.subtract(n(32).multiply(a1p3).multiply(a2p8).multiply(b1).multiply(b2sq));
        r = r.add(n(192).multiply(a1p3).multiply(a2p6).multiply(b1p3).multiply(b2sq));
        r = r.subtract(n(256).multiply(a1p3).multiply(a2p6).multiply(b1sq).multiply(b2p3));
        r = r.add(n(64).multiply(a1p3).multiply(a2p6).multiply(b1).multiply(b2p4));
        r = r.subtract(n(32).multiply(a1p3).multiply(a2p4).multiply(b1p5).multiply(b2sq));
        r = r.add(n(256).multiply(a1p3).multiply(a2p4).multiply(b1p4).multiply(b2p3));
        r = r.subtract(n(384).multiply(a1p3).multiply(a2p4).multiply(b1p3).multiply(b2p4));
        r = r.add(n(256).multiply(a1p3).multiply(a2p4).multiply(b1sq).multiply(b2p5));
        r = r.subtract(n(32).multiply(a1p3).multiply(a2p4).multiply(b1).multiply(b2p6));
        r = r.add(n(64).multiply(a1p3).multiply(a2sq).multiply(b1p5).multiply(b2p4));
        r = r.subtract(n(256).multiply(a1p3).multiply(a2sq).multiply(b1p4).multiply(b2p5));
        r = r.add(n(192).multiply(a1p3).multiply(a2sq).multiply(b1p3).multiply(b2p6));
        r = r.subtract(n(32).multiply(a1p3).multiply(b1p5).multiply(b2p6));
        r = r.subtract(n(160).multiply(a1sq).multiply(a2p7).multiply(b1p3).multiply(b2sq));
        r = r.add(n(64).multiply(a1sq).multiply(a2p7).multiply(b1sq).multiply(b2p3));
        r = r.add(n(160).multiply(a1sq).multiply(a2p5).multiply(b1p5).multiply(b2sq));
        r = r.subtract(n(384).multiply(a1sq).multiply(a2p5).multiply(b1p4).multiply(b2p3));
        r = r.add(n(192).multiply(a1sq).multiply(a2p5).multiply(b1p3).multiply(b2p4));
        r = r.subtract(n(64).multiply(a1sq).multiply(a2p5).multiply(b1sq).multiply(b2p5));
        r = r.add(n(64).multiply(a1sq).multiply(a2p3).multiply(b1p6).multiply(b2p3));
        r = r.subtract(n(192).multiply(a1sq).multiply(a2p3).multiply(b1p5).multiply(b2p4));
        r = r.add(n(384).multiply(a1sq).multiply(a2p3).multiply(b1p4).multiply(b2p5));
        r = r.subtract(n(160).multiply(a1sq).multiply(a2p3).multiply(b1p3).multiply(b2p6));
        r = r.subtract(n(64).multiply(a1sq).multiply(a2).multiply(b1p6).multiply(b2p5));
        r = r.add(n(160).multiply(a1sq).multiply(a2).multiply(b1p5).multiply(b2p6));
        r = r.add(n(32).multiply(a1).multiply(a2p8).multiply(b1p3).multiply(b2sq));
        r = r.subtract(n(192).multiply(a1).multiply(a2p6).multiply(b1p5).multiply(b2sq));
        r = r.add(n(256).multiply(a1).multiply(a2p6).multiply(b1p4).multiply(b2p3));
        r = r.subtract(n(64).multiply(a1).multiply(a2p6).multiply(b1p3).multiply(b2p4));
        r = r.add(n(32).multiply(a1).multiply(a2p4).multiply(b1p7).multiply(b2sq));
        r = r.subtract(n(256).multiply(a1).multiply(a2p4).multiply(b1p6).multiply(b2p3));
        r = r.add(n(384).multiply(a1).multiply(a2p4).multiply(b1p5).multiply(b2p4));
        r = r.subtract(n(256).multiply(a1).multiply(a2p4).multiply(b1p4).multiply(b2p5));
        r = r.add(n(32).multiply(a1).multiply(a2p4).multiply(b1p3).multiply(b2p6));
        r = r.subtract(n(64).multiply(a1).multiply(a2sq).multiply(b1p7).multiply(b2p4));
        r = r.add(n(256).multiply(a1).multiply(a2sq).multiply(b1p6).multiply(b2p5));
        r = r.subtract(n(192).multiply(a1).multiply(a2sq).multiply(b1p5).multiply(b2p6));
        r = r.add(n(32).multiply(a1).multiply(b1p7).multiply(b2p6));
        r = r.add(n(48).multiply(a2p7).multiply(b1p5).multiply(b2sq));
        r = r.subtract(n(32).multiply(a2p7).multiply(b1p4).multiply(b2p3));
        r = r.subtract(n(48).multiply(a2p5).multiply(b1p7).multiply(b2sq));
        r = r.add(n(192).multiply(a2p5).multiply(b1p6).multiply(b2p3));
        r = r.subtract(n(160).multiply(a2p5).multiply(b1p5).multiply(b2p4));
        r = r.add(n(32).multiply(a2p5).multiply(b1p4).multiply(b2p5));
        r = r.subtract(n(32).multiply(a2p3).multiply(b1p8).multiply(b2p3));
        r = r.add(n(160).multiply(a2p3).multiply(b1p7).multiply(b2p4));
        r = r.subtract(n(192).multiply(a2p3).multiply(b1p6).multiply(b2p5));
        r = r.add(n(48).multiply(a2p3).multiply(b1p5).multiply(b2p6));
        r = r.add(n(32).multiply(a2).multiply(b1p8).multiply(b2p5));
        r = r.subtract(n(48).multiply(a2).multiply(b1p7).multiply(b2p6));
        return r;
    }


}