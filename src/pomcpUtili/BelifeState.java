package pomcpUtili;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class BelifeState{
	
	private int[] belifeState;
	
	public BelifeState(){
		this.belifeState = new int[GameStateConstants.N_DEVCARDTYPES];
	}
	// state should come in with the size of N_DEVCARDTYPES
	public BelifeState(int[] state){
		this.belifeState = state;
	}
	//Get the belifeState
	public int[] getBelifeState(){
		return this.belifeState;
	}
	//Update the belifeState according to the reveal information
	public void updateState(int[] state){
		this.belifeState = state;
	}
	// Use to add one more card into the current belife state of the game
	public void addOneCard(int card){
		this.belifeState[card]++;
	}
	
}
