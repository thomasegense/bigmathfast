package dk.teg.bigmathfast.search;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import ar.alpertron.ecm.Ecm;

public class TempABCConjectureSearchTest {


	final private static int[] smallPrimes = new int[]{2,3,5,7, 11, 13, 17, 19, 23, 29,31, 37, 41, 43, 47, 53, 59, 61, 67, 71,73, 79, 83, 89, 97,101,103,107,109,113, 127,131,137,139,149,151,157,163,167,173, 179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463, 467,479,487,491,499,503,509,521,523,541, 547,557,563,569,571,577,587,593,599,601, 607,613,617,619,631,641,643,647,653,659, 661,673,677,683,691,701,709,719,727,733, 739,743,751,757,761,769,773,787,797,809, 811,821,823,827,829, 839,853,857,859,863, 877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997, 1009, 1013,1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093, 1097, 1103, 1109,1117,1123,1129,1151,1153,1163,1171,1181,1187,1193,1201,1213,1217,1223 };

	final private static BigInteger B0= new BigInteger("0");
	final private static BigInteger B1= new BigInteger("1");

	/*
	        A=313732061064595735489888741011166459055322223122499633297668272725991 =  71 ^37
			b=21961244274521701484292211870781652133872555618574974330836779090819370=2 × 5 × 7 × 71^37
			C=22274976335586297219782100611792818592927877841697473964134447363545361= 71^68

			rad(abc)= 4970
			qua=19.03166520028975
	 */

	public static void main(String[] args) {

		
		
		BigInteger C = new BigInteger("97017233784872162402203715694511008214034825609281"); //11^48
		
		BigInteger B= new BigInteger("1016120561824376019452881448012369820560120016001");
		BigInteger A=  new BigInteger("96001113223047786382750834246498638393474705593280");

		
		double quality = getQuality(A, B, C);
		System.out.println(quality);
		
		System.exit(1);

		//BigInteger rest = factorOutSmallPrimes(new BigInteger("342334232312322111111112"));
		//System.out.println(rest);


		//First loop over  prime^factor. prime is fixed, the factor increased
         double bestQuality=0d;
		for (int i =1;i<smallPrimes.length;i++) {
			BigInteger smallPrimeC= new BigInteger(""+smallPrimes[i]);
		 System.out.println("new c base:"+smallPrimeC);
			//Define C
			BigInteger c=B1;     
			ArrayList<BigInteger> factorsC= new ArrayList<BigInteger>();
			for (int factorc =1;factorc<200; factorc++) {
				c=c.multiply(smallPrimeC);
				factorsC.add(smallPrimeC);

	//		System.out.println("new c multi:"+factorc);

				for (int m =0;m<smallPrimes.length;m++) { //smaller prime than in c
					BigInteger smallPrimeA= new BigInteger(""+smallPrimes[m]);
					if (smallPrimeA.compareTo(c)>=0) {
			//System.out.println (" fast SKIP A="+smallPrimeA +" c="+c +" factorsC="+c);
						break;
					}
					//now define A
					BigInteger a=B1;
					ArrayList<BigInteger> factorsA= new ArrayList<BigInteger>();

					for (int factora =1;factora<200;factora++) {
						a=a.multiply(smallPrimeA);
						factorsA.add(smallPrimeA);

						if (a.compareTo(c)>=0) {
				//System.out.println (" SKIP A="+a +" factorsA="+ factorsA +" c="+c +" factorsC="+c);
							break; //a must be smaller than c
						}

						

						
						
						//now calculate quality
				      BigInteger b=c.subtract(a);

					  //Skip if not coprime
				      if(smallPrimeA.equals(smallPrimeC)){
				    	  // B must not have same factor
				    	  if(b.mod(smallPrimeA).equals(B0)) {
				    		  
				    //		System.out.println("Skipping due to not co-prime. A="+a +" b="+b +" c="+c);  
				           continue;		  
				    	  }
				    	  
				    	  
				    	  
				    	  
				    	  
				      }
				      
				      BigInteger rest = factorOutSmallPrimes(b);
						//So we do not factor a huge number
				      if (rest.toString().length() >35) {
						//	System.out.println("skipping too large reminder:"+rest);
							continue; 
						}
				      
						double q=getQuality(factorsA,a, factorsC,c);
					
						if (q> 1.2 || q >bestQuality) {
						bestQuality=q;
							System.out.println ("q="+q + " rest:"+rest +" B="+b +"   A="+a +" factorsA="+ factorsA +" c="+c +" factorsC="+factorsC  );
						}

					}


				}

			}
			
		}



		/*
		int cBase=101;  // must be prime
		int pow=97;		
		BigInteger c = B1; 


		ArrayList<BigInteger> factors_c = new ArrayList<BigInteger>();
		BigInteger cBaseBig =new BigInteger(""+cBase);
		for (int i=1;i<=pow;i++) {			
			c=c.multiply(cBaseBig);
			factors_c.add(cBaseBig);			 
		}




		int a_prime=7;
		BigInteger a =B1;
		BigInteger a_primeBig= new BigInteger(""+a_prime);
		ArrayList<BigInteger> factors_a = new ArrayList<BigInteger>();
		for (int a_multi=1;a_multi<150;a_multi++){
			factors_a.add(a_primeBig);
			a=a.multiply(a_primeBig);				   	
			getQuality(factors_a, a, factors_c, c);
		}

		//System.out.println(getQuality(new BigInteger("9"), new BigInteger("644047751340851140088587392493977721385325823140778332422174414330781070470655497437197221414678165288981040"), new BigInteger("644047751340851140088587392493977721385325823140778332422174414330781070470655497437197221414678165288981049")));
		 */
	}









