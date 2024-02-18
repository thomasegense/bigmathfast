package dk.teg.bigmathfast.abcconjecture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;


public class AbcConjectureTest {

	
    @Test
    void testSum() {
    	 
    	BigInteger a=new BigInteger("5");
    	BigInteger b=new BigInteger("7");
    	BigInteger c=new BigInteger("13");
    	
    	try {
        	double quality = AbcConjecture.getQuality(a, b, c);
        	fail();
    	}
    	catch(Exception e) {
    		//expected
    	}
    	
    }
	

    @Test
    void testCommonFactor() {
    	 
    	BigInteger a=new BigInteger("3");
    	BigInteger b=new BigInteger("15");
    	BigInteger c=new BigInteger("18");
    	
    	try {
        	double quality = AbcConjecture.getQuality(a, b, c);
        	fail();
    	}
    	catch(Exception e) {    	
    		//expected
    	}
    	
    }

    
    @Test
    void testHighestKnowQuality() {    	
    	BigInteger a=new BigInteger("2");
    	BigInteger b=new BigInteger("6436341");  //3^10 * 109
    	BigInteger c=new BigInteger("6436343"); // 23^5    	    	
    	
      	double quality = AbcConjecture.getQuality(a, b, c);
        assertTrue(String.valueOf(quality).startsWith("1.6299"));
      	System.out.println(quality);    
    	
    }
    
	
    
}

