package dk.teg.bigmathfast.squares;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import dk.teg.bigmathfast.util.BigMathFastUtil;
import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.primes.PollardRho;





public class SquareUtil {

    final static BigInteger B0 = new BigInteger("0");
    final static BigInteger B1 = new BigInteger("1");
    final static BigInteger B4 = new BigInteger("4");


    public static void main(String[] args) {

        //Is square of the number
        ArrayList<NumberExpressedInSumOfSquares> allAPofSquares = getAllAPofSquares(new BigInteger("65"));

        for (NumberExpressedInSumOfSquares n :allAPofSquares) {
            System.out.println(n.getAPBigNumber());
        }

        Minimum3Tuppel3SquaresInAPBigNumber m= findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(allAPofSquares);

        System.out.println(m.getAps());
        System.out.println(m.getDifference());

        /*
         *  Create all arithmetric progressions (AP) of squares having the number middle^2 as middle value. <br>
         *  Example: 1, 5^2 , 7^2 (1,25,49) is an AP of squares with step value = 24. <br>
         *  <br>
         *  If the factorization is know, it is faster to call the {@link #createAllNumberExpressedInSumOfSquares(ArrayList<BigInteger>) createAllNumberExpressedInSumOfSquares} <br>
         *  See <a href="https://www.alpertron.com.ar/ECM.HTM">Write a number as all possible combination as sum of squares</a>     
         */      

    }




    /**
     * For a giver number calculate all possible decompositions into sum of two squares for the number^2 (the input squared). <br>
     * Example: (for input  5)  5^2=3^2 +4^2<br>  
     * Some numbers can have multiple decompositions.<br>
     * 65^2=60^2+25^2,  65^2=33^2+56^2, 65=39^2+52^2,. 65^2=16^2+63^2<br>
     * The calculation is optimized using Gaussian Integers.  See <a href=" https://www.thomas-egense.dk/math/squares_in_arithmetic_progression.html">Squares in atrimetic progression</a><br>
     * There is a 1-1 correspondance between a square number express as sum of squares and an aritmetic progression of squares.<br>
     * The decomposition:  5^2=3^2 +4^1  <-> 1^2,5^2, 7^2 (AP of squares) <br>  
     * The object has a method to convert into an AP of squares. For the the number 65 this is the following 4 AP's having 65^2 as middle number:<br>
     * 
     * <ul>
     *  <li>35^2,65^2,85^2  step value=3000</li>
     *  <li>23^2,65^2,89^2  step value=3696</li>
     *  <li>13^2,65^2,91^2  step value=4056</li>
     *  <li>47^2,65^2,79^2  step value=2016</li>
     * </ul>      
     * 
     *  If the factorization is know, it is faster to call the {@link #getAllAPofSquares(ArrayList<BigInteger>) createAllNumberExpressedInSumOfSquares} <br>
     * <br>  
     *  @param Middle number that must have all prime factors ==1 (mod4) and also not having 2 as a prime  factor. Will return null in that case<br>
     *  @return List different decompositions in sum of squares.    
     */
    public static ArrayList<NumberExpressedInSumOfSquares> getAllAPofSquares(BigInteger number)  {
        
        
        ArrayList<BigInteger> factors = null;
        try {
            factors = PollardRho.factorOnlyIfAllPrimeFactors1Mod4(number);        
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;            
        }
        
        if (factors == null || factors.size()==1){//aps.size will be 2 if only 1 factor      
            return new ArrayList<NumberExpressedInSumOfSquares>(); 
        }       

        factors.addAll(factors); 

        ArrayList<NumberExpressedInSumOfSquares> aps = SquareUtil.getAllAPofSquares(factors);
        return aps;
    }
       
