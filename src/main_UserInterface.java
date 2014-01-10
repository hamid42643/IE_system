


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import javax.naming.NoInitialContextException;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

class main_UserInterface implements Serializable
{
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -4425980594168059651L;
	private static ArrayList<String> noiseWords;
	private static RecordManager  recman1;
	private static RecordManager  recman2;
	private static HTree invertedIndexTable;
	private static HTree documentVectorTable;
	private static RandomAccessFile in;
	private static String mainfile = "Assignment2_Data.txt";
	
	private static String databaseFile1 = "./Inverted_Index_Table.db";
	private static String databaseFile2 = "./Document_Vector_Table.db";
	
	//progress bar
	private static int TotalTagNum;
	private static int ProcessTagNum;
	
	private static ArrayList<Word> query;
	
	
	
	public static void main(String args[]) throws IOException{

		//String path = System.getProperty("user.dir");
		//in = new RandomAccessFile(new File(path+"/Assignment2_Data.txt"), "r");
		
		loadDatabasejdbm();
		
		//Word w = (Word)invertedIndexTable.get("income");
		
		//ArrayList<Word> w = (ArrayList<Word>)documentVectorTable.get("17668-22116");
		
		//showhashTable2();

		@SuppressWarnings("unused")
		String str = performSearch("support");

		
		System.out.print(str);
	}
	
	
	
	//terminate the main methode if any kind of exception happen
	public static void start() throws IOException
	{
		//String inputFileName = "file.xml";
		
		
		//create the database file
		loadDatabasejdbm();
		
		//Word w = (Word)invertedIndexTable.get("sentiment");
		
		//newsItemsBytes w = (newsItemsBytes)documentVectorTable.get(3345);
		
		//showhashTable1();
		
		
		//RandomAccessFile in = new RandomAccessFile(new File(inputFileName), "r");
		//indexFile(in);
		
		recman1.close();
		recman2.close();
		//System.exit(0);
	}//main

	
    	
    	//---------------------------------------------------------------------	
    	
        public static void showhashTable1() 
                throws IOException
            {
        		FastIterator keys;
        		FastIterator values;
        		
        		String str;
        		Word data;
        		
                // Display content of word hashTable
                System.out.println();
                keys = invertedIndexTable.keys();
                values = invertedIndexTable.values();
                
                @SuppressWarnings("unused")
				Word w = (Word)invertedIndexTable.get("income");

                data = (Word) values.next();
                
                while ( data != null ) {
                    System.out.print( data.toString()+"\n" );
                    data = (Word) values.next();
                }
                System.out.println();
            }
    
        
        public static void showhashTable2() 
                throws IOException
            {
        		FastIterator keys;
        		FastIterator values;
        		
        		String str;
        		ArrayList<Word> data;
        		
                // Display content of word hashTable
                System.out.println();
                keys = documentVectorTable.keys();
                values = documentVectorTable.values();
                
                @SuppressWarnings("unused")
				//Object o = values.next();
                
                String key = null;
                
                data = (ArrayList<Word>) values.next();
                key = (String) keys.next();
                
                
                while ( data != null ) {
                	System.out.print( key.toString()+"    ");
                	
                    System.out.print( data.toString()+"\n" );
                    
                    data = (ArrayList<Word>) values.next();
                    key = (String) keys.next();
                    
                    @SuppressWarnings("unused")
                    ArrayList<Word> o = (ArrayList<Word>)documentVectorTable.get(key);
                    
                    @SuppressWarnings("unused")
					int i=0;
                    i=1;
                }
                System.out.println();
            }
    //---------------------------------------------------
    public static void closeDatabasejdbm() throws IOException{
		recman1.close();
		recman2.close();
    }
    
	
	//---------------------------------------------------------------------
	
    public static void loadDatabasejdbm()
            throws IOException
        {
			String path = System.getProperty("user.dir");
			in = new RandomAccessFile(new File(path+"/"+mainfile), "r");
		
            // create or open Words record manager
            Properties props = new Properties();
            recman1 = RecordManagerFactory.createRecordManager( "./new2/Inverted_Index_Table", props );


            long recid = recman1.getNamedObject( "invertedIndexTable" );
            if ( recid != 0 ) {
            	invertedIndexTable = HTree.load( recman1, recid );
            } else {
            	System.out.print("cant find the database file 1");
            }
            
            
            
            // create or open Words record manager
            Properties props2 = new Properties();
            recman2 = RecordManagerFactory.createRecordManager( "./new2/Document_Vector_Table", props2 );


            long recid2 = recman2.getNamedObject( "documentVectorTable" );
            if ( recid2 != 0 ) {
            	documentVectorTable = HTree.load( recman2, recid2 );
            } else {
            	System.out.print("cant find the database file 2");
            }
        }


    
 //------------------------------------BOOLEAN RETRIVAL --------------------------------
    

