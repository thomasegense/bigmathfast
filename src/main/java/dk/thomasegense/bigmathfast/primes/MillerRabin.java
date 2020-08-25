package dk.thomasegense.bigmathfast.primes;

import java.math.BigInteger;
import java.util.Random;
import java.lang.Thread;


/**
 * @author cfahim (modified by Thomas Egense)
 *
 */
public class MillerRabin extends Thread {
 
  public static boolean verbose = false;
 
 
  private BigInteger m_prime;
  private long m_2factor;
  private BigInteger m_oddfactor;
  private boolean m_isprime;  
  private long m_steps;
  private static BigInteger B2= new BigInteger("2");
  private static BigInteger B3= new BigInteger("3");
  private static BigInteger B5= new BigInteger("5");
  private static BigInteger B7= new BigInteger("7");
  private static BigInteger B11= new BigInteger("11");
  private static BigInteger B13= new BigInteger("13");
  private static BigInteger B17= new BigInteger("17");
  private static BigInteger B19= new BigInteger("19");
  private static BigInteger B23= new BigInteger("23");
  
 
  //taken from:
  /*
   * Miller-Rabin wikipedia
   * Pomerance, C.; Selfridge, J. L. & Wagstaff, S. S., Jr. (1980), "The pseudoprimes to 25á109", Mathematics of Computation 35 (151): 1003Ð1026, doi:10.2307/2006210
   * Jaeschke, Gerhard (1993), "On strong pseudoprimes to several bases", Mathematics of Computation 61 (204): 915Ð926, doi:10.2307/2153262
   */
  private final BigInteger FASTMAX = new BigInteger("3");
  private final static BigInteger STARTSEARCH = new BigInteger("415911257725");
  
public static void main (String[] args){
    BigInteger big= new BigInteger("5");
    MillerRabin m1 = new MillerRabin(big, 20);
    
    
    System.out.println(m1.isPrime());
    
    System.exit (1);
    
    BigInteger current = STARTSEARCH;
    long start = System.currentTimeMillis();
    for (int i=0;i<1000;i++){
        current= current.add(B2);
        
        MillerRabin m = new MillerRabin(current, 20);
        boolean prime = m.isPrime();
            
    }
       
    
       //OLD
        current = STARTSEARCH;
       start = System.currentTimeMillis();
       for (int i=0;i<1000;i++){
           current= current.add(B2);
                 
           boolean prime = current.isProbablePrime(20);
              
       }
          System.out.println("time, old Rabin miller:"+(System.currentTimeMillis()-start));
       
    
  }
  
  public MillerRabin(BigInteger bi, long steps)
        {              
    this.setprime(bi);  
    this.m_steps = steps;//bi.min(BigInteger.valueOf(steps-1)).longValue();
    //this.m_steps = steps;
    //m_isprime = PrimalityTest();
    
        }
 
  /***
   * For Threading
   */
  public void run()
  {
    if(verbose)
      System.out.println("Running MR with " + m_prime.toString() + " as a thread");
    this.isPrime();
  }

  /***
   * Runs Miller Rabin Algorithm to test if m_prime is prime
   * @return true if m_prime is prime
   */
  public boolean isPrime()
  {
    
    if (this.m_prime.equals(B2) ) {
      return true;
      
    }
    
    this.m_isprime = PrimalityTest();
    return m_isprime;
  }
 
  /***
   * Miller Rabin algorithm to test m_prime is prime or not
   * @return true if prime
   */
  private boolean PrimalityTest() {
    if(!m_prime.testBit(0))
      return false;
    if(  (m_prime.compareTo(B2) == 0 ||
            m_prime.compareTo(B3) == 0 ||  
            m_prime.compareTo(B5) == 0 ||
            m_prime.compareTo(B7) == 0 ||
            m_prime.compareTo(B11) == 0 ||
            m_prime.compareTo(B13) == 0 ||
            m_prime.compareTo(B17) == 0 ||
            m_prime.compareTo(B19) == 0 ||
            m_prime.compareTo(B23) == 0)             
            )
      return true;
       
      GetFactorization(m_prime.subtract(BigInteger.ONE));           
      return FastPrimalityTest();
    
           
  }
 

 

  
  //This works for all primes below 3825123056546413051
  public boolean FastPrimalityTest() {
    if(verbose)
      System.out.println("Running FastPrimalityTest");
    int a_list[] = {2,3,5,7,11,13,17,19,23};
   
    for(int i = 0; i < a_list.length; i++)
    {
      BigInteger rand = BigInteger.valueOf(a_list[i]);
      if(verbose)
        System.out.println("Testing a=" + rand.toString() + " as a witness");
      BigInteger tester = rand.modPow(m_oddfactor, m_prime);
     
      if(tester.equals(BigInteger.ONE) || tester.equals(m_prime.subtract(BigInteger.ONE)))
      {
        continue;
      }
      else
      {
        boolean shouldrtn = true;
        for(long r = 1; r <= m_2factor - 1; r++)
        {
          tester = tester.modPow(B2, m_prime);
          if(tester.equals(BigInteger.ONE))
          {
            if(verbose)
              System.out.println(rand.toString() + " is a witness to our prime");
            return false;
          }
          else if(tester.equals(m_prime.subtract(BigInteger.ONE)))
          {
            shouldrtn = false;
            break;
          }
           
        }
        if(shouldrtn)
        {
          if(verbose)
            System.out.println(rand.toString() + " is a witness our number is composite");
          return false;
        }
      }
    }
   
    if(verbose)
      System.out.println("MR Could not find any witnesses, probably prime");
    return true;  
  }

  

 
  /***
   * Sets m_2factor and m_oddfactor such that divider = 2^m_2factor*m_oddfactor
   * @param divider
   *
   */
  private void GetFactorization(BigInteger divider) {
    // TODO Auto-generated method stub
    if(verbose)
      System.out.println("MR getting 2^s * d");
    int rtn = 0;
    while(!divider.testBit(rtn))
    {
      rtn++;
      //divider = divider.divide(TWO);
    }
    this.m_2factor = rtn;
    this.m_oddfactor = divider.divide(BigInteger.valueOf((long)Math.pow(2,rtn)));

    if(verbose)
      System.out.println("MR s: " + rtn + " d:" + this.m_oddfactor.toString());
  }

  /***
   * Sets the prime number to be tested
   * @param m_prime
   */
  public void setprime(BigInteger m_prime) {
    this.m_prime = m_prime;
  }

  /***
   * Gets the prime number to be tested
   * @return
   */
  public BigInteger getprime() {
    return m_prime;
  }

  public boolean GetIsPrime() {
    return m_isprime;
  }

}

