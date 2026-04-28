package dk.teg.bigmathfast.squares;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * New parameterization: (a1,b1), (a1,b2), (a2,b1)
 *
 *   p1 = a1^2 + b1^2
 *   p2 = a1^2 + b2^2
 *   p3 = a2^2 + b1^2
 *   n  = p1 * p2 * p3
 *
 * Parity: a1 even, a2 even, b1 odd, b2 odd  ->  all three primes odd.
 * Repeated primes allowed (e.g. n=845=5*13^2, a1=a2=2, b1=3, b2=1).
 *
 * Three winning APs (Gaussian products):
 *   d1: alphas (0,0,0)  all pi_bar^2
 *   d2: alphas (1,1,0)  p1,p2 scalar; p3 pi_bar^2
 *   d3: alphas (0,1,0)  p1 pi_bar^2; p2 scalar; p3 pi_bar^2
 *
 * e = d1 + d2 + d3  (signed — d3 is typically negative, so |e| = |d1|+|d2|-|d3|)
 *
 * Palindromic in a2:  e/a2^2 = C4*(u^2+2*b1^2) + C3*u + C2_orig
 * Substitution u = a2 - b1^2/a2 gives quadratic:  C4*u^2 + C3*u + C2_new = 0
 *
 *   C4     = -4*a1*(a1-b2)*(a1+b2)*(a1^2-b1^2-2*b1*b2)*(2*a1^2*b1+a1^2*b2-b1^2*b2)
 *   C2_new = -4*b1^2*C4   =>  D = C3^2 + 16*b1^2*C4^2  >= 0 ALWAYS
 *   C3     = -4*b1*(13-term degree-8 polynomial)
 *
 * Verified: a1=2, b1=3, b2=1, a2=2  ->  n=845, e=-1344, r=0.9356 ✓
 */
public class SearchHourGlass3PrimesNewParam {

    private static final double CLOSE_TO_INTEGER_THRESHOLD = 0.1;
    private static final int MAX_GAP = 500;   // max gap |a1 - b2| to search
    private static final int MAX_B1  = 500;   // b1 can exceed a1 (e.g. b1=3, a1=2 for n=845)

    public static void main(String[] args) {

        
        
        double bestQuality = 0.0001d;
        long start = System.currentTimeMillis();

        for (long a1 = 2; a1 <= Long.MAX_VALUE; a1 += 2) {  // a1 must be even

            for (int gap = 1; gap <= MAX_GAP; gap += 2) {
                long b2 = a1 - gap;   // odd since a1 even, gap odd
                if (b2 <= 0) break;

                for (long b1 = 1; b1 <= MAX_B1; b1 += 2) {  // b1 odd; can exceed a1
                    if (b1 == b2) continue;  // skip p1=p2

                    double[] a2cands = calculateOptimalA2(a1, b1, b2);

                    for (double a2d : a2cands) {
                 if (Double.isNaN(a2d) || nearestEvenLong(a2d) < 1) continue;
                        if (!isWorthChecking(a2d, b1)) continue;

                        // Check nearest even AND the two neighboring evens
                        long a2base = nearestEvenLong(a2d);
                        for (long a2 : new long[]{a2base - 2, a2base, a2base + 2}) {
                            if (a2 < 2) continue;

                //            System.out.println(a1+","+a2+","+b1+","+b2);
                            
                            BigInteger diff = calculateDifference(
                                    BigInteger.valueOf(a1), BigInteger.valueOf(b1),
                                    BigInteger.valueOf(b2), BigInteger.valueOf(a2));
                            if (diff.signum() == 0) continue;

                            BigInteger n = getMiddleN(a1, b1, b2, a2);
                            double quality = calculateQuality(diff.abs(), n);

                            if (quality >=0.7) {
                                System.out.println(Arrays.toString(a2cands));
                                System.out.println("frac*b1=" + fracTimesB1(a2d, b1));
                                System.out.println("q=" + quality
                                        + "  a1=" + a1 + ", b1=" + b1
                                        + ", b2=" + b2 + ", a2=" + a2
                                        + ", diff=" + diff.abs());
                                printThreeAPs(a1, b1, b2, a2);
                                bestQuality = quality;
                                System.out.println("time:" + (System.currentTimeMillis() - start));
                            }
                        }
                    }
                }
            }

            if (a1 % 10000 == 0) {
                System.out.println("Progress: a1=" + a1
                        + "  time:" + (System.currentTimeMillis() - start));
            }
        }
    }

