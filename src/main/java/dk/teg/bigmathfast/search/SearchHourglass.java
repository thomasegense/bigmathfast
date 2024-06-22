
package dk.teg.bigmathfast.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.util.BigMathFastUtil;




/**
 * 
 * Fact: For high numbers =1 mod4 in 21242099354121 range, only 1 in 6 has all primefactors 1 mod 4.
 * 
 * @author thomas egense
 *
 */

/*
q=1.2568 #APS:14 factors:[5, 13, 17] n=1105
q=1.5753 #APS:14 factors:[5, 13, 29] n=1885
q=1.6476 #APS:14 factors:[5, 13, 41] n=2665
q=1.2122 #APS:18 factors:[5, 5, 5, 13, 13] n=21125
q=1.449 #APS:14 factors:[5, 61, 109] n=33245
q=1.2215 #APS:14 factors:[17, 73, 229] n=284189
q=1.2716 #APS:41 factors:[5, 17, 89, 137] n=1036405
q=1.5661 #APS:41 factors:[5, 17, 41, 349] n=1216265
q=1.284 #APS:68 factors:[5, 5, 17, 41, 349] n=6081325
q=1.3036 #APS:41 factors:[5, 53, 97, 337] n=8662585
q=1.297 #APS:122 factors:[5, 13, 29, 61, 113] n=12993305
q=1.2037 #APS:23 factors:[5, 5, 61, 12101] n=18454025
q=1.2158 #APS:122 factors:[5, 13, 17, 73, 229] n=18472285
q=1.3081 #APS:113 factors:[5, 5, 17, 29, 29, 53] n=18943525
q=1.2143 #APS:83 factors:[5, 5, 5, 5, 5, 13, 13, 37] n=19540625
q=1.2385 #APS:122 factors:[5, 17, 29, 53, 173] n=22601585
q=1.2086 #APS:14 factors:[5, 53, 96769] n=25643785
q=1.2111 #APS:95 factors:[5, 5, 5, 13, 17, 977] n=26989625
q=1.2203 #APS:122 factors:[5, 17, 41, 113, 137] n=53951285
q=1.2158 #APS:203 factors:[5, 5, 17, 37, 41, 89] n=57380525
q=1.3184 #APS:203 factors:[5, 5, 13, 53, 61, 109] n=114529025
q=1.2385 #APS:203 factors:[5, 13, 17, 37, 37, 109] n=164889205
q=1.3308 #APS:122 factors:[5, 13, 37, 157, 461] n=174066685
q=1.2702 #APS:41 factors:[5, 37, 109, 9041] n=182311765
q=1.2049 #APS:14 factors:[5, 4561, 8069] n=184013545
q=1.3208 #APS:32 factors:[5, 5, 5, 1153, 1361] n=196154125
q=1.3435 #APS:113 factors:[5, 5, 13, 13, 197, 241] n=200590325
q=1.237 #APS:41 factors:[13, 101, 193, 809] n=205007881
q=1.2047 #APS:203 factors:[5, 13, 17, 17, 41, 281] n=216421985
q=1.5264 #APS:203 factors:[5, 5, 17, 53, 73, 137] n=225272525
q=1.2037 #APS:365 factors:[5, 13, 17, 53, 73, 89] n=380496805
q=1.2491 #APS:23 factors:[37, 37, 53, 5857] n=424966349
q=1.2308 #APS:41 factors:[89, 109, 157, 313] n=476716841
q=1.2065 #APS:41 factors:[13, 17, 101, 32413] n=723490573
q=1.3192 #APS:41 factors:[5, 17, 1093, 8933] n=829920365

 * 
 */
public class SearchHourglass {
	private final static BigInteger B1= new BigInteger("1");
	private final static BigInteger B4= new BigInteger("4");
	private static BigInteger currentNumber= null;
	private static double minQuality= 0d;

	public static void main(String[] args) throws Exception{
		int numberOfThreads=1;
		currentNumber= new BigInteger("21242099354121");
		//currentNumber= new BigInteger("5");
		minQuality=1.01d;
	
		while (!BigMathFastUtil.is1Mod4(currentNumber)) {
			currentNumber=currentNumber.add(B1);			
		}
		
		System.out.println("Starting #threads="+numberOfThreads +" with log-quality="+minQuality);
		
		
	    for (int i =0;i<numberOfThreads;i++) {
	    	Thread t= new Thread(new SearchHourglass().new SearchHourglassThread(i));	    			
	    	t.start();
	    }
        
	    
	}



	private static synchronized BigInteger getNext() {
		currentNumber=currentNumber.add(B4);
		return currentNumber;
	}

	private class SearchHourglassThread implements Runnable {

		private int threadNumber;
		
		public SearchHourglassThread(int threadNumber) {
		  this.threadNumber=threadNumber;
		}
			
		public void run()
		{
						
			System.out.println("Started thread:"+threadNumber);
			long start=System.currentTimeMillis();
			while (true) {            
				try {
				
				BigInteger next=getNext();
		
				
				ArrayList<BigInteger> factors = BigMathFast.factorize(next);
				String factorsStr=factors.toString();
				if (!BigMathFastUtil.allFactors1Mod4(factors)) {
					//     System.out.println("Skipping:"+toTest +" has factors == 3(mod 4). Factors:"+factorsStr);
					continue;
				}

				factors.addAll(factors);

				ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);            
				if (apSquares.size() <4) {
					// System.out.println("Skipping, not enough AP's for "+toTest  +" factors:"+factorsStr);
					continue;

				}

				BigInteger diff = SquareUtil.findMinDifferenceOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
				double quality= SquareUtil.calculateQuality(  diff, next);
				
				if (quality>minQuality) {        
					System.out.println(next+" quality="+quality + " #APS:"+apSquares.size() +" factors:"+factorsStr  );
					//System.out.println("Number="+ toTest +" diff:"+best3MatchAps.getDifference() +" q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr);
				}

				}
				catch(Throwable e) {
					e.printStackTrace();
				}
			}	
		}
	}
}

