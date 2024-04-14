package dk.teg.bigmathfast.squares;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import dk.teg.bigmathfast.ellipticcurves.EllipticCurve;
import dk.teg.bigmathfast.ellipticcurves.EllipticCurvePoint;
import dk.teg.bigmathfast.util.BigMathFastUtil;

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
    
    private static  BigInteger B0= new BigInteger("0");
    private static  BigInteger B2= new BigInteger("2");
   
    public static APDoubleUnitCircle convertToAPDoubleUnitCircle(Tuppel3SquaresInAPBigNumber tuppel) {                
        BigFraction x=new BigFraction(tuppel.getSmall(),tuppel.getMiddle());
        BigFraction y=new BigFraction(tuppel.getHigh(),tuppel.getMiddle());
        
        APDoubleUnitCircle ap2Circle=new APDoubleUnitCircle(x,y);
        return ap2Circle;        
    }
    

    /**
     * Warning, this will be the AP that can not be divided by common factor,
     * 
     * Example (2175,1540,2665) will become (435,308,533) since 5 is a common factor 
     */
    public static NumberExpressedInSumOfSquares  convertToNumberExpressInSumOfSquares(APDoubleUnitCircle ap2Circle) {                
        
        BigInteger r = ap2Circle.getX().getNumerator().subtract(ap2Circle.getY().getNumerator()).abs().divide(B2);
        BigInteger s = ap2Circle.getX().getNumerator().add(ap2Circle.getY().getNumerator()).abs().divide(B2);
        BigInteger sumSquared=r.multiply(r).add(s.multiply(s));
        
       NumberExpressedInSumOfSquares num = new NumberExpressedInSumOfSquares(r,s,sumSquared);
       return num;   
                      
    }

    public static  APRationalNumber convertToApRational(EllipticCurve ec, EllipticCurvePoint p) {
        
        //test a is negative and square
        if (ec.getA().compareTo(B0)>=0) {
            throw new IllegalArgumentException("a is not negative");           
        }
        //todo validate -a is a square.
        
        BigFraction x=p.getX();
        BigFraction y=p.getY();
        
        BigFraction xx=x.multiply(x);
        BigFraction y2=y.multiply(new BigInteger("2"));
        
        BigInteger nn=ec.getA().multiply(new BigInteger("-1"));
        BigInteger n=BigMathFastUtil.bigintroot(nn);       
        BigFraction nx2=x.multiply(n).multiply(B2);
        
        //Create the 3 numenator and 3 denumeators for the rational AP
        BigFraction n1= xx.subtract(nx2).subtract(nn);
        BigFraction d1=y2;
        
        
        BigFraction n2= xx.add(nn);
        BigFraction d2=y2;
        
        
        BigFraction n3= new BigFraction(nn).subtract(xx).subtract(nx2);
        BigFraction d3=y2;
       
        APRationalNumber ap = new APRationalNumber(n1.divide(d1),n2.divide(d2),n3.divide(d3));
        return ap;
        
    }
      
    
    public static Tuppel3SquaresInAPBigNumber convertToTuppel3APBigNumber(EllipticCurve ec, EllipticCurvePoint p) {
        APRationalNumber ap_rat = convertToApRational(ec,p);
        
        //Sanity check all denumenators are identical
        if ( !(ap_rat.getA().getDenominator().equals(ap_rat.getB().getDenominator())) || 
             !(ap_rat.getB().getDenominator().equals(ap_rat.getC().getDenominator())) ) {
            System.out.println("logic error, not same denominator:"+ap_rat);
            throw new IllegalArgumentException("logic error, not same denominator:\"+ap_rat");
        }
                    
        BigInteger small=ap_rat.getA().getNumerator().abs();
        BigInteger middle=ap_rat.getB().getNumerator().abs();
        BigInteger high=ap_rat.getC().getNumerator().abs();
        
        BigInteger diff=(middle.pow(2)).subtract(small.pow(2));               
        Tuppel3SquaresInAPBigNumber tup = new Tuppel3SquaresInAPBigNumber(small,middle,high,diff);                           
        return tup;              
    }
    
    
}
