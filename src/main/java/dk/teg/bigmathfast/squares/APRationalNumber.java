package dk.teg.bigmathfast.squares;

import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.util.BigMathFastUtil;

/**
 * @see https://kconrad.math.uconn.edu/blurbs/ugradnumthy/3squarearithprog.pdf
 * 
 * 
 */
public class APRationalNumber {
    private static BigInteger B1= new BigInteger("1");
    private static BigInteger B1_NEG= new BigInteger("-1");
    private static BigInteger B2_NEG= new BigInteger("-2");

    public static void main(String... args) {
        
        for (int i=1;i<1000;i++) {
            generateAP(new BigInteger("1"), new BigInteger(""+i));
            
        }
            
        
        //generateAP(new BigInteger("3"), new BigInteger("4"));
        //generateAP(new BigInteger("-5"), new BigInteger("3"));

    }


    public static void generateAP(BigInteger numinator,  BigInteger denominator) {

        //first calculate (x,y) =
        BigFraction m= new BigFraction(numinator,denominator);

        //These are reused 
        BigFraction minus_two_m_plus_1= ((m.multiply(B2_NEG)).add(B1));
        BigFraction minus_two_m_minus_1= ((m.multiply(B2_NEG)).add(B1_NEG));

        BigFraction m_squared= m.multiply(m);
        BigFraction m_squared_plus_1= m_squared.add(B1);

        //Calculate x
        BigFraction x_num = m_squared.add(minus_two_m_minus_1);
        BigFraction x = x_num.divide(m_squared_plus_1);

        //Calculate y
        BigFraction y_num = (m_squared.multiply(B1_NEG)).add(minus_two_m_plus_1);
        BigFraction y = y_num.divide(m_squared_plus_1);


    
        //Sanity check
        if(x.getDenominator().compareTo(y.getDenominator()) !=0){ //must be equal;
            System.out.println("logic error for input: "+numinator +","+denominator);

        }

        //Generate the AP
        BigInteger squareMiddle=x.getDenominator().multiply(x.getDenominator());

        //We dont know which is the largest
        BigInteger square1=x.getNumerator().multiply(x.getNumerator());
        BigInteger square2=y.getNumerator().multiply(y.getNumerator());


        BigInteger squareSmall;
        BigInteger squareLarge;

        if(square1.compareTo(square2) <0){ 
            squareSmall=square1;
            squareLarge=square2;
        }
        else {//Switch
            squareSmall=square2;
            squareLarge=square1;

        }
  
                
        BigInteger diff=squareMiddle.subtract(squareSmall); 
        Tuppel3SquaresInAPBigNumber squareAP = new Tuppel3SquaresInAPBigNumber(squareSmall, squareMiddle, squareLarge, diff);
        
        ArrayList<BigInteger> factorize = BigMathFast.factorize(squareMiddle);
        System.out.println("x="+x +" , y="+y +" AP:"+squareAP+" factors:"+factorize);     
    }



}
