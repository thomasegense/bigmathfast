package dk.teg.bigmathfast.squares;

import java.math.BigDecimal;

import java.math.BigInteger;

import java.util.Arrays;

import java.util.Random;

//Assume

//p1=a^2+b1^2

//p2=a^2+b2^2

//p3=a^2+b2^2

//Calculate difference of step values

/* best besides the 120 dif.

[3.72469407E8, 4614.347722933606]

q=0.7469 a=3524, b1=235, b2=3085, b3=372469407, diff=1902054607464937419970059729430502146890864

(42665571930763234059731647266319,37960639832071594532035024510225,32583267557772494115853209455567:-379340851718871023537346123446072606643295713544237564582259136) , (37940905731483297673782940886225,37960639832071594532035024510225,37980363679080522018081944025775:1497848734958389389795551379003521857856205955543712734800000) , (42648014953150787541549745601975,37960639832071594532035024510225,32606244393924730296855161584025:-377843002983912634149452626674534022205409567318124353994350000)

time:619849

[3.512592212999988E9, 770.271512403842]

frac*b1=2.0000118017196655

q=0.7488 a=750, b1=5, b2=749, b3=3512592213, diff=179423924416956307434396663433022938875000

(7901051990831811688269263683275,7797776083369716563699060596725,7693113877417789712525959123275:-1621310715454578045562509846997431848238175337287306434000000) , (7703652158748569855162406541725,7797776083369716563699060596725,7890777345087999196994194348275:1459055263381256320108053779569174895231832545698438914750000) , (7808173108893403534527231815775,7797776083369716563699060596725,7787365176637052707729596759225:-162255452073321725633879991845213260440739455021890458125000)

time:4151

 *

 */

public class SeachHourGlass3Primes3SharedSquares {

    private static BigInteger B1 = BigInteger.ONE;

    private static BigInteger B2 = BigInteger.TWO;

    private static final double CLOSE_TO_INTEGER_THRESHOLD = 1e-4;

    private static final int MAX_GAP = 500; // max |a - b2| to search

    public static void main(String[] args) {

        // BigInteger n1= getMiddleN(286, 81, 200005, 285);

        // System.out.println(n1);

        // printThreeAPs(286, 81, 200005, 285);

        // System.exit(1);

        double bestQuality = 0.0001d;

        long start = System.currentTimeMillis();

        for (long a = 2; a <= Long.MAX_VALUE; a += 2) { // a must be even

            // Systematically vary gap between a and b2

            // a is even, so a-1, a-3, a-5... are all odd

            for (int gap = 1; gap <= MAX_GAP; gap += 2) {

                long b2 = a - gap; // odd since a even, gap odd

                if (b2 <= 0)
                    break;

                long maxb1 = calculateMaxB1(a, b2);

                for (int b1 = 1; b1 <= maxb1; b1 = b1 + 2) {

                    double[] b3candidates = calculateOptimalB3(a, b1, b2);

                    long b3_1 = nearestLong(b3candidates[0]);

                    boolean odd = (b3_1 % 2 == 1);

                    if (isWorthChecking(b3candidates[0], b1) && odd) {

                        BigInteger n = getMiddleN(a, b1, b2, b3_1);

                        BigInteger diff = calculateDifference(

                                new BigInteger("" + a), new BigInteger("" + b1),

                                new BigInteger("" + b2), new BigInteger("" + b3_1)).abs();

                        double quality = calculateQuality(diff, n);

                        if (quality >= 0.74) {

                            System.out.println(Arrays.toString(b3candidates));

                            System.out.println("frac*b1=" + fracTimesB1(b3candidates[0], b1));

                            System.out.println("q=" + quality + " a=" + a + ", b1=" + b1 + ", b2=" + b2 + ", b3=" + b3_1 + ", diff=" + diff);

                            printThreeAPs(a, b1, b2, b3_1);

                            bestQuality = quality;

                            System.out.println("time:" + (System.currentTimeMillis() - start));

                        }

                    }

                    long b3_2 = nearestLong(b3candidates[1]);

                    odd = (b3_2 % 2 == 1);

                    if (isCloseToInteger(b3candidates[1]) && odd) {

                        BigInteger n = getMiddleN(a, b1, b2, b3_2);

                        BigInteger diff = calculateDifference(

                                new BigInteger("" + a), new BigInteger("" + b1),

                                new BigInteger("" + b2), new BigInteger("" + b3_2)).abs();

                        double quality = calculateQuality(diff, n);

                        if (quality > 0.74) {

                            System.out.println(Arrays.toString(b3candidates));

                            System.out.println("frac*b1=" + fracTimesB1(b3candidates[0], b1));

                            System.out.println("q=" + quality + " a=" + a + ", b1=" + b1 + ", b2=" + b2 + ", b3=" + b3_2 + ", diff=" + diff);

                            printThreeAPs(a, b1, b2, b3_2);

                            bestQuality = quality;

                            System.out.println("time:" + (System.currentTimeMillis() - start));

                        }

                    }

                }

            }

        }

    }