	public static String performSearch(String q) throws IOException{
		String words[] = prepareQuery(q);
		LinkedList<newsItemsBytes> ab = null;
		
		ArrayList<LinkedList<newsItemsBytes>> arr = new ArrayList<LinkedList<newsItemsBytes>>();
		
		//search for each word of the query and add docids the word was found in to an arraylist
		for(int i=0 ; i<words.length ; i++){
			LinkedList<newsItemsBytes> list = searchInvertedIndex(words[i]);
			
			if(list==null){
				return null;
			}
			arr.add(list);
		}
		
		
		if(words.length ==1){
			ab = arr.get(0);
		}
		else if ((words.length == 2)){
			LinkedList<newsItemsBytes> a = arr.get(0);
			LinkedList<newsItemsBytes> b = arr.get(1);

			ab = (LinkedList<newsItemsBytes>) union(a, b);
		}
		else if(words.length > 2){
			LinkedList<newsItemsBytes> a = arr.get(0);
			LinkedList<newsItemsBytes> b = arr.get(1);

			ab = (LinkedList<newsItemsBytes>) union(a, b);
			
			for(int i=2 ; i<words.length - 2 ; i++){
				
				a = arr.get(i);
				b = arr.get(i+1);

				ab = (LinkedList<newsItemsBytes>) union(a, b);
			}
		}

		//search document vector
		
		//ab is a linkedlist of all documents ids that one of the query word was found in
		return searchDocumentVector(ab);

		//return getNewsitems(ab);
	}
	
	//-----------------------------------------------------------------------------------------
	//http://stackoverflow.com/questions/5283047/intersection-union-of-arraylists-in-java 
	   public static <T> List<T> union(List<T> list1, List<T> list2) {
	        Set<T> set = new HashSet<T>();

	        set.addAll(list1);
	        set.addAll(list2);

	        return new LinkedList<T>(set);
	    }

	
	    public <T> List<T> intersection(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	            if(list2.contains(t)) {
	                list.add(t);
	            }
	        }

