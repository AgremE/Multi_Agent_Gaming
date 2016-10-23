package pomcpUtili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
public class BelifeState implements GameStateConstants{
	
	private int[][] belifeState;
	private HashMap<int[][], Integer> belife_pool = new HashMap<>();
	Random rnd;
	BoardLayout bl;
	final int TOTAL_PARTICLE = 100;
	
	// belife state initialized
	
	public BelifeState(BoardLayout bl){
		this.belifeState = new int[NPLAYERS][N_DEVCARDTYPES];
		this.bl = bl;
		rnd = new Random();
	}
	
	
	// state should come in with the size of N_DEVCARDTYPES
	public BelifeState(int[][] cardinfo, BoardLayout bl){
		
		this.belifeState = cardinfo;
		this.bl = bl;
		rnd = new Random();
		
	}
	
	//Get the whole state translate properly from the belife particle
	// By assuming we have the right particle which lead to construct the whole belife state
	public int[] getBelifeState(BoardLayout bl){
		
		int[] state = bl.cloneOfState(bl.state);
		int[][] belife_particle = this.getBelifeParticle();
		for(int ind_card = 0; ind_card < N_DEVCARDTYPES; ind_card++){
			
			for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
				
				state[OFS_PLAYERDATA[ind_player]+OFS_OLDCARDS+ind_card] = belife_particle[ind_player][ind_card];
				
			}
			
		}
		
