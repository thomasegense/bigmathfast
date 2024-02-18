package dk.teg.bigmathfast.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class BigMathFastUtil {

       private static final BigInteger B1 = new BigInteger("1");
       private static final BigInteger B4 = new BigInteger("4");

    /**
     * Calculate ln() for a BigInteger for a given log-base
     * 
     * @param number The BigInteger to calculate ln() for
     * @param The log-base used for ln.
     * 
     * @return The ln(number) value as a double
     * 
     */
    public static double bigIntLog(BigInteger number, double base) {

        // Convert the BigInteger to BigDecimal
        BigDecimal bd = new BigDecimal(number);
        // Calculate the exponent 10^exp
        BigDecimal diviser = new BigDecimal(10);
        diviser = diviser.pow(number.toString().length()-1);
        // Convert the BigDecimal from Integer to a decimal value
        bd = bd.divide(diviser);
        // Convert the BigDecimal to double
        double bd_dbl = bd.doubleValue();
        // return the log value
        return (Math.log10(bd_dbl)+number.toString().length()-1)/Math.log10(base);
    }
    

    /**
     * Calculate the squareroot rounded down to nearest Integer
     * 
     * @param n the number to calculate squareroot for.
     * 
     * @return The square root, rounded down to integer
     *      
     */
    public static BigInteger bigintroot(BigInteger n) {
        int currBit = n.bitLength() / 2;

        BigInteger remainder = n;
        BigInteger currSquared = BigInteger.ZERO.setBit(2*currBit);

        BigInteger temp = BigInteger.ZERO;
        BigInteger toReturn = BigInteger.ZERO;

        BigInteger potentialIncrement;
        do {
            temp = toReturn.setBit(currBit);
            potentialIncrement = currSquared.add(toReturn.shiftLeft(currBit+1));

            int cmp = potentialIncrement.compareTo(remainder);
            if (cmp < 0) {
                toReturn = temp;
                remainder = remainder.subtract(potentialIncrement);
            }
            else if (cmp == 0) {
                return temp;
            }
            currBit--;
            currSquared = currSquared.shiftRight(2);
        } while (currBit >= 0);
        return toReturn;
    }
    
    /**  
     * Validate all factors are == 1 (mod 4)
     * 
     * Example 5,13,19
     *  
     * @param factors
     * @return true if all factors are ==1 (mod 4)
     */
    public static boolean allFactors1Mod4(ArrayList<BigInteger> factors) {
        for (BigInteger b:factors) {
            if (!b.mod(B4).equals(B1)) {         
                return false;
            }
        }
        return true;
    }
    
    
}
