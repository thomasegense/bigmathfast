package dk.teg.bigmathfast.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

public class BigMathFastUtil {

       private static final BigInteger B_MINUS1 = new BigInteger("-1");
       private static final BigInteger B1 = new BigInteger("1");
       private static final BigInteger B4 = new BigInteger("4");

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
     * This method will find the 3 elements a,b,c from in a list such that (a+b-c) has minimum value. <br>
     * The best value if 0 if a+b=c <br>
     * The algorithm is a fast as I could make it. Sorting the list and using bisection several times. <br>
     * 
     * 
     * return BigInteger having the best minimum value. min(a+b-c) for all a,b,c in the input list
     */
    public static ArrayList<BigInteger> findBestMatchOfAddingTwoComparedToThirdBisection(ArrayList<BigInteger> numbers){
        //System.out.println("findBestMatchOfAddingTwoComparedToThirdBisection start");

        ArrayList<BigInteger> result = new  ArrayList<BigInteger> ();
        Collections.sort(numbers);
        //                                System.out.println(numbers);

        //int numberOfIterations=0;
        BigInteger bestMatch=B_MINUS1;
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
                    if (bestMatch.equals(B_MINUS1) || bestMatch.compareTo(bestMatch)<0){
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


    /**
     * Calculate the squareroot rounded down to nearest Integer
     * 
     * @param n the number to calculate squareroot for.
     * 
     * @return The square root, rounded down to integer
     *      
     */
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
    
    /**  
     * Validate all factors are == 1 (mod 4)
     * 
     * Example 5,13,19
     *  
     * @param factors
     * @return true if all factors are ==1 (mod 4)
     */
    public static boolean allFactors1Mod4(ArrayList<BigInteger> factors) {
        for (BigInteger b:factors) {        
        	if (!is1Mod4(b)) {         
                return false;
            }
        }
        return true;
    }
    
    public static boolean is1Mod4(BigInteger n) {
    	if (!n.mod(B4).equals(B1)) {         
            return false;
        }
    	return true;
    	
    }
    
    
}