	/**
	 * Fast method, if quality is very low, it will just return 0 to save time   
	 */


	private static double getQuality(ArrayList<BigInteger> factors_a,  BigInteger a, ArrayList<BigInteger> factors_c, BigInteger c) {
		BigInteger b=c.subtract(a);
		if(b.compareTo(B0)<0) {
			System.out.println("Can not substract more");
			System.exit(1);
		}

		BigInteger rest = factorOutSmallPrimes(b);
		if (rest.toString().length() > 10) {

			//System.out.println("skipping:"+rest);			
			return 0d;
		}



		ArrayList<BigInteger> factors_b = Ecm.factor(b);

		double quality = getQuality(factors_a, factors_b, factors_c, c);
		if (quality > 1.3d) {

			System.out.println("A="+a);
			System.out.println("b="+b + " ("+getLargest(factors_b) +")");			
			System.out.println("c="+c + " ("+getLargest(factors_c) +")");		
			System.out.println("qua="+quality);



		}
		else {
			//	System.out.println(quality);

		}
		return quality;

		//System.out.println(getQuality(new BigInteger("9"), new BigInteger("644047751340851140088587392493977721385325823140778332422174414330781070470655497437197221414678165288981040"), new BigInteger("644047751340851140088587392493977721385325823140778332422174414330781070470655497437197221414678165288981049")));						

	}



	private static double getQuality(BigInteger a, BigInteger b, BigInteger c) {


		ArrayList<BigInteger> factora = Ecm.factor(a);
		ArrayList<BigInteger> factorb = Ecm.factor(b);
		ArrayList<BigInteger> factorc = Ecm.factor(c);



		return getQuality(factora, factorb, factorc, c);

	}



	private static double getQuality(ArrayList<BigInteger> factora, ArrayList<BigInteger> factorb, ArrayList<BigInteger> factorc, BigInteger c) {


		HashSet<BigInteger> factor_unique = new HashSet<BigInteger>();
		factor_unique.addAll(factora);
		factor_unique.addAll(factorb);	
		factor_unique.addAll(factorc);


		BigInteger products = getProducts(factor_unique);


		double n= BigIntLog(c,2);  
		double d= BigIntLog(products,2);

		return n/d;

	}


	private static BigInteger getProducts(HashSet<BigInteger> factors) {
		BigInteger prod = B1;

		for (BigInteger b: factors) {
			prod=prod.multiply(b);
		}		
		return prod;

	}

	private static double BigIntLog(BigInteger bi, double base) {
		// Convert the BigInteger to BigDecimal
		BigDecimal bd = new BigDecimal(bi);
		// Calculate the exponent 10^exp
		BigDecimal diviser = new BigDecimal(10);
		diviser = diviser.pow(bi.toString().length()-1);
		// Convert the BigDecimal from Integer to a decimal value
		bd = bd.divide(diviser);
		// Convert the BigDecimal to double
		double bd_dbl = bd.doubleValue();
		// return the log value
		return (Math.log10(bd_dbl)+bi.toString().length()-1)/Math.log10(base);
	}

	private static BigInteger getLargest(ArrayList<BigInteger> c){
if (c.size() ==0) {
	return B1;
}
		return Collections.max(c);

	}


	private static BigInteger factorOutSmallPrimes (BigInteger b) {
		BigInteger rest=b;

		for (int i=0;i<smallPrimes.length;i++) {
			BigInteger smallPrime=new BigInteger(""+smallPrimes[i]);
			//System.out.println("testing with:"+smallPrime);
			BigInteger modulus= rest.mod(smallPrime);
			if (modulus.equals(B0)) {
				//System.out.println("factor:"+smallPrime);
				//	System.out.println("rest:"+rest);
				rest=rest.divide(smallPrime);
				i--; //divide again
			}

		}

		return rest;
	}



}
