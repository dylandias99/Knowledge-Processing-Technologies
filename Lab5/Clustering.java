
import java.util.*;


 /* @author: Dylan Dias
 */
public class Clustering {

	ArrayList<String[]> Doctokens;
	HashMap<Integer, double[]> vectorSpace;
	ArrayList<String> tlist;
	ArrayList<ArrayList<Doc>> docL;
	int numberofclusters;

	/*
	 * Constructor for attribute initialization. Number of clusters is assigned
 	 * @param numC: It is the number of clusters
	 */
	public Clustering(int numC) {
		numberofclusters = numC;
	}

	/*
	 * Load the documents to build the vector representations
  	 * @param docs
	 */
	public void preprocess(String[] docs) {
		Doctokens = new ArrayList<String[]>();
		tlist = new ArrayList<String>();
		docL = new ArrayList<ArrayList<Doc>>();
		ArrayList<Doc> docList;
		for (int m = 0; m < docs.length; m++) {
			String[] tokens = docs[m].split(" ");
			Doctokens.add(m, tokens);
			for (String token : tokens) {
				if (!tlist.contains(token)) {
					tlist.add(token);
					docList = new ArrayList<Doc>();
					Doc doc = new Doc(m, 1);
					docList.add(doc);
					docL.add(docList);
				} else {
					int index = tlist.indexOf(token);
					docList = docL.get(index);
					boolean match = false;
					for (Doc f : docList) {
						if (f.iden == m) {
							f.weight++;
							match = true;
							break;
						}
					}
					if (!match) {
						Doc f = new Doc(m, 1);
						docList.add(f);
					}
				}
			}
		}
		vectorSpace = new HashMap<Integer, double[]>();
		double[] weight;
		for(int m = 0;m < docL.size();m++) {
			docList = docL.get(m);
			for(int n = 0;n < docList.size();n++) {

				Doc d = docList.get(n);
				if (vectorSpace.containsKey(d.iden)) {
					weight = vectorSpace.get(d.iden);
					weight[m] = d.weight;
					vectorSpace.put(d.iden, weight);
				} else {
					weight = new double[tlist.size()];
					weight[m] = d.weight;
					vectorSpace.put(d.iden, weight);
				}
			}
		}

	}

	/*
	 * Cluster the documents For kmeans clustering, use the first and the ninth
	 * documents as the initial cent 
     * 2 clusters will be formed using this function
	 */
	public void cluster() {
		double[][] cent = new double[numberofclusters][];
		cent[0] = vectorSpace.get(8);
		cent[1] = vectorSpace.get(0);
		HashMap<Integer, double[]>[] clusters = new HashMap[numberofclusters];
		double[][] previous = null;
		while (!Arrays.deepEquals(previous, cent)) {
			previous = cent;
			clusters = assignCl(cent);
			cent = Centroid(clusters);
		}
		OutputString(clusters);
	}
	/*
	 * assign documents to cluster
	 * @param cent: location of ids and weight for first ninth documents are stored in a multidimensional array
	 * @return clusters: position of the documents id will be returned to the cluster
	 */
	public HashMap<Integer, double[]>[] assignCl(double[][] cent) {
		HashMap<Integer, double[]>[] clusters = new HashMap[numberofclusters];
		for (int i = 0;i < numberofclusters;i++) {
			clusters[i] = new HashMap<Integer, double[]>();
		}
		
		for(int j = 0;j < vectorSpace.size();j++) {
			double[] currDocVector = vectorSpace.get(j);
			int currDocId = j;
			double[] scores = new double[numberofclusters];
			for(int k = 0;k < numberofclusters; k++) {
				scores[k] = cossim(cent[k], currDocVector);
			}
			int clusterId = 0;
			double max = scores[clusterId];

			for(int n = 0;n < scores.length;n++) {
				if (scores[n] > max) {
					max = scores[n];
					clusterId = n;
				}
			}
			clusters[clusterId].put(currDocId, currDocVector);
		}
		return clusters;
	}
	/*
	 * Compute centroid
	 * 
	 * @param clusters: For each cluster the cluster location is retrieved.
	 * @return cent: For each data point the centroid is calculated.
	 *
	 */
	public double[][] Centroid(HashMap<Integer, double[]>[] clusters) {
		double[][] cent = new double[numberofclusters][];
		for(	int m = 0;m < clusters.length;m++) {

			HashMap<Integer, double[]> cluster = clusters[m];
			double[] mean = new double[tlist.size()];
			for (Integer iden : cluster.keySet()) {
				double[] currDocVector = cluster.get(iden);
				for(int i = 0;i < currDocVector.length;i++) {

					mean[i] += currDocVector[i];
				}
				for(int j = 0;j < mean.length; j++) {

					mean[j] = mean[j] / cluster.size();
				}
			}
			cent[m] = mean;
		}
		return cent;
	}
	/*
	 * 
	 * @param doc1
	 * @param doc2
	 * @return cosinesim: cosinesim is calculated using this function which will be the score that will be used to compare document values.
	 */
	public double cossim(double[] doc1, double[] doc2) {
		double dotProduct = 0, x = 0, y = 0;
		double cosinesim = 0;	
   		for (int i = 0;i < doc1.length;i++) {
			dotProduct += doc1[i] * doc2[i];
			x += Math.pow(doc1[i], 2);
			y += Math.pow(doc2[i], 2);
		}
		x = Math.sqrt(x);
		y = Math.sqrt(y);
		if (x != 0 | y != 0) {
			cosinesim = dotProduct / (x * y);
		}
		return cosinesim;
	}

	/*
	 * @param clusters: This is the cluster nodes that will store the location of the cluster	 
	 */
	public void OutputString(HashMap<Integer, double[]>[] clusters) {
		String cluster;
	
		for(int m = 0;m < clusters.length;m++) {
			cluster = "Cluster number: " + m + "\n";
			HashMap<Integer, double[]> clusterset = clusters[m];
			for (Integer iden : clusterset.keySet()) {
				cluster += iden + " ";
			}
			System.out.println(cluster);
		}
	}

	public static void main(String[] args) {
		String[] docs = { "hot chocolate cocoa beans",
				 "cocoa ghana africa",
				 "beans harvest ghana",
				 "cocoa butter",
				 "butter truffles",
				 "sweet chocolate can",
				 "brazil sweet sugar can",
				 "suger can brazil",
				 "sweet cake icing",
				 "cake black forest" };
		Clustering c = new Clustering(2);
		c.preprocess(docs);
		c.cluster();
	}
}

/*
 Document class for the vector representation of a document over here the associated weight and ids of the class are stored 
 */
class Doc {
	int iden;
	double weight;

	public Doc(int iden, double tw) {
		this.iden = iden;
		this.weight = tw;
	}

	public String toString() {
		String str = iden + ": " + weight;
		return str;
	}

}