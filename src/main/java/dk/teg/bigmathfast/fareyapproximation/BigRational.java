package dk.teg.bigmathfast.fareyapproximation;

import java.math.BigInteger;

public class BigRational {

  private BigInteger nominator;
  private  BigInteger denominator;
  
  
  
  public BigRational(BigInteger nominator, BigInteger denominator) {
    super();
    this.nominator = nominator;
    this.denominator = denominator;
  }
 
  
  public BigInteger getNominator() {
    return nominator;
  }
  
  public BigInteger getDenominator() {
    return denominator;
  }


  @Override
  public String toString() {
    return nominator.toString() +"/"+denominator.toString();
  }
     
}
