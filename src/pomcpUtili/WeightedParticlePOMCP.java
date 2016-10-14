package pomcpUtili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

	public class WeightedParticlePOMCP implements GameStateConstants{
		
	private int[] cardDesk;
	public static Random rnd;
	ArrayList<BelifeState> history = new ArrayList<BelifeState>();
	HashMap<Integer, BelifeState[]> belifePool = new HashMap<Integer,BelifeState[]>();
	BoardLayout bl;
	int[] current_belife_states;
	int[][] hidden_info = new int[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
	int owner;
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
	
	public WeightedParticlePOMCP(){
		this.cardDesk = new int[NCARDS];
		this.current_belife_states = new int[NCARDS];
		for(int i = 0; i < NCARDS; i++){
			current_belife_states[i] = -1;
			cardDesk[i] = -1;
		}
		rnd = new Random();
		
	}
	
}
