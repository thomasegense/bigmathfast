package dk.teg.bigmathfast.primes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;


/* 
 * Java implementation here: https://introcs.cs.princeton.edu/java/99crypto/PollardRho.java.html
 * Modified into a method with return values; 
 */


public class PollardRho {
    private final static BigInteger ZERO = new BigInteger("0");
    private final static BigInteger ONE  = new BigInteger("1");
    private final static BigInteger TWO  = new BigInteger("2");
    private final static SecureRandom random = new SecureRandom();


    public static ArrayList<BigInteger> factor(BigInteger N) {         
        ArrayList<BigInteger> factors = new  ArrayList<BigInteger>();
        factor(N,factors);
        Collections.sort(factors);
        return factors;        
    }



    private static void factor(BigInteger N, ArrayList<BigInteger> factors) {
        if (N.compareTo(ONE) == 0) return;
        if (N.isProbablePrime(20)) {
            factors.add(N);
            return;
        }
        BigInteger divisor = rho(N);
        factor(divisor,factors);
        factor(N.divide(divisor), factors);

    }


    private static BigInteger rho(BigInteger N) {
        BigInteger divisor;
        BigInteger c  = new BigInteger(N.bitLength(), random);
        BigInteger x  = new BigInteger(N.bitLength(), random);
        BigInteger xx = x;

        // check divisibility by 2
        if (N.mod(TWO).compareTo(ZERO) == 0) return TWO;

        do {
            x  =  x.multiply(x).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            xx = xx.multiply(xx).mod(N).add(c).mod(N);
            divisor = x.subtract(xx).gcd(N);
        } while((divisor.compareTo(ONE)) == 0);

        return divisor;
    }


    
    //Will return primefactors if all are =1 mod 4. Else return null
    public static ArrayList<BigInteger> factorOnlyIfAllPrimeFactors1Mod4(BigInteger N) throws Exception{                    
        return factorOnlyIfAllPrimeFactors1Mod4(N,new ArrayList<BigInteger>());    
    }
    
    // throws Exception if any prime =3 (mod4)
    private static ArrayList<BigInteger> factorOnlyIfAllPrimeFactors1Mod4(BigInteger N, ArrayList<BigInteger> currentFactors) {
      
      
      if (N.compareTo(ONE) == 0) return currentFactors;
      //if (N.isProbablePrime(20)) { 
      if (isProbablyPrime(N)) { //use fast method
          
          if (is1Mod4(N)){
            currentFactors.add(N);              
            return currentFactors;
          }           
           
          else{//Early termination
              throw new IllegalArgumentException("A factor found is not ==1 (mod 4). Factor:"+N);
           }
          }
      BigInteger divisor = rho(N);
      factorOnlyIfAllPrimeFactors1Mod4(divisor, currentFactors);
      factorOnlyIfAllPrimeFactors1Mod4(N.divide(divisor),currentFactors);
      return currentFactors;
 
  }
    
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
  
    
    /*
     * Need to test if this is faster than using MOD 4 for BigInteger
     * 
     */
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
    
}


