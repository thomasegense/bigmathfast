package dk.teg.bigmathfast.squares;

import org.apache.commons.math3.fraction.BigFraction;

public class APDoubleUnitCircle {

    private BigFraction x;
    private BigFraction y;
    
    
    public APDoubleUnitCircle(BigFraction x, BigFraction y) {        
        this.x=x;
        this.y=y;
    }


    @Override
    public String toString() {
        BigFraction length=(x.multiply(x)).add(y.multiply(y));//sanity check
        
        return "APDoubleUnitCircle [x=" + x + ", y=" + y + "] approx:[x=" + x.doubleValue() + ", y=" + y.doubleValue() + "] , length="+length;
    }
    
    
    
}
