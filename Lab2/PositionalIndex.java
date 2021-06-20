import java.util.ArrayList;
/*
@author: Dylan Dias
*/
public class PositionalIndex {
	String[] myDocs;
	ArrayList<String> termDictionary;                  
	ArrayList<ArrayList<Doc>> docLists;
	/*
	 * Construct a positional index 
	 * @param docs List of input strings or file names
	 * 
	 */
	public PositionalIndex(String[] docs)
	{
				docLists = new ArrayList<ArrayList<Doc>>();
		termDictionary = new ArrayList<String>();
		ArrayList<Doc> posList;
      		myDocs = docs;
		for(int id=0; id<myDocs.length; id++)
      {
		    String phrasewords[] = myDocs[id].split(" ");
			for(int phrasewordID = 0; phrasewordID<phrasewords.length; phrasewordID++){
         
            boolean wordsearch = false;
				if(!termDictionary.contains(phrasewords[phrasewordID])){
					termDictionary.add(phrasewords[phrasewordID]);
					posList = new ArrayList<Doc>();
					Doc doc = new Doc(id,phrasewordID);
					posList.add(doc);
					docLists.add(posList);
				}
				else{
					int init = termDictionary.indexOf(phrasewords[phrasewordID]);
					posList = docLists.get(init); 
					int k=0;
					for(Doc folder:posList) {
					if(folder.docId == id) {
                     folder.insertPosition(phrasewordID);
                     posList.set(k, folder);
                     wordsearch = true;
                     break;
					}
					k++;                 
					}
					if(!wordsearch) {
						Doc doc = new Doc(id,phrasewordID);
						posList.add(doc);
					}
				}
			}
		    
		}
	}
	public String toString()
	{
		String matrixString = new String();
		ArrayList<Doc> docList;
		for(int i=0;i<termDictionary.size();i++){
				matrixString += String.format("%-15s", termDictionary.get(i));
				docList = docLists.get(i);
				for(int j=0;j<docList.size();j++)
				{
					matrixString += docList.get(j)+ "\t";
				}
				matrixString += "\n";
			}
		return matrixString;
	}
	public ArrayList<Doc> intersect(ArrayList<Doc> list1post, ArrayList<Doc> list2post, int diff)
{
		//TASK2: TO BE COMPLETED
		ArrayList<Doc> intersectList = new ArrayList<Doc>();
		int id1 = 0, id2 =0;
		while(id1<list1post.size() && id2<list2post.size()){
			if(list1post.get(id1).docId == list2post.get(id2).docId){
				ArrayList<Integer> list1 = list1post.get(id1).positionList;
				ArrayList<Integer> list2 = list2post.get(id2).positionList;
				int iter1 = 0;
				while(iter1 < list1.size()){
					int iter2 = 0;
					while(iter2 < list2.size()){
						if((list1.get(iter1) - list2.get(iter2)) == -1){
							boolean match = false;
							int j = 0;
							for(Doc docu : intersectList){
								if(docu.docId == list1post.get(id1).docId){
									Doc item = intersectList.get(j);
									list1.get(iter1);
									item.insertPosition(list1.get(iter1));
									intersectList.set(j, item);
									match=true;
									break;
								}
								j++;
							}
							if(!match){
								Doc doc = new Doc(list1post.get(id1).docId, list1.get(iter1));
								intersectList.add(doc);
							}
						}
						++iter2;
					}
					++iter1;
				}
				id1++;
				id2++;
			}
			else if(list1post.get(id1).docId < list2post.get(id2).docId){
				id1++;
			}
			else {
				id2++;
			}
		}
		return intersectList;
	}
	/*
	 * 
	 * @param query a phrase query that consists of any number of terms in the sequential order
	 * @return ids of documents that contain the phrase
	 */
	public ArrayList<Doc> phraseQuery(String[] wordquery)
	{
				String word = wordquery[0];
		ArrayList <Doc> nulls = new ArrayList<Doc>();
		ArrayList <Doc> posting1,posting2;
		int idarr = termDictionary.indexOf(word);
		if(idarr!=-1){
			posting1 = docLists.get(idarr);
			int diff=-1;
			for(int k=1; k<wordquery.length;k++){
			int init = termDictionary.indexOf(wordquery[k]);
			if(init==-1){
				return nulls;
			}
			posting2 = docLists.get(init);
			posting1 = intersect(posting1, posting2, diff);
			--diff;
			}
			return posting1;
		}
		else {
			return nulls;
		}
	}
	public void find(String searchString){
		ArrayList<Doc> output = this.phraseQuery(searchString.split(" "));
		System.out.print(searchString+"\t");
		if(output.size()==0){
			System.out.print("NOT FOUND");
		}
		for(Doc result : output){
			System.out.print(result + "\t");
		}
		System.out.println();
	}
	
	public static void main(String[] args)
	{
      String[] docs = {"data warehouse over big data",
                       "dimensional data warehouse over big data",
                       "nlp nlp before nlp before text mining text nlp before nlp before",
                       "nlp before text classification nlp before"};
                       
		PositionalIndex posi = new PositionalIndex(docs);
		System.out.print(posi);
	 System.out.println("\nQueries \n");
    
    System.out.println("\nQuery 1: dylan");
    posi.find("dylan");
    System.out.println("\nQuery 2: data");
    posi.find("data");
    System.out.println("\nQuery 3: nlp before");
    posi.find("nlp before");
	}
}

/*
 * 
 * Document class that contains the document id and the position list
 */
class Doc{
	int docId;
	ArrayList<Integer> positionList;
	public Doc(int did)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
	}
	public Doc(int did, int position)
	{
		docId = did;
		positionList = new ArrayList<Integer>();
		positionList.add(new Integer(position));
	}
	
	public void insertPosition(int position)
	{
		positionList.add(new Integer(position));
	}
	
	public String toString()
	{
		String docIdString = ""+docId + ":<";
		for(Integer pos:positionList)
			docIdString += pos + ",";
		docIdString = docIdString.substring(0,docIdString.length()-1) + ">";
		return docIdString;		
	}
}