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
 * Three winning APs use Gaussian products with alphas:
 *   d1: (2,0,2,0) - all active
 *   d2: (2,0,1,1) - p3,p4 scalar; p1,p2 active
 *   d3: (1,0,2,0) - p1 scalar; p2,p3,p4 active
 *
 * e = d1 - d2 + d3  (signed)
 *
 * Palindromic in a3:  u = a3 - b2^2/a3
 *   C4*u^2 + C3*u + C2_new = 0
 *   -> a3 = (u + sqrt(u^2 + 4*b2^2)) / 2
 *
 * Verified: a1=2, a2=4, a3=18, b1=1, b2=5 -> n=1216265, |e|=7680, r=1.5661 ✓
 */
public class SearchHourGlass4PrimesChain {

    private static final double CLOSE_TO_INTEGER_THRESHOLD = 0.005;
    private static final int MAX_GAP_B = 500;   // max |b1 - b2|
    private static final int MAX_A2    = 200;   // max a2 to iterate
    private static final int MAX_B1    = 200;   // max b1 to iterate

    public static void main(String[] args) {

        double bestQuality = 0.001d;
        long start = System.currentTimeMillis();

        // a1 even, iterate outward
        for (long a1 = 2; a1 <= Long.MAX_VALUE; a1 += 2) {

            // a2 even, a2 != a1
            for (long a2 = 2; a2 <= MAX_A2; a2 += 2) {
                if (a2 == a1) continue;

                // b1 odd
                for (long b1 = 1; b1 <= MAX_B1; b1 += 2) {

                    // b2 odd, b2 > b1
                    for (int gapB = 2; gapB <= MAX_GAP_B; gapB += 2) {
                        long b2 = b1 + gapB;

                        double[] result = calculateBestR(a1, a2, b1, b2);
                        double quality = result[0];
                        long   a3best  = (long) result[1];

                        if (quality >= 0.7d) {
                            double[] a3cands = calculateOptimalA3(a1, a2, b1, b2);
                            BigInteger diff = calculateDifference(
                                    BigInteger.valueOf(a1), BigInteger.valueOf(a2),
                                    BigInteger.valueOf(a3best), BigInteger.valueOf(b1),
                                    BigInteger.valueOf(b2)).abs();
                            System.out.println(Arrays.toString(a3cands));
                            System.out.println("frac*b2=" + fracTimesB2(a3cands[0], b2));
                            System.out.println("q=" + quality
                                    + "  a1=" + a1 + ", a2=" + a2 + ", a3=" + a3best
                                    + ", b1=" + b1 + ", b2=" + b2
                                    + ", diff=" + diff);
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

    /**
     * Calculate the best quality ratio r for the given (a1, a2, b1, b2),
     * finding the optimal a3 analytically via the quadratic formula.
     *
     * @return double[2] where [0] = best r value, [1] = best a3 (as double cast to long)
     */
    public static double[] calculateBestR(long a1, long a2, long b1, long b2) {

        double bestR = 0.0;
        long   bestA3 = 0;

        double[] a3cands = calculateOptimalA3(a1, a2, b1, b2);

        for (double a3d : a3cands) {
            if (Double.isNaN(a3d) || nearestEvenLong(a3d) < 2) continue;
            if (!isWorthChecking(a3d, b2)) continue;

            long a3base = nearestEvenLong(a3d);
            for (long a3 : new long[]{a3base - 2, a3base, a3base + 2}) {
                if (a3 < 2) continue;

                BigInteger diff = calculateDifference(
                        BigInteger.valueOf(a1), BigInteger.valueOf(a2),
                        BigInteger.valueOf(a3), BigInteger.valueOf(b1),
                        BigInteger.valueOf(b2));
                if (diff.signum() == 0) continue;

                BigInteger n = getMiddleN(a1, a2, a3, b1, b2);
                double r = calculateQuality(diff.abs(), n);

                if (r > bestR) {
                    bestR  = r;
                    bestA3 = a3;
                }
            }
        }
        return new double[]{bestR, (double) bestA3};
    }

    // =========================================================================
    // e = d1 - d2 + d3  using Gaussian integer products with (1+i) factor
    //
    // Gaussian factors:
    //   pi1 = (a1 + b1*i),   pi1_bar = (a1 - b1*i)
    //   pi2 = (a2 + b1*i),   pi2_bar = (a2 - b1*i)
    //   pi3 = (a2 + b2*i),   pi3_bar = (a2 - b2*i)
    //   pi4 = (a3 + b2*i),   pi4_bar = (a3 - b2*i)
    //
    //   d1: (1+i) * pi1^2 * pi2_bar^2 * pi3^2 * pi4_bar^2   alphas (2,0,2,0)
    //   d2: (1+i) * pi1^2 * pi2_bar^2 * p3    * p4           alphas (2,0,1,1)
    //   d3: (1+i) * p1    * pi2_bar^2 * pi3^2 * pi4_bar^2   alphas (1,0,2,0)
    //
    //   step = (Y^2 - X^2) / 2  where w_final=(X',Y'), X=min, Y=max
    // =========================================================================
    public static BigInteger calculateDifference(BigInteger a1, BigInteger a2, BigInteger a3,
                                                  BigInteger b1, BigInteger b2) {
        BigInteger a1sq = a1.multiply(a1), b1sq = b1.multiply(b1);
        BigInteger a2sq = a2.multiply(a2), b2sq = b2.multiply(b2);
        BigInteger a3sq = a3.multiply(a3);

        BigInteger p1 = a1sq.add(b1sq);
        BigInteger p3 = a2sq.add(b2sq);
        BigInteger p4 = a3sq.add(b2sq);

        // Squared Gaussian factors (real, imag):
        // pi1^2      = (a1^2-b1^2, 2*a1*b1)
        BigInteger[] pi1sq  = {a1sq.subtract(b1sq), BigInteger.TWO.multiply(a1).multiply(b1)};
        // pi2_bar^2  = (a2^2-b1^2, -2*a2*b1)
        BigInteger[] pi2bsq = {a2sq.subtract(b1sq), BigInteger.TWO.multiply(a2).multiply(b1).negate()};
        // pi3^2      = (a2^2-b2^2, 2*a2*b2)
        BigInteger[] pi3sq  = {a2sq.subtract(b2sq), BigInteger.TWO.multiply(a2).multiply(b2)};
        // pi4_bar^2  = (a3^2-b2^2, -2*a3*b2)
        BigInteger[] pi4bsq = {a3sq.subtract(b2sq), BigInteger.TWO.multiply(a3).multiply(b2).negate()};

        // d1: (1+i) * pi1^2 * pi2_bar^2 * pi3^2 * pi4_bar^2
        BigInteger[] w1 = gaussMult(gaussMult(gaussMult(pi1sq, pi2bsq), pi3sq), pi4bsq);
        BigInteger d1 = stepFromGauss(w1);

        // d2: (1+i) * pi1^2 * pi2_bar^2 * p3 * p4
        // p3,p4 real scalars -> multiply (1+i)*pi1^2*pi2_bar^2 by p3*p4
        BigInteger[] w2base = gaussMult(pi1sq, pi2bsq);
        BigInteger scalar2  = p3.multiply(p4);
        BigInteger[] w2 = {w2base[0].multiply(scalar2), w2base[1].multiply(scalar2)};
        BigInteger d2 = stepFromGauss(w2);

        // d3: (1+i) * p1 * pi2_bar^2 * pi3^2 * pi4_bar^2
        BigInteger[] w3base = gaussMult(gaussMult(pi2bsq, pi3sq), pi4bsq);
        BigInteger[] w3 = {w3base[0].multiply(p1), w3base[1].multiply(p1)};
        BigInteger d3 = stepFromGauss(w3);

        // e = d1 - d2 + d3
        return d1.subtract(d2).add(d3);
    }

    /** Gaussian multiply: (a+bi)(c+di) = (ac-bd, ad+bc) */
    private static BigInteger[] gaussMult(BigInteger[] z1, BigInteger[] z2) {
        return new BigInteger[]{
            z1[0].multiply(z2[0]).subtract(z1[1].multiply(z2[1])),
            z1[0].multiply(z2[1]).add(z1[1].multiply(z2[0]))
        };
    }

    /** step = (Y^2 - X^2)/2 from w, where w_final = (w[0]-w[1], w[0]+w[1]) */
    private static BigInteger stepFromGauss(BigInteger[] w) {
        BigInteger X = w[0].subtract(w[1]);
        BigInteger Y = w[0].add(w[1]);
        // step = (Y^2 - X^2)/2 = ((w0+w1)^2 - (w0-w1)^2)/2 = 2*w0*w1
        return BigInteger.TWO.multiply(w[0]).multiply(w[1]);
    }

    // =========================================================================
    // Optimal a3: C4*u^2 + C3*u + C2_new = 0,  u = a3 - b2^2/a3
    //   a3 = (u + sqrt(u^2 + 4*b2^2)) / 2
    //
    // C4 = -4*a2 * P(a1,a2,b1,b2)   [complex polynomial, ~50 terms]
    // C3 = -8*b2*(a1^2-b1^2) * F1^2 * F2^2   [factored]
    // C2_new = 16*b2^2 * Q(a1,a2,b1,b2)    [complex polynomial]
    // D  = C3^2 - 4*C4*C2_new  = 64*b2^2*p3^4*G1*G2  [always >= 0 in practice]
    // =========================================================================
    public static double[] calculateOptimalA3(long a1, long a2, long b1, long b2) {

        BigInteger ba1 = BigInteger.valueOf(a1), ba2 = BigInteger.valueOf(a2);
        BigInteger bb1 = BigInteger.valueOf(b1), bb2 = BigInteger.valueOf(b2);

        BigInteger a1sq = ba1.multiply(ba1), a2sq = ba2.multiply(ba2);
        BigInteger b1sq = bb1.multiply(bb1), b2sq = bb2.multiply(bb2);
        BigInteger a1p3 = a1sq.multiply(ba1), a2p3 = a2sq.multiply(ba2);
        BigInteger a1p4 = a1sq.multiply(a1sq), a2p4 = a2sq.multiply(a2sq);
        BigInteger b1p3 = b1sq.multiply(bb1), b2p3 = b2sq.multiply(bb2);
        BigInteger b1p4 = b1sq.multiply(b1sq), b2p4 = b2sq.multiply(b2sq);
        BigInteger a1p5 = a1p4.multiply(ba1), a2p5 = a2p4.multiply(ba2);
        BigInteger a1p6 = a1p4.multiply(a1sq), a2p6 = a2p4.multiply(a2sq);
        BigInteger a1p7 = a1p6.multiply(ba1), a2p7 = a2p6.multiply(ba2);
        BigInteger a1p8 = a1p4.multiply(a1p4), a2p8 = a2p4.multiply(a2p4);
        BigInteger b1p5 = b1p4.multiply(bb1), b2p5 = b2p4.multiply(bb2);
        BigInteger b1p6 = b1p4.multiply(b1sq), b2p6 = b2p4.multiply(b2sq);
        BigInteger b1p7 = b1p6.multiply(bb1), b2p7 = b2p6.multiply(bb2);
        BigInteger b1p8 = b1p4.multiply(b1p4);

        // C3 = -8*b2*(a1^2-b1^2) * F1 * F2
        // F1 = a1*a2^4 - 2*a1*a2^3*b1 + 2*a1*a2^3*b2 - a1*a2^2*b1^2 + 4*a1*a2^2*b1*b2
        //      - a1*a2^2*b2^2 - 2*a1*a2*b1^2*b2 + 2*a1*a2*b1*b2^2 + a1*b1^2*b2^2
        //      + a2^4*b1 + 2*a2^3*b1^2 - 2*a2^3*b1*b2 - a2^2*b1^3 + 4*a2^2*b1^2*b2
        //      - a2^2*b1*b2^2 + 2*a2*b1^3*b2 - 2*a2*b1^2*b2^2 + b1^3*b2^2
        // F2 = same but with -a2^4*b1 terms (sign flipped on a2-b1 cross terms)
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

        // C4: computed term by term from expanded polynomial (-4*a2 * P)
        // Use the fact that C4 = c4 from e_poly; implement via Gaussian approach instead:
        // C4 and C2_new come from the quadratic structure. We use:
        // C4 = coefficient of a3^4 in e
        // C2_new = C2_orig + 2*b2^2*C4
        // Rather than expanding C4 directly (50+ terms), derive it from d2 structure:
        // From the palindromic property: C4 = coefficient of a3^4
        // Only d1 and d3 contain a3 (d2 has a3 only via p4=a3^2+b2^2)
        // Wait - d2 DOES contain a3! Let me reconsider...
        // Actually let me just compute C4 from d2's a3 dependence directly.
        // d2 contains p4^2 = (a3^2+b2^2)^2 as a factor, giving a3^4 terms.
        // d1 and d3 also contribute a3^4 terms.
        // The cleanest approach: compute C4 numerically using BigInteger,
        // by evaluating e at a3=1 and extracting via finite differences.
        // But actually let's implement C4 as the closed form:
        // C4 = c4 coefficient = full expansion. Use helper method.
        BigInteger C4 = computeC4(ba1, ba2, bb1, bb2,
                a1sq, a2sq, b1sq, b2sq, a1p3, a2p3, a1p4, a2p4,
                b1p3, b2p3, b1p4, b2p4, a1p5, a2p5, a1p6, a2p6,
                a1p7, a2p7, b1p5, b1p6, b1p7, b1p8);

        BigInteger C2_new = computeC2new(ba1, ba2, bb1, bb2,
                a1sq, a2sq, b1sq, b2sq, a1p3, a2p3, a1p4, a2p4,
                b1p3, b2p3, b1p4, b2p4, a1p5, a2p5, a1p6, a2p6,
                a1p7, a2p7, b1p5, b1p6, b1p7, b1p8,
                b2p5, b2p6, b2p7, a2p8);

        BigInteger discriminant = C3.multiply(C3)
                .subtract(BigInteger.valueOf(4).multiply(C4).multiply(C2_new));

        if (discriminant.signum() < 0) {
            return new double[]{Double.NaN, Double.NaN};
        }

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

    // =========================================================================
    // C4 coefficient of a3^4 in e = d1 - d2 + d3
    // From the expanded polynomial: C4 = -4*a2*(P(a1,a2,b1,b2))
    // Computed from: only d2 contributes cleanly (p4^2 gives a3^4)
    // d1 and d3 also contribute a3^4 terms via (pi4_bar^2)^2
    // We compute C4 = coeff of a3^4 as:
    //   from d2: p3^2 * p4^2 gives p3^2*(a3^4+2*a3^2*b2^2+b2^4), so coeff a3^4 = p3^2 * (coeff of a3^4 from d2_prefix)
    //   from d1: (pi4_bar^2)^2 = (a3^2-b2^2, -2*a3*b2)^2 - highest a3 term: a3^4
    //   from d3: similar
    // Rather than expanding all this, use the step formula directly:
    // stepFromGauss(w) = 2*w[0]*w[1], so coeff of a3^4 in d_i = 2 * (coeff of a3^4 in w[0]*w[1])
    // This is still complex. Instead, use finite difference: C4 = (e(a3=2)-4*e(a3=1)+6*e(a3=0)-4*e(a3=-1)+e(a3=-2))/24
    // But e is not a simple polynomial to evaluate at non-physical points.
    // SIMPLEST: just compute C4 term by term from the known expanded form.
    // The expanded C4 (from sympy) is:
    // C4 = -4*a1^4*a2^7*b1 + 8*a1^4*a2^7*b2 + ... (many terms, see below)
    // We implement it directly.
    private static BigInteger computeC4(BigInteger a1, BigInteger a2, BigInteger b1, BigInteger b2,
            BigInteger a1sq, BigInteger a2sq, BigInteger b1sq, BigInteger b2sq,
            BigInteger a1p3, BigInteger a2p3, BigInteger a1p4, BigInteger a2p4,
            BigInteger b1p3, BigInteger b2p3, BigInteger b1p4, BigInteger b2p4,
            BigInteger a1p5, BigInteger a2p5, BigInteger a1p6, BigInteger a2p6,
            BigInteger a1p7, BigInteger a2p7, BigInteger b1p5, BigInteger b1p6,
            BigInteger b1p7, BigInteger b1p8) {

        // From sympy expanded C4:
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

    private static BigInteger computeC2new(BigInteger a1, BigInteger a2, BigInteger b1, BigInteger b2,
            BigInteger a1sq, BigInteger a2sq, BigInteger b1sq, BigInteger b2sq,
            BigInteger a1p3, BigInteger a2p3, BigInteger a1p4, BigInteger a2p4,
            BigInteger b1p3, BigInteger b2p3, BigInteger b1p4, BigInteger b2p4,
            BigInteger a1p5, BigInteger a2p5, BigInteger a1p6, BigInteger a2p6,
            BigInteger a1p7, BigInteger a2p7, BigInteger b1p5, BigInteger b1p6,
            BigInteger b1p7, BigInteger b1p8,
            BigInteger b2p5, BigInteger b2p6, BigInteger b2p7,
            BigInteger a2p8) {

        BigInteger r = BigInteger.ZERO;
        // C2_new = 16*b2^2 * Q  (from sympy)
        // Rather than implementing the full polynomial, use:
        // C2_new = C2_orig + 2*b2^2*C4
        // And C2_orig is the coefficient of a3^2 in e.
        // We compute this via: evaluate e at a3=0 to get C0=b2^4*C4, then
        // use C2_orig = e_at_a3=symbolic... this is circular.
        // Just implement term by term from the sympy output:
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

    private static BigInteger n(long v) { return BigInteger.valueOf(v); }

    // =========================================================================
    // Middle number n = p1 * p2 * p3 * p4
    // =========================================================================
    public static BigInteger getMiddleN(long a1, long a2, long a3, long b1, long b2) {
        BigInteger ba1=BigInteger.valueOf(a1), ba2=BigInteger.valueOf(a2);
        BigInteger ba3=BigInteger.valueOf(a3), bb1=BigInteger.valueOf(b1), bb2=BigInteger.valueOf(b2);
        BigInteger a1sq=ba1.multiply(ba1), a2sq=ba2.multiply(ba2), a3sq=ba3.multiply(ba3);
        BigInteger b1sq=bb1.multiply(bb1), b2sq=bb2.multiply(bb2);
        BigInteger p1=a1sq.add(b1sq), p2=a2sq.add(b1sq), p3=a2sq.add(b2sq), p4=a3sq.add(b2sq);
        return p1.multiply(p2).multiply(p3).multiply(p4);
    }

    // =========================================================================
    // Print three APs  (x, n, y : step)
    // =========================================================================
    public static void printThreeAPs(long a1, long a2, long a3, long b1, long b2) {
        BigInteger ba1=BigInteger.valueOf(a1), ba2=BigInteger.valueOf(a2);
        BigInteger ba3=BigInteger.valueOf(a3), bb1=BigInteger.valueOf(b1), bb2=BigInteger.valueOf(b2);

        BigInteger a1sq=ba1.multiply(ba1), a2sq=ba2.multiply(ba2), a3sq=ba3.multiply(ba3);
        BigInteger b1sq=bb1.multiply(bb1), b2sq=bb2.multiply(bb2);
        BigInteger p1=a1sq.add(b1sq), p2=a2sq.add(b1sq), p3=a2sq.add(b2sq), p4=a3sq.add(b2sq);
        BigInteger n=p1.multiply(p2).multiply(p3).multiply(p4);
        BigInteger n2=n.multiply(n);

        BigInteger[] pi1sq  = {a1sq.subtract(b1sq), BigInteger.TWO.multiply(ba1).multiply(bb1)};
        BigInteger[] pi2bsq = {a2sq.subtract(b1sq), BigInteger.TWO.multiply(ba2).multiply(bb1).negate()};
        BigInteger[] pi3sq  = {a2sq.subtract(b2sq), BigInteger.TWO.multiply(ba2).multiply(bb2)};
        BigInteger[] pi4bsq = {a3sq.subtract(b2sq), BigInteger.TWO.multiply(ba3).multiply(bb2).negate()};

        // d1
        BigInteger[] w1=gaussMult(gaussMult(gaussMult(pi1sq,pi2bsq),pi3sq),pi4bsq);
        BigInteger d1=stepFromGauss(w1).abs();

        // d2
        BigInteger[] w2b=gaussMult(pi1sq,pi2bsq);
        BigInteger sc=p3.multiply(p4);
        BigInteger[] w2={w2b[0].multiply(sc), w2b[1].multiply(sc)};
        BigInteger d2=stepFromGauss(w2).abs();

        // d3
        BigInteger[] w3b=gaussMult(gaussMult(pi2bsq,pi3sq),pi4bsq);
        BigInteger[] w3={w3b[0].multiply(p1), w3b[1].multiply(p1)};
        BigInteger d3=stepFromGauss(w3).abs();

        printAP(n, n2, d1); System.out.print(" , ");
        printAP(n, n2, d2); System.out.print(" , ");
        printAP(n, n2, d3); System.out.println();
    }

    private static void printAP(BigInteger n, BigInteger n2, BigInteger d) {
        BigInteger x=n2.subtract(d).sqrt();
        BigInteger y=n2.add(d).sqrt();
        System.out.print("("+x+","+n+","+y+":"+d+")");
    }

    // =========================================================================
    // Helpers
    // =========================================================================
    public static long nearestEvenLong(double value) {
        long r=Math.round(value);
        if(r%2!=0){ long lo=r-1,hi=r+1; return(Math.abs(value-lo)<=Math.abs(value-hi))?lo:hi; }
        return r;
    }

    public static boolean isCloseToInteger(double value) {
        return Math.abs(value-Math.round(value)) < CLOSE_TO_INTEGER_THRESHOLD;
    }

    public static boolean isWorthChecking(double a3double, long b2) {
        if(Double.isNaN(a3double)) return false;
        if(isCloseToInteger(a3double)) return true;
        double frac=Math.abs(a3double/b2-Math.round(a3double/b2));
        return Math.abs(frac*b2-Math.round(frac*b2)) < CLOSE_TO_INTEGER_THRESHOLD*b2;
    }

    public static double fracTimesB2(double a3double, long b2) {
        double frac=Math.abs(a3double/b2-Math.round(a3double/b2));
        return frac*b2;
    }

    public static double calculateQuality(BigInteger diff, BigInteger middleNumber) {
        double a=bigIntLog(middleNumber,2), b=bigIntLog(diff,2);
        String s=String.format("%.4f",a/b);
        return Double.valueOf(s.replaceAll(",","."));
    }

    public static double bigIntLog(BigInteger number, double base) {
        BigDecimal bd=new BigDecimal(number);
        BigDecimal div=new BigDecimal(10).pow(number.toString().length()-1);
        double bd_dbl=bd.divide(div).doubleValue();
        return (Math.log10(bd_dbl)+number.toString().length()-1)/Math.log10(base);
    }
}


/* Results up to a = 66
 * [5.823546972546492, 17.99999990180589]
frac*b2=0.8235469725464917
q=1.5661  a1=2, a2=4, a3=18, b1=1, b2=5, diff=7680
(1066297,1216265,1349671:342311258016) , (586669,1216265,1616917:1135120034664) , (43235,1216265,1719515:1477431285000)
time:77
[10.000222448993917, 21.524758331741666]
frac*b2=10.000222448993917
q=0.7089  a1=2, a2=6, a3=10, b1=1, b2=23, diff=106683136296
(11268239,65746225,92293873:4195592891589504) , (17413865,65746225,91333945:4019323407512400) , (64390915,65746225,67074155:176376167213400)
time:3332
[25.124150994780337, 70.00002111172925]
frac*b2=25.124150994780337
q=0.7006  a1=2, a2=10, a3=70, b1=1, b2=133, diff=13782692908976040
(198003785959,202927039105,207733644713:1973883945824713651344) , (16475264561,202927039105,286508869073:40907948857567256678304) , (47384644195,202927039105,283043215595:38934078694435452003000)
time:9097
[17.47064091763947, 53.99999970541767]
frac*b2=2.4706409176394715
q=0.8596  a1=6, a2=12, a3=54, b1=3, b2=15, diff=330598817280
(6995974617,7979914665,8855191431:14735377218973765536) , (3849135309,7979914665,10608592437:48863195433691536744) , (283664835,7979914665,11281737915:63598572322066485000)
time:617784
[341643.99998386117, 87.64233413807445]
frac*b2=21.00001613883478
q=0.7046  a1=6, a2=14, a3=341644, b1=57, b2=115, diff=37709436206770560767893887081000
(17206770627192934404783,17727892995427453475325,18234128021318211878631:18205234640496239739634733067886449890578536) , (12314719270250714014317,17727892995427453475325,21838133377410425085069:162625879352241490660187573076162475502879136) , (13032940778870393095155,17727892995427453475325,21417255537745930562835:144420644711782960356759610569043919499381600)
time:621448
[9.432736912790425, 155.99975823909926]
frac*b2=9.432736912790425
q=0.7065  a1=10, a2=2, a3=156, b1=1, b2=25, diff=102904399430664
(4572497929,7928736845,10238261503:41957130646551264984) , (643719229,7928736845,11194434397:62450493511476199584) , (6509331931,7928736845,9130078517:20493465769324365264)
time:1235242
[5.248041420280741, 50260.00960530353]
frac*b2=1.7519585797192598
q=0.7525  a1=10, a2=4, a3=50260, b1=1, b2=7, diff=15925250876618527248
(206280265739233,281921779966645,341186506189319:36928341986112814237357127736) , (189316139954305,281921779966645,350883426784345:43639289172363400089824523000) , (269757192395747,281921779966645,293582760375821:6710947170325334975848868016)
time:1238051
[29.117734862732455, 89.99999950902944]
frac*b2=4.117734862732453
q=0.7749  a1=10, a2=20, a3=90, b1=5, b2=25, diff=1171875000000000
(416522265625,475103515625,527215234375:52232552797851562500000) , (229167578125,475103515625,631608203125:173205571695556640625000) , (16888671875,475103515625,671685546875:225438123321533203125000)
time:1258541
[1156.4073176652087, 24.000000067535893]
frac*b2=11.592682334791286
q=0.7339  a1=10, a2=22, a3=24, b1=23, b2=73, diff=1503431155414886808
(910761175529,21871587965405,30917684815297:477536874209597861528384184) , (2450070128405,21871587965405,30833907903845:472363516494346334288970000) , (21752999883157,21871587965405,21989536519451:5173356211820371824527376)
time:1261539
[40.76482880782544, 125.99999931264124]
frac*b2=5.764828807825442
q=0.738  a1=14, a2=28, a3=126, b1=7, b2=35, diff=255228906774535680
(6146990011897,7011525688265,7780584730471:11376006270838501757171616) , (3382030037869,7011525688265,9321204738517:37723365300151792384649064) , (249241171235,7011525688265,9912661791515:49099371315761387367285000)
time:1877977
[149028.0000003843, 136.30727043028216]
frac*b2=43.99999961572439
q=0.7055  a1=14, a2=164, a3=149028, b1=113, b2=121, diff=3629229874421608657505127130479600
(379877512887709855875175,474407054913671089863125,553007398418840058734975:80755128954110769325154738072046668666053485000) , (415457271768752658025625,474407054913671089863125,526801065714744610549375:52457309086327732623038179058957101408078125000) , (443581146898832400654515,474407054913671089863125,503348660095209098631395:28297819867779407472242137404432062130844880400)
time:2089124
[52.411922752918414, 161.999999116253]
frac*b2=7.411922752918415
q=0.7163  a1=18, a2=36, a3=162, b1=9, b2=45, diff=14231195050382138880
(45900589462137,52356220117065,58098910978791:634309671974919591347607456) , (25254176762349,52356220117065,69602974979157:2103400341002593582280216424) , (1861124982435,52356220117065,74019482460315:2737709998746318123245685000)
time:2497470
[64.0590166980114, 197.99999891986477]
frac*b2=9.059016698011408
q=0.7016  a1=22, a2=44, a3=198, b1=11, b2=55, diff=352893925352234196480
(228570231733657,260717204399465,289313965278151:15729109835094750483505692576) , (125757710357389,260717204399465,346600518789877:52158458955539467385185388904) , (9267806220035,260717204399465,368593311262715:67887568437740292516456885000)
time:3115958
[517.9999984244068, 152.79353026042997]
frac*b2=4.999998424406812
q=0.7161  a1=26, a2=18, a3=518, b1=37, b2=57, diff=4805818403094519921000
(1696189899365721,3359463110108865,4437896416282647:8408932213472033269714041738384) , (2134192071788721,3359463110108865,4244668300006353:6731216588896494752467688772384) , (3099722045410035,3359463110108865,3600514965607515:1677715629381356920340872887000)
time:3671272
[789.8077596025414, 34.000000005974016]
frac*b2=30.807759602541367
q=0.707  a1=28, a2=104, a3=34, b1=49, b2=253, diff=3077069588598497703750000
(21911556237930625,205259587493813125,289452725362005625:41651381961362328944994170109375000) , (122930209575929875,205259587493813125,262966081633884125:27019661831748284291327181504750000) , (165830570558004625,205259587493813125,238250327144932375:14631720126536975065068490900875000)
time:4152145
[129717.99998523004, 26.332578802785747]
frac*b2=32.00001476996863
q=0.7002  a1=30, a2=90, a3=129718, b1=49, b2=173, diff=81557496765496594331078770955880
(20250974900546557775407,22181573414523215448737,23957095271864649258383:81920214721316433588638478591575211620879520) , (8073872234323698699223,22181573414523215448737,30312653883016957703897:426834786287699944535489417565790398054091440) , (12128793327343739957213,22181573414523215448737,28929859500356170371563:344914571566465068443616435568546265204167800)
time:4527164
[7018681.999977222, 325.34921548087]
frac*b2=4.99997722110129
q=0.7006  a1=34, a2=2, a3=7018682, b1=125, b2=191, diff=118190493789819835868411472748184325000
(174758872412887527050330575,471383646285742343718577825,643322951933365892254536175:191661878498617950528098886138325509912032823801900000) , (188226614736803707530377825,471383646285742343718577825,639512177738631160706777825:186773283490364721033887169448906127780546853330000000) , (466169440201080216620170075,471383646285742343718577825,476540803073456868664250675:4888595008253347684705506509255250542958718656225000)
time:5106698
[89.43273877133709, 1987.999999797475]
frac*b2=45.56726122866291
q=0.711  a1=50, a2=44, a3=1988, b1=31, b2=135, diff=15228658356440854660301040
(110937168981740617,802588690031698453,1129597342096692527:631841549905114467252677980855052520) , (702923937402303097,802588690031698453,891176272664638753:150046543593640816954684372066801800) , (402931258492751773,802588690031698453,1061104901347196083:481795006296244991941552754127949680)
time:7760810
[18509.999999917854, 327.0173366736232]
frac*b2=91.00000008214577
q=0.7062  a1=62, a2=42, a3=18510, b1=123, b2=209, diff=5331908861071140330412416965880
(637896688649690825871,4991109513957511937505,7029611381523458121903:24504261994926950453585073680877636982716384) , (3444002844132348307305,4991109513957511937505,6161265516940710279705:13050018589925486820569835055308080309262000) , (3668368952996739599931,4991109513957511937505,6030374580846863661267:11454243405006795541876309765899969090420264)
time:9267782
[77.99999999081987, 96.04475542968108]
frac*b2=47.00000000918013
q=0.7051  a1=66, a2=180, a3=78, b1=111, b2=125, diff=23605030271567454976800000
(651134303361858825,777564862782942825,886136756180884425:180631234820123507230540007988600000) , (194872044642813675,777564862782942825,1082238013509986925:566632002051385938033016946451975000) , (467553578298561675,777564862782942825,995292862975277325:386000767254867461074044393440175000)
time:9917533
 
*/ 