    // =========================================================================
    // e = d1 + d2 + d3  (all signed — d3 is typically negative)
    // =========================================================================
    public static BigInteger calculateDifference(BigInteger a1, BigInteger b1,
                                                  BigInteger b2, BigInteger a2) {
        BigInteger a1sq = a1.multiply(a1);
        BigInteger b1sq = b1.multiply(b1);
        BigInteger b2sq = b2.multiply(b2);
        BigInteger a2sq = a2.multiply(a2);
        BigInteger p1   = a1sq.add(b1sq);
        BigInteger p2   = a1sq.add(b2sq);

        // --- d1: (0,0,0) = -4*g1*g2*g3*g4 ---
        BigInteger g1 = a1sq.multiply(a2)
                .subtract(a1.multiply(b1sq))
                .subtract(a1.multiply(b1).multiply(b2))
                .subtract(a2.multiply(b1).multiply(b2));
        BigInteger g2 = a1sq.multiply(b1)
                .add(a1.multiply(a2).multiply(b1))
                .add(a1.multiply(a2).multiply(b2))
                .subtract(b1sq.multiply(b2));
        BigInteger g3 = a1sq.multiply(a2)
                .subtract(a1sq.multiply(b1))
                .subtract(a1.multiply(a2).multiply(b1))
                .subtract(a1.multiply(a2).multiply(b2))
                .subtract(a1.multiply(b1sq))
                .subtract(a1.multiply(b1).multiply(b2))
                .subtract(a2.multiply(b1).multiply(b2))
                .add(b1sq.multiply(b2));
        BigInteger g4 = a1sq.multiply(a2)
                .add(a1sq.multiply(b1))
                .add(a1.multiply(a2).multiply(b1))
                .add(a1.multiply(a2).multiply(b2))
                .subtract(a1.multiply(b1sq))
                .subtract(a1.multiply(b1).multiply(b2))
                .subtract(a2.multiply(b1).multiply(b2))
                .subtract(b1sq.multiply(b2));
        BigInteger d1 = BigInteger.valueOf(-4).multiply(g1).multiply(g2).multiply(g3).multiply(g4);

        // --- d2: (1,1,0) = -4*a2*b1*p1^2*p2^2*(a2-b1)*(a2+b1) ---
        BigInteger d2 = BigInteger.valueOf(-4)
                .multiply(a2).multiply(b1)
                .multiply(p1.pow(2)).multiply(p2.pow(2))
                .multiply(a2.subtract(b1))
                .multiply(a2.add(b1));

        // --- d3: (0,1,0) = -4*b1*(a1+a2)*p2^2*(a1*a2-b1^2)
        //                        *(a1*a2-a1*b1-a2*b1-b1^2)*(a1*a2+a1*b1+a2*b1-b1^2) ---
        BigInteger a1a2 = a1.multiply(a2);
        BigInteger h1   = a1a2.subtract(b1sq);
        BigInteger h2   = a1a2.subtract(a1.multiply(b1)).subtract(a2.multiply(b1)).subtract(b1sq);
        BigInteger h3   = a1a2.add(a1.multiply(b1)).add(a2.multiply(b1)).subtract(b1sq);
        BigInteger d3   = BigInteger.valueOf(-4)
                .multiply(b1).multiply(a1.add(a2))
                .multiply(p2.pow(2))
                .multiply(h1).multiply(h2).multiply(h3);

        return d1.add(d2).add(d3);
    }

