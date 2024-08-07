package dk.teg.bigmathfast.search; 

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;

public class AllApSquaresDifference {

	private ArrayList<APSquareDifference> apSquaresDifs;
	private double quality;
	private BigInteger number;
	
	Minimum3Tuppel3SquaresInAPBigNumber bestMatch;
	
	private static BigInteger B0 = new BigInteger("0");

	public static void main(String[] args) {

		BigInteger b = new BigInteger("2665");
		AllApSquaresDifference al = new AllApSquaresDifference(b.multiply(new BigInteger("5")));
        System.out.println(al);
	}

	/*
	quality=1.6476
APSquareDifference [2665,2665,2665]  d=0, p=0.0, best=false]
APSquareDifference [2591,2665,2737]  d=388944, p=0.054874, best=true]
APSquareDifference [2165,2665,3085]  d=2415000, p=0.340714, best=false]
APSquareDifference [2015,2665,3185]  d=3042000, p=0.429173, best=false]
APSquareDifference [1927,2665,3239]  d=3388896, p=0.478114, best=false]
APSquareDifference [1721,2665,3353]  d=4140384, p=0.584135, best=false]
APSquareDifference [1435,2665,3485]  d=5043000, p=0.711478, best=false]
APSquareDifference [1339,2665,3523]  d=5309304, p=0.749049, best=false]
APSquareDifference [1169,2665,3583]  d=5735664, p=0.809201, best=false]
APSquareDifference [943,2665,3649]  d=6212976, p=0.876541, best=false]
APSquareDifference [635,2665,3715]  d=6699000, p=0.94511, best=true]
APSquareDifference [533,2665,3731]  d=6818136, p=0.961918, best=false]
APSquareDifference [299,2665,3757]  d=7012824, p=0.989385, best=false]
APSquareDifference [119,2665,3767]  d=7088064, p=1.0, best=true]

	 */
	public AllApSquaresDifference(BigInteger number) {
		this.number = number;

		ArrayList<NumberExpressedInSumOfSquares> apStart = SquareUtil.getAllAPofSquares(number);
		apSquaresDifs = new ArrayList<APSquareDifference>();

		bestMatch = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apStart);

		TreeSet<BigInteger> diffValues = new TreeSet<BigInteger>();
		TreeSet<BigInteger> bestDiffValues = new TreeSet<BigInteger>();
		BigInteger n0=bestMatch.getAps().get(0).getAPBigNumber().getDifference();
		BigInteger n1=bestMatch.getAps().get(1).getAPBigNumber().getDifference();
		BigInteger n2=bestMatch.getAps().get(2).getAPBigNumber().getDifference();		
		bestDiffValues.add(n0);
		bestDiffValues.add(n1);
		bestDiffValues.add(n2);
		

		for (NumberExpressedInSumOfSquares b : apStart) {
			Tuppel3SquaresInAPBigNumber apBigNumber = b.getAPBigNumber();
			diffValues.add(apBigNumber.getDifference());
		}

		// Find minimum and maximum.
		BigInteger smallest = diffValues.first();
		BigInteger highest = diffValues.last();

		for (NumberExpressedInSumOfSquares b : apStart) {
			Tuppel3SquaresInAPBigNumber apBig = b.getAPBigNumber();
			double diff = map2Double(apBig.getDifference(), smallest, highest);
			boolean isBestMatch = false;
			if (bestDiffValues.contains(apBig.getDifference())) {
				isBestMatch = true;
			}
			APSquareDifference apDif = new APSquareDifference(apBig.getSmall(), apBig.getMiddle(), apBig.getHigh(), apBig.getDifference(), diff, isBestMatch);
			apSquaresDifs.add(apDif);
		}
		Collections.sort(apSquaresDifs);//small diff first
		
	
		//calcuate quality
		BigInteger dif=n2.subtract(n1).subtract(n0);						
		quality = SquareUtil.calculateQuality(dif, number);
		
	}

	// Map into (0,1 interval)
	public static double map2Double(BigInteger value, BigInteger minimum, BigInteger maximum) {

		BigInteger span = maximum.subtract(minimum);
		BigInteger diff = value.subtract(minimum);

		BigDecimal bigD = new BigDecimal("1");
		BigDecimal d = (bigD.multiply(new BigDecimal(diff))).divide(new BigDecimal(span), 6, RoundingMode.CEILING);

		return d.doubleValue(); // between 0 and 1
	}

	public ArrayList<APSquareDifference> getApSquaresDifs() {
		return apSquaresDifs;
	}

	public void setApSquaresDifs(ArrayList<APSquareDifference> apSquaresDifs) {
		this.apSquaresDifs = apSquaresDifs;
	}

	public BigInteger getNumber() {
		return number;
	}

	public void setNumber(BigInteger number) {
		this.number = number;
	}

	public Minimum3Tuppel3SquaresInAPBigNumber getBestMatch() {
		return bestMatch;
	}

	public void setBestMatch(Minimum3Tuppel3SquaresInAPBigNumber bestMatch) {
		this.bestMatch = bestMatch;
	}

    @Override
	public String toString() {
		StringBuilder b= new StringBuilder();
		b.append("quality="+quality);
		b.append("\n");
		for (APSquareDifference ap:  apSquaresDifs) {
			b.append(ap);
			b.append("\n");
		}
    	    	
		return b.toString();
	}
	
}

