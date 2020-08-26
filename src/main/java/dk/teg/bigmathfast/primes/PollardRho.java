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


/*
 * TODO unittest
 */
    public static void main(String[] args) {
        BigInteger N = new BigInteger("10000000000000000000000000000000001");
        System.out.println(factor(N));
    }
}