    public static long calculateDifference(long a, long b1, long b2, long b3) {

        long A = -2 * b1 * (a * a - b2 * b2) * ((a * a - b1 * b2) * (a * a - b1 * b2) - a * a * (b1 + b2) * (b1 + b2));

        long B = -(a * a + b2 * b2) * (a * a + b2 * b2) * ((a * a - b1 * b1) * (a * a - b1 * b1) - 4 * a * a * b1 * b1);

        long C_new = 32 * a * a * a * a * b1 * b2 * (a * a - b1 * b2) * (b1 + b2);

        long C_org = C_new - 2 * a * a * A;

        // System.out.println(A);

        // System.out.println(B);

        // System.out.println(C_org);

        long e = 4 * a * (A * (b3 * b3 * b3 * b3 + a * a * a * a) + B * b3 * (b3 * b3 - a * a) + C_org * b3 * b3);

        return e;

    }

    public static BigInteger calculateDifference(BigInteger a, BigInteger b1, BigInteger b2, BigInteger b3) {

        BigInteger a2 = a.multiply(a);

        BigInteger b12 = b1.multiply(b1);

        BigInteger b22 = b2.multiply(b2);

        BigInteger b32 = b3.multiply(b3);

        // A = -2*b1*(a^2-b2^2) * ( (a^2-b1*b2)^2 - a^2*(b1+b2)^2 )

        BigInteger inner_A = (a2.subtract(b1.multiply(b2))).pow(2).subtract(a2.multiply((b1.add(b2)).pow(2)));

        BigInteger A = BigInteger.TWO.negate().multiply(b1).multiply(a2.subtract(b22)).multiply(inner_A);

        // B = -(a^2+b2^2)^2 * ( (a^2-b1^2)^2 - 4*a^2*b1^2 )

        BigInteger inner_B = (a2.subtract(b12)).pow(2).subtract(BigInteger.valueOf(4).multiply(a2).multiply(b12));

        BigInteger B = (a2.add(b22)).pow(2).multiply(inner_B).negate();

        // C_new = 32*a^4*b1*b2*(a^2-b1*b2)*(b1+b2)

        BigInteger C_new = BigInteger.valueOf(32).multiply(a2.pow(2)).multiply(b1).multiply(b2).multiply(a2.subtract(b1.multiply(b2))).multiply(b1.add(b2));

        // C_org = C_new - 2*a^2*A

        BigInteger C_org = C_new.subtract(BigInteger.TWO.multiply(a2).multiply(A));

        // e = 4*a * ( A*(b3^4+a^4) + B*b3*(b3^2-a^2) + C_org*b3^2 )

        BigInteger term1 = A.multiply(b32.pow(2).add(a2.pow(2)));

        BigInteger term2 = B.multiply(b3).multiply(b32.subtract(a2));

        BigInteger term3 = C_org.multiply(b32);

        BigInteger e = BigInteger.valueOf(4).multiply(a).multiply(term1.add(term2).add(term3));

        return e;

    }

