package dk.teg.bigmathfast.primes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ar.alpertron.ecm.Ecm;


public class PrimeFactoringBenchMark {

            
    @Test
    void compare() {

        long timeRho=0;
        long timeEcm=0;
        for (int i =1 ;i<10000;i++) {                     
            String number = generateNumber(70);
            
            //number="90105590165517928753889708611168689116043190507997";
            System.out.println(number);
            long start = System.currentTimeMillis();
            ArrayList<BigInteger> factorRho = Ecm.factor(new BigInteger(number));
            timeRho += (System.currentTimeMillis()-start);
                                
            
            start = System.currentTimeMillis();
            ArrayList<BigInteger> factorEcm= Ecm.factor(new BigInteger(number));
            timeEcm += (System.currentTimeMillis()-start);
            assertEquals(factorRho, factorEcm);
            System.out.println(timeRho +":"+timeEcm +" number:"+number);
            

            
            //System.out.println(timeEcm +" number:"+number);
                                     
        }       
    }

    /*
    @Test
    void testDeadlock() {
         //Before test set: int numberThreads = 4;
         //in the ECM.java class.      
        //After a few loops there will be a deadlock
        for (int i =1 ;i<100;i++) {                     
            String number = "40204407726806272821275765815528323547646475263864";            
            System.out.println("Factoring:"+number);           
            ArrayList<BigInteger> factors = Ecm.factor(new BigInteger(number));
            System.out.println("Factors:"+factors);        
        }       
    }
    */
    
 private static String generateNumber(int digits) {
     StringBuffer b = new StringBuffer();
     for (int i=0;i<digits;i++) {
         b.append((int)(Math.random()*10));
     }
     
 
 
 
     
     String number = b.toString();
     while (number.startsWith("0")) {
         number=number.substring(1);
     }     
    return number;
             
     
 }
}
