package dk.teg.bigmathfast.search;

import java.io.BufferedReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.teg.bigmathfast.BigMathFast;
import dk.teg.bigmathfast.squares.Minimum3Tuppel3SquaresInAPBigNumber;
import dk.teg.bigmathfast.squares.NumberExpressedInSumOfSquares;
import dk.teg.bigmathfast.squares.SquareUtil;
import dk.teg.bigmathfast.squares.Tuppel3SquaresInAPBigNumber;

/*
 * 
 * Interesting example, no multiple of good solotion. 
 * 15434605016465= 5* 17* 97* 109* 937* 18329  , q=1.3694 
 * 
 */
public class BestQualityAPSquaresClassification {
    
    public static final BigInteger B1 = new BigInteger("1");
    
    public static void main(String[] args) throws Exception{
        
        

        Path path = Paths.get("/home/teg/eclipse-workspace/bigmathfast/AP.txt");
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
    
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);            
       
        if (apSquares.size() <5) {
           System.out.println("Skipping, not enough AP's for "+toTest  +" factors:"+factorsStr);
            return;
            
        }
        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);
    
       double q= SquareUtil.calculateQuality(best3MatchAps.getDifference(), toTest);
       BigInteger ancestor = findAncestor(factorsOrg);
      
       System.out.print("<tr>");
       System.out.print("<td>"+ toTest+"</td>");       
       System.out.print("<td>"+ q+"</td>");
       System.out.print("<td>"+ factorsFixed+"</td>");
       System.out.print("<td>"+ apSquares.size()+"</td>");
       System.out.print("<td>"+ best3MatchAps.getDifference()+"</td>");
       System.out.print("<td>"+ ancestor+"</td>");
       System.out.print("<td>"+ getAps(best3MatchAps.getAps())+"</td>");
       System.out.println("</tr>");
    //   System.out.println("Number="+ toTest +" diff:"+best3MatchAps.getDifference() +" q="+q + " #APS:"+apSquares.size() +" factors:"+factorsStr + "ancestor:"+ancestor);
                
  }
    
    private static String getAps(List<NumberExpressedInSumOfSquares> aps) {
        
        List<Tuppel3SquaresInAPBigNumber> listBig= new ArrayList<>();
        listBig.add(aps.get(0).getAPBigNumber());
        listBig.add(aps.get(1).getAPBigNumber());
        listBig.add(aps.get(2).getAPBigNumber());
        
        Collections.sort(listBig, Comparator.comparing(Tuppel3SquaresInAPBigNumber::getDifference));
        
        String val="";
        val=val+listBig.get(0); //This first
        val=val+" , ";
        val=val+listBig.get(1);
        val=val+" , ";
        val=val+listBig.get(2);
        
        return val;
    }
    
    /**
     * Calculate the quality for the number having the factors as input.
     * 
     * If a higher quality can be found by removing one of the factors return this number.
     * Return null if there is no ancestor.
     * 
     * If is number with high quality and no ancestors that are interesting and unique. 
     * 
     * @param factors List of factors. Each factor must all be 1(mod) 4.
     * 
     */
    public static BigInteger findAncestor(ArrayList<BigInteger> factors) {
        
        BigInteger orgNumber=multiplyList(factors);
       // System.out.println("finding ancestor for:"+orgNumber);
        ArrayList<BigInteger> orgFactors = new ArrayList<BigInteger>();           
        orgFactors.addAll(factors);
        factors.addAll(factors);
    
        ArrayList<NumberExpressedInSumOfSquares> apSquares = SquareUtil.getAllAPofSquares(factors);            
        if (apSquares.size() <6) { //TODO maybe 6 (1 is the 0 diff one)
      //      System.out.println("no ancestor, too AP factors:"+apSquares);
            return null; 
            
        }
        
        Minimum3Tuppel3SquaresInAPBigNumber best3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(apSquares);    
        double orgQ= SquareUtil.calculateQuality(best3MatchAps.getDifference(), orgNumber);
       
       //Now see if we can find an ancestor will better quality
        for (int i=0;i<orgFactors.size();i++) {
            ArrayList<BigInteger> ancestorFactors = new ArrayList<BigInteger>();
            ancestorFactors.addAll(orgFactors);
            ancestorFactors.remove(i);
            BigInteger ancestorOrg = multiplyList(ancestorFactors);           
            ancestorFactors.addAll(ancestorFactors);
            
            ArrayList<NumberExpressedInSumOfSquares> ancestorFactorsApSquares = SquareUtil.getAllAPofSquares(ancestorFactors);            
        
            Minimum3Tuppel3SquaresInAPBigNumber ancestrorBest3MatchAps = SquareUtil.findBestMatchOfAddingTwoComparedToThirdBisectionFromAps(ancestorFactorsApSquares);    
            double ancestorQ= SquareUtil.calculateQuality(ancestrorBest3MatchAps.getDifference(), ancestorOrg);                    
            if (ancestorQ > orgQ) {
          //      System.out.println("found ancestor:"+ancestorQ +" with quality:"+ancestorQ);
                return ancestorOrg;
            }
        
        }
            
        
     //  System.out.println("no ancestor found"); 
       return null; //No better ancestor found
       
  }
    
    
 public static BigInteger multiplyList(List<BigInteger> list) {
        
        BigInteger b =  B1;
        
        for (BigInteger f : list) {
           b=b.multiply(f);
        }
        return b; 
        }
    
}