    public static double[] calculateOptimalB3(long a, long b1, long b2) {

        BigInteger ba = BigInteger.valueOf(a);

        BigInteger bb1 = BigInteger.valueOf(b1);

        BigInteger bb2 = BigInteger.valueOf(b2);

        BigInteger a2 = ba.multiply(ba);

        BigInteger b12 = bb1.multiply(bb1);

        BigInteger b22 = bb2.multiply(bb2);

        // A = -2*b1*(a^2-b2^2)*[(a^2-b1*b2)^2 - a^2*(b1+b2)^2]

        BigInteger innerA = (a2.subtract(bb1.multiply(bb2))).pow(2).subtract(a2.multiply(bb1.add(bb2).pow(2)));

        BigInteger A = BigInteger.TWO.negate().multiply(bb1).multiply(a2.subtract(b22)).multiply(innerA);

        // B = -(a^2+b2^2)^2 * [(a^2-b1^2)^2 - 4*a^2*b1^2]

        BigInteger innerB = (a2.subtract(b12)).pow(2).subtract(BigInteger.valueOf(4).multiply(a2).multiply(b12));

        BigInteger B = (a2.add(b22)).pow(2).multiply(innerB).negate();

        // C_new = 32*a^4*b1*b2*(a^2-b1*b2)*(b1+b2)

        BigInteger C_new = BigInteger.valueOf(32).multiply(a2.pow(2)).multiply(bb1).multiply(bb2).multiply(a2.subtract(bb1.multiply(bb2)))

                .multiply(bb1.add(bb2));

        // Discriminant = B^2 - 4*A*C_new (exact BigInteger)

        BigInteger discriminant = B.multiply(B).subtract(BigInteger.valueOf(4).multiply(A).multiply(C_new));

        if (discriminant.signum() < 0) {

            return new double[] { Double.NaN, Double.NaN };

        }

        // Convert to double for the sqrt steps

        double Ad = A.doubleValue();

        double Bd = B.doubleValue();

        double sqrtDisc = Math.sqrt(discriminant.doubleValue());

        double u1 = (-Bd + sqrtDisc) / (2 * Ad);

        double u2 = (-Bd - sqrtDisc) / (2 * Ad);

        double a2d = a2.doubleValue();

        double b3_1 = (u1 + Math.sqrt(u1 * u1 + 4 * a2d)) / 2.0;

        double b3_2 = (u2 + Math.sqrt(u2 * u2 + 4 * a2d)) / 2.0;

        return new double[] { b3_1, b3_2 };

    }

    public static long nearestLong(double value) {

        return Math.round(value);

    }

    public static long calculateMaxB1(long a, long b2) {

        // Binary search for largest b1 where discriminant >= 0

        long lo = 1;

        long hi = Math.min(a, b2); // b1 can't exceed a or b2 meaningfully

        while (lo < hi) {

            long mid = (lo + hi + 1) / 2;

            if (discriminantIsNonNegative(a, mid, b2)) {

                lo = mid;

            } else {

                hi = mid - 1;

            }

        }

        return lo;

    }

    private static boolean discriminantIsNonNegative(long a, long b1, long b2) {

        BigInteger ba = BigInteger.valueOf(a);

        BigInteger bb1 = BigInteger.valueOf(b1);

        BigInteger bb2 = BigInteger.valueOf(b2);

        BigInteger a2 = ba.multiply(ba);

        BigInteger b12 = bb1.multiply(bb1);

        BigInteger b22 = bb2.multiply(bb2);

        BigInteger innerA = (a2.subtract(bb1.multiply(bb2))).pow(2).subtract(a2.multiply(bb1.add(bb2).pow(2)));

        BigInteger A = BigInteger.TWO.negate().multiply(bb1).multiply(a2.subtract(b22)).multiply(innerA);

        BigInteger innerB = (a2.subtract(b12)).pow(2).subtract(BigInteger.valueOf(4).multiply(a2).multiply(b12));

        BigInteger B = (a2.add(b22)).pow(2).multiply(innerB).negate();

        BigInteger C_new = BigInteger.valueOf(32).multiply(a2.pow(2)).multiply(bb1).multiply(bb2).multiply(a2.subtract(bb1.multiply(bb2)))

                .multiply(bb1.add(bb2));

        BigInteger discriminant = B.multiply(B).subtract(BigInteger.valueOf(4).multiply(A).multiply(C_new));

        return discriminant.signum() >= 0;

    }

    public static BigInteger getMiddleN(long a, long b1, long b2, long b3) {

        BigInteger ba = BigInteger.valueOf(a);

        BigInteger bb1 = BigInteger.valueOf(b1);

        BigInteger bb2 = BigInteger.valueOf(b2);

        BigInteger bb3 = BigInteger.valueOf(b3);

        BigInteger a2 = ba.multiply(ba);

        BigInteger b22 = bb2.multiply(bb2);

        BigInteger b32 = bb3.multiply(bb3);

        BigInteger p1 = a2.add(bb1.multiply(bb1));

        BigInteger p2 = a2.add(b22);

        BigInteger p3 = a2.add(b32);

        BigInteger n = p1.multiply(p2).multiply(p3);

        return n;

    }

