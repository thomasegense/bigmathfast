package dk.teg.bigmathfast.ellipticcurves;



import org.apache.commons.math3.fraction.BigFraction;

public class EllipticCurvePoint {

    BigFraction  x;
    BigFraction y;
    
    public EllipticCurvePoint(BigFraction x, BigFraction y) {
        this.x = x;
        this.y = y;
    }

    public BigFraction getX() {
        return x;
    }

    public BigFraction getY() {
        return y;
    }
           
    
    
    
}