		return state;
	}
	
	//Get the belifeState
	// TODO: Make sure that the hashtable is working properly
	// Belife Particle is only portion of card that need to infer in that particular round
	public int[][] getBelifeParticle(){
		
		int max_occurance = 0;
		int tmp_value = 0;
		int[][] belife_new;
		Vector<int[][]> belife_pool_new = new Vector<>();
		
		for(int[][] key:belife_pool.keySet()){
			
			belife_new = key;
			tmp_value = belife_pool.get(belife_new);
			if(max_occurance <= tmp_value){
				if(max_occurance < tmp_value){
					belife_pool_new.clear();
				}
				belife_pool_new.addElement(belife_new);
				max_occurance = tmp_value;
			}
		}
	
		int lenght = belife_pool_new.size();
		belifeState = belife_pool_new.get(rnd.nextInt(lenght));
		
		return belifeState;
	}
	public Integer[] getBelifeSateInteger(){
		for
	}
	// Convert from Integer[][] to int[][]: needed sometime for Hashmap
	public int[][] converIntegertoInt(Integer[][] state){
		
		if(state[0].length > 0){
			
			int[][] conver_state = new int[state.length][state[0].length];
			
			for(int ind_first = 0; ind_first < state.length; ind_first++){
				
				for(int ind_second = 0; ind_second < state[ind_first].length; ind_second++){
					
					conver_state[ind_first][ind_second] = (int)state[ind_first][ind_second];
					
				}
			}
			return conver_state;
		}
		else{
			return null;
		}
			
	}
	//Update the belifeState according to the reveal information
	public void updateRevealCard(int[][] cardUsed, int[] num_card_notreveal){

		HashMap<int[][], Integer> new_belife_pool = new HashMap<>();
		int match = 0;
		int total_particle = 0;
		
		for(int[][] key: belife_pool.keySet()){
			// TODO: check the number of particle for approximate the belief state
			if(compareBelifeState(cardUsed,key)){
				int tmp_value = belife_pool.get(key);
				new_belife_pool.put(key, tmp_value);
				match++;
				total_particle += tmp_value;
			}
		}
		
		if(match == 0){
			
			int[][] particle;
			
			for(int i = 0; i < TOTAL_PARTICLE; i++){
				
				particle = addParticle(cardUsed,num_card_notreveal);
				
				if(new_belife_pool.containsKey(particle)){
					int tmp_value = new_belife_pool.get(particle);
					tmp_value++;
					new_belife_pool.replace(particle,tmp_value);
				}
				
			}
		}
		
		belife_pool.clear();
		belife_pool.putAll(new_belife_pool);
		new_belife_pool.clear();
		
		if((total_particle != 0) && (total_particle < TOTAL_PARTICLE)){
			
			int[][] particle;
	
			for(int ind_particle_add = 0 ; ind_particle_add < TOTAL_PARTICLE - total_particle; ind_particle_add++){
				
				// to increase number of particle by select the particle at random and increase its frequency 
				List<int[][]> keys = new ArrayList<int[][]>(belife_pool.keySet());
				int particle_ind = rnd.nextInt(keys.size());
				particle =keys.get(particle_ind);
				int tmp_value = belife_pool.get(keys.get(particle_ind));
				tmp_value++;
				belife_pool.replace(particle,tmp_value);
				
			}
			
		}
		
	}
	
	// update the particle when someone buy the cards
	public void updateBoughtCard(int[] cardBought){
		
		int total_card_bought = 0;
		for(int i = 0 ; i < NPLAYERS; i++){
			
			total_card_bought += cardBought[i];
			
		}
		int[] cardRemining = cardGussingDesk(total_card_bought);
		
		for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
			
			if(cardBought[ind_player] >  0){
				for(int ind_card = 0 ; ind_card < cardBought[ind_player]; ind_card++){
					
					for(int[][] key_particle:belife_pool.keySet()){
						
						//update the particle for each of the card encounter with different card selection
						// in the hope that it will give a better approximation
						int card = cardRemining[rnd.nextInt(cardRemining.length)];
						int tmp_value = belife_pool.get(key_particle);
						belife_pool.remove(key_particle);
						key_particle[ind_player][card]++;
						belife_pool.put(key_particle, tmp_value);
						
					}
				}
			}
			
		}
		
	}
	
	//To add new particle into the pool
	public int[][] addParticle(int[][] cardUsed, int[] card_notReveal){
		
		int[][] belife_particle = new int[NPLAYERS][N_DEVCARDTYPES];
		int[] state_clone = bl.cloneOfState(bl.state);
		int total_card_not_reveal = 0 ;
		int[] card_remidning;
		for(int i = 0; i < NPLAYERS ; i++){

			total_card_not_reveal += card_notReveal[i];
		
		}
		
		if(total_card_not_reveal == 0){
			// in case there is no card in the hand of the other players
			belife_particle = cardUsed;
		}
		 
		else{
			
			card_remidning = cardRemindingNotReveal( total_card_not_reveal);
			// in case some of the card still not reveal yet
			belife_particle = cardUsed;
			for(int i = 0; i < NPLAYERS; i++){
				if(card_notReveal[i] > 0 ){
					
					for(int j = 0 ; j < card_notReveal[i];j++){
						int card = card_remidning[rnd.nextInt(card_remidning.length)];
						belife_particle[i][card]++;
					}
				}
			}
		}
		// construct the beliefe particle with random card selecting
		return belife_particle;
	}
	
	// Used only in addParticle function for the reminding card in the desk
	public int[] cardRemindingNotReveal(int cardNotReveal){
		
		int cardGussing = NCARDS + cardNotReveal - bl.state[OFS_NCARDSGONE];
		int[] cardBeforeBuying = new int[cardGussing];
		// if there is no card buy
		
		if(cardGussing == 0 ){
			
			return null;
			
		}
		
		for(int i = 0 ; i < cardGussing; i++ ){
			
			cardBeforeBuying[i] = bl.cardSequence[cardGussing + i];
			
		}
		
		cardBeforeBuying = shuffleArrayofInt(cardBeforeBuying);
		return cardBeforeBuying;
		
	}
	
	
	// Construct array of gussing cards desk
	//TODO: Check this function: there is a problem I cannot use OFS_NCARDGONE because it already the end of the turn
	
	// Check
	public int[] cardGussingDesk(int cardBought){
		
		int cardGuessing = NCARDS - cardBought;
		int[] cardBeforeBuying = new int[cardGuessing];
		// if there is no card buy
		if(cardBought == 0 ){
			return null;
		}
		
		for(int i = 0 ; i < cardGuessing; i++ ){
			
			cardBeforeBuying[i] = bl.cardSequence[cardGuessing + i];
			
		}
		
		cardBeforeBuying = shuffleArrayofInt(cardBeforeBuying);
		return cardBeforeBuying;
	}
	
	// Shuffle one dimensional array of integer
	public int[] shuffleArrayofInt(int[] a)
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
	
	// Use to add one more card into the current belife state of the game
	public void addOneCard(int pl,int card){
		
		this.belifeState[pl][card]++;
		
	}
	
	// Compare the belife state
	public boolean compareBelifeState(int[][] compare_state, int[][] exist_blifestate){
		
		if(compare_state.length >= exist_blifestate.length){
			for(int ind_first = 0; ind_first < exist_blifestate.length; ind_first++){
				
				for(int ind_second = 0; ind_second < exist_blifestate[0].length; ind_second++){
					
						if(compare_state[ind_first][ind_second] < exist_blifestate[ind_first][ind_second]){
						
						return false;
					}
				}
			}
		}
		
		else{
			for(int ind_first = 0; ind_first < exist_blifestate.length; ind_first++){
				
				for(int ind_second = 0; ind_second < exist_blifestate[0].length; ind_second++){
					
					if(compare_state[ind_first][ind_second] < exist_blifestate[ind_first][ind_second]){
						
						return false;
					
					}
				}
			}
		}
		return true;
	}
	
	//get belife state in Integer form
	public Integer[][] coverIntToInteger(){
		
		Integer[][] belife = new Integer[NPLAYERS][N_DEVCARDTYPES];
		
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			for(int j = 0; j < NPLAYERS; j++){
				belife[j][i] = Integer.valueOf(this.belifeState[j][i]);
			}
		}
		
		return belife;
	}
	
	public void addBelife(int[] state){
		// add the particle into the pool
	}
	
}
