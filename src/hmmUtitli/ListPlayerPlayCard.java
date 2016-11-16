package hmmUtitli;

import java.util.ArrayList;
import java.util.List;

import smartsettlers.util.CardTimeStamp;

public class ListPlayerPlayCard {

	List<CardTimeStamp> cardListTime;
	int player;
	
	public ListPlayerPlayCard(int player){
		 cardListTime = new ArrayList<CardTimeStamp>();
		 this.player = player;
	}
}
