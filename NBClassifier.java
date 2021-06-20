
/* @author: Dylan Dias Lab #4 */

import java.util.*;

public class NBClassifier {
	private HashMap<Integer, String> docstotrain; // train the data
	private int[] classtrain; // train class values
	private int numberofclasses = 2;
	private int[] docsperclass; // number of docs per class
	private String[] stringclass; // concatenated string for a given class
	private int[] Tokens; // total number of tokens per class
	private HashMap<String, Double>[] conditionalProb; // term conditional prob
	private HashSet<String> voc; // entire 
   LoadData parse;

	/*
	   @param Datatraining the training document folder
	   @param Datatesting  the testing document folder
	   Firstly all the test and trainind data will be called using the preprocessing 
      function. All the terms will be added in the voc and the conditional probability for each term 
      will be stored along with their index in the Hashmap
   */

	@SuppressWarnings("unchecked")
	public NBClassifier(String Datatraining, String Datatesting) {
		preprocess(Datatraining, Datatesting);
		docstotrain = parse.docs;
		classtrain = parse.classes;
		docsperclass = new int[numberofclasses];
		stringclass = new String[numberofclasses];
		Tokens = new int[numberofclasses];
		conditionalProb = new HashMap[numberofclasses];
		voc = new HashSet<String>();
		for (int m = 0; m < numberofclasses; m++) {
			stringclass[m] = "";
			conditionalProb[m] = new HashMap<String, Double>();
		}
		for (int n = 0; n < classtrain.length; n++) {
			docsperclass[classtrain[n]]++;
			stringclass[classtrain[n]] += (docstotrain.get(n) + " ");
		}
		for (int p = 0; p < numberofclasses; p++) {
			String[] tokens = stringclass[p].split("[\" ()_,?:;%&-]+");
			Tokens[p] = tokens.length;
			for (String token : tokens) {
				voc.add(token);
				if (conditionalProb[p].containsKey(token)) {
					double count = conditionalProb[p].get(token);
					conditionalProb[p].put(token, count + 1);
				} else
					conditionalProb[p].put(token, 1.0);
			}
		}

		for (int q = 0; q < numberofclasses; q++) {
			Iterator<Map.Entry<String, Double>> iterator = conditionalProb[q].entrySet().iterator();
			int vSize = voc.size();
			while (iterator.hasNext()) {
				Map.Entry<String, Double> entry = iterator.next();
				String token = entry.getKey();
				Double count = entry.getValue();
				Double prob = (count + 1) / (Tokens[q] + vSize);
				conditionalProb[q].put(token, prob);
			}
		}
	}

	/*
	 Classify a test doc
	 @param doc test doc
	 @return class label 
	 Classifying is done on a single test document. Tokenization is first performed after 
    which the positive and negative score is calculated which ever one is greater is that 
    value is returned.
	 */
	public int classify(String doc) {
		int label = 0;
		int vSize = voc.size();
		double[] score = new double[numberofclasses];
		for (int m = 0;m < numberofclasses;m++) {
			score[m] = Math.log10(docsperclass[m] * 1.0 / docstotrain.size());
		}
		String[] tokens = doc.split("[\" ()_,?:;%&-]+");
		for(int n = 0;n < numberofclasses;n++) {
			for (String token : tokens) {
				if (!conditionalProb[n].containsKey(token))
					score[n] += Math.log10(1.0 / (Tokens[n] + vSize));
				else
					score[n] += Math.log10(conditionalProb[n].get(token));
			}
		}
		double largescore = score[0];
		for(int i = 0;i < score.length;i++) {
			if (largescore < score[i])
				label = i;
		}
		return label;
	}

	/*
	 Load the training documents
	 @param Datatraining
	 @param Datatesting
	 This function is used to load all the data.
	 */
	public void preprocess(String Datatraining, String Datatesting) {
		parse = new LoadData(Datatraining, Datatesting);
	}

	/*
	 Classify a set of testing documents and report the acc 
	 @param testDocs: folder that contains the testing documents
	 @param classtrain: training class values 
	 Using the test documents the true positive, false positive, false negative and 
    true negative are calculated which is used to find accuracy and correctly classified 
    files
	 */
	public void classifyAll(HashMap<Integer, String> testDocs, int[] classtrain) {
		float truepos = 0;
		float trueneg = 0;
		float falsepos = 0;
		float falseneg = 0;
		int classifiedcorrectly = 0;
		float precision;
		float recall;
		float acc;
		for (Map.Entry<Integer, String> testDoc : testDocs.entrySet()) {
			int result = classify(testDoc.getValue());
			if (classtrain[testDoc.getKey()] == 1 && result == classtrain[testDoc.getKey()]) {
				truepos++;
			} else if (classtrain[testDoc.getKey()] == 0 && result == classtrain[testDoc.getKey()]) {
				trueneg++;
			} else if (classtrain[testDoc.getKey()] == 0 && result != classtrain[testDoc.getKey()]) {
				falseneg++;
			} else if (classtrain[testDoc.getKey()] == 1 && result != classtrain[testDoc.getKey()]) {
				falsepos++;
			}
		}
		classifiedcorrectly = (int) truepos + (int) trueneg;
		acc = (truepos + trueneg) / (truepos + trueneg + falsepos + falseneg);
		System.out.println("Correctly classified " + classifiedcorrectly + " out of " + testDocs.size());
		System.out.println("Accuracy: " + acc);
	}

	
  /*
  Main function where a file is choosen at random to classify and classification is performed on all
  the docs
  */
	public static void main(String[] args) {
		NBClassifier Naive = new NBClassifier("data/train", "data/test");
		System.out.println("Classification done on a single doc:");
		int randomvalue = new Random().nextInt(Naive.parse.TestDocs.size());
		randomvalue += 1800;
		System.out.println("Doc at index : " + (randomvalue - 1800) + " which is "
				+ (Naive.classify(Naive.parse.TestDocs.get(randomvalue)) == 1 ? "Positive" : "Negative"));
		randomvalue = new Random().nextInt(Naive.parse.TestDocs.size());
		randomvalue += 1800;
		System.out.println("Doc at index : " + (randomvalue - 1800) + " which is "
				+ (Naive.classify(Naive.parse.TestDocs.get(randomvalue)) == 1 ? "Positive" : "Negative"));
		System.out.println();
		System.out.println("Classification done on all docs:");
		Naive.classifyAll(Naive.parse.TestDocs, Naive.parse.classes);
	}
}
