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
    private BigInteger a;
    private BigInteger b;
    
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
    
    public BigInteger getA() {
        return a;
    }

    public BigInteger getB() {
        return b;
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

    
 
   
}



