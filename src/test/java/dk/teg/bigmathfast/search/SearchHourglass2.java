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
 * Idea: 
(391,1105,1513:1068144)
(1057,1105,1151:103776)  < very small difference..Always so?
(221,1105,1547:1172184)
 * 
 * 
 * 
Q=1.5661
(586669,1216265,1616917:1135120034664)
(1066297,1216265,1349671:342311258016)
(43235,1216265,1719515: 1477431285000)
 * 
 * @author thomas egense
 *
 */


public class SearchHourglass2 {
    private final static BigInteger B0= new BigInteger("0");
    private final static BigInteger B1= new BigInteger("1");
    private final static BigInteger B2= new BigInteger("2");
    private final static BigInteger B4= new BigInteger("4");
    private final static BigInteger B5= new BigInteger("5");
    
	public static void main(String[] args) throws Exception {
	ArrayList<BigInteger> factors = new  ArrayList<BigInteger>();
	factors.add(new BigInteger("5"));
	factors.add(new BigInteger("17"));
	factors.add(new BigInteger("29"));
	factors.add(new BigInteger("41"));
	factors.add(new BigInteger("89"));
	factors.add(new BigInteger("113"));
	factors.add(new BigInteger("577"));
	
	//analyseAddingOneFactor(factors, newFactor);
	    
	    
	}
	
	

	public static void analyseAddingOneFactor(ArrayList<BigInteger> factors, BigInteger newFactor) {	    
                  
	    ArrayList<BigInteger> factorsOrg = new ArrayList<BigInteger>();	    	   
	    
	    factorsOrg.addAll(factors);
	    factorsOrg.addAll(factors);
	    
        ArrayList<NumberExpressedInSumOfSquares> apSquaresBefore = SquareUtil.getAllAPofSquares(factorsOrg);            
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchApsBefore = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquaresBefore);
        
        
        factorsOrg = new ArrayList<BigInteger>();              
        
        factorsOrg.addAll(factors);
        factorsOrg.addAll(factors);
        //compute next
        factorsOrg.add(newFactor);        
        factorsOrg.add(newFactor);
        
        
        
        ArrayList<NumberExpressedInSumOfSquares> apSquaresAfter = SquareUtil.getAllAPofSquares(factorsOrg);            
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchApsAfter = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquaresAfter);
        
        
        System.out.println("#APs:"+apSquaresBefore.size() +":"+apSquaresAfter.size());
        
        
        for (NumberExpressedInSumOfSquares apBefore:apSquaresBefore) {
            System.out.println("Before:"+apBefore.getAPBigNumber());
        }
        System.out.println("Q="+SquareUtil.calculateQuality(best3MatchApsBefore.getDifference(),best3MatchApsBefore.getAps().get(0).getNroot()));
        for (NumberExpressedInSumOfSquares bestApBefore : best3MatchApsBefore.getAps()) {
            System.out.println(bestApBefore.getAPBigNumber());            
        }
        
        System.out.println("diff:"+best3MatchApsBefore.getDifference());
        System.out.println("--------");
        System.out.println("diff:"+best3MatchApsAfter.getDifference());
        System.out.println("Q="+SquareUtil.calculateQuality(best3MatchApsAfter.getDifference(),best3MatchApsAfter.getAps().get(0).getNroot()));                
        for (NumberExpressedInSumOfSquares bestApAfter : best3MatchApsAfter.getAps()) {
            System.out.println(bestApAfter.getAPBigNumber());            
        }
        
        
        
        //Test how many of the new APs that are just multiply by newFactor
        
        for (NumberExpressedInSumOfSquares after: apSquaresAfter) {
            if (after.getAPBigNumber().getDifference().mod(newFactor).equals(B0) && after.getNroot().mod(newFactor).equals(B0)) {                
            System.out.println("multiplum:"+after.getAPBigNumber());
            }                      
            else { 
                System.out.println("new"+after.getAPBigNumber());
                
            }
            
            
        }
        
        
        
        
	}
    
	
		
}
