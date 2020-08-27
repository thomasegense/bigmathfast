package dk.teg.bigmathfast;

import java.math.BigInteger;
import java.util.ArrayList;

import ar.alpertron.ecm.Ecm;

public class BigMathFast {

    
    
    public ArrayList<BigInteger> factorizeEmc(BigInteger b){        
        return Ecm.factor(b);
    }
    
    
}
