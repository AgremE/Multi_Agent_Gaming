package pomcpUtili;

import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
public interface POMCPConstance{
	public int BelifePoolSize = 100;
	// The below three argument is for represent the belife state conresponding to the state in POCMP Actual State
	public int USEDCARD = 0;
	public int OLDCARD  = 1;
	public int NEWCARD = 2;
	public int OBSERVATION = 25;
	//Game State
	public final int TERMINATE_STATE = 0;
	public final int NORMAL_STATE = 1;
	public final int MAX_DEPTH = 300; // Need to think about this number why?
	public final int MAX_DEPTH_ROLLOUT  = MAX_DEPTH + 200;
	public final double DISCOUNT_FACTOR = 0.3;
	
	final int TOTAL_PARTICLE = 50;
	
	
	// for card gussing
    int CARD_KNIGHT             = 0;
    int CARD_ONEPOINT           = 1;
    int CARD_FREEROAD           = 2;
    int CARD_FREERESOURCE       = 3;
    int CARD_MONOPOLY           = 4;
	public int[] cardSequence = {       
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
}
