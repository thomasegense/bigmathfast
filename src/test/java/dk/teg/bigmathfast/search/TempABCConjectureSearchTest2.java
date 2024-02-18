package dk.teg.bigmathfast.search;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Random;

import ar.alpertron.ecm.Ecm;

public class TempABCConjectureSearchTest2 {

	//Best rest for 40 digits:196871266064657 


	//rest:11489757492193
	//A:2384291180722394495836404357826809326401
	//B:2095215178468617876085526279588298120000
	//C:4479506359191012371921930637415107446401


	/*
   Search for new perfect powers
	A =2048
	b=97970133 (23)
	c=97972181 (461)
	qua=1.324339486050807
	q=1.324339486050807 rest:1 B=97970133   A=2048 factorsA=[2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2] c=97972181 factorsC=[461, 461, 461]
	 */

	final private static Random RAND = new Random();

	final private static int[] smallPrimes = new int[]{2,3,5,7, 11, 13, 17, 19, 23, 29,31, 37, 41, 43, 47, 53, 59, 61, 67, 71,73, 79, 83, 89, 97,101,103,107,109,113, 127,131,137,139,149,151,157,163,167,173, 179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463, 467,479,487,491,499,503,509,521,523,541, 547,557,563,569,571,577,587,593,599,601, 607,613,617,619,631,641,643,647,653,659, 661,673,677,683,691,701,709,719,727,733, 739,743,751,757,761,769,773,787,797,809, 811,821,823,827,829, 839,853,857,859,863, 877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997, 1009, 1013,1019, 1021, 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093, 1097, 1103, 1109,1117,1123,1129,1151,1153,1163,1171,1181,1187,1193,1201,1213,1217,1223 };
	//final private static int[] smallPrimes1 = new int[]{2,3,5,7};
	//final private static int[] smallPrimes2 = new int[]{11,13,17};
	final private static int MIN_DIGITS=30;	
	final private static double logLimit=1.1d;

	final private static BigInteger B0= new BigInteger("0");
	final private static BigInteger B1= new BigInteger("1");
	final private static BigInteger B2= new BigInteger("2");
	final private static BigInteger B3= new BigInteger("3");
	final private static BigInteger B5= new BigInteger("5");
	final private static BigInteger B7= new BigInteger("7");
	final private static BigInteger B11= new BigInteger("11");
	final private static BigInteger B13= new BigInteger("13");
	final private static BigInteger B1021= new BigInteger("1021");
	public static void main(String[] args) {







		/*
	   BigInteger A1= new BigInteger("7");
	   BigInteger B1= new BigInteger("32761");
	   BigInteger C1= new BigInteger("32768");

	   System.out.println(getQuality(A1, B1, C1));
	   System.exit(1);
		 */	   

		/*
	   BigInteger test=new BigInteger("1234567890123456789012345678901234567890");
	   BigInteger root= Ith_Root(test, new BigInteger("10"));
	   System.out.println(calculateDifference(test, root, 10));
System.exit(1);
-*/	   
		double bestQuality=1.0d;








		BigInteger primeMax= new BigInteger("1000001");

		BigInteger currentBase= B1021;
		int maxLength=100;    

		while (true) {
			currentBase=getNextPrime(currentBase);
			System.out.println("new base:"+currentBase);	

			BigInteger currentBasePower=currentBase;

			while (currentBasePower.toString().length() <maxLength) {


				BigInteger primeInB=new BigInteger("1031");
				currentBasePower= currentBasePower.multiply(currentBase);
          //    System.out.println("currentBasePower:"+currentBasePower);

				while (primeInB.compareTo(primeMax)<0 && primeInB.compareTo(currentBasePower) <0) {
//					System.out.println(" XX currentbasePoweer " +currentBasePower +" B prime:"+primeInB);

					ArrayList<BigInteger> closets = getTwoClosestValuesFromPower(currentBasePower, Integer.parseInt(primeInB.toString()));

					//case lowest
					BigInteger lowest = closets.get(0);		    	
					BigInteger A_lowest= currentBasePower.subtract(lowest);
					double quality = getQuality(A_lowest, lowest, currentBasePower);

					//If rest is 0, then it is because root is made from smallprimes
					if (quality >= logLimit || quality > bestQuality) {
						if ( quality> bestQuality) {
							System.out.println("NEW MAX:");
							bestQuality=quality;
						}	            	   
						System.out.println("q="+quality);
						System.out.println("A:"+A_lowest);
						System.out.println("B:"+lowest);
						System.out.println("C:"+currentBasePower);	                                      	                   	            			   
					}


					//case higest
					BigInteger c_highest = closets.get(1);		    	
					BigInteger a_higest= c_highest.subtract(currentBasePower);

					quality = getQuality(a_higest,currentBasePower, c_highest);

					//If rest is 0, then it is because root is made from smallprimes
					if (quality >= logLimit || quality > bestQuality) {
					//if (true) {
						if ( quality> bestQuality) {
							System.out.println("NEW MAX:");
							bestQuality=quality;
						}
						System.out.println("q="+quality);
						System.out.println("A:"+a_higest);
						System.out.println("B:"+currentBasePower);
						System.out.println("C:"+c_highest);	                                      	                   	            			   
					}

if (quality > 1.4d) {
	System.out.println("-----------------------------------------------------------");
}



					primeInB =primeInB.nextProbablePrime();
					
					//switcbase when base are too big
				//	System.out.println("BEFORE switch base. Basepower:"+currentBasePower  +" prime:"+primeInB+  " close:"+closets.get(1));
					if (closets.get(1).compareTo(currentBasePower) >0){
					//	System.out.println("switch base. Basepower:"+currentBasePower  +" prime:"+primeInB+  " close:"+closets.get(1));
						break;
					}else {
						System.out.println("NOT switch base. Basepower:"+currentBasePower  +" prime:"+primeInB+  " close:"+closets.get(1));
						
					}
					
				}


				//    	   BigInteger gcd = EuclideanAlgorithm.gcdExtendedEuclid(C, root)[0];


			}

		}

	}
	


