<?php
if (!isset($_GET['cells'])) {
	throw new Exception("Invalid input. Must set cells to construct grid");
}

$foundWords = array();
$dictionary = loadDictionary();
$dictionarySize = count($dictionary);
$grid = getCellGrid();
solve();


class Node {
	public $row;
	public $col;

	public function __construct($row, $col) {
		$this->row = $row;
		$this->col = $col;
	}
}

/*************************************************************/
// RUN THE SCRIPT 
/*************************************************************/

/* 1. Set up dictionary */
function loadDictionary() {
	$dictionary = array();
	$handle = fopen("dictionary.txt", "r");
	if ($handle) {
		while (($line = fgets($handle)) !== false) {
			$dictionary [] = trim($line);
		}
	}
	return $dictionary;
}

/* 2. Set up grid */
function getCellGrid() {
	$cells = explode(',', $_GET ['cells']);
	$grid = array();
	for($row = 0; $row < 4; $row++) {
		$grid [$row] = array();
		for($col = 0; $col < 4; $col++) {
			$index = $row * 4 + $col;
			if (isset($cells[$index])) { 
				$cellEntry = $cells[$row * 4 + $col];
				if (!empty($cellEntry)) {
					$grid [$row] [$col] = $cellEntry;
				} else {
					throw new Exception("Invalid input. Grid index must not be empty");
				}
			} else {
				throw new Exception("Invalid input. Must include 16 characters");
			}
		}
	}
	return $grid;
}

/* 3. Solve */
function solve() {
	for($row = 0; $row < 4; $row++) {
		for($col = 0; $col < 4; $col++) {
			$stringSoFar = $GLOBALS['grid'][$row][$col];
			$visitedNodes = array(
								array(false, false, false, false),
								array(false, false, false, false),
								array(false, false, false, false),
								array(false, false, false, false)
							);
			$visitedNodes[$row][$col] = true;
			solveWithStringSoFar($row, $col, $stringSoFar, $visitedNodes);
		}
	}
}

/**
 * Recursively finds dictionary words with a given position and word constructed so far.
 * @param currentRow current row position of the built grid path
 * @param currentCol current column position of the built grid path
 * @param stringSoFar constructed word so far
 * @param visitedNodes visited nodes so far
 */
function solveWithStringSoFar($currentRow, $currentCol, $stringSoFar, $visitedNodes) {
	if (maybeInDictionary($stringSoFar)) {
		foreach (getAvailableNodes($currentRow, $currentCol, $visitedNodes) as $node) {
			$nextString = $stringSoFar;
			$nextString .= $GLOBALS['grid'][$node->row][$node->col];
			solveWithStringSoFar($node->row, $node->col, $nextString, $visitedNodes);
		}
	}
	if (isInDictionary($stringSoFar)) {
		$GLOBALS['foundWords'][] = $stringSoFar;
	}
}

/**
 * Performs a binary search to determine whether a given search term may be
 * in the pre-loaded dictionary.
 *
 * @param searchWord
 * @return true if the word, once completed, could occur in the
 *         dictionary (e.g., calling this method with searchWord of "carro"
 *         would return true because "carrot" is in the dictionary)
 *         false if the word, even once completed, could not
 *         occur in the dictionary (e.g., calling this method with
 *         searchWord of "carrotsa" would return false because
 *         no word exists which starts with "carrotsa")
 */
function maybeInDictionary($searchWord) {
	$startIndex = 0;
	$endIndex = $GLOBALS['dictionarySize'] - 1;
	return maybeInDictionaryHelper($searchWord, $startIndex, $endIndex);
}

function maybeInDictionaryHelper($searchWord, $startIndex, $endIndex) {
	$midIndex = floor(($startIndex + $endIndex) / 2);
	$midWord = $GLOBALS['dictionary'][$midIndex];
	if (strlen($midWord) >= strlen($searchWord)) {
		$midWord = substr($midWord, 0, strlen($searchWord));
	}
	$compare = strcmp($searchWord, $midWord);
	if ($startIndex == $endIndex) {
		return $compare == 0;
	}
	else if ($compare < 0) {
		return maybeInDictionaryHelper($searchWord, $startIndex, $midIndex);
	}
	else if ($compare > 0) {
		return maybeInDictionaryHelper($searchWord, $midIndex + 1, $endIndex);
	}
	else {
		return true;
	}
}

/**
 * Performs a binary search to determine whether a given search term is in the pre-loaded dictionary.
 * @param searchWord
 * @return true if the word exists in the dictionary
 */
function isInDictionary($searchWord) {
	$startIndex = 0;
	$endIndex = $GLOBALS['dictionarySize'] - 1;
	return isInDictionaryHelper($searchWord, $startIndex, $endIndex);
}

function isInDictionaryHelper($searchWord, $startIndex, $endIndex) {
	$midIndex = floor(($startIndex + $endIndex) / 2);
	$midWord = $GLOBALS['dictionary'][$midIndex];
	$compare = strcmp($searchWord, $midWord);

	if ($startIndex == $endIndex) {
		return $compare == 0;
	}
	else if ($compare < 0) {
		return isInDictionaryHelper($searchWord, $startIndex, $midIndex);
	}
	else if ($compare > 0) {
		return isInDictionaryHelper($searchWord, $midIndex + 1, $endIndex);
	}
	else {
		return true;
	}
}

/**
 * Retrieves all possible grid positions which neighbor a given grid
 * position.
 */
function getAvailableNodes($row, $col, $visitedNodes) {
	$nodes = array();
	addIfAvailable($row - 1, $col - 1, $visitedNodes, $nodes);
	addIfAvailable($row - 1, $col    , $visitedNodes, $nodes);
	addIfAvailable($row - 1, $col + 1, $visitedNodes, $nodes);
	addIfAvailable($row    , $col - 1, $visitedNodes, $nodes);
	addIfAvailable($row    , $col + 1, $visitedNodes, $nodes);
	addIfAvailable($row + 1, $col - 1, $visitedNodes, $nodes);
	addIfAvailable($row + 1, $col    , $visitedNodes, $nodes);
	addIfAvailable($row + 1, $col + 1, $visitedNodes, $nodes);
	return $nodes;
}

/**
 * Adds a given grid position to a list of nodes if the position is
 * on the board and has not been visited already.
 */
function addIfAvailable($row, $col, $visitedNodes, &$nodes) {
	if ($row >= 0 && $row <= 3) {
		if ($col >= 0 && $col <= 3) {
			if (!$visitedNodes[$row][$col]) {
				$nodes [] = new Node($row, $col);
			}
		}
	}
}
?>
