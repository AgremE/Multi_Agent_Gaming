package randomePlayer;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class RandomAgent implements GameStateConstants {
	BoardLayout bl;
	
	public double[] prior = new double[5];
	
	public RandomAgent(BoardLayout bl){
		this.bl = bl;
	}
	
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
					this.prior[i] = (double)(15 - numberCardTypeReveal[i])/(25-total_card);
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
	
	public double[] updatePriorWithCardReveal(double[] prior_input, int[] localReveal){
		int total_card = 0;
		
		for(int i = 0; i < localReveal.length; i++){
			total_card += localReveal[i];
		}
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			switch (i) {
				case CARD_KNIGHT:
					prior_input[i] = (double)(15 - localReveal[i])/(25-total_card);
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
	
	public int[] guessingCard( int total_hidden){
		
		double max = 0;
		int max_ind = -1;
		int[] guessing = new int[total_hidden];
		int[] careReveal = getCurrentRevealCard();
		updatePrior();
		double[] prior_input = prior.clone();
		for(int i = 0; i < total_hidden; i++){
			
			for(int j = 0; j < prior_input.length; j++){
				if(prior_input[j] > max){
					max_ind = j;
					max = prior_input[j];
				}
			}
			guessing[i] = max_ind;
			updateCurrentBelifeGuessCard(careReveal, max_ind);
			prior_input = updatePriorWithCardReveal(prior_input, careReveal);
		}
		return guessing;
	}
	
	public int[] updateCurrentBelifeGuessCard(int[] currentGuess, int type){
		currentGuess[type]++;
		return currentGuess;
	}
	
	public double[] update_prior(int[] guessingState){		
		int total_card = 0;
		int[] numberCardTypeReveal = new int[5];

		if(guessingState.length == 0){
			return prior;
		}
		
		for(int i = 0; i < bl.revealCardSoFar.length; i++){
			for(int j =0 ; j < bl.revealCardSoFar[0].length; j++){
				if(bl.revealCardSoFar[i][j] != -1){
					total_card++;
					numberCardTypeReveal[bl.revealCardSoFar[i][j]]++;
				}
			}
		}
		for(int i = 0; i < guessingState.length; i++){
			total_card++;
			numberCardTypeReveal[guessingState[i]]++;
		}
		
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			switch (i) {
				case CARD_KNIGHT:
					this.prior[i] = (double)(15 - numberCardTypeReveal[i])/(25-total_card);
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
		return prior;
	}
}
