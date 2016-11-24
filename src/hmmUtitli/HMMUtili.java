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
	
	// Current guess will store all the data 
	double[][] currentGuessing;
	// Model the HMM (transitional matrix)
	double[][] HMM_TRANSITIONALMATRIX;
	// Model the HMM (hiddent State guessing)
	double[][] HMM_CONDITIONALPRO;
	// guessing result
	double[] outcome;
	// Model the prior
	double[] prior;
	
	public HMMUtili(BoardLayout bl){
		
		this.bl = bl;
		currentGuessing = new double[NPLAYERS][N_DEVCARDTYPES]; // TODO: Check this condition to verify it
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
		this.updatePrior(bl.revealCardSoFar);
		bl.revealCardSoFar = bl.clearCard(bl.revealCardSoFar);
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
																HMM_CONDITIONALPRO[bl.stateRepresentation(timeFrame)]),
																			this.HMM_TRANSITIONALMATRIX);
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
	public void updatePrior(int[] RevealandBelifecard){
		int total_card = 0;
		
		for(int i = 0; i < RevealandBelifecard.length; i++){
			total_card += RevealandBelifecard[i];
		}
		for(int i = 0; i < RevealandBelifecard.length; i++){
			int num_card = RevealandBelifecard[i];
			switch (i) {
			case CARD_KNIGHT:
				this.prior[i] = (this.prior[i]*14 - num_card)/(25-total_card);
				break;
			case CARD_ONEPOINT:
				this.prior[i] = (this.prior[i]*5 - num_card)/(25 - total_card);
				break;
			case CARD_FREEROAD:
				this.prior[i] = (this.prior[i]*2 - num_card)/(25 - total_card);
				break;
			case CARD_FREERESOURCE:
				this.prior[i] = (this.prior[i]*2 - num_card)/(25 - total_card);
				break;
			case CARD_MONOPOLY:
				this.prior[i] = (this.prior[i]*2 - num_card)/(25 - total_card);
				break;
			default:
				break;
			}
		}
	}
	
	public double[][] getCurrentGuess(){
		return this.currentGuessing;
	}
	
	//
	public void updateHMM(){
		
		
		
	}
	
	
}
