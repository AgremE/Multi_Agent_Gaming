package smartsettlers.util;


/*
 * Author Agreme@MakaraPhav 
 */
 
public class CardTimeStamp {
	
	private int cardType;
	private int timeStempBought;
	private int timeStempPlay;
	
	
	public CardTimeStamp(int cardType, int timeStamp){
	
		this.cardType = cardType;
		this.timeStempBought = timeStamp;
		
	}
	
	public int getCard(){
		return this.cardType;
	}
	
	public int getBoughtCardTime(){
		return this.timeStempBought; 
	}
	
	
	public int getPlayCardTime(){
		return this.timeStempPlay;
	}

}
