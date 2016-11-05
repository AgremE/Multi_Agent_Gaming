package pomcpUtili;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	
	private HashMap<Integer, BelifeParticle> belife_pool = new HashMap<>();
	Random rnd;
	BoardLayout bl;
	
	// belife state initialized
	public BelifeState(BoardLayout bl){
		this.bl = bl;
		rnd = new Random();
	}
	
	//get total number of particles
	public int getBliefPoolSize(){
		int total = 0;
		for(int key : belife_pool.keySet()){
			
			total += key;
		}
		return total;
	}
	
	// state should come in with the size of N_DEVCARDTYPESs
	public BelifeState(int[] cardNotReveal, int[][] cardPlaiedThisRound, BoardLayout bl){
		
		// Init the belief state for more proper implication
		// Fix all the hashtable for all the hash table which involve key as an array
		this.bl = bl;
		rnd = new Random();
		if(bl.hasHiddenInfo()){
			for(int i = 0; i < POMCPConstance.TOTAL_PARTICLE; i++){
				int[] particle = samplingParticle(cardPlaiedThisRound);
				int hc_particle = getHashCodeFromStateArray(particle);
				if(belife_pool.containsKey(hc_particle)){
					
					BelifeParticle particle_input = belife_pool.get(hc_particle);
					int tmp_value = particle_input.getValue();
					tmp_value++;
					particle_input.setValue(tmp_value);
					belife_pool.put(hc_particle, particle_input);
					
				}else{
					//System.out.println("Sampling new particle\n");
					BelifeParticle particle_input = new BelifeParticle(particle, 1);
					belife_pool.put(hc_particle, particle_input);
				}
			}
		}
		else{
			int[] particle = bl.state;
			BelifeParticle belife_fully_observable = new BelifeParticle(particle, POMCPConstance.TOTAL_PARTICLE);
			int hash_state = getHashCodeFromStateArray(particle);
			belife_pool.put(hash_state,  belife_fully_observable);
		}
		
	}
	
	// to help put object into has table
	public static int getHashCode(int[] s)
    {
		int turn = s[GameStateConstants.OFS_TURN];
        int sfmlevel = s[GameStateConstants.OFS_FSMLEVEL];
        int die1 = s[GameStateConstants.OFS_DIE1];
        int die2 = s[GameStateConstants.OFS_DIE2];
        
        s[GameStateConstants.OFS_TURN] = 0;
        s[GameStateConstants.OFS_FSMLEVEL] = 0;
        s[GameStateConstants.OFS_DIE1] = 0;
        s[GameStateConstants.OFS_DIE2] = 0;
        
        int hasing_code = Arrays.hashCode(s);
        
        s[GameStateConstants.OFS_TURN] = turn;
        s[GameStateConstants.OFS_FSMLEVEL] = sfmlevel;
        s[GameStateConstants.OFS_DIE1] = die1;
        s[GameStateConstants.OFS_DIE2] = die2;

        return hasing_code;
        
        
    }
	
	// use when the particle is first created
	/*public int[][] createSamplebBelief(int[] cardStillHidden, int[][] cardUsedInThisRound){
		
		int[][] particle;
		int[] belife;
		private HashMap<int[][], Integer> new_belife_pool = new HashMap<>();
		
		for(int i = 0; i < POMCPConstance.TOTAL_PARTICLE; i++){
			
		}
		
		for(int ind_card = 0; ind_card < N_DEVCARDTYPES; ind_card++){
			
			for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
				
				state[OFS_PLAYERDATA[ind_player]+OFS_OLDCARDS+ind_card] = belife_particle[ind_player][ind_card];
				
			}
			
		}
		return belife;
	}*/
	//Get the whole state translate properly from the belife particle
	// By assuming we have the right particle which lead to construct the whole belife state
	public int[] getBelifeState(){
		
		return this.getBelifeParticle();
	}
	
	//Get the belifeState
	// Belife Particle is only portion of card that need to infer in that particular round
	public int[] getBelifeParticle(){
		
		int max_occurance = 0;
		int tmp_value = 0;
		Vector<int[]> belife_pool_new = new Vector<>();
		
		for(int key:belife_pool.keySet()){
			
			BelifeParticle tmp_particle = belife_pool.get(key);
			tmp_value = tmp_particle.getValue();
			
			if(max_occurance <= tmp_value){
				
				if(max_occurance < tmp_value){
					belife_pool_new.clear();
				}
				belife_pool_new.addElement(tmp_particle.getParticle());
				max_occurance = tmp_value;
				
			}
		}
	
		int lenght = belife_pool_new.size();
		if(lenght == 0){
			return null;
		}
		int[] belifeState = belife_pool_new.get(rnd.nextInt(lenght));
		return belifeState;
	}
	
	// This function used to help the belief pool
	// Check on this function
	public Integer[] getBelifeSateInteger(){
		Integer[] beliefState = null;
		
		return beliefState;
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
	public void updateRevealCard(int[][] cardUsed){

		HashMap<Integer, BelifeParticle> new_belife_pool = new HashMap<>();
		int match = 0;
		int total_particle = 0;
		int tmp_value = 0;
		for(int key: belife_pool.keySet()){

			if(total_particle > POMCPConstance.TOTAL_PARTICLE){
				
				belife_pool.remove(key);
				
			}else{
				BelifeParticle tmp_particle = belife_pool.get(key);
				//check existing particle
				if(compareBelifeState(getStateBeforeHashCompare(getStateFromCardUsed(cardUsed)),
											getStateBeforeHashCompare(tmp_particle.getParticle()))){
					
					tmp_value = tmp_particle.getValue();
					tmp_value++;
					tmp_particle.setValue(tmp_value);
					new_belife_pool.put(key, tmp_particle);
					match++;
					total_particle += tmp_value;
				}
			}
			
		}
		// In case there is no particle in which it represent the belief state
		if(match == 0){
			
			int[] particle;
			
			for(int i = 0; i < POMCPConstance.TOTAL_PARTICLE; i++){
				
				particle = getStateBeforeHashCompare(samplingParticle(cardUsed));
				int hc_particle = getHashCodeFromStateArray(particle);
				
				if(new_belife_pool.containsKey(hc_particle)){
					
					BelifeParticle belife_particle = new_belife_pool.get(hc_particle);
					tmp_value = belife_particle.getValue();
					tmp_value++;
					belife_particle.setValue(tmp_value);
					new_belife_pool.replace(hc_particle,belife_particle);
					
				}
				else{
					BelifeParticle belife_particle = new BelifeParticle(particle, 1);
					new_belife_pool.put(hc_particle, belife_particle);
				}
				
			}
		}
		
		if(total_particle < POMCPConstance.TOTAL_PARTICLE){
			
			int remindingParticleToAdd = POMCPConstance.TOTAL_PARTICLE - total_particle;
			ArrayList<Integer> keySet = new ArrayList<Integer>(new_belife_pool.keySet());
			
			for(int i = 0; i < remindingParticleToAdd; i++){
				
				int particle_index = keySet.get(rnd.nextInt(keySet.size()));
				BelifeParticle pick_particle = new_belife_pool.get(particle_index);
				tmp_value = pick_particle.getValue();
				tmp_value++;
				pick_particle.setValue(tmp_value);
				new_belife_pool.replace(tmp_value,pick_particle);
				
			}
			
		}
		// need to update the guessing for the unrevealed cards;
		
		// update the belief pool
		belife_pool.clear();
		belife_pool.putAll(new_belife_pool);
		new_belife_pool.clear();
		
		if((total_particle != 0) && (total_particle < POMCPConstance.TOTAL_PARTICLE)){
	
			for(int ind_particle_add = 0 ; ind_particle_add < POMCPConstance.TOTAL_PARTICLE - total_particle; ind_particle_add++){
				
				// to increase number of particle by select the particle at random and increase its frequency 
				List<Integer> keys = new ArrayList<Integer>(belife_pool.keySet());
				int particle_ind = rnd.nextInt(keys.size());
				BelifeParticle belife_particle =belife_pool.get(particle_ind);
				tmp_value = belife_particle.getValue();
				tmp_value++;
				belife_particle.setValue(tmp_value);
				belife_pool.replace(particle_ind, belife_particle);
				
			}
			
		}
		
	}
	
	// update the particle when someone buy the cards
	public void updateBoughtCard(int[] cardBought){
		
		int total_card_bought = 0;
		for(int i = 0 ; i < NPLAYERS; i++){
			
			total_card_bought += cardBought[i];
			
		}
		
		int[] cardBeforeBought = cardGuessingDesk(total_card_bought);
		
		for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
			
			if(cardBought[ind_player] >  0){
				
				for(int ind_card = 0 ; ind_card < cardBought[ind_player]; ind_card++){
					
					for(int key:belife_pool.keySet()){
						
						// update the particle for each of the card encounter with different card selection
						// in the hope that it will give a better approximation
						int card = cardBeforeBought[rnd.nextInt(cardBeforeBought.length)];
						BelifeParticle tmp_particle = belife_pool.get(key);
						//remove first before we put it back with the same frequency of tmp_value
						
						belife_pool.remove(key);
						tmp_particle.getParticle()[OFS_PLAYERDATA[ind_player]+OFS_OLDCARDS+card]++;
						belife_pool.put(key, tmp_particle);
						
					}
				}
			}
		}
	}
	
	// update particle one the state is fully observable
	// when there is no card in other player hand.
	// Clear the pool and put the state with fully observation information in it
	public void updateFullyObservable(){

		belife_pool.clear();
		BelifeParticle totally_inform = new BelifeParticle(bl.state, POMCPConstance.TOTAL_PARTICLE);
		int hc_state_fully_obser = getHashCodeFromStateArray(bl.state);
		belife_pool.put(hc_state_fully_obser,totally_inform);
		
	}
	
	//To add new particle into the pool
	public int[] samplingParticle(int[][] cardUsed){
		
		
		int[] belife_particle;
		
		int[] state_clone = BoardLayout.cloneOfState(bl.state);
		
		int fsmlevel    = state_clone[OFS_FSMLEVEL];
        int pl          = state_clone[OFS_FSMPLAYER+ fsmlevel];
        
		int[] hide_state = bl.hideState(pl, state_clone);
		int total_card_not_reveal = 0 ;
		int[] card_remidning;
		
		for(int i = 0; i < NPLAYERS ; i++){

			if(bl.player[i].isPOMCP()){
				
				continue;
				
			}
			else{
				
				total_card_not_reveal += bl.eachPlayerCardNotReveal[i];
				
			}
		
		}
		
		if(total_card_not_reveal == 0){
			// in case there is no card in the hand of the other players
			belife_particle = state_clone;
		}
		 
		else{
			// in case some of the card still not reveal yet
			card_remidning = cardRemindingNotReveal( total_card_not_reveal);
			belife_particle = hide_state;
			for(int ind_pl = 0; ind_pl < NPLAYERS; ind_pl++){
				for(int ind_card = 0; ind_card < N_DEVCARDTYPES; ind_card++){
					
					if(bl.player[ind_pl].isPOMCP()){
						continue;
					}
					else
					{
						belife_particle[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS + ind_card] = cardUsed[ind_pl][ind_card];
					}
					
				}
			}
			// Guessing the unreveal card from the other players
			for(int i = 0; i < NPLAYERS; i++){
				
				if(bl.player[i].isPOMCP()){
					continue;
				}
				else{
					if(bl.eachPlayerCardNotReveal[i] > 0 ){
						
						for(int j = 0 ; j < bl.eachPlayerCardNotReveal[i];j++){
							
							int card = card_remidning[rnd.nextInt(card_remidning.length)];
							belife_particle[OFS_PLAYERDATA[i]+OFS_OLDCARDS + card]++;
							
						}
					}
				}
			}
		}
		// construct the beliefe particle with random card selecting
		return belife_particle;
	}
	
	
	// Used only in addParticle function for the reminding card in the desk when the player is still not reveal their card yet
	public int[] cardRemindingNotReveal(int cardNotReveal){
		
		int cardGussing = NCARDS + cardNotReveal - bl.state[OFS_NCARDSGONE];
		int[] cardBeforeBuying = new int[cardGussing];
		// if there is no card buy
		
		if(cardGussing == 0 ){
			System.out.println("There is no more card to guess");
			return null;
			
		}
		
		for(int i = 0 ; i < cardNotReveal; i++ ){
			
			cardBeforeBuying[i] = bl.cardSequence[cardGussing + i - 1];
			
		}
		
		cardBeforeBuying = shuffleArrayofInt(cardBeforeBuying);
		return cardBeforeBuying;
		
	}
	
	
	// Construct array of guessing cards desk
	public int[] cardGuessingDesk(int cardBought){
		
		// Card reminding after cardBought eliminated
		int cardGone = bl.state[OFS_NCARDSGONE];
		int cardStartingIndex = cardGone - cardBought;
		int cardGuessing = NCARDS  - cardGone + cardBought;
		int[] cardBeforeBuying = new int[cardGuessing];
		// if there is no card buy
		if(cardBought == 0 ){
			return null;
		}
		
		for(int i = 0 ; i < cardGuessing; i++ ){
			
			cardBeforeBuying[i] = bl.cardSequence[cardStartingIndex+i];
			
		}
		cardBeforeBuying = shuffleArrayofInt(cardBeforeBuying);
		
		return cardBeforeBuying;
	}
	
	//Randomize in specific interval
	public int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rnd.nextInt((max - min)) + min;

	    return randomNum;
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
	
	
	// Compare the belife state
	public boolean compareBelifeState(int[] compare_state, int[] exist_blifestate){
		
		
		
		for(int ind_first = 0; ind_first < NPLAYERS; ind_first++){
			
			for(int ind_second = 0; ind_second < N_DEVCARDTYPES; ind_second++){
				
					if(compare_state[OFS_PLAYERDATA[ind_first]+OFS_OLDCARDS + ind_second] 
							< exist_blifestate[OFS_PLAYERDATA[ind_first]+OFS_OLDCARDS + ind_second]){
					
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	//get belife state in Integer form
	public Integer[][] coverIntToInteger(){
		
		Integer[][] belife = new Integer[NPLAYERS][N_DEVCARDTYPES];
		
		for(int i = 0; i < N_DEVCARDTYPES; i++){
			for(int j = 0; j < NPLAYERS; j++){
				belife[j][i] = Integer.valueOf(this.belifeState[j][i]);
			}
		}
		
		return belife;
	}*/
	
	// Make state from Card used
	public int[] getStateFromCardUsed(int[][] cardUsed){
		
		int fsmlevel    = bl.state[OFS_FSMLEVEL];
        int pl          = bl.state[OFS_FSMPLAYER+ fsmlevel];
       
		int[] hideState = bl.hideState(pl, bl.cloneOfState(bl.state));
		
		for(int ind_pl=0; ind_pl < NPLAYERS; ind_pl++){
			
			for(int ind_card=0; ind_card < N_DEVCARDTYPES; ind_card++){
				hideState[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS+ind_card] = cardUsed[ind_pl][ind_card];
			}
		}
		return hideState;
	}
	
	
	// add a new particle into belief pool
	public void addParticle(int[] particle, int value){
		
		int hc_key = getHashCodeFromStateArray(particle);
		BelifeParticle belife_particle = new BelifeParticle(particle, value);
		belife_pool.put(hc_key, belife_particle);
		
	}
	
	// check the existence of the particle
	public boolean particleExist(int[] particle){
		
		if(belife_pool.containsKey((particle))){
			return true;
		}
		return false;
	}
	
	//public get value for particle
	public int getValueForParticle(int[] particle){
		
		return belife_pool.get(getHashCodeFromStateArray(particle)).getValue();
	
	}
	
	//Push value into belief pool
	public void putParticleIntoBeliefPool(int[] particle, int value){
		
		int hc_key = getHashCodeFromStateArray(particle);
		BelifeParticle belife_particle = new BelifeParticle(particle, value);
		belife_pool.put(hc_key, belife_particle);
		
	}
	
	// add one card action for belief pool class
	public void addOneCard(int pl, int card){
		
	}
	
	// get hash code for each of the observation state
	public int getHashCodeFromStateArray(int[] state){
		{
			int turn = state[GameStateConstants.OFS_TURN];
	        int sfmlevel = state[GameStateConstants.OFS_FSMLEVEL];
	        int die1 = state[GameStateConstants.OFS_DIE1];
	        int die2 = state[GameStateConstants.OFS_DIE2];
	        
	        state[GameStateConstants.OFS_TURN] = 0;
	        state[GameStateConstants.OFS_FSMLEVEL] = 0;
	        state[GameStateConstants.OFS_DIE1] = 0;
	        state[GameStateConstants.OFS_DIE2] = 0;
	        
	        int hasing_code = Arrays.hashCode(state);
	        
	        state[GameStateConstants.OFS_TURN] = turn;
	        state[GameStateConstants.OFS_FSMLEVEL] = sfmlevel;
	        state[GameStateConstants.OFS_DIE1] = die1;
	        state[GameStateConstants.OFS_DIE2] = die2;

	        return hasing_code;
		        
		}
	}
	public static int[] getStateBeforeHashCompare(int[] state){
		
		int[] state_clone = state.clone();
		
		state_clone[GameStateConstants.OFS_TURN] = 0;
		state_clone[GameStateConstants.OFS_FSMLEVEL] = 0;
		state_clone[GameStateConstants.OFS_DIE1] = 0;
		state_clone[GameStateConstants.OFS_DIE2] = 0;
		
		return state_clone;
	}
}
