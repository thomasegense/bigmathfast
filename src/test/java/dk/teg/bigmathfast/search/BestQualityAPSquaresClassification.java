package dk.teg.bigmathfast.search;

import java.io.BufferedReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;

/*
 * 
 * Interesting example, no multiple of good solotion. 
 * 15434605016465= 5* 17* 97* 109* 937* 18329  , q=1.3694 
 * 
 */
public class BestQualityAPSquaresClassification {
    
    
    public static void main(String[] args) throws Exception{
        
        

        Path path = Paths.get("E:\\studie\\bigmathfast\\ap.txt");
        BufferedReader reader = Files.newBufferedReader(path);        
        String line;
        while(( line = reader.readLine()) != null ) {
        String number = line.split(" ")[0];
        
        examine(new BigInteger(number));
        
                
        }
    }

    

    public static void examine(BigInteger toTest) {
        
        ArrayList<BigInteger> factors = BigMathFast.factorize(toTest);
        ArrayList<BigInteger> factorsFixed = new ArrayList<BigInteger>();
        factorsFixed.addAll(factors);
        
        ArrayList<BigInteger> factorsOrg = new ArrayList<BigInteger>();
        factorsOrg.addAll(factors);
        
        String factorsStr=factors.toString();
       
                  
        factors.addAll(factors);
    
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.createAllNumberExpressedInSumOfSquares(factors);            
       
        if (apSquares.size() <5) {
           System.out.println("Skipping, not enough AP's for "+toTest  +" factors:"+factorsStr);
            return;
            
        }
        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
    
       double q= SearchHourglass.getQuality(best3MatchAps.getDifference(), toTest);
       BigInteger ancestor = SearchHourglass.findAncestor(factorsOrg);
      
       System.out.print("<tr>");
       System.out.print("<td>"+ toTest+"</td>");       
       System.out.print("<td>"+ q+"</td>");
       System.out.print("<td>"+ factorsFixed+"</td>");
       System.out.print("<td>"+ apSquares.size()+"</td>");
       System.out.print("<td>"+ best3MatchAps.getDifference()+"</td>");
       System.out.print("<td>"+ ancestor+"</td>");
       System.out.print("<td>"+ best3MatchAps.getAps()+"</td>");
       System.out.println("</tr>");
    //   System.out.println("Number="+ toTest +" diff:"+best3MatchAps.getDifference() +" q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr + "ancestor:"+ancestor);
      
       
  }
    
}
