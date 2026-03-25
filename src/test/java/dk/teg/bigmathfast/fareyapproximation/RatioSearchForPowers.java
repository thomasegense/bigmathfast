package dk.teg.bigmathfast.fareyapproximation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

public class RatioSearchForPowers {

    /* This is not a perfect triple (doesn't add to 0 - impossible by
    Fermat-Wiles theorem), but a relatively good one to start with. I used
     continued fraction of 2^(1/55) to find a good rational approximation
    of this number, say m/n. Then m^55 is close to 2n^55 and by trial and
  error I can find a good triple of the form m, -n+r, -n-r.

*/



    @Test
    void testnRootDigits() {

        int bestDigit=100000;

        int power=55;

        for (int i=2;i<10000;i++) {

             BigInteger val=new BigInteger(""+i);
            BigRational approxNRoot = approxNRoot(i, power);
            BigInteger nominator=approxNRoot.getNominator();
            BigInteger denominator=approxNRoot.getDenominator();
            BigInteger dif = nominator.pow(power).subtract(new BigInteger(""+i).multiply(denominator.pow(power)));         
            int length=dif.abs().toString().length();

            if (getDigits(nominator) != power || getDigits(denominator) != power) {
                continue;
            }
                
            
         if (length <= bestDigit) {
            System.out.println(denominator);
            System.out.println(nominator);
             System.out.println(i+":"+length);
            bestDigit=length;
         }
        }
    }


    private BigRational approxNRoot(int val,int nRoot) {
        MathContext mc= new MathContext(800);
        BigDecimal b1= new BigDecimal("1");
        BigDecimal valDec= new BigDecimal(""+val);
        BigDecimal root = nthRoot(valDec, nRoot,mc);        
        BigRational r = FareyRationalApproxmation.fareyApproxWithMaxDenom(root, nRoot);
        return r;

    }

    @Test

    void testTemp() {
        MathContext mc= new MathContext(800);
        BigDecimal b1= new BigDecimal("1");
        BigDecimal b2= new BigDecimal("2");
        BigDecimal b55= new BigDecimal("55");
        BigDecimal root = nthRoot(b2, 55,mc);
        System.out.println(root);
        BigRational r = FareyRationalApproxmation.fareyApproxWithMaxDenom(root, 55);
        BigInteger nominator = r.getNominator();
        BigInteger denominator = r.getDenominator();
        System.out.println(r.getDenominator());
        System.out.println(r.getNominator());                

        //3515796511023442395917096631189667743098165919908592009
        //3471766099948234163186042775080100897278599531250245549
        BigInteger dif = nominator.pow(55).subtract(new BigInteger("2").multiply(denominator.pow(55)));
        System.out.println(dif);
    }    



    private static BigDecimal nthRoot(BigDecimal v, int n, MathContext mc) {
        int ROOT_PRECISION = 200;
        BigDecimal res = new BigDecimal(Math.pow(v.doubleValue(), 1.0/n), mc);

        for (int i = 0; i < ROOT_PRECISION; i++) {

            res = res.add(v.divide(res.pow(n-1, mc), mc).subtract(res, mc).divide(new BigDecimal(n), mc), mc);
        }

        return res;

    }

    
    private static int getDigits(BigInteger b) {
        return b.abs().toString().length();
    }
    
}
