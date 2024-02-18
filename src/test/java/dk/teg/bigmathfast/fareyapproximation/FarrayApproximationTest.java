package dk.teg.bigmathfast.fareyapproximation;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.BigMathFast;

import java.math.BigDecimal;
import java.math.BigInteger;



public class FarrayApproximationTest {

    BigDecimal PI = BigMathFast.PI;            
    
    
    @Test
    void testPiRationalApprox() {
                        
        /*
         Using these values, which are best approximations in growing order of denominator
             
        |----------------+----------|
        | Fraction       | Decimals |
        |----------------+----------|
        | 3              |      0.8 |
        | 22/7           |      2.9 |
        | 333/106        |      4.1 |
        | 355/113        |      6.6 |
        | 103993/33102   |      9.2 |
        | 104348/33215   |      9.5 |
        | 208341/66317   |      9.9 |
        | 312689/99532   |     10.5 |
        | 833719/265381  |     11.1 |
        | 1146408/364913 |     11.8 |
        |----------------+----------|
        */
                       
        // 22/7
        BigRational rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("7"));

        assertEquals(22,rat.getNominator().intValue());
        assertEquals(7,rat.getDenominator().intValue());              
          
        // 22/7  (still!)
        rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("100"));
        assertEquals(22,rat.getNominator().intValue());
        assertEquals(7,rat.getDenominator().intValue());              
         
        
        // 333/106
        rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("110"));
        assertEquals(333,rat.getNominator().intValue());
        assertEquals(106,rat.getDenominator().intValue());
                  
        
        // 355/113        
         rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("999"));
         assertEquals(355,rat.getNominator().intValue());
         assertEquals(113,rat.getDenominator().intValue());
    
        
        //  103993/33102       
         rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("33102"));
         assertEquals(103993,rat.getNominator().intValue());
         assertEquals(33102,rat.getDenominator().intValue());
         
         // 104348/33215        
          rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("33215"));
          assertEquals(104348,rat.getNominator().intValue());
          assertEquals(33215,rat.getDenominator().intValue());
              
          // 1146408/364913
         rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, new BigInteger("364913"));
         assertEquals(1146408,rat.getNominator().intValue());
         assertEquals(364913,rat.getDenominator().intValue());
     
         //Test the method with maximum digits
         
         // 22/7        
         rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, 2);
         assertEquals(22,rat.getNominator().intValue());
         assertEquals(7,rat.getDenominator().intValue());

         
         // 355/113        
          rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, 3);
          assertEquals(355,rat.getNominator().intValue());
          assertEquals(113,rat.getDenominator().intValue());
     
          //312689/99532:                 
          rat = FareyRationalApproxmation.fareyApproxWithMaxDenom(PI, 5);
          assertEquals(312689,rat.getNominator().intValue());
          assertEquals(99532,rat.getDenominator().intValue());
               
    }
                
}

