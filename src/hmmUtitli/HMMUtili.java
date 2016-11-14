package hmmUtitli;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

/*
 * Author: Agreme@MakaraPhav
 * */
public class HMMUtili implements HMMConstance, GameStateConstants{

	BoardLayout bl;
	
	// Current guess will store all the data 
	int[][] currentGuessing = new int[NPLAYERS][N_DEVCARDTYPES];
	// Model the HMM (transitional matrix)
	int[][] HMM_A = new int[N_DEVCARDTYPES][N_DEVCARDTYPES];
	// Model the HMM (hiddent State guessing)
	int[][] HMM_B_CARDTIMESTEM = new int[N_DEVCARDTYPES][300];
	int[][] HMM_B_TIMESTEMCARD = new int[300][N_DEVCARDTYPES];
	int[][] HMM_B = new int[N_DEVCARDTYPES][N_DEVCARDTYPES];
	// Model the prior
	int[] prior = new int[N_DEVCARDTYPES];
	
	public HMMUtili(BoardLayout bl){
		this.bl = bl;
	}
	
	public void updateHMMGuessing(){
		
	}
	
	public void updatePrior(){
		
	}
	
	public void updateHMMB(){
		
	}
}
