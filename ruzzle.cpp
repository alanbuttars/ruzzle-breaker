// Author: Alan Buttars, AlanButtars.com

#include "stdafx.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
using namespace std;

class Node {
	int row;
	int col;

public:
	Node::Node(int r, int c) {
		row = r;
		col = c;
	}

	int getRow() {
		return row;
	}

	int getCol() {
		return col;
	}
};

void readInDictionary();
void readInGrid();
void solve();
void solveWithStringSoFar(int row, int col, string stringSoFar, bool visitedNodes[4][4]);
bool isInDictionary(string searchWord);
bool isInDictionary(string searchWord, int startIndex, int endIndex);
bool maybeInDictionary(string searchWord);
bool maybeInDictionary(string searchWord, int startIndex, int endIndex);
vector<Node> getAvailableNodes(int row, int col, bool visitedNodes[4][4]);
void addIfAvailable(int row, int col, bool visitedNodes[4][4], vector<Node> &nodes);

char grid[4][4];
vector<string> dictionary;
vector<string> foundWords;


int main() {
	readInDictionary();
	readInGrid();
	solve();

	cout << "FOUND : " << foundWords.size() << endl;
	for (string &foundWord : foundWords) {
		cout << foundWord << endl;
	}
}

void readInDictionary() {
	cout << "Loading dictionary..." << endl;
	ifstream infile("dictionary.txt");
	string line;
	while (getline(infile, line)) {
		dictionary.push_back(line);
	}
	infile.close();
}

void readInGrid() {
	cout << "Print the grid (16 characters):" << endl;
	for (int row = 0; row < 4; row++) {
		for (int col = 0; col < 4; col++) {
			cin >> grid[row][col];
		}
	}
}


void solve() {
	for (int row = 0; row < 4; row++) {
		for (int col = 0; col < 4; col++) {
			string stringSoFar = string(1, grid[row][col]);
			bool visitedNodes[4][4] = {0};
			visitedNodes[row][col] = true;
			solveWithStringSoFar(row, col, stringSoFar, visitedNodes);
		}
	}
}

void solveWithStringSoFar(int row, int col, string stringSoFar, bool visitedNodes[4][4]) {
	if (maybeInDictionary(stringSoFar)) {
		for (Node &node : getAvailableNodes(row, col, visitedNodes)) {
			string nextString = stringSoFar + grid[node.getRow()][node.getCol()];
			solveWithStringSoFar(node.getRow(), node.getCol(), nextString, visitedNodes);
		}
	}
	if (isInDictionary(stringSoFar)) {
		foundWords.push_back(stringSoFar);
	}
}

bool isInDictionary(string searchWord) {
	int startIndex = 0;
	int endIndex = dictionary.size() - 1;
	return isInDictionary(searchWord, startIndex, endIndex);
}

bool isInDictionary(string searchWord, int startIndex, int endIndex) {
	int midIndex = (startIndex + endIndex) / 2;
	string midWord = dictionary[midIndex];
	int compare = searchWord.compare(midWord);
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

bool maybeInDictionary(string searchWord) {
	int startIndex = 0;
	int endIndex = dictionary.size() - 1;
	return maybeInDictionary(searchWord, startIndex, endIndex);
}

bool maybeInDictionary(string searchWord, int startIndex, int endIndex) {
	int midIndex = (startIndex + endIndex) / 2;
	string midWord = dictionary[midIndex];
	if (midWord.length() >= searchWord.length()) {
		midWord = midWord.substr(0, searchWord.length());
	}
	int compare = searchWord.compare(midWord);
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

vector<Node> getAvailableNodes(int row, int col, bool visitedNodes[4][4]) {
	vector<Node> * nodes = new vector<Node>();
	addIfAvailable(row - 1, col - 1, visitedNodes, *nodes);
	addIfAvailable(row - 1, col    , visitedNodes, *nodes);
	addIfAvailable(row - 1, col + 1, visitedNodes, *nodes);
	addIfAvailable(row    , col - 1, visitedNodes, *nodes);
	addIfAvailable(row    , col + 1, visitedNodes, *nodes);
	addIfAvailable(row + 1, col - 1, visitedNodes, *nodes);
	addIfAvailable(row + 1, col    , visitedNodes, *nodes);
	addIfAvailable(row + 1, col + 1, visitedNodes, *nodes);
	return *nodes;
}


void addIfAvailable(int row, int col, bool visitedNodes[4][4], vector<Node> &nodes) {
	if (row >= 0 && row <= 3) {
		if (col >= 0 && col <= 3) {
			if (!visitedNodes[row][col]) {
				nodes.push_back(*(new Node(row, col)));
			}
		}
	}
}
