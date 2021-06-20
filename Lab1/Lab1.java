import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Arrays;
/*

@author Dylan Dias

*/

public class Lab1 {
    private ArrayList<String> data = new ArrayList<String>(); // assignindex adds the data
    private ArrayList<String> stopwords = new ArrayList<String>(); // scanstopwords add stop words
    static String[] docs = { "cv000_29416.txt", "cv001_19502.txt", "cv002_17424.txt" , "cv003_12683.txt",
            "cv004_12641.txt"};
    private ArrayList<String> terms; //Contains words of all the documents
    private ArrayList<ArrayList<Integer>> dlists;
    private ArrayList<Integer> dlist;
    private ArrayList<ArrayList<String>> myDocs = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> finalData = new ArrayList<ArrayList<String>>();
    


    public String readfilesdata(String File) throws Exception {
        String datafiles = new String(Files.readAllBytes(Paths.get(File)));
        return datafiles;
    }


    public ArrayList<String> assignindex(int i, String datafiles) {
        data.add(i, datafiles.toLowerCase());
        return data;
    }

 

    public void scanstopwords() {
        try {
            File stopfile = new File("stopwords.txt");
            Scanner sc = new Scanner(stopfile);
            String Words = "";
            while (sc.hasNextLine()) {
                Words += sc.nextLine().toLowerCase() + " ";
            }
           // System.out.println("" + Arrays.toString(Words));
            String stopwordsdata[] = Words.split(" ");
            //System.out.println("" + Arrays.toString(stopwordsdata));
            Arrays.sort(stopwordsdata);
            for (String word : stopwordsdata) {
                stopwords.add(word);
               // System.out.println(stopwords);
            }
        //    System.out.println(stopwords);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public int stopwordscan(String id) {
        int i = 0;
        int j = stopwords.size() - 1;
         //System.out.println("j = " + j);
        while (i <= j) {
            int k = i + ( (j - i) / 2 );
            int result = id.compareTo(stopwords.get(k));
        //   System.out.println(j+ "k = "+ k + " result = " + result + " i = "+ i);
            if (result < 0)
                j = k - 1;
            else if (result > 0)
                i = k + 1;
            else
                return k;
        }
        return -1;
    }



    public ArrayList<String> tokenize(String document) {
     //  System.out.println("mydocs: " + document);
        String[] tok = document.split("[ '.,&#?!:;$%+()\\-\\/*\"]+");
      //  for(String toks: tok){
      //  System.out.println(toks);
      //  }
        ArrayList<String> ptok = new ArrayList<String>();
        ArrayList<String> stem = new ArrayList<String>();

        for (String t : tok) {
            if (stopwordscan(t) == -1) {
                ptok.add(t);
           //    System.out.println("ptok = " + ptok);
            }
        }
        Stemmer stemming = new Stemmer();
        for (String t : ptok) {
           // System.out.println(t);
            stemming.add(t.toCharArray(), t.length());
           
            stemming.stem();
            stem.add(stemming.toString());
            stemming = new Stemmer();
        //    System.out.println(stem);
        }
        return stem;
    }

   

    private void InvertedIndex() {
       terms = new ArrayList<String>();       //terms = termsList
       dlists = new ArrayList<ArrayList<Integer>>();  //docs = docLists
       dlist = new ArrayList<Integer>(); //dlist = docList
       //for(ArrayList<String> docs: myDocs){
       //System.out.println(docs.size() +"Documents after tokenization: " + docs);
       //}
        for (int i = 0; i < myDocs.size(); i++) {
            for (String word : myDocs.get(i)) {
                if (!terms.contains(word)) {
                    terms.add(word);
               //     System.out.println("terms = " + terms);
                    dlist = new ArrayList<Integer>();
                    dlist.add(i);
                //   System.out.println("dlist = " + dlist);
                    dlists.add(dlist);
           //        System.out.println("dlists = "+dlists);
                } else {
                    int index = terms.indexOf(word);
                    dlist = dlists.get(index);
                    if (!dlist.contains(i)) {
                        dlist.add(i);
                        dlists.set(index, dlist);
                    }
                }
            }
        }

        String out = "";
    //   for(String docs: terms){
      // System.out.println("Documents after tokenization: " + docs);
     //  }
        for (int j = 0; j < terms.size(); j++) {
            out += String.format("%-15s", terms.get(j));
            dlist = dlists.get(j);
            for (int k = 0; k < dlist.size(); k++) {
                out += dlist.get(k) + "\t";
            }
            out += "\n";
        }
       System.out.println(out);

    }

   

    public ArrayList<Integer> findindex(String query) {
        int id = terms.indexOf(query);
    //    System.out.println(id + ": Index value");
        if (id >= 0) {
            return dlists.get(id);
        } else
            return null;
    }

   

    public ArrayList<Integer> mergingforAND(ArrayList<Integer> plist1, ArrayList<Integer> plist2) {
        ArrayList<Integer> mlist = new ArrayList<Integer>(); //mergeList
        int m = 0, n = 0;
        if (plist1 != null || plist2 != null) {
            while (m < plist1.size() && n < plist2.size()) {
                if (plist1.get(m) == plist2.get(n)) {
                    mlist.add(plist1.get(m));
                    m++;
                    n++;
                } else if (plist1.get(m) < plist2.get(n)) {
                    m++;
                } else {
                    n++;
                }
            }
        } else {
            System.out.println("Searched queries are invalid and not available please check.");
        }
        return mlist;
    }

   
    public void onewordquery(String getword) {
        String[] expression = getword.split(" ");
       // System.out.println("The word to be found is: \n" + Arrays.toString(expression));
        if (expression.length == 1) {
            ArrayList<Integer> alist = new ArrayList<>();
            Stemmer st = new Stemmer();
            String test = getword.toLowerCase();
            st.add(test.toCharArray(), test.length());
            st.stem();
          //  System.out.println("st = " + st);
            alist = findindex(st.toString());
            System.out.println("" + Arrays.toString(expression)+ " : " + "" + alist);
            if (alist != null) 
            
            
            {
               System.out.println("After performing one word query operation we get the following Documents:");
                for (int k = 0; k < alist.size(); k++) {
                   System.out.println(docs[alist.get(k)]);
                }
            } else {
              //  System.out.println("The word " + getword + "" + " is not present in any document");
            }
        } else {
            System.out.println("More than 1 word is being entered");
        }
    }

  
    public void twowordqueryAND(String getwords) {
        ArrayList<Integer> result1 = new ArrayList<>();
        ArrayList<Integer> result2 = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<String> sub1 = new ArrayList<>();
        ArrayList<String> sub2 = new ArrayList<>();
      //  System.out.println("Queries entered are: \n" + getwords);
      System.out.println("The words to be found are: " + getwords);
        String[] bothwords = getwords.split(" ");
        if (bothwords.length == 2) {
            String getword1 = getwords.split(" ")[0];
            String getword2 = getwords.split(" ")[1];
      //      System.out.println("getword1 = " + getword1);
            sub1 = (tokenize(getword1.toLowerCase()));
      //      System.out.println("sub1" + sub1);
            sub2 = (tokenize(getword2.toLowerCase()));
      //      System.out.println("sub2" + sub2);
            for (String words : sub1) {
                result1 = findindex(words);
                System.out.println(""+ words + " : " + "" + result1);
            }
            for (String words : sub2) {
                result2 = findindex(words);
                System.out.println(""+ words + " : " + "" + result2);
         //       System.out.println(result2 + " : result2");
            }
            result = mergingforAND(result1, result2);
            //System.out.println("ANDresult" + result);
            if (result.size() == 0) {
                System.out.println("List is not present in the document");
            } else {
            System.out.println("After performing AND operation we get the following documents");
                for (int k : result){
                    System.out.println(docs[k]);
                    }
            }
        } else {
            System.out.println("Invalid Query please fill 2 words only");
        }
    }
public ArrayList<Integer> mergingforOR(ArrayList<Integer> plist1, ArrayList<Integer> plist2) {
        ArrayList<Integer> mlist1 = new ArrayList<Integer>(); //mergeList
       
       
        if(plist1 != null && plist2 != null){
        mlist1.addAll(plist1);
        for(int m =0; m < plist2.size(); m++){
        if(!mlist1.contains(plist2.get(m)))
        {
        mlist1.add(plist2.get(m));
        }
        }
        
        }
        else if(plist1 == null && plist2 != null){
        mlist1.addAll(plist2);
        
        }
        else if(plist2 == null && plist1 != null){
        mlist1.addAll(plist1);
        
        }
        else {System.out.println("No Output");}
        return mlist1;
        }
        
       
       public void twowordqueryOR(String getwords) {
        ArrayList<Integer> result1 = new ArrayList<>();
        ArrayList<Integer> result2 = new ArrayList<>();
        ArrayList<Integer> result = new ArrayList<>();
        ArrayList<String> sub1 = new ArrayList<>();
        ArrayList<String> sub2 = new ArrayList<>();
        System.out.println("The words to be found are: " + getwords);
        String[] bothwords = getwords.split(" ");
        if (bothwords.length == 2) {
            String getword1 = getwords.split(" ")[0];
            String getword2 = getwords.split(" ")[1];
          //  System.out.println("getword1 = " + getword1);
          //  System.out.println("getword2 = " + getword2);
            sub1 = (tokenize(getword1.toLowerCase()));
         //   System.out.println("sub1" + sub1);
            sub2 = (tokenize(getword2.toLowerCase()));
          //  System.out.println("sub2" + sub2);
            for (String words : sub1) {
                result1 = findindex(words);
                System.out.println("" + words +" : " + "" + result1);
            }
            for (String words : sub2) {
                result2 = findindex(words);
                
                System.out.println("" + words + " : " +"" + result2);
         //       System.out.println(result2 + " : result2");
            }
            result = mergingforOR(result1, result2);
           // System.out.println("Result : " + result);
            if (result.size() == 0) {
                System.out.println("List is not present in the document");
            } else {
             System.out.println("After performing OR operation we get the following documents");
                 
                for (int k : result){
                    System.out.println(docs[k]);
                    }
            }
        } else {
            System.out.println("Invalid Query please fill 2 words only");
        }
    }
   
   
    private void sort(ArrayList<String> post, ArrayList<Integer> sizeL) {
        // System.out.println("sizeL = " + sizeL);
        for (int k = 0; k < sizeL.size() - 1; k++) {
            String dup2;
            int small = k;
          //  System.out.println("Init small = " + small);
            for (int m = k + 1; m < sizeL.size(); m++) {
                if (sizeL.get(m) < sizeL.get(small))
                    small = m;
           //         System.out.println("small = " + small);
                    
            }
            int dup = sizeL.get(small);
          //  System.out.println("sizeL1 : " + sizeL);
          //  System.out.println("dup: " + dup);
            sizeL.set(small, sizeL.get(k));
          //  System.out.println("sizeL2 : " + sizeL);
            sizeL.set(k, dup);
          //  System.out.println("SizeL3 = " + sizeL);
            dup2 = post.get(small);
         //   System.out.println("dup2 = " + dup2);
            post.set(small, post.get(k));
          //  System.out.println("post1 : " + post);
            post.set(k, dup2);
         //   System.out.println("post2 : " + post);
        }
    }

     

    public void threewordqueryAND(String getwords) {
    
        String[] allwords = getwords.split(" ");
        ArrayList<String> all = new ArrayList<String>();
        all.addAll(Arrays.asList(allwords));
      //  System.out.println(" " + Arrays.toString(allwords));
        System.out.println("The words to be found are: " + getwords);
        ArrayList<Integer> outcome = new ArrayList<>();
        ArrayList<Integer> initlist;
        ArrayList<String> postlist = new ArrayList<String>();
        ArrayList<Integer> sizeoflist = new ArrayList<Integer>();
   //     ArrayList<String> allwords = new ArrayList<String>();
        ArrayList<String> getword = tokenize(getwords.toLowerCase());
      //  for(int i: getwords)
      //  System.out.println("Getwords : " + getword);
        if (getword.size() >= 3) {
            for (String word : getword) {
           
                initlist = findindex(word.toLowerCase());
               System.out.println(" " + word + ": " + initlist);
              
                if (initlist == null) {
                    System.out.println("Word not found");
                    break;
                } else {
                    postlist.add(word);
                 //   System.out.println("postlist: " + postlist);
                    sizeoflist.add(initlist.size());
               //     System.out.println("Init : " + initlist);
             //       System.out.println("sizeoflist : " + sizeoflist);
                }
            }
            
            System.out.println("Before sort = " + postlist);
           System.out.println("Before sort = " + sizeoflist);
            sort(postlist, sizeoflist);
            System.out.println("After sort = " + sizeoflist);
           System.out.println("After sort = " + postlist);
            for (String findword : postlist) {
          //  System.out.println("Postlist : " + findword);
                initlist = findindex(findword);
        //    System.out.println("initlist : " + initlist);
        //    for(Integer word: outcome) {
        //    System.out.println("outcome : " + outcome);
      //      }   
         //   System.out.println("Init outcome: " + outcome);
                if (outcome.size() == 0) {
                    for (Integer i = 0; i < initlist.size(); i++) {
                        outcome.add(initlist.get(i));
         //               System.out.println("Numeric: " + outcome);
                    }
                } else {
                    outcome = mergingforAND(outcome, initlist);
                   
        //            System.out.println("Outcome : " + outcome);
                    if (outcome.size() == 0) {
                        break;
                    }
                }
            }
            if (outcome.size() != 0) {
            System.out.println("After performing AND operation we get the following Documents:");
                for (int i : outcome){
              //  System.out.println("Words are present in following Documents:");
                    System.out.println(docs[i]);
                    }
            } else {
                System.out.println("Merged List not found");
            }
        } else {
            System.out.println("Invalid Query please fill 3 or more words");
        }
    }

    public static void main(String args[]) throws Exception {
        Lab1 p = new Lab1();
        int i = 0;
        String datafiles = "";
        for (String list : docs) {
            datafiles = p.readfilesdata("Lab1_Data/" + list);
            p.assignindex(i++, datafiles);
         //   System.out.println(i + ". " + datafiles + "DATAFILES \n");
        }
        p.scanstopwords();
        ArrayList<String> DataFiles = p.data;
        ArrayList<ArrayList<String>> myDocs = p.myDocs;
        for (String file : DataFiles) {
            myDocs.add(p.tokenize(file));
        }
        p.InvertedIndex();
        System.out.println("\nsearch for 1 variable :");
        p.onewordquery("Time");
        
        System.out.println("\nsearch for 2 variable using AND :");
      //  p.twowordqueryAND("Dylan Cool");
        p.twowordqueryAND("element Border");
        
        System.out.println("\nsearch for 2 variable using OR :");
        p.twowordqueryOR("Rest stalker");
     //   p.twowordqueryOR("Dias Mihir");
        
        System.out.println("\nSearch for 3 or more variable using AND :");
        p.threewordqueryAND("Fact Strain Sound");

    }

}


/*




[0, 1, 2] [2] [1, 2]
Dylan     boy good


[2] [1,2] [0, 1, 2]
boy good  Dylan
*/