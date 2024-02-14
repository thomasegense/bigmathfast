package dk.teg.bigmathfast.squares;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.primes.MillerRabin;


public class ApSquaresSpectralVisualization {

    final static BigInteger B1 = new BigInteger("1");
    //Let p = (a²+b²) and q=(c²+d²). Then it can be checked pq=(ac-bd)²+(ad+bc)²=(ac+bd)²+(ad-bc)²,
    public static void main(String[] args) {
        //
        ///BigInteger number = new BigInteger("19742462232025");        
     /*
        BigInteger number = new BigInteger(""+551713585);
         printBestMatch(number);
         System.exit(1);
       */  
        BigInteger number = new BigInteger(""+132984460015L*73L);
        
        
        //BigInteger number = new BigInteger("160401041545");  // this is the monster hit!
      //BigInteger number = new BigInteger(""+62921*1105);  // this is the mega monster hit!
      //  BigInteger number = new BigInteger("19742462232025"); //Largest known hit     
    //   BigInteger number = new BigInteger("13258873225"); //above with 1489 divived out
        
        
       for (int i =1;i<1000000;i++) {
           int maybePrime=4*i+1;
           BigInteger maybePrimeBig = new BigInteger(""+maybePrime);
           
           if (new MillerRabin(maybePrimeBig,20).isPrime()) {
           //   System.out.println("trying:"+maybePrime);
               BigInteger numberTest=number.multiply(maybePrimeBig);
               printBestMatch(numberTest);
           }


           
       }
       

    
}
    
    public static void printBestMatch(BigInteger number) {
        
        
        ArrayList<BigInteger> factors = BigMathFast.factorize(number);
        
        factors.addAll(factors);
        
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);
        ArrayList<BigInteger> lines = new ArrayList<BigInteger>();
        
        for (NumberExpressedInSumOfSquares n : apSquares) {            
            lines.add(n.getAPBigNumber().getDifference());
            
        }
        lines.remove(0); //The trivial. Now first
        
        
        Collections.sort(lines);
        
        for (BigInteger line : lines) {
//            System.out.println(line);
            
        }
        
        //assertEquals(1823,apSquares.size());        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        BigInteger ratio=number.divide(best3MatchAps.getDifference());
        if (ratio.compareTo(B1)>=0) {
        
        System.out.println("best diff:");
        System.out.println("factors:"+factors);        
        System.out.println("number:"+number);
        System.out.println(best3MatchAps.getDifference() +" ratio:"+ratio);
        }
        
        
    }
    
    /*
     * Do brute force match. Very slow
     */
    public void testBestMatchAlgorithmCorrect() {
        BigInteger number = new BigInteger("19742462232025"); //Largest know hit     
        
        ArrayList<BigInteger> factors = BigMathFast.factorize(number);
        System.out.println(factors);
        factors.addAll(factors);
        
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);
        ArrayList<BigInteger> lines = new ArrayList<BigInteger>();
        
        for (NumberExpressedInSumOfSquares n : apSquares) {            
            lines.add(n.getAPBigNumber().getDifference());
            
        }
        lines.remove(0); //The trivial. Now first
        
        
        Collections.sort(lines);
        //assertEquals(1823,apSquares.size());        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        System.out.println("best diff:");
        System.out.println(best3MatchAps.getDifference() +" ratio:"+number.divide(best3MatchAps.getDifference()));
        
        System.out.println("slow best match starting...");
        
        BigInteger best= new BigInteger("999999999999999999999999999999999999999999999999999999");
        for (int i =0;i<lines.size()-2;i++) {
            for (int j =i+1;j<lines.size()-1;j++) {
                for (int k =j+1;k<lines.size();k++) {
                    BigInteger diff = (lines.get(k).subtract(lines.get(j)).subtract(lines.get(i))).abs();
                    
                    if (diff.compareTo(best)<0) {
                        best=diff;
                        System.out.println("new best:"+best);                        
                    }                   
                }   
            }            
        }
        System.out.println("slow best match finished. Best:"+best);
        //Best match from algorithm is 32509509696 
        
        
    }
    
    
}
