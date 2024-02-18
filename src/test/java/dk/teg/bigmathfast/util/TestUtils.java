package dk.teg.bigmathfast.util;

import java.math.BigInteger;

public class TestUtils {
	
	   
	/*
	 * Return a somewhat random BigInteger with up a number of digits.
	 *  
	 */
	
	 public static BigInteger generateRandomNumber(int digits) {
	     StringBuffer b = new StringBuffer();
	     for (int i=0;i<digits;i++) {
	         b.append((int)(Math.random()*10));
	     }
	     	    
	     String number = b.toString();
	     while (number.startsWith("0")) {
	         number=number.substring(1);
	     }     
	    
	     if (number.length()==0) {
	    	 return new BigInteger("0");
	     }
	     return new BigInteger(number);
	             
	     
	 }

}
