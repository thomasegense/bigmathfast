package dk.teg.bigmathfast.permutations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class PermutationsTest {

	
	 
	 @Test
	    void testPermutations() {
		 
		 Permutations<String> p = new Permutations<String>();
		 
		 ArrayList<String> list = new ArrayList<String>();
		 list.add("A");
		 list.add("B");
		 list.add("C");
		 list.add("D");
		 		 
		 ArrayList<ArrayList<String>> allPermutations = p.getAllPermutations(list);
		 assertEquals(24,allPermutations.size());
		 
		 //Test they are all different. Make string by concatenate elements
		// Convert elements to strings and concatenate them, separated by commas		 
		 HashSet<String> different = new HashSet<String>();
		 
		 for (ArrayList<String> perm: allPermutations) {
			 String joined = perm.stream().map(Object::toString).collect(Collectors.joining("")); //Value will be 'ABCD' etc.
             different.add(joined) ;			 			
		 }
		 assertEquals(24,different.size());				 
	 }
		 		  		 
	
}

