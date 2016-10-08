package pomcpUtili;
import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.poi.hslf.record.InteractiveInfo;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

//Author: Makara Phav
//This class use only with POMCPPlayer Only

public class BelieveStatePool implements BelieveStateConstance{
	public final int NUMBER_PARTICLE_ADDED = 10;
	ArrayList<BelifeState> history = new ArrayList<BelifeState>();
	HashMap<Integer, BelifeState[]> belifePool = new HashMap<Integer,BelifeState[]>();
	//BelifeState[][] belifePool;
	BoardLayout bl;
	int[] current_belife_states;
	int[][] hidden_info = new int[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
	int owner;
	Random rnd;
	public BelieveStatePool(BoardLayout bl, int pl){
		this.bl = bl;
		//this.state = state;
		this.owner = pl;
		for(int i = 0; i < GameStateConstants.NPLAYERS; i++){
			belifePool.put(i, new BelifeState[BelifePoolSize]);
		}
	}
	
	// when there is someone bought a card from the desk, we will start to initialized the belief state for guessing
	/*
	 * number_card is responsible for storing the number of cards that pl bought in this round
	 * */
	void initBeliefState(int[] hide_state, int pl, int number_card){
		//Try to initialized the based on the prior probability to each state of the game
		for(int ind_card = 0; ind_card < number_card; ind_card++){
			 for(int ind_pl = 0; ind_pl < GameStateConstants.NPLAYERS; ind_pl++){
				 BelifeState[] belife_state_pl = this.belifePool.get(ind_pl);
				 for(int ind_belifeState = 0; ind_belifeState < BelifePoolSize; ind_belifeState++){
					 // Selected the card randomized uniformly distributed with the prior probability
					 //TODO: Check again on the how we pick up the card
					 int cardPickRandom = this.randomizedCard();
					 // map->BelifePool->BelifeState->addOneCard
					 
					 belife_state_pl[ind_belifeState].addOneCard(cardPickRandom);
					 
				 }
				 belifePool.put(ind_pl, belife_state_pl);
			 }
		}
	}

	
	// for Checking particle deprivation problem
	public boolean checkParticleDeprivation(BelifeState curr_knowleadge,int pl){
		for(int ind_pool = 0; ind_pool < BelifePoolSize; ind_pool++){
			if(acceptedBelifeState(curr_knowleadge.getBelifeState(), belifePool.get(pl)[ind_pool].getBelifeState())){
				return false;
			}
		}
		return true;
	}
	//If there is player play his or her card, we update the belief state pool
	public void updateBliefPool(int[] state, int pl, int cardPlaied){
		
		// If the winner buy card no need to update because the state will be show directly to him
		
		BelifeState[] newBelifeState = new BelifeState[BelifePoolSize];
		int[] curr_knowleadge = new int[GameStateConstants.N_DEVCARDTYPES];
		
		// Survey type of card that we can get from player who reveal his or her card right now
		// After the card has been play we need to update the belief pool
		
		if(pl!=this.owner){
			
			//Survey for history of card used first
			
			for(int ind_card = 0; ind_card < GameStateConstants.N_DEVCARDTYPES; ind_card++){
				curr_knowleadge[ind_card] += bl.state[GameStateConstants.OFS_PLAYERDATA[pl] 
												+ GameStateConstants.OFS_USEDCARDS 
												+ ind_card];
			}
			BelifeState curr_revealState = new BelifeState(curr_knowleadge);
			
			boolean finish_updating = false;
			int number_cadidate = 0;
			int number_round = 0;
			
			// If there is no particle in the field that meet the evident that reveal
			if(this.checkParticleDeprivation(curr_revealState, pl)){
				int[] totalCardforPl = getTotalCard(pl);
				for(int ind_pool = 0; ind_pool < NUMBER_PARTICLE_ADDED; ind_pool++){
					
				}
				
			}
			// There is a problem with particle deprivation
			do{
				int ind_pool = rnd.nextInt(BelifePoolSize)+1;
				number_round++;
				if(acceptedBelifeState(curr_revealState.getBelifeState(),
						this.belifePool.get(pl)[ind_pool].getBelifeState())){
					newBelifeState[number_cadidate] = belifePool.get(pl)[ind_pool];
					number_cadidate++;
				}
				if(number_cadidate == BelifePoolSize){
					finish_updating = true;
				}
				
			}while(!finish_updating);
			/*
			for(int ind_pool = 0; ind_pool < BelifePoolSize; ind_pool++){
				if(acceptedBelifeState(curr_knowleadge,belifePool[ind_pool][pl])){
						
				}
			}*/
			
		}
	}
	
	//get total number of card from the specific player
	public int[] getTotalCard(int pl){
		int total[] = new int[3];
		
		for(int type_card = 0; type_card < 3; type_card++){
			for(int ind_card = 0; ind_card<GameStateConstants.N_DEVCARDTYPES; ind_card++){
				total[type_card] += bl.state[GameStateConstants.OFS_PLAYERDATA[pl] 
										+ GameStateConstants.OFS_USEDCARDS 
										+ ind_card];
			}
		}
		
		return total;
	}
	
	//Send signal to other player that there is a player in the game buy the card from the deck of development card
	public void notifyBelifeState(int pl, int numberCardBuying){
		// randomized selecting the card to get the uniformly distributed poopl of cards
		BelifeState[] belifeState = belifePool.get(pl);
		for(int ind_card = 0; ind_card < numberCardBuying; ind_card++){
			for(int ind_belifeState = 0; ind_belifeState < BelifePoolSize; ind_belifeState++){
				 // Selected the card randomized uniformly distributed with the prior probability
				 int cardPickRandom = this.randomizedCard();
				 belifeState[ind_belifeState].addOneCard(cardPickRandom);
			 }
		}
		//update the belifestate about player pl
		belifePool.put(pl, belifeState);
		
	}
	
	// compare an acceptable belife state
	public boolean acceptedBelifeState(int[] curr_knowleadge,int[] belifeparticle){
		for(int ind_card = 0 ; ind_card < GameStateConstants.N_DEVCARDTYPES; ind_card++){
			if(belifeparticle[ind_card] < curr_knowleadge[ind_card]){
				return false;
			}
		}
		return true;
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
	
	//Build a distribution for the belief state it self and pick the one with the highest probability of specific player
	public BelifeState getCurrentBeliefStateFromPlayer(int pl){
		
		BelifeState[] pool = this.belifePool.get(pl);
		Map<Integer[], Integer> distribution = new HashMap<Integer[],Integer>();
		// Make a probablity distribution from this point
		BelifeState[] belifestatetemp = belifePool.get(pl);
		
		for(int ind_pool = 0; ind_pool < BelifePoolSize; ind_pool++){
			
			Integer[] temp_state = belifestatetemp[ind_pool].getBelifeStateInteger();
			
			if(distribution.containsKey(temp_state)){
				int count = 0;
				count = distribution.get(temp_state);
				count++;
				distribution.put(temp_state, count);
			}
			else{
				distribution.put(temp_state, 1);
			}
		}
		
		// Loop through the whole map to get the highest count as beliefe state
		int index_belife = 0;
		int temp_count = 0;
		int[] temp_state = new int[GameStateConstants.N_DEVCARDTYPES];
		
		Iterator<Entry<Integer[], Integer>> it = distribution.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int pair_value = (int) pair.getValue();
	        if(temp_count < pair_value){
	        	temp_count = pair_value;
	        	temp_state = (int[]) pair.getKey();
	        }
	    }
	    
	    BelifeState belife = new BelifeState((int [])temp_state);
		return belife;
	}
	
	// Compare development card only because it is a key for the information asymmetric problem that we have
	public boolean compareState(BelifeState revealState, BelifeState belifeState){
		for(int ind_dev = 0; ind_dev < GameStateConstants.N_DEVCARDTYPES; ind_dev++){
			
			if(revealState.getBelifeState()[ind_dev] > belifeState.getBelifeState()[ind_dev]){
				
				return false;
			
			}
		}
		
		return true;
	}
}
