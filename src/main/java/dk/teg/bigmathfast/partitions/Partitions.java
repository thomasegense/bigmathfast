package dk.teg.bigmathfast.partitions;

import java.util.ArrayList;

/**
 * Class to find all partitions of a given number. For example all partions of 4
 * are:
 * 
 * @see <a href=
 *      "https://www.wikiwand.com/en/Partition_(number_theory)">Paritions</a>
 * 
 * @author Thomas Egense
 */

public class Partitions {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		System.out.println(partition(80).size() + " millis:" + (System.currentTimeMillis() - start));

	}

	/**
	 * Find all partitions of a given integer. For example 4 has the following 5 partitions: <br>
	 *  [ [4], [3, 1], [2, 2], [2, 1, 1], [1, 1, 1, 1] ]
	 * <br>
	 * Performance:<br>
	 *  n=50 has 204226 and take 500 millis<br>
	 *  n=70 has 4087968 partitions and take 3 seconds<br>
	 *  n=80 has 15796476 partitions and take 22 seconds<br>
	 * 
	 * @param n The integer to find all partitions for.
	 * @return
	 */

	public static ArrayList<ArrayList<Integer>> partition(int n) {

		if (n == 0) { // Pr definition
			ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> partition0 = new ArrayList<Integer>();
			list.add(partition0);
			return list;
		}

		ArrayList<String> partitionsStr = new ArrayList<String>();
		partition(n, n, "", partitionsStr);

		// Convert to integers
		ArrayList<ArrayList<Integer>> list = convert(partitionsStr);
		return list;
	}

	/**
	 * Find all partitions for a given integer. Option to only include solutions having different numbers
	 * 
	 * See {@link Partitions#partition(Integer)} for more documentation
	 * 
	 * 
	 * @param n            The integer to find all partitions for.
	 * @param noDuplicates If true the list will consist of different integers.
	 *                     Solutions with same integer repeated will be removed.
	 * @return
	 */
	public static ArrayList<ArrayList<Integer>> partition(int n, boolean noDuplicates) {
		ArrayList<ArrayList<Integer>> allPartitions = partition(n);
		if (noDuplicates) {
			return filterUniqueOnly(allPartitions);
		} else {
			return allPartitions;
		}
	}

	/*
	 * Recursive method
	 * 
	 */
	private static void partition(int n, int max, String prefix, ArrayList<String> partitions) {
		if (n == 0) {
			partitions.add(prefix);
			return;
		}

		for (int i = Math.min(max, n); i >= 1; i--) {
			partition(n - i, i, prefix + " " + i, partitions);
		}
	}

	// TODO Maybe make public
	/*
	 * private static ArrayList<String[]>
	 * filterUniqueByNumberOfTermsAndMaxNumber(int numberOfTerms, int
	 * maxNumber,ArrayList<String> partitions) { ArrayList<String[]>
	 * matchedPartitions = new ArrayList<String[]>();
	 * 
	 * for (String partition : partitions) {
	 * 
	 * String[] tokens = partition.trim().split(" "); // Check max number if
	 * (Integer.valueOf(tokens[0]) > maxNumber) { continue; }
	 * 
	 * // Check number of terms if (tokens.length != numberOfTerms) { continue; }
	 * 
	 * // Check no duplicates if (!hasDuplicates(tokens)) {
	 * matchedPartitions.add(tokens); } }
	 * 
	 * return matchedPartitions; }
	 */

	// They are sorted largest first
	// TODO remove this and use the integer
	private static boolean hasDuplicates(String[] tokens) {

		for (int i = 0; i < tokens.length - 1; i++) {
			if (tokens[i].equals(tokens[i + 1])) {
				return true;
			}

		}
		return false;
	}

	/**
	 * Remove all lists having duplicate entries Optimized using the fact that the lists are sorted.
	 * 
	 */
	private static ArrayList<ArrayList<Integer>> filterUniqueOnly(ArrayList<ArrayList<Integer>> lists) {
		ArrayList<ArrayList<Integer>> filtered = new ArrayList<ArrayList<Integer>>();

		for (ArrayList<Integer> list : lists) {
			if (!hasDuplicates(list)) {
				filtered.add(list);
			}
		}
		return filtered;
	}

	// They are sorted largest first
	private static boolean hasDuplicates(ArrayList<Integer> numbers) {
		for (int n = 0; n < numbers.size() - 1; n++) {
			if (numbers.get(n).equals(numbers.get(n + 1))) {
				return true;
			}
		}
		return false;
	}

	// TODO move new class
	private static ArrayList<ArrayList<Integer>> getAllPermutations(ArrayList<Integer> ints) {
		if (ints.size() == 1) {
			ArrayList<ArrayList<Integer>> list = new ArrayList<>();
			list.add(ints);
			return list;
		} else {
			ArrayList<ArrayList<Integer>> list = new ArrayList<>();
			for (Integer i : ints) {
				ArrayList<Integer> subList = new ArrayList<>(ints);
				subList.remove(i);
				ArrayList<ArrayList<Integer>> subListNew = getAllPermutations(subList);
				for (ArrayList<Integer> _list : subListNew) {
					ArrayList<Integer> local = new ArrayList<>();
					local.add(i);
					local.addAll(_list);
					list.add(local);
				}
			}
			return list;
		}
	}

	private static ArrayList<ArrayList<Integer>> convert(ArrayList<String> paritions) {
		ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();

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
