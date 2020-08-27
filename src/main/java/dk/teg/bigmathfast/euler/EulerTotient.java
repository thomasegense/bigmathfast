package dk.teg.bigmathfast.euler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dk.teg.bigmathfast.primes.MillerRabin;
import dk.teg.bigmathfast.primes.PollardRho;
import ar.alpertron.ecm.*;


/*
 * @author Thomas Egense
 *  
 * Fast inverse Euler Totient implementation.
 * Based on: https://insa.nic.in/writereaddata/UpLoadedFiles/IJPAM/20005a81_22.pdf
 * Using PollardRho for fast prime decomposition and MillerRabin for fast primality test (BigInteger.isPrime is slow multithreaded) 
 */

public class EulerTotient {
  
    
  private static ExecutorService pool = null;    
  
  
  private final static BigInteger B0 = new BigInteger("0");
  private final static BigInteger B1 = new BigInteger("1");
  private final static BigInteger B2 = new BigInteger("2");
  
  
  //The cache will only contain values for odd numbers.
  private static HashMap<BigInteger, ArrayList<BigInteger>> inverseEulerTotientCacheOdds = new HashMap<BigInteger, ArrayList<BigInteger>>();
  
  static {
    //The recursion will require the first value 
    ArrayList<BigInteger> b1List = new ArrayList<BigInteger>();
    b1List.add(B1);
    b1List.add(B2);
    inverseEulerTotientCacheOdds.put(B1, b1List);            
  }

  public static void main(String[] args) {

      
    // This site: https://www.dcode.fr/euler-totient
    // Can calculate Euler and InverseEuler for small numbers

    //This site can factorize and calculate euler totient for very large numbers. (extremely fast)
    //https://www.alpertron.com.ar/ECM.HTM      

    //System.out.println(eulerTotient(new BigInteger("6")));
    //System.out.println(eulerTotient(new BigInteger("590394090")));
    //Is the factorization  that takes all the time for this one. Also very hard factorization.

    //System.out.println(eulerTotient(new BigInteger("65521244312323233232325430390390390390902")));

      //3031634148236289733373855928919180891127808
     //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("3031634148236289733373855928919180891127808"),true);
     
      long start = System.currentTimeMillis();
      
      //Takes 35 minutes
     //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("24721808588772212736"),true);
            
      ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("576"));
      
      //20 secs
 //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("24142391199972864"),true);     
     
     //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("65521244312323233232325430390390390390904"),true);
     //  inverseEulerTotient(new BigInteger("3"),true);
    //  System.out.println( inverseEulerTotient(new BigInteger("4"),true));      
      
