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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.naming.NoInitialContextException;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.FastIterator;
import jdbm.htree.HTree;

class main implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5056349285972943767L;
	/**
	 * 
	 */

	private static ArrayList<String> noiseWords;
	private static RecordManager  recman1;
	private static RecordManager  recman2;
	private static HTree invertedIndexTable;
	private static HTree documentVectorTable;
	
	//progress bar
	private static int TotalTagNum;
	private static int ProcessTagNum;
	
	//terminate the main methode if any kind of exception happen
	public static void main(String[] args) throws IOException
	{
		String inputFileName = "Assignment2_Data.txt";
		
		TotalTagNum = countTag("newsitem", inputFileName);
		TotalTagNum = TotalTagNum/2;
		noiseWords = readNoiseWords("noisewords.txt");
		
		
		//create the database file
		createDatabasejdbm();
		
		//ArrayList<Word> w = (ArrayList<Word>)documentVectorTable.get("195569-197562");
		
		//newsItemsBytes w = (newsItemsBytes)documentVectorTable.get(3345);
		
		showhashTable1();
		//showhashTable2();
		
		RandomAccessFile in = new RandomAccessFile(new File(inputFileName), "r");
		//indexFile(in);
		
		recman1.close();
		recman2.close();
		System.exit(0);
	}//main
	

	
	//---------------------------------------------------------------------
	
	
	private static void indexFile(RandomAccessFile in) throws IOException{
		//pathern: anything but words
		Pattern p1 = Pattern.compile("[^a-zA-Z]+");
		
		String line=null;
		boolean isFirstNewsitemTag = true;
		LinkedList<newsItemsBytes> arr = new LinkedList<newsItemsBytes>();
		Long byteOffset;
		long startOffset = 0;
		
		
		//contain all the words inside one newsitem tag
		//each line represent one element of the arraylist
		ArrayList<String[]> wordsInsideOneNewsitem = new ArrayList<String[]>();

		// Read the input line by line
		while(true)
		{	
			byteOffset = in.getFilePointer();
			
			if((line = in.readLine()) == null){
				break;
			}
			
			//split by pathern and put them into an array
			//for example [0]="word1" [1]="word2" [2]="word3"
			String[] words = p1.split(line);
			
			newsItemsBytes news = null;
			
			wordsInsideOneNewsitem.add(words);
			
			if(words[1].equals("newsitem")){
				
				//if begining of a newsitem tag
				if(isFirstNewsitemTag==true){
					//add byte of begining of newsitem
					news = new newsItemsBytes();
				
					startOffset = byteOffset;
					//get the last element of the queue
					news.setBeginingByte(byteOffset);
					arr.addLast(news);
					isFirstNewsitemTag=false;

				//if end of a newsitem tag
				}else{
					//add byte of end of newsitem
					news = arr.removeLast();
					news.setEndingByte(byteOffset);
					arr.addLast(news);
					isFirstNewsitemTag=true;
					
					//add words inside an opening and closing newsitem tags to the hashtable
					addToHashtables(wordsInsideOneNewsitem, startOffset, byteOffset);
					
					//empty the varible
					wordsInsideOneNewsitem = new ArrayList<String[]>();
				}	
			}
		}
	}
	
	//---------------------------------------------------------------------
	
	//its end of the newsitem tag so
	//add all the words inside the newsitem tag to the final list
	private static void addToHashtables(ArrayList<String[]> arr2, long startOffset, long endOffset){
		ArrayList<Word> wordsArray = new ArrayList<Word>();
		
		for(int i=0; i<arr2.size() ; i++){
			for(int j=0; j<arr2.get(i).length ; j++){
				
				//processing a single word
				String w =arr2.get(i)[j];
				
				if(w.equalsIgnoreCase("sentiment")){
					@SuppressWarnings("unused")
					int ipp=0;
				}
				
				if(!isNoiseWord(w)){
					newsItemsBytes n = new newsItemsBytes();
					n.setBeginingByte(startOffset);
					n.setEndingByte(endOffset);
					
					//wordsArr = updateList(wordsArr, arr2.get(i)[j], n);
					addToInvertedIndexTable(arr2.get(i)[j].toLowerCase(), n);
					
					wordsArray = updateNewsitemWordsArray(arr2.get(i)[j].toLowerCase(), wordsArray);
				}
			}
		}
		
		newsItemsBytes n = new newsItemsBytes();
		n.setBeginingByte(startOffset);
		n.setEndingByte(endOffset);
		
		//calculate logFreqNormalized of each  word object here
		
		addToDocumentVectorTable(wordsArray, n);
		
		arr2 = new ArrayList<String[]>();
		ProcessTagNum++;
		updateProgressBar();
	}

	//---------------------------------------------------------------------------
	public static void addToDocumentVectorTable(ArrayList<Word> arr, newsItemsBytes n){
		ArrayList<Word> arr2 = calLogFreqWeighting(arr);
		try {
			documentVectorTable.put(n.toString(), arr2);
			recman2.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	
	
	//get a word as input and update the newsitem words list
	public static ArrayList<Word> updateNewsitemWordsArray(String word, ArrayList<Word> arr){
		boolean found=false;
		
		for(int j=0; j<arr.size(); j++){
			//word found update frequency
			if(arr.get(j).getWord().equalsIgnoreCase(word)){
				Word w = arr.remove(j);
				w.incrementFreq();
				arr.add(j, w);
				found = true;
				break;
			}
		}
		
		if(found==false){
			Word w = new Word();
			w.setWord(word);
			w.incrementFreq();
			arr.add(w);
		}
		
		return arr;
	}
	
	//---------------------------------------------------------------------------
	//read the main hashtable file - search the file for the input word - if found update the data part
	//- if not found add the word as a new key
	public static void addToInvertedIndexTable(String word, newsItemsBytes n){
		try {
			
			Word w = (Word)invertedIndexTable.get(word);
			//key exists
			if(w!=null){
				//word exist, add docID, the word was found in, to the list
				w.addNewsitem(n);
				
				//save back the docId list to the database
				invertedIndexTable.put(word.toLowerCase(), w);
				
				//w = (Word)hashtable.get(word);
			}else{

				w = new Word();
				w.setWord(word.toLowerCase());
				w.addNewsitem(n);
				invertedIndexTable.put(word.toLowerCase(), w);
			}
			
			recman1.commit();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//---------------------------------------------------------------------------
	public static ArrayList<Word> updateList(ArrayList<Word> arr, String word, newsItemsBytes n){
		boolean found=false;
		
		if(!arr.isEmpty()){
		
			//search for the word in the arraylist
			for(int i=0; i<arr.size() ; i++){
				if(arr.get(i).getWord().equalsIgnoreCase(word)){

					
					//word exist, add the newsitem, the word was found in, to the list
					@SuppressWarnings("unused")
					Word w = arr.get(i);
					arr.get(i).addNewsitem(n);
					found=true;
				}
				
				if(found==true){
					break;
				}
			}
		}
		
		if(found==false){
			Word w = new Word();
			w.setWord(word.toLowerCase());
			w.addNewsitem(n);
			arr.add(w);
		}
		return arr;
	}


	//---------------------------------------------------------------------

	  /**
	obtained from http://alvinalexander.com/blog/post/java/how-open-read-file-java-string-array-list
*/
private static ArrayList<String> readNoiseWords(String filename)
{
	ArrayList<String> arr = new ArrayList<String>();

	try{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		Pattern p1 = Pattern.compile("[^a-zA-Z]+");
		
		while ((line = reader.readLine()) != null){
			String[] words = p1.split(line);
			
			for(int i=0 ; i<words.length ; i++){
				String str = words[i];
				arr.add(words[i]);
			}
		}
		reader.close();
	}
	catch (Exception e){
		System.err.format("Exception occurred trying to read '%s'.", filename);
		e.printStackTrace();
	}
	return arr;
}

//---------------------------------------------------------------------------
	private static String getNewsitem(RandomAccessFile in, long starPointer, long endPointer){
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
                }
                System.out.println();
            }
    //---------------------------------------------------
    
    private static int countTag(String tag, String filename){
    	int count = 0;

    	try{
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
    		String line;
    		Pattern p1 = Pattern.compile("[^a-zA-Z]+");
    		
    		while ((line = reader.readLine()) != null){
    			String[] words = p1.split(line);
    			
    			for(int i=0 ; i<words.length ; i++){
    				if(words[i].equals(tag)){
    					count++;
    				}
    			}
    		}
    		reader.close();
    	}
    	catch (Exception e){
    		System.err.format("Exception occurred trying to read '%s'.", filename);
    		e.printStackTrace();
    	}
    	return count;
    }
         
    
  
	
	private static void updateProgressBar(){
		double num = (ProcessTagNum * 100)/TotalTagNum;
		num = Math.floor(num);
		
		printProgBar((int) num);
		
	}
	
	//obtained from http://nakkaya.com/2009/11/08/command-line-progress-bar/
	public static void printProgBar(int percent){
	    StringBuilder bar = new StringBuilder("[");

	    for(int i = 0; i < 50; i++){
	        if( i < (percent/2)){
	            bar.append("=");
	        }else if( i == (percent/2)){
	            bar.append(">");
	        }else{
	            bar.append(" ");
	        }
	    }

	    bar.append("]   " + percent + "%     ");
	    System.out.print("\r" + bar.toString());
	}
	
	//---------------------------------------------------------------------------
	private static boolean isNoiseWord(String word){
		
		for(int i=0 ; i<noiseWords.size() ; i++){
			if(noiseWords.get(i).endsWith(word)){
				return true;
			}
		}
		
		return false;
	}
	
	//---------------------------------------------------------------------
	
    public static void createDatabasejdbm()
            throws IOException
        {
            // create or open Words record manager
            Properties props = new Properties();
            recman1 = RecordManagerFactory.createRecordManager( "./new2/Inverted_Index_Table", props );


            long recid = recman1.getNamedObject( "invertedIndexTable" );
            if ( recid != 0 ) {
            	invertedIndexTable = HTree.load( recman1, recid );
            } else {
            	invertedIndexTable = HTree.createInstance( recman1 );
                recman1.setNamedObject( "invertedIndexTable", invertedIndexTable.getRecid() );
            }
            
            
            
            // create or open Words record manager
            Properties props2 = new Properties();
            recman2 = RecordManagerFactory.createRecordManager( "./new2/Document_Vector_Table", props2 );


            long recid2 = recman2.getNamedObject( "documentVectorTable" );
            if ( recid2 != 0 ) {
            	documentVectorTable = HTree.load( recman2, recid2 );
            } else {
            	documentVectorTable = HTree.createInstance( recman2 );
            	recman2.setNamedObject( "documentVectorTable", documentVectorTable.getRecid() );
            }
        }


}//class









