package pomcpUtili;
//Author: Makara Phav
//This class use only with POMCPPlayer Only
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class BelieveStatePool implements BelieveStateConstance{
	
	int[][] belifePool = new int[BelifePoolSize][GameStateConstants.STATESIZE];
	BoardLayout bl;
	int[] state;
	int owner;
	public BelieveStatePool(BoardLayout bl,int[] state, int pl){
		this.bl = bl;
		this.state = state;
		this.owner = pl;
	}
	void initBeliefState(int[] state, int pl){
		
		
	}
	//If there is player play his or her card, we update the belief state pool
	public void updateBliefPool(int[] state, int pl, int cardPlaied){
		if(pl != this.owner){
			
			int[] new_card = new int[GameStateConstants.NPLAYERS];
			int[] old_card = new int[GameStateConstants.NPLAYERS];
			int[][] used_cards = new int[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
			
		}
		
	}
	//Build a distribution for the belief state it self and pick the one with the highest probability
	public int[] getCurrentBeliefState(int pl){
		int[] state=null;
		//int[][] playerBeliefPool = this.belifePool[pl];
		// need to use HashMap to store the data
		return state;
	}
	// Compare development card only because it is a key for the information asymmetric problem that we have
	public boolean compareState(int[] first_state, int[] second_state){
		
		for(int ind = 0; ind<GameStateConstants.N_DEVCARDTYPES;ind++){
			for(int ind_pl =0; ind_pl < GameStateConstants.NPLAYERS; ind_pl++){
				if(first_state[GameStateConstants.OFS_PLAYERDATA[ind_pl]+GameStateConstants.OFS_OLDCARDS+ind]
						!=second_state[GameStateConstants.OFS_PLAYERDATA[ind_pl]+GameStateConstants.OFS_OLDCARDS+ind]){
					return false;
				}
				if(first_state[GameStateConstants.OFS_PLAYERDATA[ind_pl]+GameStateConstants.OFS_NEWCARDS+ind]
						!=second_state[GameStateConstants.OFS_PLAYERDATA[ind_pl]+GameStateConstants.OFS_NEWCARDS+ind]){
					return false;
				}
			}
		}
		return true;
	}
}
