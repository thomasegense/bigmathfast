package dk.teg.bigmathfast.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.Test;


public class InverseElementInZNTest {

	private static BigInteger B0 = new BigInteger("0");
	private static BigInteger B1 = new BigInteger("1");
	private static BigInteger B2 = new BigInteger("2");
	
    @Test
    void testPrimeTwoWithMultiplicity() {
        	
    	// Prime=2 has special solutions.
    	//For multiplicity 1 they collapse to 1 solution: 1
    	//For multiplicity 2 they collapse to 2 solutions 1, (p-1)
    	//For multiplicity >2 always48 solutions ,  1,  (p/2)-1 , (p/2)+1, (p-1).
    	    	
    	//number=2 
    	BigInteger prime = B2;
    	int multiplicity =1;    	
    	ArrayList<BigInteger> rootsFor2 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);
    	
    	assertEquals(1, rootsFor2.size());
    	assertTrue(rootsFor2.contains(B1));
    	
    	// number=4
    	 multiplicity =2;    	
    	ArrayList<BigInteger> rootsFor4 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);    	
    	assertEquals(2, rootsFor4.size());
    	assertTrue(rootsFor4.contains(B1));
    	assertTrue(rootsFor4.contains(new BigInteger("3")));
    	
    	// number=8
   	    multiplicity =3;    	
   	    ArrayList<BigInteger> rootsFor8 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);    	
   	    System.out.println(rootsFor8);
     	assertEquals(4, rootsFor8.size());
   	    assertTrue(rootsFor8.contains(B1));
   	    assertTrue(rootsFor8.contains(new BigInteger("3")));
   	    assertTrue(rootsFor8.contains(new BigInteger("5")));
   	    assertTrue(rootsFor8.contains(new BigInteger("7")));
   	    
    	
   	    //256. Just testing correct number
   	    multiplicity =8;    	
   	    ArrayList<BigInteger> rootsFor256 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);    	
        System.out.println(rootsFor256);
   	    assertEquals(4, rootsFor256.size());
   	    assertTrue(rootsFor256.contains(B1));
   	    assertTrue(rootsFor256.contains(new BigInteger("127")));
  	    assertTrue(rootsFor256.contains(new BigInteger("129")));
  	    assertTrue(rootsFor256.contains(new BigInteger("255")));
  	 
   	    
     	//large number...
   	    multiplicity =256;    	
   	    ArrayList<BigInteger> rootsForLargeNumber = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);    	       
   	    assertEquals(4,  rootsForLargeNumber.size());   	    
    	//This time test they are solutions for x^2=1 mod (p^n)
   	    BigInteger largeNumber=B2.pow(multiplicity);
   	    
   	    for (BigInteger root : rootsForLargeNumber) {
   	    	//assertEquals(B1, (root.multiply(root)).mod(largeNumber)); Faster with method below   	    	 
   	    	assertEquals(B1, root.modPow(B2, largeNumber));	
   	    }
   	    
    }
    
    @Test
    void testOddPrimeWithMultiplicity() {
      //for n =p^x , the only solultions are 1 and (p^x)-1
    	
    	BigInteger prime = new BigInteger("3");
    	int multiplicity =1;    	
    	ArrayList<BigInteger> rootsFor3 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);
    	assertEquals(2,  rootsFor3.size());
    	assertTrue(rootsFor3.contains(B1));
        assertTrue(rootsFor3.contains(B2));
    	
    	
        prime = new BigInteger("37");
    	multiplicity =1;    	
    	ArrayList<BigInteger> rootsFor37 = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);
    	assertEquals(2,  rootsFor37.size());
    	assertTrue(rootsFor37.contains(B1));
        assertTrue(rootsFor37.contains(new BigInteger("36")));
        
        //Go large
        multiplicity =6;    	
        BigInteger largePrime = BigInteger.probablePrime(250, new Random());
        ArrayList<BigInteger> rootsForLarge = InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(largePrime, multiplicity); 
    	assertEquals(2,  rootsForLarge.size());
    	assertTrue(rootsForLarge.contains(B1));
        
    	//Tests both solutions        	
    	BigInteger largeNumber=largePrime.pow(multiplicity);
    	for (BigInteger root : rootsForLarge) {
    	    	//assertEquals(B1, (root.multiply(root)).mod(largeNumber)); Faster with method below   	    	 
    	    	assertEquals(B1, root.modPow(B2, largeNumber));	
    	    }
	    }
        
        

   
    @Test
    void testTwoPrimes() {
    	
   // 	InverseElementInZN.findRootsOfUnityForPrimeWithMultiplicity(prime, multiplicity);
    	
    	
    }
    
    
}

