package dk.teg.bigmathfast.ellipticcurves;


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
     EllipticCurvePoint p1 = new EllipticCurvePoint(new BigFraction(-12),new BigFraction(72));         
     assertTrue(ec.validatePointOnCurve(p1));
     
     //validate (-2,3) is NOT on the curve
     EllipticCurvePoint p2 = new EllipticCurvePoint(new BigFraction(-21),new BigFraction(3));
     assertFalse(ec.validatePointOnCurve(p2));
       
     EllipticCurvePoint p3= ec.doublePoint(p1);
     System.out.println(p3.getX() +","+p3.getY());
     
        
    }
    
}
