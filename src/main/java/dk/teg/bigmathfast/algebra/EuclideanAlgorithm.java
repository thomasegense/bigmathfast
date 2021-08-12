package dk.teg.bigmathfast.algebra;

import java.math.BigInteger;

public class EuclideanAlgorithm {

	   public static BigInteger B0 = new BigInteger("0");
	   public static BigInteger B1 = new BigInteger("1");
	
    /* Find greatest common divisor of two numbers.
     * Express the gcd as a linear combination of the two numbers using Euclidean Algorithm.  
     *      
     * return array [d, a, b] such that d = gcd(p, q), ap + bq = d
     *
     * Example: p=5, q=3 , return value is{ 1, 2, -1} 
     *  gcd(3, 5) = 1
     *  gcd(3, 5) = 1 =  2(3) + -1(5)
     *
     * For numbers over 4000 digits each, it can be required to increase MaxJavaStackTraceDepth in the JVM
     */
    
    public static BigInteger[] gcdExtendedEuclid(BigInteger p, BigInteger q) {

        if (q.equals(B0)) {
            return new BigInteger[] { p, B1, B0 };
        }

        BigInteger[] vals = gcdExtendedEuclid(q, p.mod(q));
        BigInteger d = vals[0];
        BigInteger a = vals[2];
        BigInteger b = vals[1].subtract((p.divide(q)).multiply(vals[2]));
        return new BigInteger[] { d, a, b };
    }

    
    
}