    // =========================================================================
    // Optimal a2:  C4*u^2 + C3*u + C2_new = 0,  u = a2 - b1^2/a2
    //   C4     = -4*a1*(a1-b2)*(a1+b2)*(a1^2-b1^2-2*b1*b2)*(2*a1^2*b1+a1^2*b2-b1^2*b2)
    //   C2_new = -4*b1^2*C4
    //   C3     = -4*b1*(inner)  [13-term polynomial]
    //   D      = C3^2 + 16*b1^2*C4^2  >= 0 always
    // =========================================================================
    public static double[] calculateOptimalA2(long a1, long b1, long b2) {

        BigInteger ba1 = BigInteger.valueOf(a1);
        BigInteger bb1 = BigInteger.valueOf(b1);
        BigInteger bb2 = BigInteger.valueOf(b2);
        BigInteger a1sq = ba1.multiply(ba1);
        BigInteger b1sq = bb1.multiply(bb1);
        BigInteger b2sq = bb2.multiply(bb2);

        // C4
        BigInteger C4 = BigInteger.valueOf(-4)
                .multiply(ba1)
                .multiply(ba1.subtract(bb2))
                .multiply(ba1.add(bb2))
                .multiply(a1sq.subtract(b1sq).subtract(BigInteger.TWO.multiply(bb1).multiply(bb2)))
                .multiply(BigInteger.TWO.multiply(a1sq).multiply(bb1)
                        .add(a1sq.multiply(bb2))
                        .subtract(b1sq.multiply(bb2)));

        // C2_new = -4*b1^2*C4
        BigInteger C2_new = BigInteger.valueOf(-4).multiply(b1sq).multiply(C4);

        // C3 inner = 3*a1^8 - 10*a1^6*b1^2 - 16*a1^6*b1*b2 - 2*a1^6*b2^2
        //          + 3*a1^4*b1^4 + 16*a1^4*b1^3*b2 + 28*a1^4*b1^2*b2^2
        //          + 16*a1^4*b1*b2^3 + 3*a1^4*b2^4
        //          - 2*a1^2*b1^4*b2^2 - 16*a1^2*b1^3*b2^3
        //          - 10*a1^2*b1^2*b2^4 + 3*b1^4*b2^4
        BigInteger a1p4 = a1sq.pow(2);
        BigInteger a1p6 = a1sq.multiply(a1p4);
        BigInteger a1p8 = a1p4.pow(2);
        BigInteger b1p3 = b1sq.multiply(bb1);
        BigInteger b1p4 = b1sq.pow(2);
        BigInteger b2p3 = b2sq.multiply(bb2);
        BigInteger b2p4 = b2sq.pow(2);

        BigInteger inner = BigInteger.valueOf(3).multiply(a1p8)
                .subtract(BigInteger.TEN.multiply(a1p6).multiply(b1sq))
                .subtract(BigInteger.valueOf(16).multiply(a1p6).multiply(bb1).multiply(bb2))
                .subtract(BigInteger.TWO.multiply(a1p6).multiply(b2sq))
                .add(BigInteger.valueOf(3).multiply(a1p4).multiply(b1p4))
                .add(BigInteger.valueOf(16).multiply(a1p4).multiply(b1p3).multiply(bb2))
                .add(BigInteger.valueOf(28).multiply(a1p4).multiply(b1sq).multiply(b2sq))
                .add(BigInteger.valueOf(16).multiply(a1p4).multiply(bb1).multiply(b2p3))
                .add(BigInteger.valueOf(3).multiply(a1p4).multiply(b2p4))
                .subtract(BigInteger.TWO.multiply(a1sq).multiply(b1p4).multiply(b2sq))
                .subtract(BigInteger.valueOf(16).multiply(a1sq).multiply(b1p3).multiply(b2p3))
                .subtract(BigInteger.TEN.multiply(a1sq).multiply(b1sq).multiply(b2p4))
                .add(BigInteger.valueOf(3).multiply(b1p4).multiply(b2p4));

        BigInteger C3 = BigInteger.valueOf(-4).multiply(bb1).multiply(inner);

        // D = C3^2 + 16*b1^2*C4^2  (always >= 0)
        BigInteger discriminant = C3.multiply(C3)
                .add(BigInteger.valueOf(16).multiply(b1sq).multiply(C4.multiply(C4)));

        double C4d     = C4.doubleValue();
        double C3d     = C3.doubleValue();
        double sqrtD   = Math.sqrt(discriminant.doubleValue());

        double u1   = (-C3d + sqrtD) / (2 * C4d);
        double u2   = (-C3d - sqrtD) / (2 * C4d);
        double b1sq_d = (double) b1 * b1;

        double a2_1 = (u1 + Math.sqrt(u1 * u1 + 4 * b1sq_d)) / 2.0;
        double a2_2 = (u2 + Math.sqrt(u2 * u2 + 4 * b1sq_d)) / 2.0;

        return new double[]{a2_1, a2_2};
    }

    // =========================================================================
    // Middle number  n = p1 * p2 * p3
    // =========================================================================
    public static BigInteger getMiddleN(long a1, long b1, long b2, long a2) {
        BigInteger ba1 = BigInteger.valueOf(a1), bb1 = BigInteger.valueOf(b1);
        BigInteger bb2 = BigInteger.valueOf(b2), ba2 = BigInteger.valueOf(a2);
        BigInteger a1sq = ba1.multiply(ba1);
        BigInteger p1   = a1sq.add(bb1.multiply(bb1));
        BigInteger p2   = a1sq.add(bb2.multiply(bb2));
        BigInteger p3   = ba2.multiply(ba2).add(bb1.multiply(bb1));
        return p1.multiply(p2).multiply(p3);
    }

