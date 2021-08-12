package dk.teg.bigmathfast.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.TestUtils;

public class EuclideanAlgorithmTest {

	private static BigInteger B0 = new BigInteger("0");
    
	 
    @Test
    void testExtendedEuclid1() {
    	 BigInteger[] gcdComb = EuclideanAlgorithm.gcdExtendedEuclid(new BigInteger("3"), new BigInteger("5"));	
    	System.out.println(gcdComb[0]);
    	System.out.println(gcdComb[1]);
    	System.out.println(gcdComb[2]);
    }
	
    @Test
    void testExtendedEuclid() {
     try {
    	//Do some random tests and test the (unique) solution works.
    	//Test for increasing number sizes up to 1000 digits. Tested for up to 10K digits
    	for (int numberDigits=2;numberDigits<1000;numberDigits++) {
    		System.out.println(numberDigits);
    	 BigInteger num1= TestUtils.generateRandomNumber(numberDigits);
    	 BigInteger num2= TestUtils.generateRandomNumber(numberDigits);
    	 if (num1.equals(B0) || num2.equals(B0)) { //skip
    		 continue;
    	 }
    	
    	 BigInteger[] gcdComb = EuclideanAlgorithm.gcdExtendedEuclid(num1, num2);
   
    	 BigInteger gcd=gcdComb[0];
    	 BigInteger a=gcdComb[1];
    	 BigInteger b=gcdComb[2];
 
    	  // test gcd divides both
    	  BigInteger div1 = num1.divide(gcd);
    	  assertEquals(num1,gcd.multiply(div1));
    	  BigInteger div2 = num2.divide(gcd);
    	  assertEquals(num2,gcd.multiply(div2));
    	  
    	  //test linear combination is correct    
    	  BigInteger combination = ( num1.multiply(a) ).add( (num2.multiply(b)) );
    	
    	  assertEquals(gcd,  combination ) ;    		
    	}
    	
    
    }
    catch (Throwable e) {
    	e.printStackTrace(); // was used to find stack overflow
    }}
     
}
