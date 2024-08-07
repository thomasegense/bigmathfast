package dk.teg.bigmathfast.search;

import java.math.BigInteger;

public class APSquareDifference  implements Comparable<APSquareDifference>{
	private BigInteger small;
	private BigInteger middle;
	private BigInteger high;
	private BigInteger difference;
	private double diffPercentage;
	private boolean bestMatch;
	
	
	public APSquareDifference(BigInteger small, BigInteger middle, BigInteger high, BigInteger difference,double diffPercentage, boolean bestMatch) {
		super();
		this.small = small;
		this.middle = middle;
		this.high = high;
		this.difference = difference;
		this.diffPercentage = diffPercentage;
		this.bestMatch=bestMatch;
	}


	public BigInteger getSmall() {
		return small;
	}


	public BigInteger getMiddle() {
		return middle;
	}


	public BigInteger getHigh() {
		return high;
	}


	public BigInteger getDifference() {
		return difference;
	}


	public double getDiffPercentage() {
		return diffPercentage;
	}






    @Override
	public String toString() {
		return "APSquareDifference ["+ small +"," + middle + "," + high + "]  d="
				+ difference + ", p=" + diffPercentage + ", best=" + bestMatch + "]";
	}


	@Override public int compareTo( APSquareDifference  a)
   {        
            return this.difference.compareTo(a.difference);     
   }    

}