    /**
     * Express the number squared having the given factorization as all combinations of a sum of two squares.
     * 
     * If factorization is know, this method is a faster version of {@link #getAllAPofSquares((BigInteger) getAllAPofSquares}. <br>
     * See above method for full documentation
     *   
     * @param factors All factors must be ==1 (mod 4) and not 2.
     * @return List different decompositions in sum of squares. 
     * 
     */    
    public static ArrayList<NumberExpressedInSumOfSquares> getAllAPofSquares(ArrayList<BigInteger> factors){

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

   



    /**
     * Having two numbers expressed as sum of squares the product can also be expressed as sum of squares.<br>
     * Using the identity: (a^2+b^2)(c^2+d^2)=(ac+bd)^2+(ad-bc)^2 = (ac-bd)^2+(ad+bc)^2 <br>
     * There can be up to two different ways to express the product as a sum of squares.<br> 
     * Example:<br>
     * 5=1^2+2^2 and 13=3^2+2^3 -> 5*13=65=7^2+4^2=8^1+1^1 (two different solutions)
     * 
     * @param n1 A number expressed as sum of squares
     * @param n2 A number expressed as sum of squares
     * 
     * @return The product n1*n2 express as a sum of squares. There can be 1 or 2 different solutions.
     * 
     */
    
    public static ArrayList<NumberExpressedInSumOfSquares> combineNumberExpressedInSumOfSquares(NumberExpressedInSumOfSquares n1, NumberExpressedInSumOfSquares n2){
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

    
    /**
     * This is the recursive method for combining several numbers express as sum of squares.
     * 
     * 
     * See descripton for combining two {@link #combineNumberExpressedInSumOfSquares(NumberExpressedInSumOfSquares,NumberExpressedInSumOfSquares) combineNumberExpressedInSumOfSquares}
     * 
     * @param List of NumberExpressedInSumOfSquares
     * @param A single NumberExpressedInSumOfSquares
     * @return A new List ofNumberExpressedInSumOfSquares. The combining will most often give more total combinations that size of the input list.
 
     */
    public static ArrayList<NumberExpressedInSumOfSquares> combineNumberExpressedInSumOfSquaresMultiple ( ArrayList<NumberExpressedInSumOfSquares> oldList, NumberExpressedInSumOfSquares newOne){

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


    
    /**
     * Not optimized! Also should only be called if the difference is very small (high ratio).
     * 
     * 
     * @param aps
     * @return The 3 APS that gives the minimum difference
     */
    
    public static Minimum3Tuppel3SquaresInAPBigNumber findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(ArrayList<NumberExpressedInSumOfSquares> aps){
        ArrayList<BigInteger> list = new         ArrayList<BigInteger>();

        for (int i=0;i<aps.size();i++){
            // no need to match the AP=(n,n,n,0) case.
            if (!aps.get(i).getAPBigNumber().getDifference().equals(B0)){ list.add(aps.get(i).getAPBigNumber().getDifference());}
        }


        ArrayList<BigInteger> results = BigMathFastUtil.findBestMatchOfAddingTwoComparedToThirdBisection(list);      
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


    
    /**
     * TODO
     * 
     * @param aps
     * @return
     */
    public static BigInteger findMinDifferenceOfAddingTwoComparedToThirdBisectionFromAps(ArrayList<NumberExpressedInSumOfSquares> aps){
        ArrayList<BigInteger> list = new  ArrayList<BigInteger>();

        for (int i=0;i<aps.size();i++){
            // no need to match the AP=(n,n,n,0) case.
            if (!aps.get(i).getAPBigNumber().getDifference().equals(B0)){
            	list.add(aps.get(i).getAPBigNumber().getDifference());
             }
        }

        ArrayList<BigInteger> results = BigMathFastUtil.findBestMatchOfAddingTwoComparedToThirdBisection(list);             
                       
        return results.get(3);
        	
    }

    



    /**
     * A sub-problem of the unsolved 'magic square of squares' is the Hourglass problem.<br>
     * Finding 3 AP of squares with same middle number such the the difference of each from the middle number (step value) has s1+s2=s3. 
     * 
     * Example:<br>
     * The number 1885 (5*13*29) has 7 different AP with 1885^2 as middle number.<br>
     * The 3 AP giving the minimum difference are:
     * <ul>
     *  <li>(1015^2 ,1885^2,2465^2:2523000) , stepvalue calculated from  (1885^2-1015^2=2523000)</li>
     *  <li>(1651^2,1885^2,2093^2:827424)</li>
     *  <li>(1363^2,1885^2,2291^2:1695456)</li>
     *  </ul>
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
        if (!BigMathFastUtil.allFactors1Mod4(factors)) {
            // System.out.println("Skipping:"+toTest +" has factors == 3(mod 4). Factors:"+factorsStr);
            throw new IllegalArgumentException("Not all factors of the numbers are ==1 (mod 4). Factors:"+factorsStr);            
        }                  
        factors.addAll(factors); //Yes, factors are double since the AP  are for squares.

        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);            
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

        double a=BigMathFastUtil.bigIntLog(middleNumber, 2);
        double b=BigMathFastUtil.bigIntLog(diff, 2);        

        //return diff.divide(middleNumbber).doubleValue();
        String fourDigits=String.format("%.4f",a/b);


        return Double.valueOf(fourDigits.replaceAll(",", "."));


    }
    

}
