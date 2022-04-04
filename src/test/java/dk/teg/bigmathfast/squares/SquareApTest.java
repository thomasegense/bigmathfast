package dk.teg.bigmathfast.squares;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.util.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.util.SquareUtil;
import dk.teg.bigmathfast.util.Tuppel3SquaresInAPBigNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;



public class SquareApTest{

    BigDecimal PI = BigMathFast.PI;            
    
    
    @Test
    void testCreateAllNumberExpressedInSumOfSquares() {
        
        
        
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
              
              
264 is best difference              
              
              
 */

        
       //All primes must be   ==1 (mod 4)!!
        BigInteger number = new BigInteger("1105");
        
        ArrayList<BigInteger> factors = BigMathFast.factorize(number);
                       
        
        BigInteger product= new BigInteger("1");
        
        for (BigInteger factor : factors) {            
            product =product.multiply(factor);
        }
        
        assertEquals(product,number);
        
       
        //Test the 4 was to express 1105 as sum of squares
        ArrayList<NumberExpressedInSumOfSquares> squareSum = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);
        assertEquals(4 , squareSum.size());                       
        for (NumberExpressedInSumOfSquares squares : squareSum) {                    
          BigInteger sum = ( squares.getR().multiply(squares.getR())).add(squares.getS().multiply(squares.getS()) );        
           assertEquals(product , sum);                    
        }

               
        
        factors.addAll(factors); 
        //Transform into AP with middle number 1105*1105
                
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);
        
         // Test the AP of squares about.
         for (NumberExpressedInSumOfSquares apSquare :apSquares) {                                
             Tuppel3SquaresInAPBigNumber ap = apSquare.getAPBigNumber();
             
             BigInteger lowDiff = ap.getMiddle().multiply(ap.getMiddle()).subtract(ap.getSmall().multiply(ap.getSmall()));
             BigInteger highDiff = ap.getHigh().multiply(ap.getHigh()).subtract(ap.getMiddle().multiply(ap.getMiddle()));
             assertEquals(highDiff,ap.getDifference());
             assertEquals(lowDiff,ap.getDifference());                            
         }

        BigInteger minimum = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
        
          assertEquals(new BigInteger("264"),minimum);
        
 
        
        
        
    }
                
}

