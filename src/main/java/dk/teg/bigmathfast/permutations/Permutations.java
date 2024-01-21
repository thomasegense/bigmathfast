package dk.teg.bigmathfast.permutations;

import java.util.ArrayList;

public class Permutations <E> {
	
	
  	/**
     * 	
     * @param elements
     * @return
     */

	public  ArrayList<ArrayList<E>> getAllPermutations(ArrayList<E> elements) {
		if (elements.size() == 1) {
			ArrayList<ArrayList<E>> permutations = new ArrayList<ArrayList<E>>();
			permutations.add(elements);
			return permutations;
		} else {
			ArrayList<ArrayList<E>> list = new ArrayList<>();
			for (E element : elements) {
				ArrayList<E> subList = new ArrayList<E>(elements);
				subList.remove(element);
				ArrayList<ArrayList<E>> subListNew = getAllPermutations(subList);
				for (ArrayList<E> _list : subListNew) {
					ArrayList<E> local = new ArrayList<>();
					local.add(element);
					local.addAll(_list);
					list.add(local);
				}
			}
			return list;
		}
	}

	
}
