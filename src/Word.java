import java.awt.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;


class Word implements Serializable{
	//vector space model varibles
	int freq=0;
	double logFreq;
	
	//logFreq divided by length normalized vector of all the logFreqs
	
	//logFreq / sqrt(sum_i(logFreq_i^2))
	double logFreqNormalized;
	
	private String word;
	LinkedList<newsItemsBytes> list;
	
	Word(){
		list = new LinkedList<newsItemsBytes>();
		word="";
	}
	
	
	//add newsitem to the list
	public void addNewsitem(newsItemsBytes n){
		if(list.isEmpty()){
			list.addFirst(n);
		}else{
			//if the item is not alrwady in the list
			if(list.getLast().compareTo(n)!=0){
				list.addLast(n);
			}
		}
	}
	


	public double getLogFreqNormalized() {
		return logFreqNormalized;
	}


	public void setLogFreqNormalized(double d) {
		this.logFreqNormalized = d;
	}


	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}




	public LinkedList<newsItemsBytes> getList() {
		return list;
	}


	public void setList(LinkedList<newsItemsBytes> list) {
		this.list = list;
	}


	public double getLogFreq() {
		return logFreq;
	}


	public void setLogFreq(double logFreq) {
		this.logFreq = logFreq;
	}


	public void incrementFreq() {
		freq++;
		logFreq = Math.log10(freq)+1;
	}



	@Override
	public String toString() {
		if(!list.isEmpty()){
			return "Word [word=" + word + ", list=" + list.toString() + "]";
		}else{
			return "[word=" + word + " logFreqNormalized=" + logFreqNormalized +"]";
		}
	}



	@Override
	public boolean equals(Object w) {
		return (this.word.equalsIgnoreCase(((Word)w).getWord()));
	}

	


}