    public static void printThreeAPs(long a, long b1, long b2, long b3) {

        BigInteger ba = BigInteger.valueOf(a);

        BigInteger bb1 = BigInteger.valueOf(b1);  

        BigInteger bb2 = BigInteger.valueOf(b2);

        BigInteger bb3 = BigInteger.valueOf(b3);

        BigInteger a2 = ba.multiply(ba);

        BigInteger b22 = bb2.multiply(bb2);

        BigInteger b32 = bb3.multiply(bb3);

        BigInteger p1 = a2.add(bb1.multiply(bb1));

        BigInteger p2 = a2.add(b22);

        BigInteger p3 = a2.add(b32);

        BigInteger n = p1.multiply(p2).multiply(p3);

        BigInteger n2 = n.multiply(n);

        // d1 = 4*p2^2 * P*Q*(P^2-Q^2), P=a^2+b1*b3, Q=a*(b3-b1)

        BigInteger P = a2.add(bb1.multiply(bb3));

        BigInteger Q = ba.multiply(bb3.subtract(bb1));

        BigInteger d1 = BigInteger.valueOf(4).multiply(p2.pow(2))

                .multiply(P).multiply(Q)

                .multiply(P.pow(2).subtract(Q.pow(2)));

        // d2 = 4*p3^2 * Pp*Qp*(Qp^2-Pp^2), Pp=a^2-b1*b2, Qp=a*(b1+b2)

        BigInteger Pp = a2.subtract(bb1.multiply(bb2));

        BigInteger Qp = ba.multiply(bb1.add(bb2));

        BigInteger d2 = BigInteger.valueOf(4).multiply(p3.pow(2))

                .multiply(Pp).multiply(Qp)

                .multiply(Qp.pow(2).subtract(Pp.pow(2)));

        // d3 = 4*(p1*p3)^2 * a*b2*(b2^2-a^2)

        BigInteger d3 = BigInteger.valueOf(4).multiply(p1.multiply(p3).pow(2))

                .multiply(ba).multiply(bb2)

                .multiply(b22.subtract(a2));

        // For each AP: x = sqrt(n^2 - d), y = sqrt(n^2 + d)

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

    public static boolean isCloseToInteger(double value) {

        double fractionalPart = Math.abs(value - Math.round(value));

        return fractionalPart < CLOSE_TO_INTEGER_THRESHOLD;

    }

    // New broader filter - catches both patterns:

    // 1. b3 itself close to integer

    // 2. b3/b1 has fractional part close to m/b1 (frac*b1 close to integer)

    public static boolean isWorthChecking(double b3double, long b1) {

        if (isCloseToInteger(b3double))
            return true;

        double frac = Math.abs(b3double / b1 - Math.round(b3double / b1));

        double fracTimesB1 = frac * b1;

        return Math.abs(fracTimesB1 - Math.round(fracTimesB1)) < CLOSE_TO_INTEGER_THRESHOLD * b1;

    }

    /**
     * 
     * The quality is a measure of small the best difference is compared to the
     * middle number in the AP.
     * 
     * The highest known quality is 1.6476 for the number 2665, that have APs with
     * diff=120.
     * 
     * 1885 also have diff=120, but only quality 1.5753 because the difference is
     * relative higher compared to the number
     * 
     * 
     * 
     * log(diff) /log(middleNumber^2)
     * 
     * 
     * 
     * @see <a href="https://thomas-egense.dk/math/BestQualityAP.html">Best quality
     *      for numbers up to 10E13.</a>
     * 
     * 
     * 
     * @param diff
     * 
     * @param middleNumbber
     * 
     * @return The quality as a double
     * 
     */

    public static double calculateQuality(BigInteger diff, BigInteger middleNumber) {

        double a = bigIntLog(middleNumber, 2);

        double b = bigIntLog(diff, 2);

        // return diff.divide(middleNumbber).doubleValue();

        String fourDigits = String.format("%.4f", a / b);

        return Double.valueOf(fourDigits.replaceAll(",", "."));

    }

    /**
     * 
     * Calculate ln() for a BigInteger for a given log-base
     * 
     * 
     * 
     * @param number The BigInteger to calculate ln() for
     * 
     * @param The    log-base used for ln.
     * 
     * 
     * 
     * @return The ln(number) value as a double
     * 
     * 
     * 
     */

    public static double bigIntLog(BigInteger number, double base) {

        // Convert the BigInteger to BigDecimal

        BigDecimal bd = new BigDecimal(number);

        // Calculate the exponent 10^exp

        BigDecimal diviser = new BigDecimal(10);

        diviser = diviser.pow(number.toString().length() - 1);

        // Convert the BigDecimal from Integer to a decimal value

        bd = bd.divide(diviser);

        // Convert the BigDecimal to double

        double bd_dbl = bd.doubleValue();

        // return the log value

        return (Math.log10(bd_dbl) + number.toString().length() - 1) / Math.log10(base);

    }

    public static double fracTimesB1(double b3double, long b1) {

        double frac = Math.abs(b3double / b1 - Math.round(b3double / b1));

        return frac * b1;

    }

}