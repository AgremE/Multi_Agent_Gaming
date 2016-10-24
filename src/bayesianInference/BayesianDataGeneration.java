package bayesianInference;
/*
 * Makara Phav @ Agreme
 * */
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.poi.hmef.attribute.MAPIAttribute;

import smartsettlers.boardlayout.GameStateConstants;

/*
 *  This class generating data
 *  probability is updating due to temperal time step because the prior of game statistic estimation is update due to time of card
 *  pick up 
 */

public class BayesianDataGeneration implements GameStateConstants {
	
	private int[] cardDesk;
	public static Random rnd = new Random();
	public static int MAX_LENGHT = 1000000;// For now just play 1000 games first
	public int timeStep = 0; // There are total of 25 time step
	public static float[][] likelihoodDataTimeStemp = new float[NCARDS][N_DEVCARDTYPES];
	// Card sequence for the game 25
	public static int[] cardSequence = {       
	        //14
	        CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, 
	        CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, 
	        // 5
	        CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, 
	        // 2, 2, 2
	        CARD_MONOPOLY, CARD_MONOPOLY,
	        CARD_FREERESOURCE, CARD_FREERESOURCE,
	        CARD_FREEROAD, CARD_FREEROAD
	    };
	
	// Bayesian Data generation likelihood
	public BayesianDataGeneration(){
		this.cardDesk = new int[GameStateConstants.NCARDS];
	}
	
	// Shuffle the array to generate the different game cenarious
	public static int[] fisherYateShuffle(int[] a)
    {
		
        int firstvalue;
        int pos1, pos2=0;
        int nSteps = 1000;
        int i;
        int N = a.length;
        
        pos1 = rnd.nextInt(N);
        firstvalue = a[pos1];
        
        for (i=0; i<nSteps; i++)
        {
            pos2 = rnd.nextInt(N);
            a[pos1] = a[pos2];
            pos1 = pos2;
        }
        
        a[pos2] = firstvalue;
        return a;
    }
	
	// Converse from int to Integer
	public static Integer[] convertInteger(int[] a){
		
		int length = a.length;
		Integer[] a_new = new Integer[length];
		
		for(int i = 0; i < length; i++){
			a_new[i] = Integer.valueOf(a[i]);
		}
		return a_new;
		
	}
	
	// main function of the program
	/*
	 * This program use to simulate likelihood for the model of Bayesain data generation itself 
	 * put data into excel
	 * 
	 * */
	public static void main(String[] args){
		
		Map<Integer[],Integer> collectionState = new HashMap<Integer[], Integer>();
		Integer[] setCard;
		int[] cardDesk;
		
		// start constructing the data table from here
		for(int ind = 0; ind < MAX_LENGHT; ind++){
			
			cardDesk = fisherYateShuffle(cardSequence);
			setCard = convertInteger(cardDesk);
			
			if(collectionState.containsKey(setCard)){
				int value = collectionState.get(setCard);
				value++;
				collectionState.put(setCard, value);
			}
			
			for(int ind_card = 0; ind_card < NCARDS; ind_card++){
				likelihoodDataTimeStemp[ind_card][cardDesk[ind]]++;
			}
			
		}
		// Constructing the likelihood data spreadsheet
		// Prior will change after each of iteration
		for(int ind_card = 0; ind_card < NCARDS; ind_card++){
			
			for(int ind = 0; ind < N_DEVCARDTYPES; ind++){
					
				float numberCardTypeAppear = likelihoodDataTimeStemp[ind_card][ind];
				float fraction = numberCardTypeAppear/MAX_LENGHT;
				likelihoodDataTimeStemp[ind_card][ind] = fraction;
			}
			
		}
		
	}
	
}
