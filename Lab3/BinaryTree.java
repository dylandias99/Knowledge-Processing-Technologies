import java.util.*;

/**
 * 
 * @author: Dylan Dias
 */
 
class BTNode {
	BTNode left, right;
	String term;
	ArrayList<Integer> docLists;

	
	public BTNode(String term, ArrayList<Integer> docList) {
		this.term = term;
		this.docLists = docList;
	}

}


public class BinaryTree {

	
	public void add(BTNode node, BTNode iNode) {
		BTNode parent = node;
		BTNode child = iNode;
		if (child.term.compareTo(parent.term) < 0) {
			if (parent.left != null) {
				add(parent.left, child);
			} else {
				parent.left = child;
				System.out.println("Inserted " + child.term + " to left node " + parent.term);
			}
		} else if (child.term.compareTo(parent.term) > 0) {
			if (parent.right != null) {    
				add(parent.right, child);
			} else {
				parent.right = child;
				System.out.println("Inserted " + child.term + " to right node " + parent.term);

			}
		}
	}

	
	public BTNode search(BTNode n, String key) {
      BTNode parent = n;
		while (parent != null) {
			if (parent.term.compareTo(key) == 0) {
            return parent;
			} else {
				if (parent.term.compareTo(key) > 0) {
					parent = parent.left;
				} else {
					parent = parent.right;
				}
			}
		}
		return null;
	}


	public ArrayList<BTNode> wildCardSearch(BTNode n, String key, ArrayList<BTNode> result) {
      if (n == null) {
			return result;
		}
		if (n.term.startsWith(key)) {
			wildCardSearch(n.left, key, result);
			if (n.term.startsWith(key) == true) {   
				result.add(n);
			}
			wildCardSearch(n.right, key, result);
			return result;
		} else {
			if (n.term.compareTo(key) < 0) {
				wildCardSearch(n.right, key, result);
			} else {
				wildCardSearch(n.left, key, result);
			}
			return result;
		}
	}

	/*
	 * Print the inverted index based on the increasing order of the terms in a
	 * subtree
	 * 
	 * @param node the root node of the subtree
	 */
	public void printInOrder(BTNode node) {
		BTNode parent = node;
		if (parent != null) {
			printInOrder(parent.left);
			System.out.println(parent.term + " " + parent.docLists);
			printInOrder(parent.right);
		}
	}
}
