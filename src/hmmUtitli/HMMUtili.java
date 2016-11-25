package hmmUtitli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

/*
 * Author: Agreme@MakaraPhav
 * */
public class HMMUtili implements HMMConstance, GameStateConstants{

	BoardLayout bl;
	// Current guess will store all the data within probability form
	double[][] currentGuessing;
	// Model the HMM (transitional matrix)
	double[][] HMM_TRANSITIONALMATRIX;
	// Model the HMM (hiddent State guessing)
	double[][] HMM_CONDITIONALPRO;
	// guessing result
	double[] outcome;
	// Model the prior
	double[] prior;
	//get current guess in the card form
	int[][] cardGuessing = new int[NPLAYERS][NCARDS];
	
	public HMMUtili(BoardLayout bl_input){
		
		this.bl = bl_input;
		currentGuessing = new double[NPLAYERS][NCARDS]; // TODO: Check this condition to verify it
		HMM_TRANSITIONALMATRIX = new double[N_DEVCARDTYPES][N_DEVCARDTYPES];
		HMM_CONDITIONALPRO = new double[N_DEVCARDTYPES][17];
		prior = new double[N_DEVCARDTYPES];
		this.readDataHMM();
		
	}
	
	// Call when the HMM first init
	public void readDataHMM(){
		  try {
			  
		        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\AILAB\\Documents\\hmmData.txt")));         
		        boolean conditionalMatrixRead = false;
		        String line;
		        int lineDevCon = 0;
		        int lineDevTran = 0;
		        String[] lineSplit; 
		        while ((line = br.readLine()) != null) {
		        	if(line == "Conditional_Probability_Matrix"){
		        		conditionalMatrixRead = true;
		        	}
		        	if(conditionalMatrixRead){
		        		lineSplit = line.split(",");
		        		for(int i = 0; i < lineSplit.length; i++){
		        			if((i < 22)&&(lineDevCon < N_DEVCARDTYPES)){
		        				this.HMM_CONDITIONALPRO[lineDevCon][i] = Double.valueOf(lineSplit[i]);
		        			}
		        		}
		        		lineDevCon++;
		        	}else{
		        		lineSplit = line.split(",");
		        		for(int i = 0; i < lineSplit.length; i++){
		        			if((i < N_DEVCARDTYPES)&&(lineDevTran < N_DEVCARDTYPES)){
		        				this.HMM_TRANSITIONALMATRIX[lineDevTran][i] = Double.valueOf(lineSplit[i]);
		        			}
		        		}
		        		lineDevTran++;
		        	}
		        }
		        br.close();

		    } 
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("There is an error file reading from HMM agent: " + e.getMessage());
		}
	}
	
	// Update the guessing according to the time
	//TODO: Check the update variable whether it is updated correctly
	public void updateHMMGuessing(int timeFrame){
		int totalHiddenCard = 0;
		this.updatePrior();
		for(int i =0; i<bl.eachPlayerCardNotReveal.length;i++){
			totalHiddenCard += bl.eachPlayerCardNotReveal[i];
		}
		
		if(totalHiddenCard == 0){
			return;
		}
		
		if(totalHiddenCard == 1){
			this.currentGuessing[0] = Matrix.multiplyByMatrix(this.prior, this.HMM_CONDITIONALPRO[bl.stateRepresentation(timeFrame)]);
		}
		else{
			for(int i = 0 ; i < totalHiddenCard ; i++){
				if(i == 0){
					this.currentGuessing[i] = Matrix.multiplyByMatrix(this.prior, this.HMM_CONDITIONALPRO[bl.stateRepresentation(timeFrame)]);
				}else{
					int previousState = getIndexMaxElement(this.currentGuessing[i-1]);
					this.currentGuessing[i] = Matrix.multiplyByMatrix
															(Matrix.multiplyByMatrix(prior,
																			this.HMM_TRANSITIONALMATRIX[previousState])
																				,this.HMM_CONDITIONALPRO[bl.stateRepresentation(timeFrame)]);
				}
			}
		}
	}
	
	public int getIndexMaxElement(double[] list){
		double temp_max = 0.0;
		int temp_ind = 0;
		for(int i = 0; i<list.length; i++){
			if(list[i] > temp_max){
				temp_max = list[i];
				temp_ind = i;
			}
			
		}
		return temp_ind;
	}
	
	// Update the prior according to the guessing and reveal cards
	public void updatePrior(){
		
		int total_card = 0;
		int[] numberCardTypeReveal = new int[N_DEVCARDTYPES];
		
		for(int i = 0; i < bl.revealCardSoFar.length; i++){
			for(int j =0 ; j < bl.revealCardSoFar[0].length; j++){
				if(bl.revealCardSoFar[i][j] != -1){
					total_card++;
					numberCardTypeReveal[bl.revealCardSoFar[i][j]]++;
				}
			}
		}
		
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			switch (i) {
				case CARD_KNIGHT:
					this.prior[i] = (14 - numberCardTypeReveal[i])/(25-total_card);
					break;
				case CARD_ONEPOINT:
					this.prior[i] = (5 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_FREEROAD:
					this.prior[i] = (2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_FREERESOURCE:
					this.prior[i] = (2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_MONOPOLY:
					this.prior[i] = (2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				default:
					break;
			}
			
		}
		
	}
	
	//store the probability that get the computation
	public double[][] getCurrentGuess(){
		return this.currentGuessing;
	}
	//Get current guess card with the max of probability guessing
	public int[][] getCurrentCardGuess(){
		return this.cardGuessing;
	}
	
	
	//
	public void updateHMM(){
		
	}
	
	
}
