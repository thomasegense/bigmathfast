
package dk.teg.bigmathfast.squares;

import java.math.BigInteger;


public class Tuppel3SquaresInAPBigNumber {

       
        BigInteger  small;
        BigInteger  middle;
        BigInteger  high;
        BigInteger  difference;
        
       
        public Tuppel3SquaresInAPBigNumber(BigInteger small, BigInteger middle,
                        BigInteger high, BigInteger difference) {
                super();
                this.small = small;
                this.middle = middle;
                this.high = high;
                this.difference = difference;
        }
        
        public BigInteger getSmall() {
                return small;
        }
        public void setSmall(BigInteger small) {
                this.small = small;
        }
        public BigInteger getMiddle() {
                return middle;
        }
        public void setMiddle(BigInteger middle) {
                this.middle = middle;
        }
        public BigInteger getHigh() {
                return high;
        }
        public void setHigh(BigInteger high) {
                this.high = high;
        }
        public BigInteger getDifference() {
                return difference;
        }
        public void setDifference(BigInteger difference) {
                this.difference = difference;
        }
                   
		public String toString(){
                return "("+ small+","+middle+","+high+":"+difference+")";
        }

        public BigInteger getMagicSum(){
                return small.multiply(small).add(middle.multiply(middle)).add(high.multiply(high));
               
        } 
        
}