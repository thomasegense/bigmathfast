package dk.teg.bigmathfast.ellipticcurves;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.math3.fraction.BigFraction;

import dk.teg.bigmathfast.squares.APRationalNumber;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.util.BigMathFastUtil;

//see https://www.wikiwand.com/en/Elliptic_curve_point_multiplication
public class EllipticCurve {

    private static BigInteger B0=new BigInteger("0");
    private static BigInteger B2=new BigInteger("2");
    BigInteger a;
    BigInteger b;
    
    /**
     * Elliptic curve on the form : y^2 = x^3 + ax + b  (over rationals Q)
     * 
     * 
     * @param a
     * @param b
     */
    
    public EllipticCurve(BigInteger a, BigInteger b) {
       this.a=a;
       this.b=b;       
    }
    
    /**
     * Validate the point is on the curve.
     * 
     * @param p EllipticCurve point. 
     * 
     * @return true if point is on the curve
     */
    public boolean validatePointOnCurve(EllipticCurvePoint p) {
        
        BigFraction x= p.getX();
        BigFraction y= p.getY();
        BigFraction a_dec= new BigFraction(a);
        BigFraction b_dec= new BigFraction(b,new BigInteger("1"));
        
        BigFraction rightSide= x.pow(3).add(x.multiply(a_dec)).add(b_dec);
        BigFraction leftSide= y.pow(2);
                
        return rightSide.equals(leftSide);                        
    }
    
   /**
    * This will add a point p to it self. P+P=2P. You can not use the normal addition rule for same point
    * 
    * @param p Point on the elliptic curve
    * @return The result of p+p
    */
    
    public EllipticCurvePoint doublePoint(EllipticCurvePoint p) {
        BigFraction x= p.getX();
        BigFraction y= p.getY();
        
        BigFraction lambaNumerator= x.pow(2).multiply(new BigInteger("3")).add(a); 
        BigFraction lambaDenumerator = y.multiply(new BigInteger("2"));
        BigFraction lamba= lambaNumerator.divide(lambaDenumerator);
        
        
        BigFraction newX=lamba.pow(2).subtract(x.multiply(new BigInteger("2")));
        BigFraction newY=lamba.multiply(x.subtract(newX)).subtract(y);
        
        return new EllipticCurvePoint(newX, newY);
    }

    
    
    /**
     * This will add a point p to it self. P+P=2P. You can not use the normal addition rule for same point
     * 
     * @param p Point on the elliptic curve
     * @return The result of p+p
     */
     
     public EllipticCurvePoint addPoints(EllipticCurvePoint P,EllipticCurvePoint Q) {
         
         if (P.equals(Q)){
             return doublePoint(P);//same point is special case
         }
         
         BigFraction x_p= P.getX();
         BigFraction y_p= P.getY();
         
         BigFraction x_q= Q.getX();
         BigFraction y_q= Q.getY();
         
         BigFraction lambaNumerator= y_q.subtract(y_p);         
         BigFraction lambaDenumerator = x_q.subtract(x_p);
         BigFraction lamba= lambaNumerator.divide(lambaDenumerator);
         
         
         BigFraction x_r=lamba.pow(2).subtract(x_p).subtract(x_q);         
         
         BigFraction y_r=lamba.multiply(x_p.subtract(x_r)).subtract(y_p);
         
         return new EllipticCurvePoint(x_r, y_r);
     }

    
   public APRationalNumber convertToApRational(EllipticCurvePoint p) {
       
       //test a is negative and square
       if (a.compareTo(B0)>=0) {
           throw new IllegalArgumentException("a is not negative");           
       }
       //todo validate -a is a square.
       
       BigFraction x=p.getX();
       BigFraction y=p.getY();
       
       BigFraction xx=x.multiply(x);
       BigFraction y2=y.multiply(new BigInteger("2"));
       
       BigInteger nn=a.multiply(new BigInteger("-1"));
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
     
   
   public Tuppel3SquaresInAPBigNumber convertToTuppel3APBigNumber(EllipticCurvePoint p) {
       APRationalNumber ap_rat = convertToApRational(p);
       
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



