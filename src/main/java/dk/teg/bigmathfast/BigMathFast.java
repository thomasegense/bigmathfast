package dk.teg.bigmathfast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ar.alpertron.ecm.Ecm;
import dk.teg.bigmathfast.euler.EulerTotient;
import dk.teg.bigmathfast.primes.PollardRho;

public class BigMathFast {

    /*
     * Just an example to run the factorization from a command line.
     * Example :
     *  java -cp bigmathfast-1.0-jar-with-dependencies.jar dk.teg.bigmathfast.BigMathFast 93035149443954345347665179408833277091909532522394543659489519897196854705698057
     *  This 70 digits will be factorized in 25 seconds. This is worst case for a 70 digits number. 
     * 
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Must time a number as input (1 value only)");
            System.exit(1);                               
        }
        
        //System.out.println(factorize(11111111111111L));
        
        BigInteger b= new BigInteger(args[0]);
        long start = System.currentTimeMillis();
        ArrayList<BigInteger> factors = Ecm.factor(b);
        System.out.println("Factorization time in millis:"+(System.currentTimeMillis()-start));
        System.out.println(factors);          
    }
    
    
    /**
     * This method will factorize an integer into prime factors. 
     * 
     * 
     * If the number has less than 22 digits, the Pollardh algorithm will be used.
     * If the number has 22 digits or more, the ECM/Siqs  algorithm will be used.
     * 
     * A number with 70 digits will be factorized in 30 seconds in worst case.
     * The complexity dependens on the second largest prime factor.
     * If the second largest prime factor is larger than 45 digits, it can take
     * many days for factorize.   
     *    
     * @see https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
)    * @see https://www.alpertron.com.ar/ECM.HTM
     *   
     * @param b The BigInteger to be factorized
     * @return ArrayList<BigInteger> with the prime factors in sorted order. 
     *  
     */    
    public static ArrayList<BigInteger> factorize(BigInteger b){        
        if (b.toString().length() <22) {
            return PollardRho.factor(b); 
        }
        else {
          return Ecm.factor(b);                
        }       
    }
       
    
    /**
     * This method will factorize an integer into prime factors. 
     * 
     * Will use the PollardRho algorithm for small numbers (long)
     * To factorize large numbers uset the factorize(BigInteger b) method 
     * 
    
     * @see https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm
     *   
     * @param b The BigInteger to be factorized
     * @return ArrayList<BigInteger> with the prime factors in sorted order. 
     *  
     */    
    public static List<Long> factorize(Long b){        
             ArrayList<BigInteger> factor = PollardRho.factor(new BigInteger(""+b)); 
             List<Long>  factorsLong  =factor.stream().map( s ->  s.longValue()).collect(Collectors.toList());
             return factorsLong;             
    }
    
    /**
     * Calculate the Euler Totient (phi) for an number     
     * Running time is dependant on the factorization time of the input number
     * @see https://en.wikipedia.org/wiki/Euler%27s_totient_function    
     *   
     * The number of solutions depends on the number of divisors (or total number of prime factors).
     *      
     * @param b The BigInteger to calculate the Euler Totient
     * @return BigInteger The Euler Totient 
     *  
     */    
    public static BigInteger eulerTotient(BigInteger b) {        
        return EulerTotient.eulerTotient(b);
    }
        
    
    /**
     * Calculate the Inverse Euler Totient (invphi) for an number
     * Uses algorithm described by Hansraj Gupta and is the fastests known.
     * 
     * @see http://www.new.dli.ernet.in/rawdataupload/upload/insa/INSA_2/20005a81_22.pdf      
     * @see https://en.wikipedia.org/wiki/Euler%27s_totient_function          
     *   
     * @param b The BigInteger to calculate the Euler Totient
     * @return ArrayList<BigInteger> All numbers have Euler Totient equal to b 
     *  
     */
    public static ArrayList<BigInteger> inverseEulerTotient(BigInteger b) {        
        return EulerTotient.inverseEulerTotient(b);
    }
        
}
