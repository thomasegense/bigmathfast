package dk.teg.bigmathfast.util;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;




public class SquareUtil {

  //Important method! really fast!
    //Factors are prime=1 mod 4!
    //tilsvarende som http://wims.unice.fr/wims/en_tool~number~twosquares.en.html
    public static ArrayList<NumberExpressedInSumOfSquares> createAllNumberExpressedInSumOfSquares(ArrayList<BigInteger> factors){

        ArrayList<NumberExpressedInSumOfSquares> numberOfExpressions = new ArrayList<NumberExpressedInSumOfSquares>();  
        DecomposedPrime p1 = DecomposedPrime.create(factors.get(0));

        numberOfExpressions.add(p1.getNumberExpressedInSumOfSquares());

        for (int i =1;i<factors.size();i++){//First allready used

            HashSet<NumberExpressedInSumOfSquares> temp = new HashSet<NumberExpressedInSumOfSquares>();
            DecomposedPrime p = DecomposedPrime.create(factors.get(i));

            for (int j =0;j<numberOfExpressions.size();j++){

             ArrayList<NumberExpressedInSumOfSquares> newExpressions = SquareUtil.combineNumberExpressedInSumOfSquares(p.getNumberExpressedInSumOfSquares(), numberOfExpressions.get(j));

                temp.addAll(newExpressions);                                                  
            }                                  
            numberOfExpressions=  new ArrayList<NumberExpressedInSumOfSquares>();
            Iterator<NumberExpressedInSumOfSquares>  it=temp.iterator();

            while (it.hasNext()){
                numberOfExpressions.add(it.next());  
            }                                                                                        
        }      
        
        return numberOfExpressions;
    }
    
    //(a2+b2)(c2+d2)=(ac+bd)2+(ad-bc)2= (ac-bd)2+(ad+bc)^2
    public static ArrayList<NumberExpressedInSumOfSquares> combineNumberExpressedInSumOfSquares( NumberExpressedInSumOfSquares n1, NumberExpressedInSumOfSquares n2){
        ArrayList<NumberExpressedInSumOfSquares> list = new ArrayList<NumberExpressedInSumOfSquares>();
        BigInteger  n1n2= n1.getN().multiply(n2.getN());


        NumberExpressedInSumOfSquares new1 = new NumberExpressedInSumOfSquares(
                (n1.getR().multiply(n2.getR())).add(n1.getS().multiply(n2.getS())),
                (n1.getR().multiply(n2.getS())).subtract(n1.getS().multiply(n2.getR())),
                n1n2);

        NumberExpressedInSumOfSquares new2 = new NumberExpressedInSumOfSquares(
                (n1.getR().multiply(n2.getR())).subtract(n1.getS().multiply(n2.getS())),
                (n1.getR().multiply(n2.getS())).add(n1.getS().multiply(n2.getR())),
                n1n2);

        //if (!new1.getR().equals(BigInteger.ZERO) && !new1.getS().equals(BigInteger.ZERO))
            list.add(new1);     

        //if (!new2.getR().equals(BigInteger.ZERO) && !new2.getS().equals(BigInteger.ZERO))
            list.add(new2);

        return list;
    } 

 // mersenne prime forum - BigInteger
 	public static BigInteger bigintroot(BigInteger n) {
 		int currBit = n.bitLength() / 2;

 		BigInteger remainder = n;
 		BigInteger currSquared = BigInteger.ZERO.setBit(2*currBit);

 		BigInteger temp = BigInteger.ZERO;
 		BigInteger toReturn = BigInteger.ZERO;

 		BigInteger potentialIncrement;
 		do {
 			temp = toReturn.setBit(currBit);
 			potentialIncrement = currSquared.add(toReturn.shiftLeft(currBit+1));

 			int cmp = potentialIncrement.compareTo(remainder);
 			if (cmp < 0) {
 				toReturn = temp;
 				remainder = remainder.subtract(potentialIncrement);
 			}
 			else if (cmp == 0) {
 				return temp;
 			}
 			currBit--;
 			currSquared = currSquared.shiftRight(2);
 		} while (currBit >= 0);
 		return toReturn;
 	}

    
}
