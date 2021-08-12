package dk.teg.bigmathfast.algebra;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;


public class InverseElementInZNTest {

	private static BigInteger B0 = new BigInteger("0");
	private static BigInteger B1 = new BigInteger("1");
	private static BigInteger B2 = new BigInteger("2");
	
    @Test
    
    void testPrimeWithMultiplicityForPrime2() {
        	
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
    	
    }
}

