package ruzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Alan Buttars, AlanButtars.com
 */
public class Ruzzle {

	private static char[][] grid = new char[4][4];
	private static List<String> dictionary = new ArrayList<String>();
	private static Set<String> foundWords = new HashSet<String>();

	public static void main(String[] args) throws Exception {
		readInDictionary();
		readInGrid();
		solve();
		
		for (String found : foundWords) {
			System.out.println(found);
		}
	}

	private static void readInDictionary() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("dictionary.txt"));
		while (scanner.hasNext()) {
			dictionary.add(scanner.nextLine().trim());
		}
		scanner.close();
	}

	private static void readInGrid() {
		System.out.println("Enter the grid like this:");
		System.out.println("\tabcd");
		System.out.println("\tefgh");
		System.out.println("\tijkl");
		System.out.println("\tmnop");
		Scanner scanner = new Scanner(System.in);
		String nextline = null;
		for (int row = 0; row < 4; row++) {
			nextline = scanner.nextLine();
			char[] chars = nextline.toCharArray();
			for (int col = 0; col < 4; col++) {
				grid[row][col] = chars[col];
			}
		}
		scanner.close();
	}

	private static void solve() {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				StringBuilder stringSoFar = new StringBuilder(grid[row][col]);
				boolean[][] visitedNodes = new boolean[4][4];
				visitedNodes[row][col] = true;
				solveWithStringSoFar(row, col, stringSoFar, visitedNodes);
			}
		}
	}

	/**
	 * Recursively finds dictionary words with a given position and
	 * string word constructed so far.
	 * @param startingRow starting row position of the word
	 * @param startingCol starting column position of the word
	 * @param currentRow current row position of the built grid path
	 * @param currentCol current column position of the built grid path
	 * @param stringSoFar constructed word so far
	 * @param pathSoFar constructed grid path so far
	 */
	private static void solveWithStringSoFar(int currentRow, int currentCol, StringBuilder stringSoFar,
			boolean[][] visitedNodes) {
		String string = stringSoFar.toString();
		if (maybeInDictionary(string)) {
			for (Node node : getAvailableNodes(currentRow, currentCol, visitedNodes)) {
				StringBuilder sb = new StringBuilder(string);
				sb.append(grid[node.row][node.col]);
				solveWithStringSoFar(node.row, node.col, sb, visitedNodes);
			}
		}
		if (isInDictionary(string)) {
			foundWords.add(string);
		}
	}

	private static boolean isInDictionary(String searchWord) {
		int startIndex = 0;
		int endIndex = dictionary.size() - 1;
		return isInDictionary(searchWord, startIndex, endIndex);
	}

	private static boolean isInDictionary(String searchWord, int startIndex, int endIndex) {
		int midIndex = (startIndex + endIndex) / 2;
		String midWord = dictionary.get(midIndex);
		int compare = searchWord.compareTo(midWord);
		if (startIndex == endIndex) {
			return compare == 0;
		}
		else if (compare < 0) {
			return isInDictionary(searchWord, startIndex, midIndex);
		}
		else if (compare > 0) {
			return isInDictionary(searchWord, midIndex + 1, endIndex);
		}
		else {
			return true;
		}
	}

	/**
	 * Performs a binary search to determine whether a given search term may be
	 * in the pre-loaded dictionary.
	 * @param searchWord
	 * @return true if the word, once completed, could occur in the
	 *         dictionary (e.g., calling this method with searchWord of "carro"
	 *         would return because "carrot" is in the
	 *         dictionary)
	 *         
	 *		   false if the word, even once completed, could not
	 *         occur in the dictionary (e.g., calling this method with
	 *         searchWord of "carrotsa" would return false because
	 *         no word exists which starts with "carrotsa")
	 */
	private static boolean maybeInDictionary(String searchWord) {
		int startIndex = 0;
		int endIndex = dictionary.size() - 1;
		return maybeInDictionary(searchWord, startIndex, endIndex);
	}

	/**
	 * Helper method which completes a search of the searchWord within the
	 * dictionary indices given as parameters.
	 * @param searchWord
	 * @param startIndex starting index from which to search in the dictionary
	 * @param endIndex ending index from which to search in the dictionary
	 * @return
	 */
	private static boolean maybeInDictionary(String searchWord, int startIndex, int endIndex) {
		int midIndex = (startIndex + endIndex) / 2;
		String midWord = dictionary.get(midIndex);
		if (midWord.length() >= searchWord.length()) {
			midWord = midWord.substring(0, searchWord.length());
		}
		int compare = searchWord.compareTo(midWord);
		if (startIndex == endIndex) {
			return compare == 0;
		}
		else if (compare < 0) {
			return maybeInDictionary(searchWord, startIndex, midIndex);
		}
		else if (compare > 0) {
			return maybeInDictionary(searchWord, midIndex + 1, endIndex);
		}
		else {
			return true;
		}
	}

	/**
	 * Retrieves all possible grid positions which neighbor a given grid
	 * position.
	 */
	private static List<Node> getAvailableNodes(int row, int col, boolean[][] visitedNodes) {
		List<Node> nodes = new ArrayList<Node>();
		addIfAvailable(row - 1, col - 1, visitedNodes, nodes);
		addIfAvailable(row - 1, col    , visitedNodes, nodes);
		addIfAvailable(row - 1, col + 1, visitedNodes, nodes);
		addIfAvailable(row    , col - 1, visitedNodes, nodes);
		addIfAvailable(row    , col + 1, visitedNodes, nodes);
		addIfAvailable(row + 1, col - 1, visitedNodes, nodes);
		addIfAvailable(row + 1, col    , visitedNodes, nodes);
		addIfAvailable(row + 1, col + 1, visitedNodes, nodes);
		return nodes;
	}

	/**
	 * Adds a given grid position to a list of {@link Node}s if the position is
	 * on the board and has not been visited already.
	 */
	private static void addIfAvailable(int row, int col, boolean[][] visitedNodes, List<Node> nodes) {
		if (row >= 0 && row <= 3) {
			if (col >= 0 && col <= 3) {
				if (!visitedNodes[row][col]) {
					nodes.add(new Node(row, col));
				}
			}
		}
	}

	/**
	 * Inner class which represents a grid position.
	 */
	static class Node {
		int row;
		int col;

		public Node(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
}   
