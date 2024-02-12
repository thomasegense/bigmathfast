package dk.teg.bigmathfast.search;

import java.math.BigInteger;
import java.util.Random;

import dk.teg.bigmathfast.abcconjecture.AbcConjecture;

public class SearchAbcForHighQuality {

	private static Random ran = new Random();
	
	public static void main (String[] args) {
		
		int digits=60; //Search for numbers this size
		
		
		while (true) {
		   
			//Generate 'c' as p1^m*p2. 
			BigInteger c_prime_with_high_multiplicity = getRandomPrime(20000);
		    BigInteger c_bigpart=createPrimeWithMultiplicity(c_prime_with_high_multiplicity, digits);		   
		   BigInteger c_prime_with_low_multiplicity=getRandomPrime(40); // Not too big.		   
		   //System.out.println(c_prime_with_high_multiplicity );
		   //System.out.println(c_prime_with_low_multiplicity );
		   BigInteger c= c_bigpart.multiply(c_prime_with_low_multiplicity);
		   
		   
		   //Generate 'b' as p3^x. Such that b is as close to a as possible to 'a', but not higher		   
		   BigInteger b_prime = getRandomPrime(50); //b must not be one of c's factors!		   
		   while(b_prime.equals(c_prime_with_low_multiplicity) || b_prime.equals(c_prime_with_high_multiplicity)) {
			 b_prime = getRandomPrime(40);   
		   }
		   
		   BigInteger b= createPrimeMultiple(b_prime, c);		  
		   //Create 'a' such that a+b=c;
		   BigInteger a=c.subtract(b);
		   double q=0;
		   try {
		    q= AbcConjecture.getQuality(a, b, c);
		   
		   }
		   catch(Exception e) {
			   System.out.println("c_prime_with_high_multiplicity:"+c_prime_with_high_multiplicity);
			   System.out.println("c_prime_with_lower_multiplicity:"+c_prime_with_low_multiplicity);
			   System.out.println("b_prime:"+b_prime);
			   
		   }
		   if (q>1) {
		   System.out.println("q="+q +" for: "+a +","+b+","+c);
		   }
		   
		   
		}
		
	}
	
	
	/* return a prime with maxNumber (approximate)
	 * 
	 */
	private static BigInteger getRandomPrime(int maxNumber) {
		
		int random1 = ran.nextInt(maxNumber);
		
		BigInteger prime = new BigInteger(""+random1); //maybe not prime 
		prime=prime.nextProbablePrime(); //But now is prime
	    return prime;
	}
	
	
	// Will take power of prime until minimum digits reached
	private static BigInteger createPrimeWithMultiplicity(BigInteger prime, int minimumDigits) {
		BigInteger multi = prime;
		while (multi.toString().length() <minimumDigits) {
			multi=multi.multiply(prime);
		}
		return multi;
	
	}
	
	private static BigInteger createPrimeMultiple(BigInteger prime, BigInteger maxLimit) {
		BigInteger before=prime;
		
		while(true) {
			BigInteger next=before.multiply(prime);
			if (next.compareTo(maxLimit)==1) {
				return before;
			}
			before=next;
		
			
		}
		
		
		
		
		
		
	}
}
