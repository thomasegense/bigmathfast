package dk.teg.bigmathfast.search;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.TreeSet;

public class ApSquaresDifferenceWhenAddingFactors {

	private static final BigInteger B4 = new BigInteger("4");
	public static void main(String[] args) {
		
		BigInteger b= new BigInteger("2665");				
		BigInteger factors= new BigInteger("1");
		
		while (true) {
			factors=factors.add(B4);
			if (!factors.isProbablePrime(10)) {
				continue;
			}		
  		 TreeSet<Double> newDiffs = newDiffs(b, factors);
  		 System.out.println("factor:"+factors +" diffs:"+newDiffs +" quality:");
  	   
  		 
  		 
		}
		
	}
	
	public static TreeSet<Double> newDiffs(BigInteger b, BigInteger factors){

		BigInteger multiplied= b.multiply(factors);
		AllApSquaresDifference allDif = new AllApSquaresDifference(b,null);
		AllApSquaresDifference allDifWithFactor = new AllApSquaresDifference(multiplied,allDif.getHighestDifference().multiply(factors).multiply(factors));
		
		
 
		
		
		
		//Set of the small squares in AP multiplied by the factors. This will be the new trivial small squares that is just multiplied by the factors			
		TreeSet<BigInteger> trivialSmalls= new TreeSet<BigInteger>();
		for (APSquareDifference ap : allDif.getApSquaresDifs()) {
			trivialSmalls.add(ap.getSmall().multiply(factors));
		}
		
		TreeSet<Double> newDiffs= new TreeSet<Double>();
		for (APSquareDifference apDif :allDifWithFactor.getApSquaresDifs()) {
			if (trivialSmalls.contains(apDif.getSmall())) {			
		//	System.out.println("OLD:"+apDif);
			}
			else {
			//	System.out.println("NEW:"+apDif);
			newDiffs.add(apDif.getDiffPercentage());
			}						
		}	
		
       return newDiffs; 		
	}
	
}

