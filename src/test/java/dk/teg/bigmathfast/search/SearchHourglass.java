package dk.teg.bigmathfast.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.util.BigMathFastUtil;




/**
 * best finds=
 * 
 * @author thoma
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
    private final static BigInteger B2= new BigInteger("2");
    private final static BigInteger B4= new BigInteger("4");
    private final static BigInteger B5= new BigInteger("5");

    
    
    public static void main(String[] args) {
        searchHourGlassFrom(new BigInteger("757380525"), 1.2d);
        
    }
    
    
    
    public static void mainOLD(String[] args) {
		
	    
	    //quality should be much different
		
		// BigInteger number = new BigInteger("1885"); //smallest diff    , q=1.57
	                                    //Number=2665 diff:120 q=1.6476 #APS:14 factors:[5, 13, 41]
	    // BigInteger number = new BigInteger("1216265"); //smallest diff    , q=1.56     //[5, 17, 41, 349]
	     //BigInteger nextPrime1Mod4= new BigInteger("830969211025"); //monster q=1.4349 #APS:365 factors:[5, 29, 41, 157, 617, 9029]               
         //BigInteger number = new BigInteger("166193842205"); // megamonster  , a=1.47				  	        	   
	     //BigInteger number = new BigInteger("195057177965"); //medium , q=1.19
	     //BigInteger number = new BigInteger("203206516925"); //medium , q=1.10 [5, 5, 17, 37, 53, 157, 1553]
	     //BigInteger number = new BigInteger("2697582499525"); //medium , q=1.13 [5, 5, 17, 37, 53, 157, 1553]	       	       	       	     
	     //BigInteger number = new BigInteger("15434605016465"); // very good hit, q=1.36 [5, 17, 97, 109, 937, 18329]				
	    //BigInteger number = new BigInteger("19742462232025"); //monster    , a=1.26	                                      
//                                            
	 //   BigInteger number = new BigInteger("21242099354125"); //very large good match 30661285800 , q=1.27 [5, 5, 5, 13, 17, 29, 113, 461, 509]
	                                                                                         
	    //gInteger number=new BigInteger("5199676544945");	      
	   //logResultWithValidate(number);
	       
	       BigInteger nextPrime1Mod4= new BigInteger("830969211025");	     
	       
	       
	       while (true) {

	       
	       BigInteger number=B5; //always 5
	       int n=5;

	       number=number.multiply(new BigInteger("13")); //extra
	       number=number.multiply(new BigInteger("17")); //extra
	       number=number.multiply(new BigInteger("29")); //extra
	       number=number.multiply(new BigInteger("37")); //extra
	       
	       Random ran = new Random();	       
	       n=7+n+ran.nextInt(500);
	       number=number.multiply(getNextPrime1Mod4(n));	       
           n=10+n+ran.nextInt(5000);
           number=number.multiply(getNextPrime1Mod4(n));
                                            
           n=20+n+ran.nextInt(2000000);
           BigInteger t=getNextPrime1Mod4(n);
           //System.out.println(t);
           number=number.multiply(t);
           
           
	                     	         
	           
	      //System.out.println(number);
           calculateRatioAndLogIfAboveLimit(number, 0.8d);
	       }  
	        
	       
	}
	

	public static BigInteger getNextPrime1Mod4(long nLong) {
	    BigInteger n=new BigInteger(""+nLong);
	    n=n.nextProbablePrime(); 
	    while (!n.mod(B4).equals(B1)) {
            n=n.nextProbablePrime();
        }
                               
         return n;
	}
	
    public static void calculateRatioAndLogIfAboveLimit(BigInteger toTest, double minRatio) {
    
          ArrayList<BigInteger> factors = BigMathFast.factorize(toTest);
          String factorsStr=factors.toString();
          if (!BigMathFastUtil.allFactors1Mod4(factors)) {
         //     System.out.println("Skipping:"+toTest +" has factors == 3(mod 4). Factors:"+factorsStr);
              return;
          }
                    
          factors.addAll(factors);
      
          ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);            
          if (apSquares.size() <4) {
             // System.out.println("Skipping, not enough AP's for "+toTest  +" factors:"+factorsStr);
              return;
              
          }
          
          Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
      
         double q= SquareUtil.calculateQuality(best3MatchAps.getDifference(), toTest);
        if (q>minRatio) {        
            System.out.println("q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr  +" n="+toTest);
            //System.out.println("Number="+ toTest +" diff:"+best3MatchAps.getDifference() +" q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr);
        }
         
    }
	
	
	
public static void searchHourGlassFrom(BigInteger n, double minQuality) {
    
    BigInteger current=n;
    while(true) {
       calculateRatioAndLogIfAboveLimit(current, minQuality);
       current=current.add(B4); 
        
                
    }
    
    
}
    
}
