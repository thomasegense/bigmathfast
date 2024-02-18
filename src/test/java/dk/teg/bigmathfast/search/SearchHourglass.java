package dk.teg.bigmathfast.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;




/**
 * best finds=
 * 
 * @author thoma
 *
 */


public class SearchHourglass {
    private final static BigInteger B1= new BigInteger("1");
    private final static BigInteger B2= new BigInteger("2");
    private final static BigInteger B4= new BigInteger("4");
    private final static BigInteger B5= new BigInteger("5");
	public static void main(String[] args) {
		
	    
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
	       logResultWithValidate(number);
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
	
    public static void logResultWithValidate(BigInteger toTest) {
    
          ArrayList<BigInteger> factors = BigMathFast.factorize(toTest);
          String factorsStr=factors.toString();
          if (!SquareUtil.allFactors1Mod4(factors)) {
             // System.out.println("Skipping:"+toTest +" has factors == 3(mod 4). Factors:"+factorsStr);
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
        if (q>0.85d) {
        
            System.out.println("q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr  +" n="+toTest);
            //System.out.println("Number="+ toTest +" diff:"+best3MatchAps.getDifference() +" q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr);
        }
         
    }
	
	
	

}
