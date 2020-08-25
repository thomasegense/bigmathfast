package dk.thomasegense.bigmathfast.primes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import dk.thomasegense.bigmathfast.primes.ecm.ecm;

public class PollardRhoTest {

    
    
    
    @Test
    void compare() {

        long timeRho=0;
        long timeEcm=0;
        for (int i =1 ;i<100;i++) {                     
            String number = generateNumber(24);
                        
            long start = System.currentTimeMillis();
            ArrayList<BigInteger> factorRho = PollardRho.factor(new BigInteger(number));
            timeRho += (System.currentTimeMillis()-start);
                                
            start = System.currentTimeMillis();
            ArrayList<BigInteger> factorEcm= ecm.factor(new BigInteger(number));
            timeEcm += (System.currentTimeMillis()-start);
            assertEquals(factorRho, factorEcm);
            System.out.println(timeRho +":"+timeEcm +" number:"+number);                         
        }       
    }

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
