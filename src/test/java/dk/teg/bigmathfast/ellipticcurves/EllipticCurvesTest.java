package dk.teg.bigmathfast.ellipticcurves;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Test;

public class EllipticCurvesTest {

    
    @Test
    public void testEllipticCurve() {
     
     //curve is y^2=x^3-(24*24)x    (b=0)   
     int a  =-24*24;         
     EllipticCurve ec= new EllipticCurve(new BigInteger(""+a),new BigInteger("0")); 
          
     
     //validate (-12,72) is on the curve
     EllipticCurvePoint p = new EllipticCurvePoint(new BigFraction(-12),new BigFraction(72));         
     assertTrue(ec.validatePointOnCurve(p));
     
     //validate (-2,3) is NOT on the curve
     EllipticCurvePoint p_invalid = new EllipticCurvePoint(new BigFraction(-21),new BigFraction(3));
     assertFalse(ec.validatePointOnCurve(p_invalid));
     
     //Validate double method
      EllipticCurvePoint pp= ec.doublePoint(p); //(25,-35)
     assertEquals("25",pp.getX().toString());
     assertEquals("-35" ,pp.getY().toString());

     //Validate general add method will use special case for same P.
     EllipticCurvePoint p4=ec.addPoints(p, p);
     assertEquals(p4.getX().toString(),"25");
     assertEquals(p4.getY().toString(),"-35");
     
     //Test 2P+P now, two different values
     
     EllipticCurvePoint ppp=ec.addPoints(p, pp);// 3p=(-6348/1369,-2568456/50653)
     System.out.println("3P="+ppp);
    
     assertEquals("-6348",ppp.getX().getNumerator().toString());
     assertEquals("1369",ppp.getX().getDenominator().toString());
     assertEquals("-2568456",ppp.getY().getNumerator().toString());
     assertEquals("50653",ppp.getY().getDenominator().toString());
     
     
     
     
     
   
     
     
        
    }
    
}
