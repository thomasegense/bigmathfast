package dk.teg.bigmathfast.primes;

import java.math.BigInteger;
import java.util.ArrayList;

import ar.alpertron.ecm.Ecm;



public class DeadlockTest {

    public static void main(String[] args) {
       
             
        
    //Before test set: int numberThreads = 4;
         //in the ECM.java class.      
        //After a few loops there will be a deadlock
        for (int i =1 ;i<10000;i++) {          
          long start = System.currentTimeMillis();           
                        
                        
            String number60Digits = "40204407726806272821275765815528323547646475263864"; //This one is easy to produce deadlock. takes 200 millis            
            String number70Digits = "2008366610044614145105509426936481148630631765118331491742083502567441"; // 16 seconds with 8 threads                    
            String number80Digits = "93035149443954345347665179408833277091909532522394543659489519897196854705698057"; 
            String number90Digits = "235619162309580984868967318620943039846576548536713751373304739395055583551615448989006587"; // takes 30 minutes with 8 threads
            
            
            String numberToFactor=number70Digits; // <---- change to 60Digits to get deadlock fast
            
            System.out.println("Starting factoring for:"+numberToFactor);            
            ArrayList<BigInteger> factors = Ecm.factor(new BigInteger(numberToFactor),8);  // 8 threads
            System.out.println("Factors:"+factors + " time:"+(System.currentTimeMillis()-start));        
        
        }       
    }
        
   

    
}
