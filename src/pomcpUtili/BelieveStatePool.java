package pomcpUtili;
import java.util.Random;

//Author: Makara Phav
//This class use only with POMCPPlayer Only
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class BelieveStatePool implements BelieveStateConstance{
	
	int[][][] belifePool;
	BoardLayout bl;
	int[] current_belife_states;
	int owner;
	Random rnd;
	public BelieveStatePool(BoardLayout bl,int[] state, int pl){
		this.bl = bl;
		//this.state = state;
		this.owner = pl;
		this.belifePool = new int[BelifePoolSize][GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
	}
	
	//
	void initBeliefState(int[] hide_state, int pl, int number_card){
		//Try to initialized the based on the prior probability to each state of the game
		for(int ind_card = 0; ind_card < number_card; ind_card++){
			 for(int ind_pl = 0; ind_pl < GameStateConstants.NPLAYERS; ind_pl++){
				 for(int ind_belifeState = 0; ind_belifeState < BelifePoolSize; ind_belifeState++){
					 // Selected the card randomized uniformly distributed with the prior probability
					 //TODO: Check again on the how we pick up the card
					 int cardPickRandom = this.randomizedCard();
					 belifePool[ind_belifeState][ind_pl][cardPickRandom]++;
				 }
			 }
		}
	}
	
	//If there is player play his or her card, we update the belief state pool
	public void updateBliefPool(int[] state, int pl, int cardPlaied){
		
		// If the winner buy card no need to update because the state will be show directly to him
		
		int[][][] newBelifeState = new int[BelifePoolSize][GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
		int[] used_card = new int[GameStateConstants.N_DEVCARDTYPES];
		if(pl!=this.owner){
			//Survey for history of card used first
			for(int ind_card = 0; ind_card < GameStateConstants.N_DEVCARDTYPES; ind_card++){
				used_card[ind_card] += bl.state[GameStateConstants.OFS_PLAYERDATA[pl] 
												+ GameStateConstants.OFS_USEDCARDS 
												+ ind_card];
			}
			for(int ind_pool = 0; ind_pool < BelifePoolSize; ind_pool++){
				if
			}
			
		}
	}
	
	//Send signal to other player that there is a player in the game buy the card from the deck of development card
	public void notifyBelifeState(int pl, int NumberCardBuying){
		// randomized selecting the card to get the uniformly distributed poopl of cards
		for(int ind_card = 0; ind_card < NumberCardBuying; ind_card++){
			
			for(int ind_belifeState = 0; ind_belifeState < BelifePoolSize; ind_belifeState++){
				 // Selected the card randomized uniformly distributed with the prior probability
				 int cardPickRandom = this.randomizedCard();
				 belifePool[ind_belifeState][pl][cardPickRandom]++;
			 }
		}
		
	}
	
	//Select the card at the random fashion
	public int randomizedCard(){
		int indCardSelected;
		indCardSelected = rnd.nextInt(GameStateConstants.NCARDS)+1;
		int cardType = -1;
		if(indCardSelected >= 0){
			cardType = bl.cardSequence[indCardSelected];
		}
		return cardType;
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
