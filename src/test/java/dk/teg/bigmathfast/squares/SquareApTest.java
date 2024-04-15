package dk.teg.bigmathfast.squares;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.primes.MillerRabin;
import dk.teg.bigmathfast.util.BigMathFastUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;



public class SquareApTest{

    BigDecimal PI = BigMathFast.PI;            

    
    /*
    The unittest will test the logic below

    Factorizing:1105
    Wait for factorization to finish...
    Factorization completed
    Prime factors:[5, 13, 17]

    1105 can be expresses as sum of two squares in 4 different ways:
    1105=4^2 + 33^2
    1105=9^2 + 32^2
    1105=12^2 + 31^2
    1105=23^2 + 24^2

    1105^2 appear in 13 different arithmetric progressions of squares as middle number:
    (367^2 , 1105^2 , 1519^2) with step value:1086336
    (809^2 , 1105^2 , 1337^2) with step value:566544
    (73^2 , 1105^2 , 1561^2) with step value:1215696
    (455^2 , 1105^2 , 1495^2) with step value:1014000
    (391^2 , 1105^2 , 1513^2) with step value:1068144
    (995^2 , 1105^2 , 1205^2) with step value:231000
    (1057^2 , 1105^2 , 1151^2) with step value:103776
    (799^2 , 1105^2 , 1343^2) with step value:582624
    (221^2 , 1105^2 , 1547^2) with step value:1172184
    (155^2 , 1105^2 , 1555^2) with step value:1197000
    (923^2 , 1105^2 , 1261^2) with step value:369096
    (533^2 , 1105^2 , 1469^2) with step value:936936
    (595^2 , 1105^2 , 1445^2) with step value:867000

    //And 14 with the trivial (1105^2 , 1105^2 , 1105^2) with step value:0.  

    Next find the 3 AP such that difference added of 2 of them is as close to another difference
    264 is best difference 
    And this happens for the 3 AP (where without the power 2 notation)
    (391,1105,1513:1068144)
    (1057,1105,1151:103776)
    (221,1105,1547:1172184)
    1172184 - (1068144+103776) = 264

    */

    @Test
    void testCreateAllNumberExpressedInSumOfSquares() {

        //All primes must be   ==1 (mod 4)!!
        BigInteger number = new BigInteger("1105");

        ArrayList<BigInteger> factors = BigMathFast.factorize(number);


        BigInteger product= new BigInteger("1");

        for (BigInteger factor : factors) {            
            product =product.multiply(factor);
        }

        assertEquals(product,number);


        //Test the 4 was to express 1105 as sum of squares
        ArrayList<NumberExpressedInSumOfSquares> squareSum = SquareUtil.getAllAPofSquares(factors);
        assertEquals(4 , squareSum.size());                       
        for (NumberExpressedInSumOfSquares squares : squareSum) {                    
            BigInteger sum = ( squares.getR().multiply(squares.getR())).add(squares.getS().multiply(squares.getS()) );        
            assertEquals(product , sum);                    
        }



        factors.addAll(factors); 
        //Transform into AP with middle number 1105*1105

        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);

        assertEquals(14,apSquares.size());

