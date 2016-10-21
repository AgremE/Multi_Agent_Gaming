package pomcpUtili;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
public class BelifeState {
	
	private int[] belifeState;
	
	// belife state initialized
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
	
	//get belife state in Integer form
	public Integer[] getBelifeStateInteger(){
		
		Integer[] belife = new Integer[GameStateConstants.N_DEVCARDTYPES];
		
		for(int i = 0; i < GameStateConstants.N_DEVCARDTYPES; i++){
			belife[i] = Integer.valueOf(this.belifeState[i]);
		}
		
		return belife;
	}
}
