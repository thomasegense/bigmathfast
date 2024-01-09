package dk.teg.bigmathfast.partitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * Class to find all partitions of a given number. 
 * For example all partions of 4 are:
 * 
 * @see  <a href="https://www.wikiwand.com/en/Partition_(number_theory)">Paritions</a>    
 * 
 * @author Thomas Egense
 */
 

public class Partitions {


	
	

    public static void main(String[] args) throws Exception {    
    	long start= System.currentTimeMillis();
    	System.out.println(partition(80).size() +" millis:"+(System.currentTimeMillis()-start));
    	
    }
	
    
    /**
     * Find all partitions of a given integer.
     * For example 4 has the following 5 partitions: [ [4], [3, 1], [2, 2], [2, 1, 1], [1, 1, 1, 1] ]
     * 
     * Performance
     * n=50 has 204226 and take 500 millis
     * n=70 has 4087968 partitions and take 3 seconds
     * n=80 has 15796476 partitions and take 22 seconds
     * @param n The integer to find all partitions for. 
     * @return
     */
	
    public static ArrayList<ArrayList<Integer>> partition(int n) {
    	ArrayList<String> partitionsStr = new ArrayList<String>();    	
    	partition(n, n, "", partitionsStr);
    	    	
    	//Convert to integers
    	ArrayList<ArrayList<Integer>> list = convert(partitionsStr);
    	return list;
    	
    	
    	
    }
    private static void partition(int n, int max, String prefix, ArrayList<String> partitions ) {
        if (n == 0) {          
            partitions.add(prefix);
            return;
        }

        for (int i = Math.min(max, n); i >= 1; i--) {
            partition(n-i, i, prefix + " " + i, partitions);
        }
    }

    //TODO maybe make public
    private static ArrayList<String[]> filterUniqueByNumberOfTermsAndMaxNumber(int numberOfTerms, int maxNumber, ArrayList<String> partitions) {
   	ArrayList<String[]> matchedPartitions= new ArrayList<String[]>(); 
	
    	for (String partition: partitions) {
    		    		
    		String[] tokens = partition.trim().split(" ");
    		//Check max number
    	     if(Integer.valueOf(tokens[0]) > maxNumber) {
    	    	 continue;
    	     }
    		
    		//Check number of terms
    		if (tokens.length != numberOfTerms) {
    			continue;    			
    		}

    		//Check no duplicates
           if (!hasDuplicates(tokens)) {    		    		
    		matchedPartitions.add(tokens);
           }
    	}
    	
    	
    	
    return matchedPartitions;	
    }
    
   
    
    //They are sorted largest first
    //TODO 
    private static boolean hasDuplicates(String[] tokens) {
    	
    	for (int i=0;i<tokens.length-1;i++) {
    		if (tokens[i].equals(tokens[i+1])) {
    			return true;
    		}
    		
    	}
    	return false;
    }
    
    
    //TODO move new class
    private static ArrayList<ArrayList<Integer>> getAllPermutations(ArrayList<Integer> ints) {
        if (ints.size() == 1) {
            ArrayList<ArrayList<Integer>> list = new ArrayList<>();
            list.add(ints);
            return list;
        } else {
            ArrayList<ArrayList<Integer>> list = new ArrayList<>();
            for (Integer i: ints) {
                ArrayList<Integer> subList = new ArrayList<>(ints);
                subList.remove(i);
                ArrayList<ArrayList<Integer>> subListNew = getAllPermutations(subList);
                for (ArrayList<Integer> _list: subListNew) {
                    ArrayList<Integer> local = new ArrayList<>();
                    local.add(i);
                    local.addAll(_list);
                    list.add(local);
                }
            }
            return list;
        }
    }
      
    private static ArrayList<ArrayList<Integer>> convert(ArrayList<String> paritions){
    	ArrayList<ArrayList<Integer>>  list = new ArrayList<ArrayList<Integer>>();
    
    	for (String partitionStr : paritions) {
    	ArrayList<Integer> partition = new ArrayList<Integer>();
    	String[] tokens = partitionStr.trim().split(" ");
    	for (String s : tokens) {
    		partition.add(Integer.valueOf(s));
    	}
    	list.add(partition);
    	}
    	return list;
    }
    
	
   
}
 