	        return list;
	    }
	//--------------------------------------------------------------------------------------------
	
	private static String getNewsitems(/*LinkedList<newsItemsBytes> arr*/ ArrayList<document> docs){
		String str = null;
		try {
			str = null;
				
				int index = findMaxScore(docs);
				
				while(index!=-1){
					String htmlStr = getNewsitem(docs.get(index).getStartByte(), 
							docs.get(index).getEndByte());
					
					Document doc = Jsoup.parse(htmlStr);
					
			
					
					str += doc.getElementsByTag("text").text();
					
					str+="\n\n\n----------Similarity Score: "+docs.get(index).getScore()+"-----\n\n\n";
					
					
					docs.remove(index);
					index = findMaxScore(docs);
				}
				
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		return str;
	}
	
	private static int findMaxScore(ArrayList<document> docs){
		if(docs==null){
			return -1;
		}
		
		double maxScore=-1;
		
		int index = -1;
		
		for(int j=0 ; j<docs.size() ; j++){
			if(docs.get(j).getScore()>maxScore){
				maxScore = docs.get(j).getScore();
				index = j;
			}
		}

		return index;
	}
	
	
	//--------------------------------------------------------------------------------
	private static LinkedList<newsItemsBytes> removeDublicates(LinkedList<newsItemsBytes> arr){
		LinkedList<newsItemsBytes> arrClean = new LinkedList<newsItemsBytes>();
		
		for(int j=0 ; j<arr.size() ; j++){
			newsItemsBytes news = arr.get(j);
			
			boolean found = false;
			
			for(int i=0 ; i<arrClean.size() ; i++){
				if(news.compareTo(arrClean.get(i))==0){
					found = true;
					break;
				}
			}
			
			if(found==false){
				arrClean.add(arr.get(j));
			}
		}
		
		return arrClean;
	}
	//-----------------------------------------------------------------------------------------
	
	
	private static String getNewsitem(long starPointer, long endPointer){
		byte[] b = new byte[(int) (endPointer-starPointer)];
		String newsitem = null;
		
		try {
			in.seek(starPointer);
			in.read(b);
			newsitem = new String(b, "UTF-8");  // example for one encoding type
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return newsitem;
	}


	//-----------------------------------------------------------------------------------------
	
	
	private static String[] prepareQuery(String q){
		String[] queryWords = q.split("\\W+");
		
		ArrayList<Word> arr = new ArrayList<Word>();
		
		for(int i=0 ; i<queryWords.length ; i++){
			Word w = new Word();
			w.setWord(queryWords[i]);
			w.incrementFreq();
			arr.add(w);
		}
		
		query = calLogFreqWeighting(arr);
		
		return queryWords;
	}
	
	//calculate sum_i(logFreq_i^2)
	private static double calSumOfNormalizedFreqs(ArrayList<Word> arr){
		double sum = 0;
		for(int i=0 ; i<arr.size() ; i++){
			sum += Math.pow(arr.get(i).getLogFreq(), 2);
		}
		
		return Math.sqrt(sum);
	}
	
	
	//logFreq / sqrt(sum_i(logFreq_i^2))
	private static ArrayList<Word> calLogFreqWeighting(ArrayList<Word> arr){
		double sum = calSumOfNormalizedFreqs(arr);
		
		for(int i=0 ; i<arr.size() ; i++){
			arr.get(i).setLogFreqNormalized(roundTwoDecimals(arr.get(i).getLogFreq()/sum));
		}
		
		return arr;
	}
	
	static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat(".###");
        return Double.valueOf(twoDForm.format(d));
	}
	
	//-------------------------------------------------------------------------------------
	//search for the word in the database and return a list of DocIDs the word is in
	private static LinkedList<newsItemsBytes> searchInvertedIndex(String wordStr){
		Word w = null;
		try {
			w = (Word)invertedIndexTable.get(wordStr);
			
			
			if(w!=null){
				return w.getList();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
    
//----------------------------------COSINE SIMILARITY--------------------------------------------
	

	
	//calculate similarity score between query and ONE document
	public static double calCosineSimilarity(ArrayList<Word> doc){
		Double score = 0.0;
		
		for(int i=0 ; i<query.size() ; i++){
			for(int j=0 ; j<doc.size() ; j++){
				if(doc.get(j).equals(query.get(i))){
					
					Word a = doc.get(j);
					Word b = query.get(i);
					
					score += a.getLogFreqNormalized() * b.getLogFreqNormalized();		
				}
			}
		}
		return score;
	}
	
	
	//calculate similarity score between query and ALL the documents
	public static String calCosineSimilarities(ArrayList<document> docs){
		for(int i=0 ; i<docs.size() ; i++){
			double score = calCosineSimilarity(docs.get(i).getWords());
			
			//remove the dcument from the list, update its similarity(doc, query) score
			//add the document back to the list
			
			document d = docs.get(i);
			docs.remove(i);
			d.setScore(score);
			docs.add(i, d);
		}
		
		return getNewsitems(docs);
	}
	

	
	//-----------------------------------------------------------------------------------------
	//creates a list of all the documents that one of the query words was found in
	public static String searchDocumentVector(LinkedList<newsItemsBytes> ab){
		ArrayList<document> docs = new ArrayList<document>();
		
		for(int i=0 ; i<ab.size() ; i++){
			document doc = new document();
			doc.setStartByte(ab.get(i).getBeginingByte());
			doc.setEndByte(ab.get(i).getEndingByte());
			
			newsItemsBytes n = ab.get(i);
			
			ArrayList<Word> arr = searchDocumentVectorFile(n);
			doc.setWords(arr);
			
			docs.add(doc);
		}
		
		return calCosineSimilarities(docs);
			
	}
	
	
	
	//search for the word in the database and return a list of DocIDs the word is in
	private static ArrayList<Word> searchDocumentVectorFile(newsItemsBytes n){
		ArrayList<Word> arr= null;
		
		//key = (newsItemsBytes) keys.next();
		
		try {
			//arr = (ArrayList<Word>)documentVectorTable.get((Object)n);
			
		
			arr = (ArrayList<Word>)documentVectorTable.get(n.toString());
			
			if(arr!=null){
				return arr;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}//class


class document{
	long startByte;
	long endByte;
	
	public long getStartByte() {
		return startByte;
	}
	public void setStartByte(long startByte) {
		this.startByte = startByte;
	}
	public long getEndByte() {
		return endByte;
	}
	public void setEndByte(long endByte) {
		this.endByte = endByte;
	}
	ArrayList<Word> words;
	
	double score;
	
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public ArrayList<Word> getWords() {
		return words;
	}
	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}
	
}