   //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("368640"),true);
      //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("32"),true);
      //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("6"),true);
      //ArrayList<BigInteger> solutions = inverseEulerTotient(new BigInteger("2310"),true);
      Collections.sort(solutions);
      System.out.println("#solutions:"+solutions.size());
  //    System.out.println("solutions:"+solutions);
      System.out.println("cache size:"+inverseEulerTotientCacheOdds.size());
   System.out.println("millis:"+(System.currentTimeMillis()-start));
  
  }
  

  /*
   * Calculate the Euler Totient (phi) for a number.
   * This is just the product of the euler totient for each prime with multiplicity
   */
  public static BigInteger eulerTotient(BigInteger b) {
    
    ArrayList<BigInteger> factors = factor(b);
    Map<BigInteger, Integer> primesAndMultiplicity = countMultiplicites(factors);   

    BigInteger eulerTotient = B1; 
    for (BigInteger prime: primesAndMultiplicity.keySet()) {
      eulerTotient= eulerTotient.multiply(eulerTotientForPrimeWithMultiplicity(prime,primesAndMultiplicity.get(prime)));
    }  
    return eulerTotient;
  }


  public static ArrayList<BigInteger> getDivisors(Map<BigInteger,Integer> primesWithMultiplicty){  
    ArrayList<BigInteger> divisors = new ArrayList<BigInteger>();
    divisors.add(new BigInteger("1"));  //start set  
    for (BigInteger factor : primesWithMultiplicty.keySet()) {
      ArrayList<BigInteger> powers = getPowers(factor, primesWithMultiplicty.get(factor));     
      divisors = multiplySets(divisors, powers);
    }

    return divisors;
  }


  
  
  /*  
   * This method will be called by itself recursive
   */
  private static ArrayList<BigInteger> inverseEulerTotentOdds(BigInteger b){

      //Use cache
      if (inverseEulerTotientCacheOdds.containsKey(b)) {
        return inverseEulerTotientCacheOdds.get(b);
      }
      
      ArrayList<BigInteger> results =   new ArrayList<BigInteger>();

      ArrayList<BigInteger> factors = factor(b);
      
      Map<BigInteger, Integer> primesAndMultiplicity = countMultiplicites(factors);   
      ArrayList<BigInteger> divisors = getDivisors(primesAndMultiplicity);       
      //System.out.println("divisors:"+divisors);
      
      ArrayList<BigInteger> primes = add1AndKeepPrimesOnlyReversedOrder(divisors);
      

      if (primesAndMultiplicity.get(B2) != null)
      {   
        primes.remove(B2);
      }
      for (BigInteger prime : primes) {
        Integer multiplicity = primesAndMultiplicity.get(prime);
        if (multiplicity == null) {
          multiplicity=1;
        }     
              
        //loop as long phi(p^d) does divide number.
        //This can be larger than the multiplicity of the prime.
        
        int multi = 0;
        while (true) {
            multi++;
          
          BigInteger phi_primeD = eulerTotientForPrimeWithMultiplicity(prime,multi);         
          if (!doesDivide (b,phi_primeD )) {
              break;  //skip loop
          }
          
          BigInteger mDiv_phi_primeD = b.divide(phi_primeD);         
        
          //ignore all odd mDiv_phi_primeD (except 1)
          if (!mDiv_phi_primeD.equals(B1) && !isEven(mDiv_phi_primeD)) {
            continue;            
          }
                          
           ArrayList<BigInteger> tmpSet = new ArrayList<BigInteger>();
           tmpSet.add(prime.pow(multi));
           
           
           ArrayList<BigInteger> localInverse =  inverseEulerTotentOdds(mDiv_phi_primeD);
           //Remove elements with primefactors less than prime
           
           ArrayList<BigInteger>  localInverseWithoutSmallFactors = removeNumbersWithSmallPrimeFactors(localInverse, prime);
           
           ArrayList<BigInteger> newSolutions=multiplySets(tmpSet,localInverseWithoutSmallFactors); // only odds 
         
           results.addAll(newSolutions);               
        }
      }
       
          inverseEulerTotientCacheOdds.put (b, results); // Results will be changed ?                  
          return results;
              
      
  }

  public static ArrayList<BigInteger> inverseEulerTotient(BigInteger b) { 
   
        
    if (!B1.equals(B1) && !isEven(b)) {
   System.out.println("skipping odds >1 :"+b);
       return new  ArrayList<BigInteger>();
      
    }
     pool = Executors.newFixedThreadPool(8); // 8 threads
    
     
    
    //First get all odds
    ArrayList<BigInteger> results=inverseEulerTotentOdds(b);
     
      
      //TODO use iterator!
      int multi = 0;
      while (true) {
          multi++;
        
        BigInteger phi_primeD = eulerTotientForPrimeWithMultiplicity(B2,multi);         
        if (!doesDivide (b,phi_primeD )) {
            break;  //skip loop
        }
      
                  
          BigInteger mDiv_phi_primeD = b.divide(phi_primeD);                  

          if (!mDiv_phi_primeD.equals(B1) && !isEven(mDiv_phi_primeD)) {
           continue;
          }
          //System.out.println(B2 +":"+multi + ":"+phi_primeD+ ":"+ mDiv_phi_primeD);
        
          //Will always be cached
          ArrayList<BigInteger> oddSolutions = inverseEulerTotientCacheOdds.get(mDiv_phi_primeD);         
          
          //We have to remove all all even solutions since prime 2 will divide them.
          //But only 2 is  even number that can be here.
          //TODO clear cache and only remove B2 ?
          ArrayList<BigInteger>  localInverseWithoutSmallFactors = removeNumbersWithSmallPrimeFactors(oddSolutions, B2);
                     
          //System.out.println("cache for:"+mDiv_phi_primeD +":"+ oddSolutions);
          ArrayList<BigInteger> multiSet = new ArrayList<BigInteger>();
          multiSet.add((B2.pow(multi)));          
          //System.out.println("multi sets:"+multiSet +" "+ oddSolutions + " for div:"+mDiv_phi_primeD);
          ArrayList<BigInteger> newSolutions=multiplySets(multiSet, localInverseWithoutSmallFactors); // only odds
          
          //System.out.println("adding #:"+newSolutions.size());
          results.addAll(newSolutions);
          
          
        }
        return results;  
      
       

    
  }
    /*
     * If maxPrimeFactor is small. It is faster to try a few division than factoring a big Number.
     * This happens a lot in this algorithm!
     * 
     * Can be heavy optimzied, but this is not the bottleneck! 
     */
    private static ArrayList<BigInteger> removeNumbersWithSmallPrimeFactors(ArrayList<BigInteger> numbers, BigInteger maxPrimeFactor){

      ArrayList<BigInteger>  keepers = new ArrayList<BigInteger>();

      for (BigInteger b : numbers) {
        ArrayList<BigInteger> factors = factor(b);
        boolean smallFactor = false;
        for (BigInteger factor :factors) {         
          if (factor.compareTo(maxPrimeFactor) <=0) { //TODO, maybe faster to try small divisors than factor
            //    System.out.println("discarding:"+b +" due to primefactor <="+maxPrimeFactor);
            smallFactor=true;
            break;
          }      
        }
        if (!smallFactor) {
          //    System.out.println("keeping: "+b +" for maxPrimeFactor:"+maxPrimeFactor);
          keepers.add(b);
        }
      }   

      return keepers;
    }


    /*
     * Multiplies all combinations of the two sets.
     */
    private static ArrayList<BigInteger> multiplySets(ArrayList<BigInteger> s1, ArrayList<BigInteger> s2){
      ArrayList<BigInteger> multiplySet = new ArrayList<BigInteger>();
      for (BigInteger b1 : s1) {
        for (BigInteger b2 : s2) {        
          multiplySet.add(b1.multiply(b2));
        }     
      }        
      return multiplySet;

    }

    /* 
     * Count multiplicities for primes. 
     * Ie 2,2,2,3,7 will be put into map
     * {2,3}
     * {3,1} 
     * {7,1}
     *
     */
    private static Map<BigInteger, Integer> countMultiplicites(  ArrayList<BigInteger> factorization){

      Map<BigInteger, Integer> multiplicities = new  HashMap<BigInteger, Integer> ();

      for (BigInteger b : factorization) {
        if (multiplicities.containsKey(b)) { //increase count
          multiplicities.put(b, multiplicities.get(b) +1);        
        }
        else { //add 
          multiplicities.put(b,1);
        }
      }           
      return multiplicities;    
    }


    /*
     *Add 1 to each element in list. Only keep primes
     *Return list sorted small numbers first 
     */
    private static ArrayList<BigInteger> add1AndKeepPrimesOnlyReversedOrder(ArrayList<BigInteger> list){
      ArrayList<BigInteger> primes = new ArrayList<BigInteger>();

      for (BigInteger b : list) {
        BigInteger b_add1 = b.add(new BigInteger("1"));

        MillerRabin m = new MillerRabin(b_add1, 20);
        if (m.isPrime()) {
          primes.add(b_add1);
        }            
      }  
      Collections.sort(primes); //sort
      Collections.reverse(primes); //desc
      return primes;
    }

    /*
     * Return all values of base^i for  0<= i <=max multiplicit
     * 
     * For base = 2 and maxMultiplicity=4
     * return {1,2,4,8,16}
     * 
     */
    private static ArrayList<BigInteger> getPowers(BigInteger base, int maxMultiplicity){
      ArrayList<BigInteger> powers = new ArrayList<BigInteger>();    
      BigInteger current = new BigInteger("1"); 
      for (int i = 0;i <= maxMultiplicity;i++) {
        powers.add(current);
        current=current.multiply(base);
      }
      return powers;
    }


private static boolean doesDivide(BigInteger b1, BigInteger b2) {
    if (B0.equals(b1.remainder(b2))){
        return true;
    }
    return false;
 }
    
    /*
     * Used to calculate the Euler Totient. It is just a multiplication of each prime+multiplicity from this method
     */
    private static BigInteger eulerTotientForPrimeWithMultiplicity(BigInteger prime, int multiplicity) {    
      return prime.pow(multiplicity-1).multiply(prime.subtract(B1));    
    }

    public static boolean isEven(BigInteger number)
    {
      return number.getLowestSetBit() != 0;
    }

    
    
    private static ArrayList<BigInteger> factor (BigInteger b) {
    if (b.toString().length() > 22) {
        return Ecm.factor(b);
    }else {
    System.out.println("factoring:"+b);
        return PollardRho.factor(b);        
    }
        

    }    
  
    

}


class CalculateOddThread implements Runnable    
{ 
    private BigInteger odd; 
      
    public CalculateOddThread(BigInteger odd) 
    { 
        this.odd = odd; 
    } 
    
    public void run() 
    { 
        
    }
    
}