    // =========================================================================
    // Print three APs  ( x, n, y : step )
    // =========================================================================
    public static void printThreeAPs(long a1, long b1, long b2, long a2) {
        BigInteger ba1 = BigInteger.valueOf(a1), bb1 = BigInteger.valueOf(b1);
        BigInteger bb2 = BigInteger.valueOf(b2), ba2 = BigInteger.valueOf(a2);

        BigInteger a1sq = ba1.multiply(ba1);
        BigInteger b1sq = bb1.multiply(bb1);
        BigInteger b2sq = bb2.multiply(bb2);
        BigInteger a2sq = ba2.multiply(ba2);
        BigInteger p1   = a1sq.add(b1sq);
        BigInteger p2   = a1sq.add(b2sq);
        BigInteger n    = p1.multiply(p2).multiply(a2sq.add(b1sq));
        BigInteger n2   = n.multiply(n);

        // d1 (0,0,0) — take abs for printing
        BigInteger g1 = a1sq.multiply(ba2).subtract(ba1.multiply(b1sq))
                .subtract(ba1.multiply(bb1).multiply(bb2)).subtract(ba2.multiply(bb1).multiply(bb2));
        BigInteger g2 = a1sq.multiply(bb1).add(ba1.multiply(ba2).multiply(bb1))
                .add(ba1.multiply(ba2).multiply(bb2)).subtract(b1sq.multiply(bb2));
        BigInteger g3 = a1sq.multiply(ba2).subtract(a1sq.multiply(bb1))
                .subtract(ba1.multiply(ba2).multiply(bb1)).subtract(ba1.multiply(ba2).multiply(bb2))
                .subtract(ba1.multiply(b1sq)).subtract(ba1.multiply(bb1).multiply(bb2))
                .subtract(ba2.multiply(bb1).multiply(bb2)).add(b1sq.multiply(bb2));
        BigInteger g4 = a1sq.multiply(ba2).add(a1sq.multiply(bb1))
                .add(ba1.multiply(ba2).multiply(bb1)).add(ba1.multiply(ba2).multiply(bb2))
                .subtract(ba1.multiply(b1sq)).subtract(ba1.multiply(bb1).multiply(bb2))
                .subtract(ba2.multiply(bb1).multiply(bb2)).subtract(b1sq.multiply(bb2));
        BigInteger d1 = BigInteger.valueOf(-4).multiply(g1).multiply(g2).multiply(g3).multiply(g4).abs();

        // d2 (1,1,0) — abs
        BigInteger d2 = BigInteger.valueOf(4).multiply(ba2).multiply(bb1)
                .multiply(p1.pow(2)).multiply(p2.pow(2))
                .multiply(ba2.subtract(bb1)).multiply(ba2.add(bb1)).abs();

        // d3 (0,1,0) — abs
        BigInteger a1a2 = ba1.multiply(ba2);
        BigInteger h1   = a1a2.subtract(b1sq);
        BigInteger h2   = a1a2.subtract(ba1.multiply(bb1)).subtract(ba2.multiply(bb1)).subtract(b1sq);
        BigInteger h3   = a1a2.add(ba1.multiply(bb1)).add(ba2.multiply(bb1)).subtract(b1sq);
        BigInteger d3   = BigInteger.valueOf(4).multiply(bb1).multiply(ba1.add(ba2))
                .multiply(p2.pow(2)).multiply(h1).multiply(h2).multiply(h3).abs();

        printAP(n, n2, d1);
        System.out.print(" , ");
        printAP(n, n2, d2);
        System.out.print(" , ");
        printAP(n, n2, d3);
        System.out.println();
    }

    private static void printAP(BigInteger n, BigInteger n2, BigInteger d) {
        BigInteger x = n2.subtract(d).sqrt();
        BigInteger y = n2.add(d).sqrt();
        System.out.print("(" + x + "," + n + "," + y + ":" + d + ")");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /** Round to nearest even long (a2 must be even so p3=a2^2+b1^2 is odd) */
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

    public static boolean isWorthChecking(double a2double, long b1) {
        if (Double.isNaN(a2double)) return false;
        if (isCloseToInteger(a2double))  return true;
        double frac = Math.abs(a2double / b1 - Math.round(a2double / b1));
        return Math.abs(frac * b1 - Math.round(frac * b1)) < CLOSE_TO_INTEGER_THRESHOLD * b1;
    }

    public static double fracTimesB1(double a2double, long b1) {
        double frac = Math.abs(a2double / b1 - Math.round(a2double / b1));
        return frac * b1;
    }

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
}
