package dk.teg.bigmathfast.squares;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class DecomposedPrime {

    public static HashMap<BigInteger, DecomposedPrime> cache = new HashMap<BigInteger, DecomposedPrime>();
    public static BigInteger maxPrimeInCache = new BigInteger("10000000"); //10M. pi(100M) which are =1 mod 4 is 2880504 which takes 700MB cache memory
                                            
    
    //Factory method, only way to create DecomposedPrime. Uses cache internally
    public static DecomposedPrime create(BigInteger b) {


        if (b.compareTo(maxPrimeInCache) < 0) {
            return getFromCache(b);
        }

         // not use cache
        return new DecomposedPrime(b);

    }

    private static BigInteger b2 = new BigInteger("2");

    BigInteger prime = BigInteger.ZERO;
    BigInteger s = BigInteger.ZERO;
    BigInteger r = BigInteger.ZERO;

    // Prime must be =1 mod 4
    private DecomposedPrime(BigInteger prime) {
        this.prime = prime;
        init();
    }

    public BigInteger getPrime() {
        return prime;
    }

    public BigInteger getS() {
        return s;
    }

    public BigInteger getR() {
        return r;
    }

    private void init() {
        ArrayList<BigInteger> factorPrimeInTwoSquares = GaussianInteger.factorPrimeInTwoSquares(prime);
        r= factorPrimeInTwoSquares.get(0);
        s= factorPrimeInTwoSquares.get(1);                      
    }

    public Tuppel3SquaresInAPBigNumber getSquareAP() {
        BigInteger small = r.subtract(s).abs();
        BigInteger middle = prime;
        BigInteger high = r.add(s);
        BigInteger difference = r.multiply(s).multiply(b2);

        return new Tuppel3SquaresInAPBigNumber(small, middle, high, difference);

    }

    public String toString() {
        return "p=" + prime + " r=" + r + " s=" + s;
    }

    
    public NumberExpressedInSumOfSquares getNumberExpressedInSumOfSquares() {
        return new NumberExpressedInSumOfSquares(r, s, prime);

    }

    public static int getCurrentCacheSize(){
        return cache.size();
    }
    
    //if not syncronized it WILL block in multithreaded
    private static synchronized DecomposedPrime getFromCache(BigInteger b) {
        DecomposedPrime p = cache.get(b);
        if (p != null) {
            // System.out.println(b +" found in cache");
            return p;
        } else {
            p = new DecomposedPrime(b);
            cache.put(b, p);
            // System.out.println(b +" added to cache");
            return p;
        }
    }
}