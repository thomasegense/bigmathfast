package dk.teg.bigmathfast.algebra;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

//puzzle: https://www.research.ibm.com/haifa/ponderthis/challenges/July2021.html
// see https://www.wikiwand.com/en/Carmichael_number
public class InverseElementInZN {

    public static BigInteger B0 = new BigInteger("0");
    public static BigInteger B1 = new BigInteger("1");
    public static BigInteger B1minus = new BigInteger("-1");
    public static BigInteger B2 = new BigInteger("2");
    public static BigInteger B3 = new BigInteger("3");
    public static BigInteger B4 = new BigInteger("4");
    public static BigInteger B5 = new BigInteger("5");
    public static BigInteger B6 = new BigInteger("6");
    public static BigInteger B7 = new BigInteger("7");
    public static BigInteger B12 = new BigInteger("12");
    public static BigInteger B18 = new BigInteger("18");

    //(6k + 1)(12k + 1)(18k + 1) is Carmichael number if all 3 factors prime
    public static void main(String[] args) {

    	  BigInteger p = B3;
          BigInteger q = B5;

          BigInteger vals[] = EuclideanAlgorithm.gcdExtendedEuclid(q, p);
          System.out.println("gcd(" + q + ", " + p + ") = " + vals[0]);
          System.out.println(vals[1] + "(" + q + ") + " + vals[2] + "(" + p + ") = " + vals[0]);
    	
        /*
        int prime = 11;
        int multiplicity =6;
        int val=  (int) Math.pow(prime, multiplicity);
        System.out.println(val);
        findRootsOfUnityBruteForce(val);
        System.out.println(findRootsOfUnityForPrimeWithMultiplicity(new BigInteger(""+prime), multiplicity));
        
        System.exit(1);
        */
        /*
         * 600000000000000000000000000017881 1200000000000000000000000000035761
         * 1800000000000000000000000000053641
         * 1296000000000000000000000000115866360000000000000000000003452935539600000000000000000034300331977681
         * findCarmichaelNumber(new BigInteger("100000000000000000000000000000000"));
         */

        //  findRootsOfUnityBruteForce(105);

        //System.out.println(inverseElementInZn(new BigInteger("7"), new BigInteger("15"))); //invers til 15 i mod 7
        //System.out.println(inverseElementInZn(new BigInteger("15"), new BigInteger("7"))); //invers til 7 i mod 15
        //int[] x = gcdExtendedEuclidInt(15,7);

        //System.out.println(x[0]);
        //System.out.println(x[1]);
        //System.out.println(x[2]);
        //System.out.println(inverseElementInZn(new BigInteger("7"), new BigInteger("15"))); //invers til 15 i mod 7

        
        System.out.println(inverseElementInZn(new BigInteger("30"), new BigInteger("5")));
        
        
        ArrayList<BigInteger> findRootsOfUnityMerging = findRootsOfUnityMerging(new BigInteger("600000000000000000000000000017881"), new BigInteger("1200000000000000000000000000035761"), new BigInteger("1800000000000000000000000000053641"));
        Collections.sort(findRootsOfUnityMerging);
        System.out.println(findRootsOfUnityMerging);
        
        System.exit(1);

        //findRootsOfUnityBruteForce(15);
        // 3: 1,2
        // 5:1,4
        //15  :1,4,11,14

        /*
         * 
         * x =1 mod 3 AND x = 1 mod 5 x =-1 mod 3 AND x = 1 mod 5 x =1 mod 3 AND x = -1
         * mod 5 x =-1 mod 3 AND x = -1 mod 5
         */

        //3 og 5 indbyrdes primiske , dvs der findes m*3+n*5=1
        //løsning m=2, n =1    

        System.out.println(inverseElementInZn(new BigInteger("199"), new BigInteger("61")));
        System.exit(1);
  

        System.out.println("gcd(" + p + ", " + q + ") = " + vals[0]);
        System.out.println(vals[1] + "(" + p + ") + " + vals[2] + "(" + q + ") = " + vals[0]);

        System.exit(1);

        //5: 1,4
        //11:1,10
        //55  :1,21,34,54

        System.exit(1);

    }

    public static void findRootsOfUnityBruteForce(int n) {
        for (int i = 1; i < n; i++) {

            long i_long= (long) i;
            long rest = ((i_long*i_long) % n);
            if (rest == 1) {
                System.out.println(i);
            }
        }

    }