        // Test the AP of squares about.
        for (NumberExpressedInSumOfSquares apSquare :apSquares) {                                
            Tuppel3SquaresInAPBigNumber ap = apSquare.getAPBigNumber();
            ///       System.out.println(ap);

            BigInteger lowDiff = ap.getMiddle().multiply(ap.getMiddle()).subtract(ap.getSmall().multiply(ap.getSmall()));
            BigInteger highDiff = ap.getHigh().multiply(ap.getHigh()).subtract(ap.getMiddle().multiply(ap.getMiddle()));
            assertEquals(highDiff,ap.getDifference());
            assertEquals(lowDiff,ap.getDifference());                            
        }

        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);                             
        assertEquals(new BigInteger("264"),best3MatchAps.getDifference());

        TreeSet<BigInteger> diffs = new TreeSet<BigInteger>(); 

        for (NumberExpressedInSumOfSquares tup : best3MatchAps.getAps() ) {
            diffs.add(tup.getAPBigNumber().getDifference());                                    
        }
        //they are now sorted.
        Iterator<BigInteger> it = diffs.iterator();

        BigInteger difCalculated = it.next().add(it.next().subtract(it.next()));
        difCalculated=difCalculated.abs(); //is negative
        assertEquals(new BigInteger("264"),difCalculated);                      
    }


    

    @Test
    void  testBestMinimumDifference() {
        //trest the optimized bi-section method to find minmimum difference is correct by comparing to a brute force        
        //TODO
    }
    
    @Test
    void  testRatio() {

        //All prime factors == 1 mod 4
        //This number has an exceptional low difference for its size.
        BigInteger number = new BigInteger("166193842205"); //the mega monster

        ArrayList<BigInteger> factors = BigMathFast.factorize(number);

        factors.addAll(factors);

        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);

        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        assertEquals(new BigInteger("40551216"), best3MatchAps.getDifference());
        assertEquals(new BigInteger("4098"), best3MatchAps.getRatio());

    }


    //Temp test
    @Test
    void  testLargeNumberEc() {

        //All prime factors == 1 mod 4
        //This number has an exceptional low difference for its size.
        

        ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
        factors.add(new BigInteger("5"));
        factors.add(new BigInteger("1013"));
        factors.add(new BigInteger("35597"));
        factors.add(new BigInteger("17121457"));
        factors.add(new BigInteger("24406569079020707833"));
        factors.add(new BigInteger("110048847414305387604721"));
        factors.add(new BigInteger("19216522980404357294903148100873"));                
        
        factors.addAll(factors);

        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);
        System.out.println(apSquares.size());
        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        double q=SquareUtil.calculateQuality(best3MatchAps.getDifference(), best3MatchAps.getAps().get(0).getAPBigNumber().getMiddle());
        System.out.println(q);
        
    }


    
    @Test
    void  testLargeNumber() {

        //All prime factors == 1 mod 4
        //This number has an exceptional low difference for its size.
        BigInteger number = new BigInteger("19742462232025");

        ArrayList<BigInteger> factors = BigMathFast.factorize(number);
        System.out.println(factors);
        factors.addAll(factors);

        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);
        assertEquals(1823,apSquares.size());        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        assertEquals(new BigInteger("32509509696"), best3MatchAps.getDifference());
    }



    @Test
    void  testCombineAPs() {
        ArrayList<BigInteger> factors = new  ArrayList<BigInteger>();
        factors.add(new BigInteger("5"));
        factors.add(new BigInteger("13"));
        factors.addAll(factors);


        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);   
        System.out.println(apSquares);   
        DecomposedPrime p1 = DecomposedPrime.create(new BigInteger("17"));
        NumberExpressedInSumOfSquares newOne = p1.getNumberExpressedInSumOfSquares();


        //Must combine twice. Maybe do this is method.
        ArrayList<NumberExpressedInSumOfSquares> combined = SquareUtil.combineNumberExpressedInSumOfSquaresMultiple(apSquares, newOne);
        combined = SquareUtil.combineNumberExpressedInSumOfSquaresMultiple(combined, newOne);
        Minimum3Tuppel3SquaresInAPBigNumber tup = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(combined);

        assertEquals(new BigInteger("264"),tup.getDifference());

    }

  
    @Test
    void  testCalculateQualityQuality() {
        BigInteger number = new BigInteger("1885");
        assertEquals(SquareUtil.calculateQuality(new BigInteger("120"), number),1.5753d);       
    }



    @Test
    void  testApSquaresQuality() {
        BigInteger number = new BigInteger("1885");
        double q= SquareUtil.calculateQualityForAPSquares(number);
        assertEquals(1.5753d,q);

        BigInteger number2 = new BigInteger("15434605016465"); // Has  365 different APs
        double q2= SquareUtil.calculateQualityForAPSquares(number2);
        assertEquals(1.3694d,q2);               
    }






}

