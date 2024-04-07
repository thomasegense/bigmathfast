package dk.teg.bigmathfast.ellipticcurves;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;

import dk.teg.bigmathfast.squares.APRationalNumber;

public class EllipticCurvesTest {

    
    @Test
    public void testEllipticCurve() {
     
     //curve is y^2=x^3-(24*24)x    (b=0)   
     int a  =-24*24;         
     EllipticCurve ec= new EllipticCurve(new BigInteger(""+a),new BigInteger("0")); 
          
     
     //validate (-12,72) is on the curve
     EllipticCurvePoint p = new EllipticCurvePoint(new BigFraction(-12),new BigFraction(72));         
     assertTrue(ec.validatePointOnCurve(p));
     
     APRationalNumber apRational_p = ec.convertToApRational(p); //this should be (1,5,7)
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
     APRationalNumber apRational_pp = ec.convertToApRational(pp); //this should be (1151/70,-1201/70,1249/70)
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
    
}
