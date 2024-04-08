package dk.teg.bigmathfast.squares;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * Class that transform between the 3 different representations of an AP of squares
 *  
 *  <ul>
 *  <li>Ap of squares. Example  1^2 , 5^2, 7^2</li> 
 *  <li>Rational points on the circle x*2+y*2=2</li>
 *  <li>Rational points on the elliptic curve y^2=x^3-(n^2)x. n^2 is the difference(step values) of the AP.  </li>
 *
 * </ul>
 * TODO example with 1^2,5^2,7^2 transformed to all types
 * 
 */
public class SquareAPConversion {

    
    public static APDoubleUnitCircle convertToAPDoubleUnitCircle(Tuppel3SquaresInAPBigNumber tuppel) {
         
        
        BigFraction x=new BigFraction(tuppel.getSmall(),tuppel.getMiddle());
        BigFraction y=new BigFraction(tuppel.getHigh(),tuppel.getMiddle());
        
        APDoubleUnitCircle ap2Circle=new APDoubleUnitCircle(x,y);
        return ap2Circle;        
    }
    
}