    public static void findCarmichaelNumber(BigInteger base) {

        while (true) {
            base = base.add(B2);

            BigInteger factor1 = (B6.multiply(base)).add(B1);
            if (!factor1.isProbablePrime(10)) {
                continue;
            }

            BigInteger factor2 = (B12.multiply(base)).add(B1);
            if (!factor2.isProbablePrime(10)) {
                continue;
            }

            BigInteger factor3 = (B18.multiply(base)).add(B1);
            if (!factor3.isProbablePrime(10)) {
                continue;
            }
            BigInteger number = factor1.multiply(factor2).multiply(factor3);

            System.out.println(factor1);
            System.out.println(factor2);
            System.out.println(factor3);
            System.out.println("Carmichael for :" + base + " number:" + number);
            System.out.println("------");

        }

    }

    //expand so not only finding x^2=1
    public static ArrayList<BigInteger> findRootsOfUnity(BigInteger p1, BigInteger p2) {

        BigInteger n = p1.multiply(p2);

        BigInteger w1 = p1.multiply(inverseElementInZn(p2, p1));
        BigInteger w2 = p2.multiply(inverseElementInZn(p1, p2));

        //System.out.println(w1);
        //System.out.println(w2);

        BigInteger r1 = (B1.multiply(w1)).add(B1.multiply(w2));
        BigInteger r2 = (B1minus.multiply(w1)).add(B1.multiply(w2));
        BigInteger r3 = (B1.multiply(w1)).add(B1minus.multiply(w2));
        BigInteger r4 = (B1minus.multiply(w1)).add(B1minus.multiply(w2));

        //fix java negativ modulus
        r1 = r1.add(n).mod(n);
        r2 = r2.add(n).mod(n);
        r3 = r3.add(n).mod(n);
        r4 = r4.add(n).mod(n);

        ArrayList<BigInteger> results = new ArrayList<BigInteger>();
        results.add(r1);
        results.add(r2);
        results.add(r3);
        results.add(r4);

        return results;

    }

    
    /*
     * It is up to the called to check the number is prime.
     * 
     * This is the basic component for the other roots method for combined numbers.  
     */
    public static ArrayList<BigInteger> findRootsOfUnityForPrimeWithMultiplicity(BigInteger prime, int multiplicity) {
        ArrayList<BigInteger> roots= new ArrayList<BigInteger>();
        //fast hardcoding
        if (B2.equals(prime)) {                       
            if (multiplicity==1) {  
              roots.add(B1);
            }
            else if (multiplicity==2) {
                roots.add(B1);
                roots.add(B3);                                
            }
            else { //prime 2 with multiplicty > 2 has 4 solutions. 
                BigInteger half = prime.pow(multiplicity-1);                
                roots.add(B1);
                roots.add(half.subtract(B1));
                roots.add(half.add(B1));
                roots.add(prime.pow(multiplicity).subtract(B1));                                
            }            
        }
        else { //odd prime with multiplicy. Always only the two trivial solutions.
            roots.add(B1);
            roots.add(prime.pow(multiplicity).subtract(B1));
        }        
        return roots;                    
        
    }
    
    
    static ArrayList<BigInteger> addMoreRootsOfUnity(ArrayList<BigInteger> roots1, BigInteger number1, BigInteger number2){
        BigInteger n= number1.multiply(number2);
        

        BigInteger w2 = number1.multiply(inverseElementInZn(number2, number1));
        BigInteger w1 = number2.multiply(inverseElementInZn(number1, number2));
        
        ArrayList<BigInteger> results = new ArrayList<BigInteger>();
        for (BigInteger current : roots1) {
            //
            //Assume number2 only has trivial

            //System.out.println(w1);
            //System.out.println(w2);

            BigInteger r1 = (current.multiply(w1)).add(B1.multiply(w2));
            BigInteger r2 = (current.multiply(w1)).add(B1minus.multiply(w2));

            //fix java negativ modulus
            r1 = r1.add(n).mod(n);
            r2 = r2.add(n).mod(n);

            results.add(r1);
            results.add(r2);

        }

        return results;
        
        
    }
    //    
    //expand so not only finding x^2=1
    static ArrayList<BigInteger> findRootsOfUnityMerging(BigInteger p1, BigInteger p2, BigInteger p3) {
        //first finde for first two

        ArrayList<BigInteger> rootsOfUnity = findRootsOfUnity(p1, p2); // now these will contain more than -1 and 1
        return addMoreRootsOfUnity(rootsOfUnity, p1.multiply(p2), p3);

    }

    

    
  
    /*
     * TODO fix, so throwing exception when element does not exist.
     * 
     */
    public static BigInteger inverseElementInZn(BigInteger n, BigInteger element) {

        BigInteger[] vals = EuclideanAlgorithm.gcdExtendedEuclid(n, element);
        //TODO check q is one
        if (vals[2].compareTo(B0) < 0) {
            return vals[2].add(n);
        } else {
            return vals[2];
        }
    }

}
