package dk.teg.bigmathfast.abcconjecture;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;

import ar.alpertron.ecm.Ecm;

/**
 * Class can calculate the quality of an ABC triplet
 * ABC Conjecture: https://www.math.leidenuniv.nl/~desmit/abc/index.php?set=2
 * List of high quality triplets: https://www.math.leidenuniv.nl/~desmit/abc/index.php?set=2 
 * List is not updated. As of 2023 there are 160 known triplets with quality > 1.4
 * 
 */
public class AbcConjecture {

	final private static BigInteger B0= new BigInteger("0");
	final private static BigInteger B1= new BigInteger("1");
	
	
	/**
	 * Triplet must satisfy: a+b=c and must have no common primefactors
	 * This will be validated
	 * 
	 * 
	 * @param a 
	 * @param b
	 * @param c
	 * @return
	 */
	public static double getQuality(BigInteger a, BigInteger b, BigInteger c) {
		
		BigInteger sum=a.add(b);
		if (!c.equals(sum)) {
			throw new IllegalArgumentException("a+b does not equal c");
		}
		
		ArrayList<BigInteger> factora = Ecm.factor(a);
		ArrayList<BigInteger> factorb = Ecm.factor(b);
		ArrayList<BigInteger> factorc = Ecm.factor(c);

		boolean hasIntersection1 =factora.stream().anyMatch(factorb::contains);
		boolean hasIntersection2 =factora.stream().anyMatch(factorc::contains);
		boolean hasIntersection3 =factorb.stream().anyMatch(factorc::contains);

		if (hasIntersection1 || hasIntersection2 || hasIntersection3) {
			throw new IllegalArgumentException("The numbers has a share prime factor:"+a +" ," +b +" , "+c);
		}		
		return getQuality(factora, factorb, factorc, c);

	}

   
  
  /**
   * If you already know factors this is a faster way to calculate quality.
   * But the sum a+b=c condition and no co-primes must be validated before calling this method
   * 
   * @param factora
   * @param factorb
   * @param factorc
   * @param c
   * @return
   */
	public static double getQuality(ArrayList<BigInteger> factora, ArrayList<BigInteger> factorb, ArrayList<BigInteger> factorc, BigInteger c) {


		HashSet<BigInteger> factor_unique = new HashSet<BigInteger>();
		factor_unique.addAll(factora);
		factor_unique.addAll(factorb);	
		factor_unique.addAll(factorc);


		BigInteger products = getProducts(factor_unique);


		double n= BigIntLog(c,2);  
		double d= BigIntLog(products,2);

		return n/d;


	}


	private static double BigIntLog(BigInteger bi, double base) {
		// Convert the BigInteger to BigDecimal
		BigDecimal bd = new BigDecimal(bi);
		// Calculate the exponent 10^exp
		BigDecimal diviser = new BigDecimal(10);
		diviser = diviser.pow(bi.toString().length()-1);
		// Convert the BigDecimal from Integer to a decimal value
		bd = bd.divide(diviser);
		// Convert the BigDecimal to double
		double bd_dbl = bd.doubleValue();
		// return the log value
		return (Math.log10(bd_dbl)+bi.toString().length()-1)/Math.log10(base);
	}


	private static BigInteger getProducts(HashSet<BigInteger> factors) {
		BigInteger prod = B1;

		for (BigInteger b: factors) {
			prod=prod.multiply(b);
		}		
		return prod;

	}

}
