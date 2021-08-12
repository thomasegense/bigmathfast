
package dk.teg.bigmathfast.util;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;


public class GaussianInteger extends Object {

	private static BigInteger B0 = new BigInteger("0");
	private static BigInteger B1 = new BigInteger("1");
	private static BigInteger B2 = new BigInteger("2");
	private static BigInteger B4 = new BigInteger("4");
    
	private BigInteger x,y;
    
    public GaussianInteger( BigInteger  u, BigInteger  v) {
        x=u;
        y=v;
    }
    public  BigInteger  real() {
        return x;
    }
    
    public  BigInteger imag() {
        return y;
    }
        
    public GaussianInteger plus(GaussianInteger w) {
        return new GaussianInteger(x.add(w.real()),y.add(w.imag()));
    }
    public GaussianInteger minus(GaussianInteger w) {
        return new GaussianInteger(x.subtract(w.real()),y.subtract(w.imag()));
    }
    
        
    public GaussianInteger times(GaussianInteger w) {
       BigInteger newx=x.multiply(w.real()).subtract(y.multiply(w.imag()));
       BigInteger newy=x.multiply(w.imag()).add(y.multiply(w.real()));
    	
       
       return new GaussianInteger(newx,newy);
    }
    
    
    public boolean isOrigin(){
    if (this.x.equals(B0) && this.y.equals(B0) ) return true;
       return false;    
    }
    	
    
    public static GaussianInteger gcd(GaussianInteger g1, GaussianInteger g2){
    	ArrayList<GaussianInteger> reminders = new ArrayList<GaussianInteger>();
    	reminders.add(g1);
    	reminders.add(g2);
    	GaussianInteger last=reminders.get(reminders.size()-1);
    	
    	while (!last.isOrigin()){
    		GaussianInteger temp1 =reminders.get(reminders.size()-2);
    		GaussianInteger temp2 =reminders.get(reminders.size()-1);
    		GaussianInteger div = temp1.div(temp2);
    		GaussianInteger reminder= temp1.minus(div.times(temp2));
    		reminders.add(reminder);
    		last = reminder;    		    		    	
    	}    	
    	return reminders.get(reminders.size()-2);
    }
 
    //(x+i*y)/(s+i*t) = ((x*s+y*t) + i*(y*s-y*t)) / (s^2+t^2)
    public GaussianInteger div(GaussianInteger w) {
     
    	//Go around BigDecimal round correct
    	BigInteger xx= w.real().multiply(w.real());
        BigInteger yy= w.imag().multiply(w.imag());
        BigInteger xx_plus_yy=xx.add(yy);
        BigDecimal xx_plus_yy_dec = new BigDecimal(xx_plus_yy);
        
        BigInteger newx = x.multiply(w.real()).add(y.multiply(w.imag()));
        BigInteger newy=  y.multiply(w.real()).subtract(x.multiply(w.imag()));

        BigDecimal newx_decimal = new BigDecimal(newx);
        BigDecimal newy_decimal = new BigDecimal(newy);
        
        BigInteger tempx= newx_decimal.divide(xx_plus_yy_dec,BigDecimal.ROUND_HALF_UP).toBigInteger();
        BigInteger tempy= newy_decimal.divide(xx_plus_yy_dec,BigDecimal.ROUND_HALF_UP).toBigInteger();
        
        return new GaussianInteger(tempx,tempy);
        
        
        
    }
    
    
	/*
	 *  eksempel 29: 2^2+5^2

start med 2:
2^((29-1)/2) = -1 mod 29

12= 2^7 mod 29

(144+1=0 mod p)
29|(12-i)(12+i)

find gcd(29,12+i) i Z[i]
a1=29
a2=12+i

a1/a2=29/(12+i)=2.4 -0.2i, afrundet 2 +0i
reminder 29-2*(12+i)= 29-24-2i=5-2i
a3=5-2i   (*)
a2/a3=  12+i/5-2i = 2 + i , 
a4=reminder (12+i)-(5-2i)(2+i)=12+i-10-5i+4i-2=0


eksempel 101: 10^1+1^2
pr�v med 2:
2^((101-1)/2) = -1 mod 101
10=  2^25 mod 101


100+1=0 mod 101
101|(10-i)(10+i)
find gcd(101,10+i) i Z[i]

a1/a2= 101/(10+i)=10-i 
a2 (*)
a3=reminder= 101-(10-i)*(10+1)=0;



eksempel 37: 1^2+6^2
pr�v med 2:
2^(18)=-1 mod 37

31= 2^9 mod 37
961+1=0  mod 37
37| (31+i)(31-i)

find gcd(37,31+i)
a1/a2= 37/(31+i)~ 1 + 0i
a3=reminder = 37-(31+i)=-6+i; (*)
a2/a3=(31+i)/(-6+i) = -5-i;
a4=reminder= (31+i)-(-6+i)*(-5-i)= (31+i)+(6-i)*(-5-i)=31+i -30 -6i+5i-1=0; 
*/
	

    
    
    /*
     * Prime MUST be 1 mod 4
     * 
     */        
    public static ArrayList<BigInteger> factorPrimeInTwoSquares (BigInteger prime){
    BigInteger prime_minus_1_div2=prime.subtract(B1).divide(B2);
    BigInteger prime_minus_1_div4=prime.subtract(B1).divide(B4);	
    
    int candidate=1; //start search from 2
    
      while (true){
       candidate++;
        BigInteger a = new BigInteger(""+candidate);
        BigInteger reminder = a.modPow(prime_minus_1_div2, prime); //always -1 og 1 mod 0
        if (B1.equals(reminder)){
        	//do nothing
        }
                                      		    	
       else{ //reminder =p-1=-1 mod p! this is what we are looking for
        break;
       }      	 
      }
            
      BigInteger a = new BigInteger(""+candidate);      
      BigInteger t = a.modPow(prime_minus_1_div4, prime);  
                
      GaussianInteger gcd = GaussianInteger.gcd( new GaussianInteger(prime,B0), new GaussianInteger(t,B1) );           
      ArrayList<BigInteger> squares = new  ArrayList<BigInteger>();
      squares.add(gcd.x.abs());
      squares.add(gcd.y.abs());
      
      return squares;
    }

    
   
    public String toString() {

       return x+" + "+y+"i";
        
    }       

}


