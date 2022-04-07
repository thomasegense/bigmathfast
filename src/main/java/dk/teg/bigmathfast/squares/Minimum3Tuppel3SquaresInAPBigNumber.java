package dk.teg.bigmathfast.squares;

import java.math.BigInteger;
import java.util.ArrayList;

public class Minimum3Tuppel3SquaresInAPBigNumber {

    ArrayList<NumberExpressedInSumOfSquares> aps;
    BigInteger difference;
    
    
     
    public Minimum3Tuppel3SquaresInAPBigNumber(ArrayList<NumberExpressedInSumOfSquares> aps,BigInteger difference) {
        this.aps=aps;
        this.difference=difference;       
    }

    public ArrayList<NumberExpressedInSumOfSquares> getAps() {
        return aps;
    }

    public void setAps(ArrayList<NumberExpressedInSumOfSquares> aps) {
        this.aps = aps;
    }

    public BigInteger getDifference() {
        return difference;
    }

    public void setDifference(BigInteger difference) {
        this.difference = difference;
    }
    
    public BigInteger getRatio() {
        return aps.get(0).getNroot().divide(difference);
    }
    
}
