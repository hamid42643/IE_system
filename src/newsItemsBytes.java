
import java.io.Serializable;


class newsItemsBytes implements Comparable<newsItemsBytes>, Serializable{
	private long beginingByte=0;
	private long endingByte=0;
	
	newsItemsBytes(){
		
	}
	
	newsItemsBytes(long beginingByte, long endingByte){
		this.beginingByte = beginingByte;
		this.endingByte = endingByte;
	}
	
	public long getBeginingByte() {
		return beginingByte;
	}
	public void setBeginingByte(long beginingByte) {
		this.beginingByte = beginingByte;
	}
	public long getEndingByte() {
		return endingByte;
	}
	public void setEndingByte(long endingByte) {
		this.endingByte = endingByte;
	}
	
	@Override
	public int compareTo(newsItemsBytes n) {

		long num = this.beginingByte - n.beginingByte;
		
		if(num<0){
			return -1;
		}else if(num>0){
			return 1;
		}else{
			return 0;
		}
	}

	
	

	@Override
	public String toString() {
		return beginingByte+"-"+endingByte;
	}
	
	
	
}

/*
import java.io.Serializable;


class newsItemsBytes implements Comparable<newsItemsBytes>, Serializable{
	private long beginingByte=0;
	private long endingByte=0;
	
	newsItemsBytes(){
		
	}
	
	newsItemsBytes(long beginingByte, long endingByte){
		this.beginingByte = beginingByte;
		this.endingByte = endingByte;
	}
	
	public long getBeginingByte() {
		return beginingByte;
	}
	public void setBeginingByte(long beginingByte) {
		this.beginingByte = beginingByte;
	}
	public long getEndingByte() {
		return endingByte;
	}
	public void setEndingByte(long endingByte) {
		this.endingByte = endingByte;
	}
	
	@Override
	public int compareTo(newsItemsBytes n) {

		long num = this.beginingByte - n.beginingByte;
		
		if(num<0){
			return -1;
		}else if(num>0){
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public String toString() {
		return beginingByte+"-"+endingByte;
	}
	
	
	
}

*/