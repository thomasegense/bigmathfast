package dk.teg.bigmathfast.util;

import java.math.BigInteger;
import java.util.ArrayList;

public class Minimum3Tuppel3SquaresInAPBigNumber {

    ArrayList<Tuppel3SquaresInAPBigNumber> aps;
    BigInteger difference;
    
    
     
    public Minimum3Tuppel3SquaresInAPBigNumber(ArrayList<Tuppel3SquaresInAPBigNumber> aps,BigInteger difference) {
        this.aps=aps;
        this.difference=difference;       
    }

    public ArrayList<Tuppel3SquaresInAPBigNumber> getAps() {
        return aps;
    }

    public void setAps(ArrayList<Tuppel3SquaresInAPBigNumber> aps) {
        this.aps = aps;
    }

    public BigInteger getDifference() {
        return difference;
    }

    public void setDifference(BigInteger difference) {
        this.difference = difference;
    }
    
    
    
}