	//take prime to a power just below B such that next power will be above B
	private static ArrayList<BigInteger> getTwoClosestValuesFromPower(BigInteger B, int prime){

		BigInteger previous=B1;

		BigInteger primeB=new BigInteger(""+prime);

		ArrayList<BigInteger> closest= new ArrayList<BigInteger>();
		while(true) {

			BigInteger next=previous.multiply(primeB);

			if (B.compareTo(next)<0) {
				closest.add(previous);
				closest.add(next);			
				return closest;
			}

			previous=next;


		}



	}




	private static BigInteger generateNumberWithSmallFactors(int[] smallPrimes3, int minDigits) {

		BigInteger target =B1;

		while(target.toString().length() < minDigits) {
			int prime= smallPrimes3[RAND.nextInt(smallPrimes3.length)];
			target=target.multiply(new BigInteger(""+prime));		   		  
		}	  	   
		return target;

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


	public static BigInteger calculateDifference(BigInteger number, BigInteger root, int power) {

		BigInteger multiple= root.pow(power);
		return number.subtract(multiple);	 

	}


	public static BigInteger Ith_Root(BigInteger N, BigInteger K) {

		BigInteger K1 = K.subtract(BigInteger.ONE);
		BigInteger S  = N.add(BigInteger.ONE);
		BigInteger U  = N;
		while (U.compareTo(S)==-1) {
			S = U;
			U = (U.multiply(K1).add(N.divide(pow(U,K1)))).divide(K);
		}
		//String str=""+N+"^1/"+K+"="+S;System.out.println(str);
		return S;   
	} 

	public static BigInteger pow(BigInteger base, BigInteger exponent) {
		BigInteger result = BigInteger.ONE;
		while (exponent.signum() > 0) {
			if (exponent.testBit(0)) result = result.multiply(base);
			base = base.multiply(base);
			exponent = exponent.shiftRight(1);
		}
		return result;
	}


	private static double getQuality(BigInteger a, BigInteger b, BigInteger c) {

if (a.toString().length() > 60 && b.toString().length() >60) {
	
	//System.out.println("skippping factoring:"+ a + " , " +b);
	return 0; 
}
		
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


	private static BigInteger getNextPrime(BigInteger b) {

		return b.nextProbablePrime();
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


	private static BigInteger getProducts(HashSet<BigInteger> factors) {
		BigInteger prod = B1;

		for (BigInteger b: factors) {
			prod=prod.multiply(b);
		}		
		return prod;

	}
}

