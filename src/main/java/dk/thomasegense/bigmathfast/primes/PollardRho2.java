package dk.thomasegense.bigmathfast.primes;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
   

public class PollardRho2 {
    private final static BigInteger ZERO = new BigInteger("0");
    private final static BigInteger ONE  = new BigInteger("1");
    private final static BigInteger TWO  = new BigInteger("2");
    private final static SecureRandom random = new SecureRandom();
    
    
    public static ArrayList<BigInteger> factor(BigInteger N) {                    
       //First divide all factors of 2 out.
      ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
      while (N.mod(TWO).compareTo(ZERO) == 0){
        factors.add(TWO);
        N=N.divide(TWO);
      }           
       ArrayList<BigInteger> oddFactors = factor(N,new ArrayList<BigInteger>());      
       factors.addAll(oddFactors);
       Collections.sort(factors);
       return factors;
      
    }

    //Will return primefactors if all are =1 mod 4. If not it will throw IllegalArgumentException
    public static ArrayList<BigInteger> factorOnlyIfAllPrimeFactors1Mod4(BigInteger N) {                    
        return factorOnlyIfAllPrimeFactors1Mod4(N,new ArrayList<BigInteger>());    
    }
    
        
    private static BigInteger rho(BigInteger N) {
        BigInteger divisor;
        BigInteger c  = new BigInteger(N.bitLength(), random);
        BigInteger x  = new BigInteger(N.bitLength(), random);
        BigInteger xx = x;
      
        do {
            x  =  x.multiply(x).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            divisor = x.subtract(xx).gcd(N);
        } while((divisor.compareTo(ONE)) == 0);

        return divisor;
    }

    private static ArrayList<BigInteger> factor(BigInteger N, ArrayList<BigInteger> currentFactors) {
   
           
        if (N.compareTo(ONE) == 0) return currentFactors;
      //  if (N.isProbablePrime(20)) { currentFactors.add(N); return currentFactors; }
        if (isProbablyPrime(N)) { currentFactors.add(N); return currentFactors; }
        BigInteger divisor = rho(N);       
        factor(divisor, currentFactors);
        factor(N.divide(divisor),currentFactors);
        return currentFactors;
   
    }

   
    
    private static ArrayList<BigInteger> factorOnlyIfAllPrimeFactors1Mod4(BigInteger N, ArrayList<BigInteger> currentFactors) {
    	   
        
        if (N.compareTo(ONE) == 0) return currentFactors;
        //if (N.isProbablePrime(20)) { 
        if (isProbablyPrime(N)) { //use fast method
        	
        	if (is1Mod4(N)){
        	  currentFactors.add(N);         	  
        	  return currentFactors;
        	}        	
        	 
        	else{
        		throw new IllegalArgumentException("Has not all primes = 1 mod 4");
        	 }
        	}
        BigInteger divisor = rho(N);
        factorOnlyIfAllPrimeFactors1Mod4(divisor, currentFactors);
        factorOnlyIfAllPrimeFactors1Mod4(N.divide(divisor),currentFactors);
        return currentFactors;
   
    }

    //BigInteger.isProbablyPrime will block on multi threaded due to SecureRandom not generating enough from dev/random
    private static boolean isProbablyPrime(BigInteger b){
        MillerRabin m = new MillerRabin(b, 20);
    
        /* For test the custom rabin miller gives same result
        if (m.isPrime() != b.isProbablePrime(20)){ 
            System.out.println("prime problem:"+b);
     System.exit(1);
        }
    */
        return m.isPrime();                
    }
    
	private static boolean is1Mod4(BigInteger number){
		String numberStr=number.toString();
		String lastTwoDigits=null;
		if (numberStr.length()==1){
			lastTwoDigits=numberStr;
		}
		else{
			lastTwoDigits=numberStr.substring(numberStr.length()-2,numberStr.length());
		}

		long lastTwoDigitsLong = Long.parseLong(lastTwoDigits);

		return (lastTwoDigitsLong %4 == 1);

	}



    //Fast method to test if all primefactors=1 mod 4 for a number N. If a negative is found method returns before having to full factorize the number.
	public static boolean hasOnlyPrimefactors1Mod4(BigInteger N) {
		if (N.compareTo(ONE) == 0) return true;

		if (N.isProbablePrime(20)) {
			if (! is1Mod4(N)){
				return false;        	        	
			}
			else{
				return true; 
			}
						
		}
		BigInteger divisor = rho(N);
		if (!hasOnlyPrimefactors1Mod4(divisor))   return false;
		if (!hasOnlyPrimefactors1Mod4(N.divide(divisor)))  return false;
	
		return true; 
	}
    
   
    public static void main(String[] args) {
      BigInteger N =new BigInteger("65521244312323233232325430390390390390902"); //hard example, two large primefactors      
        ArrayList<BigInteger> factors = factor(N);
        System.out.println(factors);
       
    }
}  