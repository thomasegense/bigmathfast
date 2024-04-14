package dk.teg.bigmathfast.ellipticcurves;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.squares.APDoubleUnitCircle;
import dk.teg.bigmathfast.squares.APRationalNumber;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareAPConversion;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;

public class EllipticCurvesTest {

    
    @Test
    public void testEllipticCurve() {
     
     //curve is y^2=x^3-(24*24)x    (b=0)   
     int a  =-24*24;         
     EllipticCurve ec= new EllipticCurve(new BigInteger(""+a),new BigInteger("0")); 
          
     
     //validate (-12,72) is on the curve
     EllipticCurvePoint p = new EllipticCurvePoint(new BigFraction(-12),new BigFraction(72));         
     assertTrue(ec.validatePointOnCurve(p));
     
     APRationalNumber apRational_p = SquareAPConversion.convertToApRational(ec, p); //this should be (1,5,7)
     assertEquals("1", apRational_p.getA().toString());
     assertEquals("5", apRational_p.getB().toString());
     assertEquals("7", apRational_p.getC().toString());
     
     
     
     
     
     //validate (-2,3) is NOT on the curve
     EllipticCurvePoint p_invalid = new EllipticCurvePoint(new BigFraction(-21),new BigFraction(3));
     assertFalse(ec.validatePointOnCurve(p_invalid));
     
     //Validate double method
      EllipticCurvePoint pp= ec.doublePoint(p); //(25,-35)
     assertEquals("25",pp.getX().toString());
     assertEquals("-35" ,pp.getY().toString());

     
     //
     APRationalNumber apRational_pp = SquareAPConversion.convertToApRational(ec, pp); //this should be (1151/70,-1201/70,1249/70)
     System.out.println(apRational_pp);
     assertEquals("1151", apRational_pp.getA().getNumerator().toString());
     assertEquals("70", apRational_pp.getA().getDenominator().toString());     
     assertEquals("-1201", apRational_pp.getB().getNumerator().toString());
     assertEquals("70", apRational_pp.getB().getDenominator().toString());
     assertEquals("1249", apRational_pp.getC().getNumerator().toString());
     assertEquals("70", apRational_pp.getC().getDenominator().toString());
     
     
     //Validate general add method will use special case for same P.
     EllipticCurvePoint p4=ec.addPoints(p, p);
     assertEquals(p4.getX().toString(),"25");
     assertEquals(p4.getY().toString(),"-35");
     
     //Test 2P+P now, two different values     
     EllipticCurvePoint ppp=ec.addPoints(p, pp);// 3p=(-6348/1369,-2568456/50653)     
    
     assertEquals("-6348",ppp.getX().getNumerator().toString());
     assertEquals("1369",ppp.getX().getDenominator().toString());
     assertEquals("-2568456",ppp.getY().getNumerator().toString());
     assertEquals("50653",ppp.getY().getDenominator().toString());
         
    }
    

    @Test
    public void testTemp() {
     
        //curve is y^2=x^3-(24*24)x    (b=0)   
        int a  =-24*24;         
        EllipticCurve ec= new EllipticCurve(new BigInteger(""+a),new BigInteger("0")); 
             
        
        //validate (-12,72) is on the curve
        EllipticCurvePoint p = new EllipticCurvePoint(new BigFraction(-12),new BigFraction(72));         

        EllipticCurvePoint p_cycle=p;
        for (int i=0;i<10;i++) {
                    
            assertTrue(ec.validatePointOnCurve(p));
            p_cycle=ec.addPoints(p_cycle, p);               
            Tuppel3SquaresInAPBigNumber tup = SquareAPConversion.convertToTuppel3APBigNumber(ec, p_cycle);
            System.out.println("point:"+p_cycle +" AP rat:"+tup);
        }
        
           
        
    }
    
    @Test
    public void testTemp2() {
     //get all AP and  map to circle to see pattern
        ArrayList<NumberExpressedInSumOfSquares> allAPofSquares = SquareUtil.getAllAPofSquares(new BigInteger("2665"));
        Minimum3Tuppel3SquaresInAPBigNumber bestDiffTuppels = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(allAPofSquares);

        /*
        for (NumberExpressedInSumOfSquares n:allAPofSquares) {
            APDoubleUnitCircle ap2Circle = SquareAPConversion.convertToAPDoubleUnitCircle(n.getAPBigNumber());
            System.out.println(ap2Circle+"  AP:"+n);
        }
        */
        //Best diffs:
       // APDoubleUnitCircle [x=23 / 65, y=89 / 65] approx:[x=0.35384615384615387, y=1.3692307692307693] , length=2  AP:(952,561,1105)
       // APDoubleUnitCircle [x=1057 / 1105, y=1151 / 1105] approx:[x=0.9565610859728507, y=1.0416289592760182] , length=2  AP:(1104,47,1105)
       //APDoubleUnitCircle [x=1 / 5, y=7 / 5] approx:[x=0.2, y=1.4] , length=2  AP:(884,663,1105)
         //       System.out.println(bestDiffTuppels.getAps());
        
                
                for (NumberExpressedInSumOfSquares n:bestDiffTuppels.getAps()) {
                    APDoubleUnitCircle ap2Circle = SquareAPConversion.convertToAPDoubleUnitCircle(n.getAPBigNumber());                    
                    NumberExpressedInSumOfSquares n_converted = SquareAPConversion.convertToNumberExpressInSumOfSquares(ap2Circle);                    
                    System.out.println("org n:"+n);
                    System.out.println("ap2Circle:"+ap2Circle);
                    System.out.println("n_converted:"+n_converted);
                    
                    
                }
    
    }
}


