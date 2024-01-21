package dk.teg.bigmathfast.parititions;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import dk.teg.bigmathfast.partitions.Partitions;

public class PartitionsTest {

	@Test
	    void testPartitions() {
		 		  
		 //Pr defition this has the empty set as  solution;
		 // solution{}
		 ArrayList<ArrayList<Integer>> partitions0 = Partitions.partition(0);		
		 assertEquals(1,partitions0.size());
		 assertEquals(0,partitions0.get(0).size());
		 
		 // solution {1}
		 ArrayList<ArrayList<Integer>> partitions1 = Partitions.partition(1);		
		 assertEquals(1,partitions1.size());
		 assertEquals(1,partitions1.get(0).get(0));
		 
		 ArrayList<ArrayList<Integer>> partitions10 = Partitions.partition(10);		
		 assertEquals(42,partitions10.size());
		 	 		 
		 ArrayList<ArrayList<Integer>> partitions20 = Partitions.partition(20);		
		 assertEquals(627,partitions20.size());
		 
		 //test sum=20  for partitions20 solutions
		 for (ArrayList<Integer> list : partitions20) {
			 int sum = list.stream().reduce(0, Integer::sum);
			 assertEquals(20,sum);			
		 }
		 		 
	 }
	 
	 
	 @Test
	    void testPartitionsUniqueOnly() {
		 		  		 
		 ArrayList<ArrayList<Integer>> partitions10 = Partitions.partition(10,true);		
		 assertEquals(10,partitions10.size());

		 //test every solution only has unique numbers
		 for (ArrayList<Integer> list : partitions10) {
			 HashSet<Integer> set = new HashSet<Integer>();
			 set.addAll(list);
			 assertEquals(list.size(),set.size());			 			 
		 }
		 		 
	 }
	 
	
}
