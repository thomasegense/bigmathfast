package dk.teg.bigmathfast.squares;

import java.math.BigInteger;

import dk.teg.bigmathfast.util.SquareUtil;




//r^2+s^2=n , r<=s
public class NumberExpressedInSumOfSquares {

      public final static BigInteger B2= new BigInteger("2");
     
      public BigInteger r;
      public BigInteger s;
      public BigInteger n;
      private  BigInteger nSqroot;
     
      public NumberExpressedInSumOfSquares(BigInteger sumFactor1, BigInteger sumFactor2,
                      BigInteger n) {        
             
              sumFactor1=sumFactor1.abs();
              sumFactor2=sumFactor2.abs();
             
              if (sumFactor1.compareTo(sumFactor2)>0){
                      r=sumFactor1;
                      s=sumFactor2;
              }
              else{
                      r=sumFactor2;//reverse
                      s=sumFactor1;                                        
              }
             
             
              this.n = n;
          this.nSqroot=SquareUtil.sqrt(n);
     
      }
                 
      public BigInteger getNroot() {
          return nSqroot;
  }
      
      public BigInteger getR() {
              return r;
      }

      public void setR(BigInteger r) {
              this.r = r;
      }

      public BigInteger getS() {
              return s;
      }

      public void setS(BigInteger s) {
              this.s = s;
      }

      public BigInteger getN() {
              return n;
      }

      public void setN(BigInteger n) {
              this.n = n;
      }
     
      public Tuppel3SquaresInAPBigNumber getAPBigNumber(){
              return new Tuppel3SquaresInAPBigNumber(r.subtract(s),nSqroot,r.add(s),B2.multiply(r.multiply(s)));
             
      }

     
     
      @Override
      public int hashCode() {
              final int prime = 31;
              int result = 1;
              result = prime * result + ((n == null) ? 0 : n.hashCode());
              result = prime * result + ((r == null) ? 0 : r.hashCode());
              result = prime * result + ((s == null) ? 0 : s.hashCode());
              return result;
      }

      @Override
      public boolean equals(Object obj) {
              if (this == obj)
                      return true;
              if (obj == null)
                      return false;
              if (getClass() != obj.getClass())
                      return false;
              NumberExpressedInSumOfSquares other = (NumberExpressedInSumOfSquares) obj;
              if (n == null) {
                      if (other.n != null)
                              return false;
              } else if (!n.equals(other.n))
                      return false;
              if (r == null) {
                      if (other.r != null)
                              return false;
              } else if (!r.equals(other.r))
                      return false;
              if (s == null) {
                      if (other.s != null)
                              return false;
              } else if (!s.equals(other.s))
                      return false;
              return true;
      }
     
     
      public String toString(){
          return "("+r+","+s+","+nSqroot+")";
  }
     
     
     
} 
