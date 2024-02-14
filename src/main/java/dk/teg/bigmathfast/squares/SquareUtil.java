package dk.teg.bigmathfast.squares;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import dk.teg.bigmathfast.squares.DecomposedPrime;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.primes.PollardRho;





public class SquareUtil {

    final static BigInteger B0 = new BigInteger("0");
    final static BigInteger B1 = new BigInteger("1");
    final static BigInteger B4 = new BigInteger("4");
 
    
    public static void main(String[] args) {
        
        //Is square of the number
        ArrayList<NumberExpressedInSumOfSquares> allAPofSquares = getAllAPofSquares(new BigInteger("1885"));
        System.out.println(allAPofSquares);
        
        Minimum3Tuppel3SquaresInAPBigNumber m= findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(allAPofSquares);
        
        System.out.println(m.getAps());
        System.out.println(m.getDifference());
        
        
        
      }
    
    
    /**
     *  
     * 
     */
    public static ArrayList<NumberExpressedInSumOfSquares> getAllAPofSquares(BigInteger big){


          ArrayList<BigInteger> factors = PollardRho.factorOnlyIfAllPrimeFactors1Mod4(big);
                 
          if (factors == null || factors.size()==1){//aps.size will be 2 if only 1 factor      
              return new ArrayList<NumberExpressedInSumOfSquares>(); 
          }       

          factors.addAll(factors); 

          ArrayList<NumberExpressedInSumOfSquares> aps = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);
          return aps;
      }
    
    
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
    
    public static ArrayList<NumberExpressedInSumOfSquares>  combineNumberExpressedInSumOfSquaresMultiple ( ArrayList<NumberExpressedInSumOfSquares> oldList, NumberExpressedInSumOfSquares newOne){
                
        ArrayList<NumberExpressedInSumOfSquares> numberOfExpressions = new ArrayList<NumberExpressedInSumOfSquares>();  
        
        HashSet<NumberExpressedInSumOfSquares> temp = new HashSet<NumberExpressedInSumOfSquares>();
        
        for (int j =0;j<oldList.size();j++){
            ArrayList<NumberExpressedInSumOfSquares> newExpressions = SquareUtil.combineNumberExpressedInSumOfSquares( oldList.get(j),newOne);
               temp.addAll(newExpressions);                                                  
        }                                  
                   
        numberOfExpressions=  new ArrayList<NumberExpressedInSumOfSquares>();
        Iterator<NumberExpressedInSumOfSquares>  it=temp.iterator();

        while (it.hasNext()){                           
            numberOfExpressions.add(it.next());  
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

 	public static Minimum3Tuppel3SquaresInAPBigNumber findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(ArrayList<NumberExpressedInSumOfSquares> aps){
 	    
 	    ArrayList<BigInteger> list = new         ArrayList<BigInteger>();

 	    for (int i=0;i<aps.size();i++){
 	        // no need to match the AP=(n,n,n,0) case.
 	        if (!aps.get(i).getAPBigNumber().getDifference().equals(B0)){ list.add(aps.get(i).getAPBigNumber().getDifference());}
 	    }
 	    
 	    
 	   ArrayList<BigInteger> results = findBestMatchOfAddingTwoComparedToThirdBisection(list);      
 	   //Now match the difference to the difference in AP. (not optimal) 	       
 	    
 	   
 	   BigInteger bestMatch=results.remove(3); //Difference, not from an AP
 	   ArrayList<NumberExpressedInSumOfSquares > bestAps = new ArrayList<NumberExpressedInSumOfSquares>();  
 	   
 	   
 	   
 	   for (NumberExpressedInSumOfSquares ap : aps) {
 	      Tuppel3SquaresInAPBigNumber tuppelAP = ap.getAPBigNumber(); 
 	      
 	       if (results.contains(tuppelAP.getDifference())) {
 	           bestAps.add(ap); 	           
 	       }
 	       if (bestAps.size() == 3) { 	           	           
 	           return new Minimum3Tuppel3SquaresInAPBigNumber(bestAps,bestMatch); 	            	           
 	          
 	       } 	       
 	   } 	   
 	   
 	   throw new RuntimeException("Logic error! aps:"+aps.toString());
 	   
 	}
 	

    public static ArrayList<BigInteger> findBestMatchOfAddingTwoComparedToThirdBisection(ArrayList<BigInteger> numbers){
    //System.out.println("findBestMatchOfAddingTwoComparedToThirdBisection start");
        
        ArrayList<BigInteger> result = new  ArrayList<BigInteger> ();
        Collections.sort(numbers);
        //                                System.out.println(numbers);

        //int numberOfIterations=0;
        BigInteger bestMatch=new BigInteger("9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999");
        BigInteger bestSumOfTwoPart1=null;
        BigInteger bestSumOfTwoPart2=null;
        BigInteger bestSumMiddle=null;
        
        for (int i=0;i<numbers.size()-2;i++){
            for (int j=i+1;j<numbers.size()-1;j++){
                
                BigInteger sumOfTwoPart1= numbers.get(i);
                BigInteger sumOfTwoPart2= numbers.get(j);
                BigInteger sumOfTwo=sumOfTwoPart1.add(sumOfTwoPart2); 

                
                int minIndex=j+1;
                int maxIndex=numbers.size();
                int currentIndexJump= (maxIndex-minIndex)/2;
                int currentIndex=Math.min(minIndex+currentIndexJump,maxIndex-1);

                /*
                        System.out.println("sumOfTwo:"+sumOfTwo);
                        System.out.println("minIndex:"+minIndex);
                        System.out.println("maxIndex:"+maxIndex);
                        System.out.println("currentJump:"+currentIndexJump);
                        System.out.println("currentIndex:"+currentIndex);
                 */

                //See if sumOfTwo allready higher that highest number. (which will then be best match with highest number).
                //If it is higher, then return match and end complete method!

                if (j==i+1){ //Two cosequtive numbers on list
                    BigInteger differenceLast=sumOfTwo.subtract(numbers.get(maxIndex-1));
                    
                    BigInteger differenceLastAbs= differenceLast.abs();
                    if (differenceLastAbs.compareTo(bestMatch)<0){
                        bestMatch =differenceLastAbs; //Do not forget this match
                        bestSumMiddle = numbers.get(maxIndex-1);
                        bestSumOfTwoPart1=sumOfTwoPart1;
                        bestSumOfTwoPart2=sumOfTwoPart2;
                    }

                    if (differenceLast.compareTo(BigInteger.ZERO)>0){

                        //System.out.println("breaking! sumOfTwo:"+sumOfTwo+", last:"+numbers.get(maxIndex));
                        //  break;
                    //    System.out.println("findBestMatchOfAddingTwoComparedToThirdBisection end2");
             
                        result.add(bestSumMiddle);
                        result.add(bestSumOfTwoPart1);
                        result.add(bestSumOfTwoPart2);
                        result.add(bestMatch);
                        
                        return result;
                    }
                }


                while (currentIndexJump !=0){ //main loop, bisection
                    //                System.out.println("currentIndex:"+currentIndex);
                    //                                        System.out.println("currentIndexJump:"+currentIndexJump);
                    //                                        System.out.println("checking against:"+numbers.get(currentIndex));
                    if (currentIndexJump%2==1 && currentIndexJump>1){
                        currentIndexJump=(currentIndexJump+1)/2;                                                                                                                          
                    }
                    else{                                                
                        currentIndexJump=(currentIndexJump)/2;
                    }

                    if (sumOfTwo.compareTo(numbers.get(currentIndex))> 0){              
                        currentIndex=Math.min(currentIndex+currentIndexJump,maxIndex-1);
                    }
                    else{                                                
                        currentIndex=Math.max(currentIndex-currentIndexJump,minIndex);                                                  
                    }                                          
                }//end main loop, bisection

                //System.out.println("final bisection index:"+currentIndex);
                //check the index number and one to each side for best match
                BigInteger difference=null;
                BigInteger differenceAbs=null;

                if (currentIndex-1>=minIndex){
                    //                System.out.println("final bisection index against:"+numbers.get(currentIndex-1));
                    difference=sumOfTwo.subtract(numbers.get(currentIndex-1));
                    differenceAbs= difference.abs();
                    if (differenceAbs.compareTo(bestMatch)<0){
                        bestMatch =differenceAbs;
                        bestSumMiddle = numbers.get(currentIndex-1);                        
                        bestSumOfTwoPart1=sumOfTwoPart1;
                        bestSumOfTwoPart2=sumOfTwoPart2;
                    }
                }

                //System.out.println("final bisection index against:"+numbers.get(currentIndex));
                difference=sumOfTwo.subtract(numbers.get(currentIndex));
                differenceAbs= difference.abs();
                if (differenceAbs.compareTo(bestMatch)<0){                                        
                    bestSumMiddle = numbers.get(currentIndex);
                    bestMatch =differenceAbs;
                    bestSumOfTwoPart1=sumOfTwoPart1;
                    bestSumOfTwoPart2=sumOfTwoPart2;
                }


                if (currentIndex+1<=maxIndex-1){
                    //                        System.out.println("final bisection index against:"+numbers.get(currentIndex+1));
                    difference=sumOfTwo.subtract(numbers.get(currentIndex+1));
                    differenceAbs= difference.abs();
                    if (differenceAbs.compareTo(bestMatch)<0){                        
                        bestMatch =differenceAbs;
                        bestSumMiddle = numbers.get(currentIndex+1);
                        bestSumOfTwoPart1=sumOfTwoPart1;
                        bestSumOfTwoPart2=sumOfTwoPart2;
                    }
                }
            }
        }
    //  System.out.println("findBestMatchOfAddingTwoComparedToThirdBisection end1");
        result.add(bestSumMiddle);
        result.add(bestSumOfTwoPart1);
        result.add(bestSumOfTwoPart2);
        result.add(bestMatch);
        
        return result;
    } 
    

    /*
     * Fast implementation of squareRoot for BigIntegers
     *   
     */
    public static BigInteger sqrt(BigInteger n) {
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

    /**
     * A sub-problem of the unsolved 'magic square of squares' is the Hourglass problem.<br>
     * Finding 3 AP of squares with same middlenumber such the the difference of each from the middle number (step value) has s1+s2=s3. 
     * 
     * Example:<br>
     * The number 1885 (5*13*29) has 7 different AP with 1885^2 as middle number.<br>
     * The 3 AP giving the minimum difference are:<br>
     *  (1015^2 ,1885^2,2465^2:2523000)          (1885^2-1015^2=2523000)<br>
     *  (1651^2,1885^2,2093^2:827424)<br>
     *  (1363^2,1885^2,2291^2:1695456)<br>
     *  Difference 2523000-827424-1695456=120<br>
     *   <br>
     *  The quality q for the number 1885 with difference is q=1.5753<br> 
     *  For calculation of the quality details see {@link #calculateQuality() calculateQuality}<br> 
     <br>
     * @param number The BigInteger to calcuate the quality for
     * @return
     * @throws IllegalArgumentException If the number does not have all primefactors ==1 (mod4) or there are less than 3 primefactors.
     */
    public static double calculateQualityForAPSquares(BigInteger number) throws IllegalArgumentException {
       return calculateQualityForAPSquaresImpl(number, null);            
    }
   
    /**
    * See this method for the full documentation {@link #calculateQualityForAPSquares(BigInteger) calculateQualityForAPSquares}
    *  
    * @param factorsKnow The prime factors of the number to calculate the quality for.<br>
    * Knowing the factors will save a factorization. <br>
    * 
    * 
    * @return
    * @throws IllegalArgumentException If the number does not have all primefactors ==1 (mod4) or there are less than 3 primefactors.
    */
    public static double calculateQualityForAPSquares( ArrayList<BigInteger> factorsKnown) throws IllegalArgumentException {
        return calculateQualityForAPSquaresImpl(null, factorsKnown);            
     }
    
  /*
   * Method that two different public methods will call dependant of the number is already factored   
   * Will only factor number is factors is null
   *
   *
   */
  private static double calculateQualityForAPSquaresImpl(BigInteger number,  ArrayList<BigInteger> factorsKnown) throws IllegalArgumentException {
      
      if (number== null && factorsKnown== null) {
          throw new IllegalArgumentException("Number or factors are both null");
      }
      
      ArrayList<BigInteger> factors = new ArrayList<BigInteger>();
      if (factorsKnown== null || factorsKnown.size() ==0) {
           factors= BigMathFast.factorize(number);      
          
      }
      else {
          factors=BigMathFast.factorize(number);          
      }
              
      
        
        String factorsStr=factors.toString();
        if (!allFactors1Mod4(factors)) {
           // System.out.println("Skipping:"+toTest +" has factors == 3(mod 4). Factors:"+factorsStr);
            throw new IllegalArgumentException("Not all factors of the numbers are ==1 (mod 4). Factors:"+factorsStr);            
        }                  
        factors.addAll(factors); //Yes, factors are double since the AP  are for squares.
    
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);            
        if (apSquares.size() <4) {
           // System.out.println("Skipping, not enough AP's for "+toTest  +" factors:"+factorsStr);
            throw new IllegalArgumentException("Not enough prime factors to generate 4 or more AP. Prime factors:"+factorsStr);
            
        }
        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);     
        
       double q= calculateQuality(best3MatchAps.getDifference(), number);
       //  System.out.println("q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr  +" n="+toTest);
       return q;
             
  }
    
    
    /**
     * The quality is a measure of small the best difference is compared to the middle number in the AP.
     * The highest known quality is 1.6476 for the number 2665, that have APs with diff=120. 
     * 1885 also have diff=120, but only quality 1.5753 because the difference is relative higher compared to the number
     * 
     * log(diff) /log(middleNumber^2)
     * 
     * @see <a href="https://thomas-egense.dk/math/BestQualityAP.html">Best quality for numbers up to 10E13.</a>
     * 
     * @param diff 
     * @param middleNumbber
     * @return The quality as a double 
     */
    public static double calculateQuality(BigInteger diff, BigInteger middleNumber) {
        
        double a=bigIntLog(middleNumber, 2);
        double b=bigIntLog(diff, 2);        

        //return diff.divide(middleNumbber).doubleValue();
       String fourDigits=String.format("%.4f",a/b);
        

        return Double.valueOf(fourDigits.replaceAll(",", "."));
        

    }
    

    /**
     * Calculate ln() for a BigInteger for a given log-base
     * 
     * @param number The BigInteger to calculate ln() for
     * @param The log-base used for ln.
     * 
     * @return The ln(number) value as a double
     * 
     */
    public static double bigIntLog(BigInteger number, double base) {

        // Convert the BigInteger to BigDecimal
        BigDecimal bd = new BigDecimal(number);
        // Calculate the exponent 10^exp
        BigDecimal diviser = new BigDecimal(10);
        diviser = diviser.pow(number.toString().length()-1);
        // Convert the BigDecimal from Integer to a decimal value
        bd = bd.divide(diviser);
        // Convert the BigDecimal to double
        double bd_dbl = bd.doubleValue();
        // return the log value
        return (Math.log10(bd_dbl)+number.toString().length()-1)/Math.log10(base);
    }
    
    
    /**
     *  
     * Validate all factors are == 1 (mod 4)
     * 
     * Example 5,13,19
     * 
     * 
     * @param factors
     * @return true if all factors are ==1 (mod 4)
     */
     public static boolean allFactors1Mod4(ArrayList<BigInteger> factors) {
        for (BigInteger b:factors) {
            if (!b.mod(B4).equals(B1)) {         
                return false;
            }
            
        }
        return true;
    }
    
}
