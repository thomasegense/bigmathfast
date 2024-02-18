package dk.teg.bigmathfast.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class BigMathFastUtilTest {

    
    
    @Test
    void  testBigIntLogaritm() {      
        assertTrue( BigMathFastUtil.bigIntLog(new BigInteger("10"), 10) - 1d <0.0001);
        assertTrue( BigMathFastUtil.bigIntLog(new BigInteger("2"), 2) - 1d <0.0001);
        assertTrue( BigMathFastUtil.bigIntLog(new BigInteger("100"), 10) - 2d <0.0001);               
        assertTrue( BigMathFastUtil.bigIntLog(new BigInteger("100"), 2.7182818d) - 4.60517d <0.0001); //natural log (e)    
    }

    
    
    /**
     *  Important to test the optimization bi-section search method is correct 
     */
    @Test
    void  findBestMatchOfAddingTwoComparedToThirdBisection() {       
        int numberInSet=500; //brute force is n^3, so will be slow fast. 500 takes 1 - 2 seconds
        
        HashSet<BigInteger> intSet= new HashSet<BigInteger>(); //Set so unique values
        for (int i=0;i<numberInSet;i++) {
            intSet.add(TestUtils.generateRandomNumber(10)); //Random 10 digits numbers
        }
        
        if (intSet.size() <3) { //just to show I thought of this :)
            return;
        }
        
        ArrayList<BigInteger> intList= new ArrayList<BigInteger>(); 
        intList.addAll(intSet);        
        long start=System.currentTimeMillis();
        ArrayList<BigInteger> bestDiffs = BigMathFastUtil.findBestMatchOfAddingTwoComparedToThirdBisection(intList);
        long timeBisection=System.currentTimeMillis()-start;
     
        BigInteger bestDiff=bestDiffs.get(3); //Last element is difference        
        
        Collections.sort(intList); //lowest first
        //Compare to slow brute forces search
        BigInteger bestDiffBruteForce = intList.get(2).subtract(intList.get(1)).subtract(intList.get(0));// Just start value
        bestDiffBruteForce=bestDiffBruteForce.abs();
        
        start=System.currentTimeMillis();
        for (int i=0;i<intList.size()-2;i++) {
        
            for (int j=i+1;j<intList.size()-1;j++) {                
                for (int k=j+1;k<intList.size();k++) {
                    BigInteger diff=intList.get(k).subtract(intList.get(j)).subtract(intList.get(i));// Just start value
                    diff=diff.abs();
                    if (diff.compareTo(bestDiffBruteForce) <0) {               
                        bestDiffBruteForce=diff;
                    }                    
                }
            }
            
        }
        long timeBruteForce=System.currentTimeMillis()-start;
        
        assertEquals(bestDiff, bestDiffBruteForce);
        System.out.println("Min. diff benchmark: bi-section:"+timeBisection +" brute force:"+timeBruteForce +" minimum difference:"+bestDiff);
                
         
    }
    
    
}
