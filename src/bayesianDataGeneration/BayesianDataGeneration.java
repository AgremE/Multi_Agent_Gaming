package bayesianDataGeneration;

import java.util.Random;

import smartsettlers.boardlayout.GameStateConstants;

/*
 *  This class generating data
 *  probability is updating due to temperal time step because the prior of game statistic estimation is update due to time of card
 *  pick up 
 */

public class BayesianDataGeneration {
	
	private int[] cardDesk;
	public Random rnd = new Random();
	public int MAX_LENGHT = 1000000;// For now just play 1000 games first
	public int timeStep = 0; // There are total of 25 time step
	
	public BayesianDataGeneration(){
		this.cardDesk = new int[GameStateConstants.NCARDS];
	}
	
	// Shuffle the array to generate the different game cenarious
	public void ShuffleIntArray(int[] a)
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
    }
	
	public static void main(String[] args){
		
	}
	
}
