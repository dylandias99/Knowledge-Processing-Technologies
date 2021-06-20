import java.util.*;

/**
 * 
 * @author: Dylan Dias
 */
public class BTreeIndex {
	String[] Document;
	BinaryTree termsinlist;
	BTNode rooting;
	ArrayList<ArrayList<Integer>> docLists;
	public BTreeIndex(String[] docs) {
		Document = docs;
		ArrayList<String> indi = new ArrayList<String>();
		docLists = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> docList = new ArrayList<Integer>();
		termsinlist = new BinaryTree();
		for (int k = 0; k < Document.length; k++) {
			String[] words = Document[k].split(" ");
			for (String word : words) {
				if (!indi.contains(word)) {
					indi.add(word);
				}
			}
		}
		Collections.sort(indi);
		System.out.println("Sorted names are:-");
		System.out.println(indi + "\n");
		int start = 0;
		int end = indi.size() - 1;
		int mid = (start + end) / 2;
		BTNode in = new BTNode(indi.get(mid), docList);
		rooting = in;
		for (int m = 0; m < Document.length; m++) 
      {
			String[] tokens = Document[m].split(" ");
			for (String token : tokens) {
				if (termsinlist.search(in, token) == null) {
					docList = new ArrayList<Integer>();
					docList.add(new Integer(m));
					docLists.add(docList);
					termsinlist.add(rooting, new BTNode(token, docList));
				} else {
					BTNode indexNode = termsinlist.search(in, token);
					docList = indexNode.docLists;
					if (!docList.contains(new Integer(m))) {
						docList.add(new Integer(m));
					}
					indexNode.docLists = docList;
				}
			}

		}
		System.out.println("\nResult for print in order:");
		termsinlist.printInOrder(rooting);
	}

	
	public ArrayList<Integer> find(String word) {
      BTNode node = termsinlist.search(rooting, word);
		if (node == null){
			return null;
         }
		else return node.docLists;
	}

	
	public ArrayList<Integer> find(String[] word) {
		ArrayList<Integer> result = find(word[0]);
		int termId = 1;
		while (termId < word.length) {
			ArrayList<Integer> result1 = find(word[termId]);
			result = merge(result, result1);
			termId++;
		}
		return result;
	}

	public ArrayList<Integer> wildCardSearch(String wildcard) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		ArrayList<BTNode> results = termsinlist.wildCardSearch(rooting, wildcard, new ArrayList<BTNode>());
		if (results.size() > 0) {
			BTNode start = results.get(0);
			output = start.docLists;
			if (results.size() > 1) {
				for (BTNode node : results) {
					output = join(output, node.docLists);
				}
			}
		}
		return output;
	}


	public ArrayList<Integer> join(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		ArrayList<Integer> output = new ArrayList<Integer>();
		int m = a1.size();
		int n = a2.size();
		int i = 0, j = 0;
		while (i < m && j < n) {
			if (a1.get(i) < a2.get(j))
				output.add(a1.get(i++));
			else if (a2.get(j) < a1.get(i))
				output.add(a2.get(j++));
			else {
				output.add(a2.get(j++));
				i++;
			}
		}
		while (i < m)
			output.add(a1.get(i++));
		while (j < n)
			output.add(a2.get(j++));
		return output;
	}

	private ArrayList<Integer> merge(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		ArrayList<Integer> combList = new ArrayList<Integer>();
		int pi1 = 0, pi2 = 0;
		while (pi1 < a1.size() && pi2 < a2.size()) {
			if (a1.get(pi1).intValue() == a2.get(pi2).intValue()) {
				combList.add(a1.get(pi1));
				pi1++;
				pi2++;
			} else if (a1.get(pi1) < a2.get(pi2))
				pi1++;
			else
				pi2++;
		}
		return combList;
	}

		public static void main(String[] args) {
		String[] docs = { "text warehousing over big data", "dimensional data warehouse over big data",
				"nlp text before warzone text mining", "nlp before text classification" };
		BTreeIndex binTree = new BTreeIndex(docs);
		
		// Single Query
		 
		System.out.println("\nSingle Query");
		String[] oneword = { "nlp" };
        System.out.println(Arrays.toString(oneword));
		for (int j = 0; j < oneword.length; j++) {
			ArrayList<Integer> result = binTree.find(oneword[j]);
			if (result != null) {
				System.out.println(oneword[j] + ": " + result);
			} else {
				System.out.println("Word not found in the dictionary");
			}
		}
      
		
	//	 For conjunctive queries
		 
		String[] query = { "nlp", "mining", "text" };
		System.out.println("\nCojunctive queries");
        System.out.println(Arrays.toString(query));
		ArrayList<Integer> output1 = binTree.find(query);
		if (output1 != null && !output1.isEmpty()) {
			System.out.println("Result for the query : " + output1);
		} else {
			System.out.println("Word not found in the dictionary");
		}
		
		//  For Wildcard queries
		 
		System.out.println("\nWildcard queries");
		String[] wildcard = { "nl" };
        System.out.println(Arrays.toString(wildcard));
		for (int i = 0; i < wildcard.length; i++) {
			ArrayList<Integer> output2 = binTree.wildCardSearch(wildcard[i]);
			if (output2 != null && !output2.isEmpty()) {
				System.out.println(wildcard[i] + ": " + output2);
			} else {
				System.out.println("Word not found in the dictionary");
			}
		}
	}
}