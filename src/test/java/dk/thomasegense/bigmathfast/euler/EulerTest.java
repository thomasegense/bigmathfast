package dk.thomasegense.bigmathfast.euler;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class EulerTest {

    
    @Test
    void testEulerTotient() {
     //Just compare some values.        
        assertEquals(new BigInteger("1"), EulerTotient.eulerTotient( new BigInteger("2")));                
        assertEquals(new BigInteger("2"), EulerTotient.eulerTotient( new BigInteger("3")));
        assertEquals(new BigInteger("8"), EulerTotient.eulerTotient( new BigInteger("16")));                
        assertEquals(new BigInteger("12"), EulerTotient.eulerTotient( new BigInteger("42")));
        assertEquals(new BigInteger("192"), EulerTotient.eulerTotient( new BigInteger("576")));        
        assertEquals(new BigInteger("9900989745690363342983381554848000"), EulerTotient.eulerTotient(new BigInteger("10000000000000000000000000000000001"))); //Hard factorization       
        assertEquals(new BigInteger("711074604856637023865256874613665274265600"), EulerTotient.eulerTotient(new BigInteger("3031634148236289733373855928919180891127808")));               
    }
            
    @Test
    void testInverseEulerTotientBruteForce() {
        
        //MaxInverseValue and maxValue need to be picked carefully to make sure all bruteforce solutions has been calculted
        //If maxInverseValue is not set high enough, the solutions may be incomplete.
        
        //Large test. Takes some minutes.
        //int maxInverseValue= 3000000;
        //int maxValue=500000;
        
        //Resonable time for a unittest
         int maxInverseValue= 300000;
         int maxValue=50000;
        
        HashMap<BigInteger, ArrayList<BigInteger>> inverseEulerMap = generateBruteForceInverseEulerMap(maxInverseValue);
        
        
        for (int i = 1;i<maxValue;i++) {
            BigInteger current = new BigInteger (""+i);            
            ArrayList<BigInteger> solutionsCalculated = EulerTotient.inverseEulerTotient(current, true);
            Collections.sort(solutionsCalculated);
            ArrayList<BigInteger> solutionsBruteForced = inverseEulerMap.get(current);
            if (solutionsBruteForced == null) {
                solutionsBruteForced = new ArrayList<BigInteger>(); //empty set
            }
                       
            assertEquals(solutionsBruteForced.size(),solutionsCalculated.size(), "Not same number of solutions in invphi for:"+current);
            
            //checke elements are the same
            for (BigInteger sol:solutionsCalculated) {
                assertTrue(solutionsBruteForced.contains(sol) , "invphi for "+i+ " is missing value:"+sol);
             }               
                
            }                        
        }
            
    
    /*
     * Will generate inverse Euler map but only for maximum values up to maxInverseValue.
     * This means the map can be incomplete for the larger values. 
     * A rule of thumb for the small numbers in this test it is complete for up to 0.25*maxInverseValue. 
     */    
    private static  HashMap<BigInteger, ArrayList<BigInteger>> generateBruteForceInverseEulerMap(int maxInverseValue) {
        HashMap<BigInteger, ArrayList<BigInteger>> inverseEulerMap = new HashMap<BigInteger, ArrayList<BigInteger>>();  
        for (int i = 1;i< maxInverseValue;i++) {
            BigInteger current = new BigInteger(""+i);
            
            BigInteger eulerTotient = EulerTotient.eulerTotient(current);
            
            ArrayList<BigInteger> solutions = inverseEulerMap.get(eulerTotient);
            if (solutions == null){
                 solutions = new ArrayList<BigInteger>();
                 solutions.add(current);
                 inverseEulerMap.put(eulerTotient, solutions);                
            }
            else {
                solutions.add(current);
            }                                                            
        }       
                                
       return inverseEulerMap; 
    }
    
}

