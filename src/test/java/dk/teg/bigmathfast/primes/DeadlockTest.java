package dk.teg.bigmathfast.primes;

import java.math.BigInteger;
import java.util.ArrayList;

import ar.alpertron.ecm.Ecm;


/*
 * Test class to show multithreading will give deadload when factorizing... Code to complex to fix unfortunately.
 * 
 */
public class DeadlockTest {

    public static void main(String[] args) {
   
        //After a few loops there will be a deadlock for the 60digits. Harder to producer for the higher numbers
        for (int i =1 ;i<1000;i++) {          
          long start = System.currentTimeMillis();                                   
                        
            String number60Digits = "40204407726806272821275765815528323547646475263864"; //This one is easy to produce deadlock. takes 200 millis            
            String number70Digits = "2008366610044614145105509426936481148630631765118331491742083502567441"; // 16 seconds with 8 threads. harder to produce deadlock but possible.                    
            String number80Digits = "93035149443954345347665179408833277091909532522394543659489519897196854705698057"; // takes 2 minutes with 8 threads
            String number90Digits = "235619162309580984868967318620943039846576548536713751373304739395055583551615448989006587"; // takes 30 minutes with 8 threads
            String numberRSA100=    "1522605027922533360535618378132637429718068114961380688657908494580122963258952897654000350692006139";
            
            String numberToFactor=number60Digits;
            System.out.println("Starting factoring for:"+numberToFactor);            
            ArrayList<BigInteger> factors = Ecm.factor(new BigInteger(numberToFactor),1);  // 8 threads
            System.out.println("Factors:"+factors + " time:"+(System.currentTimeMillis()-start));        
        
        }       
    }
        
   

    
}
