package dk.teg.bigmathfast.primes;

import java.math.BigInteger;
import java.util.ArrayList;

import ar.alpertron.ecm.Ecm;



public class Test {

    public static void main(String[] args) {
       
             
        
    //Before test set: int numberThreads = 4;
         //in the ECM.java class.      
        //After a few loops there will be a deadlock
        for (int i =1 ;i<10000;i++) {          
          long start = System.currentTimeMillis();           
                        
                        
                        
            String number70Digits = "2008366610044614145105509426936481148630631765118331491742083502567441";                    
            String number80Digits = "93035149443954345347665179408833277091909532522394543659489519897196854705698057";
            String number90Digits = "235619162309580984868967318620943039846576548536713751373304739395055583551615448989006587";
            
            
            String numberToFactor=number90Digits;
            
            System.out.println("Starting factoring for:"+numberToFactor);            
            ArrayList<BigInteger> factors = Ecm.factor(new BigInteger(numberToFactor),8);
            System.out.println("Factors:"+factors + " time:"+(System.currentTimeMillis()-start));        
        
        }       
    }
        
   

    
}
