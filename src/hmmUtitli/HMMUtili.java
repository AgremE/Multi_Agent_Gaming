package hmmUtitli;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import smartsettlers.AppFrame;
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
	double[][] HMM_TRANSITIONALMATRIX = new double[N_DEVCARDTYPES][N_DEVCARDTYPES];
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
		currentGuessing = new double[NCARDS][N_DEVCARDTYPES]; // TODO: Check this condition to verify it
		HMM_CONDITIONALPRO = new double[TIMESTATESTEP][N_DEVCARDTYPES];
		prior = new double[]{(double)14/25,(double)5/25,(double)2/25,(double)2/25,(double)2/25};
		initTranMatrix();
		this.readDataHMM();
		
	}
	public void initTranMatrix(){
		this.HMM_TRANSITIONALMATRIX[0][0] = (double)13/24;
		this.HMM_TRANSITIONALMATRIX[0][1] = (double)5/24;
		this.HMM_TRANSITIONALMATRIX[0][2] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[0][3] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[0][4] = (double)2/24;
		

		this.HMM_TRANSITIONALMATRIX[1][0] = (double)14/24;
		this.HMM_TRANSITIONALMATRIX[1][1] = (double)4/24;
		this.HMM_TRANSITIONALMATRIX[1][2] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[1][3] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[1][4] = (double)2/24;
		

		this.HMM_TRANSITIONALMATRIX[2][0] = (double)14/24;
		this.HMM_TRANSITIONALMATRIX[2][1] = (double)5/24;
		this.HMM_TRANSITIONALMATRIX[2][2] = (double)1/24;
		this.HMM_TRANSITIONALMATRIX[2][3] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[2][4] = (double)2/24;
		

		this.HMM_TRANSITIONALMATRIX[3][0] = (double)14/24;
		this.HMM_TRANSITIONALMATRIX[3][1] = (double)5/24;
		this.HMM_TRANSITIONALMATRIX[3][2] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[3][3] = (double)1/24;
		this.HMM_TRANSITIONALMATRIX[3][4] = (double)2/24;
		

		this.HMM_TRANSITIONALMATRIX[4][0] = (double)14/24;
		this.HMM_TRANSITIONALMATRIX[4][1] = (double)5/24;
		this.HMM_TRANSITIONALMATRIX[4][2] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[4][3] = (double)2/24;
		this.HMM_TRANSITIONALMATRIX[4][4] = (double)1/24;
	}
	// Call when the HMM first init
	public void readDataHMM(){
		  try {
			  
		        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\AILAB\\Documents\\hmmNewDataPro1000.txt")));         
		        String line;
		        int lineDevCon = 0;
		        String[] lineSplit; 
		        while ((line = br.readLine()) != null) {
		        	lineSplit = line.split(",");
	        		for(int i = 0; i < lineSplit.length; i++){
	        			if((i < TIMESTATESTEP)&&(lineDevCon < N_DEVCARDTYPES)){
	        				this.HMM_CONDITIONALPRO[i][lineDevCon] = Double.valueOf(lineSplit[i]);
	        			}
	        		}
	        		lineDevCon++;
		        }
		        br.close();

		    } 
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("There is an error file reading from HMM agent: " + e.getMessage());
		}
		  //this.HMM_CONDITIONALPRO = this.convertDataIntoPro(this.HMM_CONDITIONALPRO);
	}
	// update local prior
	public double[] updatePriorWithCardReveal(double[] prior_input, int[] localReveal){
		int total_card = 0;
		
		for(int i = 0; i < localReveal.length; i++){
			total_card += localReveal[i];
		}
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			switch (i) {
				case CARD_KNIGHT:
					prior_input[i] = (double)(14 - localReveal[i])/(25-total_card);
					break;
				case CARD_ONEPOINT:
					prior_input[i] = (double)(5 - localReveal[i])/(25 - total_card);
					break;
				case CARD_FREEROAD:
					prior_input[i] = (double)(2 - localReveal[i])/(25 - total_card);
					break;
				case CARD_FREERESOURCE:
					prior_input[i] = (double)(2 - localReveal[i])/(25 - total_card);
					break;
				case CARD_MONOPOLY:
					prior_input[i] = (double)(2 - localReveal[i])/(25 - total_card);
					break;
				default:
					break;
			}
			
		}
		return prior_input;
	}
	
	public int[] getCurrentRevealCard(){
		int[] numberCardTypeReveal = new int[N_DEVCARDTYPES];
		
		for(int i = 0; i < bl.revealCardSoFar.length; i++){
			for(int j =0 ; j < bl.revealCardSoFar[0].length; j++){
				if(bl.revealCardSoFar[i][j] != -1){
					numberCardTypeReveal[bl.revealCardSoFar[i][j]]++;
				}
			}
		}
		return numberCardTypeReveal;
	}
	
	public int[] updateCurrentBelifeGuessCard(int[] currentGuess, int type){
		currentGuess[type]++;
		return currentGuess;
	}
	// Update the guessing according to the time
	//TODO: Check the update variable whether it is updated correctly

	public void updateHMMGuessing(int timeFrame, int currentPlayer, int totalHiddenCard){
		
		this.updatePrior();
		
		if(totalHiddenCard == 0){
			return;
		}
		
		int[] cardReveal = getCurrentRevealCard();
		double[] prior_input = new double[N_DEVCARDTYPES];
		prior_input = prior.clone();
		
		if(totalHiddenCard == 1){
			int timedifferent = 0;
			for(int ind_card = 0; ind_card < NCARDS; ind_card++){
				for(int pl = 0; pl < NPLAYERS; pl++){
					if(currentPlayer == pl){
						continue;
					}
					else{
						if(bl.firstBought[pl][ind_card] > 0){
							timedifferent = timeFrame - bl.firstBought[pl][ind_card];
						}
					}
				}
			}
			this.currentGuessing[0] = Matrix.multiplyByMatrix(this.prior, this.HMM_CONDITIONALPRO[bl.stateRepresentation(timedifferent)]);
		}
		else{
			int[] timedifferent = new int[totalHiddenCard];
			for(int ind_card = 0; ind_card < NCARDS; ind_card++){
				int time_ind = 0;
				for(int pl = 0; pl < NPLAYERS; pl++){
					if(currentPlayer == pl){
						continue;
					}
					else{
						if(bl.firstBought[pl][ind_card] > 0){
							timedifferent[time_ind] = timeFrame - bl.firstBought[pl][ind_card];
							time_ind++;
						}
					}
				}
			}
			for(int i = 0 ; i < totalHiddenCard ; i++){
				if(i == 0){
					this.currentGuessing[i] = Matrix.multiplyByMatrix(this.prior, this.HMM_CONDITIONALPRO[bl.stateRepresentation(timedifferent[i])]);
				}else{
					
					int previousState = getIndexMaxElement(this.currentGuessing[i-1]);
					cardReveal = updateCurrentBelifeGuessCard(cardReveal, previousState);
					prior_input = updatePriorWithCardReveal(prior_input, cardReveal);
					//should update the prior first
					this.currentGuessing[i] = Matrix.multiplyByMatrix
															(Matrix.multiplyByMatrix(prior_input,
																			this.HMM_TRANSITIONALMATRIX[previousState])
																				,this.HMM_CONDITIONALPRO[bl.stateRepresentation(timedifferent[i])]);
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
	// try to conver the conditional pro data into probability data
	public double[][] convertDataIntoPro(double[][] data){
		double[][] dataPro = new double[TIMESTATESTEP][N_DEVCARDTYPES] ;
		double[] totalCount = new double[N_DEVCARDTYPES];
		for(int i = 0; i < TIMESTATESTEP; i++){
			for(int j = 0; j < dataPro[0].length; i++){
				dataPro[i][j] = data[i][j] + 1;
				totalCount[j] += data[i][j];
			}
		}
		for(int i = 0; i < TIMESTATESTEP; i++){
			for(int j = 0; j < dataPro[0].length; i++){
				dataPro[i][j] = dataPro[i][j]/totalCount[i];
			}
		}
		
		return dataPro;
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
					this.prior[i] = (double)(14 - numberCardTypeReveal[i])/(25-total_card);
					break;
				case CARD_ONEPOINT:
					this.prior[i] = (double)(5 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_FREEROAD:
					this.prior[i] = (double)(2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_FREERESOURCE:
					this.prior[i] = (double)(2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				case CARD_MONOPOLY:
					this.prior[i] = (double)(2 - numberCardTypeReveal[i])/(25 - total_card);
					break;
				default:
					break;
			}
			
		}
		
	}
	
	public double[][] getConditionalPro(){
		return this.HMM_CONDITIONALPRO;
	}
	
	//store the probability that get the computation
	public double[][] getCurrentGuess(){
		return this.currentGuessing;
	}
	
	//Get current guess card with the max of probability guessing
	public int[] getCurrentProCardGuess(int totalHiddenCards){
		int[] cardPrediction = new int[totalHiddenCards];
		for(int i = 0; i < totalHiddenCards; i++){
			int type = this.getIndexMaxElement(this.currentGuessing[i]);
			cardPrediction[i] = type;
		}
		return cardPrediction;
	}
	
	
	//
	public void updateHMM(){
		
	}
	public static void main(String[] args) {
        double[][] hmm_proba = new double[N_DEVCARDTYPES][TIMESTATESTEP];
        double[] total_count = new double[N_DEVCARDTYPES];
		try {
			  
	        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\AILAB\\Documents\\hmmData.txt")));         
	        String line;
	        int lineDevCon = 0;
	        String[] lineSplit; 
	        while ((line = br.readLine()) != null) {
	        	lineSplit = line.split(",");
        		for(int i = 0; i < lineSplit.length; i++){
        			if((i < TIMESTATESTEP)&&(lineDevCon < N_DEVCARDTYPES)){
        				hmm_proba[lineDevCon][i] = Double.valueOf(lineSplit[i]);
        				
        			}
        		}
        		lineDevCon++;
	        }
	        br.close();

	    } 
	catch (Exception e) {
		// TODO: handle exception
		System.out.println("There is an error file reading from HMM agent: " + e.getMessage());
	}
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			double total = 0;
			for(int j = 0; j < TIMESTATESTEP; j++){
				total += hmm_proba[i][j]; 
			}
			total_count[i] = total;
		}
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			for(int j = 0; j < TIMESTATESTEP; j++){
				hmm_proba[i][j] = hmm_proba[i][j]/total_count[i];
				System.out.print(hmm_proba[i][j]+", ");
			}
			System.out.println("");
		}
		try {
			String content = "";
			File file = new File("C:\\Users\\AILAB\\Documents\\hmmConditionalProData.txt");
			for(int i = 0; i < N_DEVCARDTYPES; i++){
				double total = 0;
				for(int j = 0; j < TIMESTATESTEP; j++){
					content = content.concat(Double.toString(hmm_proba[i][j])+','); 
				}
	    		content = content.concat("\n");
			}
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(),false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
