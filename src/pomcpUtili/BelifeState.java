package pomcpUtili;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
public class BelifeState {
	
	private int[] belifeState;
	private HashMap<int[], Integer> belife_pool = new HashMap<>();
	Random rnd = new Random();
	final int MAX_HEAP_BELIFE = 100;
	
	
	// belife state initialized
	public BelifeState(){
		this.belifeState = new int[GameStateConstants.N_DEVCARDTYPES];
	}
	
	// state should come in with the size of N_DEVCARDTYPES
	public BelifeState(int[] state){
		
		this.belifeState = state;
		
	}
	
	//Get the belifeState
	// TODO: Make sure that the hashtable is working properly
	public int[] getBelifeState(){
		
		int max_occurance = 0;
		int tmp_value = 0;
		int[] belife_new;
		Vector<int[]> belife_pool_new = new Vector<>();
		Iterator it = belife_pool.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry en = (Map.Entry)it.next();
			tmp_value = (int)en.getValue();
			if(max_occurance <= tmp_value){
				if(max_occurance < tmp_value){
					belife_pool_new.clear();
				}
				belife_pool_new.addElement((int[])en.getKey());
			}
		}
		int lenght = belife_pool_new.size();
		belifeState = belife_pool_new.get(rnd.nextInt(lenght));
		return belifeState;
	}
	
	//Update the belifeState according to the reveal information
	public void updateState(int[] cardUsed, int num_card_notreveal){
		Iterator it = belife_pool.entrySet().iterator();
		HashMap<int[], Integer> new_belife_pool = new HashMap<>();
		int match = 0;
		while(it.hasNext()){
			Map.Entry en = (Map.Entry)it.next();
			if(compareBelifeState(cardUsed,(int [])en.getKey())){
				new_belife_pool.put((int [])en.getKey(), (int)en.getValue());
				match++;
			}
		}
		if(match == 0){
			int[] particle;
			for(int i = 0; i < MAX_HEAP_BELIFE; i++){
				particle = addParticle(cardUsed,num_card_notreveal);
				new_belife_pool.put(particle,1);
			}
		}
		belife_pool.clear();
		belife_pool.putAll(new_belife_pool);
		new_belife_pool.clear();
		
	}
	//To add new particle into the pool
	public int[] addParticle(int[] cardUsed, int card_notReveal){
		int[] belife_particle = new int[cardUsed.length + card_notReveal];
		if(card_notReveal == 0){
			belife_particle = cardUsed;
		}
		else{
			for(int i = 0; i < card_notReveal; i++){
				int card = rnd.nextInt(card_notReveal);
				// TODO: need to form a particle that represent the belife state
			}
		}
		// construct the beliefe particle with random card selecting
		return belife_particle;
	}
	// Use to add one more card into the current belife state of the game
	public void addOneCard(int card){
		
		this.belifeState[card]++;
		
	}
	// Compare the belife state
	public boolean compareBelifeState(int[] compare_state, int[] exist_blifestate){
		
		if(compare_state.length >= exist_blifestate.length){
			for(int ind_state = 0; ind_state < exist_blifestate.length; ind_state++){
				if(compare_state[ind_state] < exist_blifestate[ind_state]){
					return false;
				}
			}
		}
		
		else{
			for(int ind_state = 0; ind_state < compare_state.length; ind_state++){
				if(compare_state[ind_state] < exist_blifestate[ind_state]){
					return false;
				}
			}
		}
		return true;
	}
	//get belife state in Integer form
	public Integer[] getBelifeStateInteger(){
		
		Integer[] belife = new Integer[GameStateConstants.N_DEVCARDTYPES];
		
		for(int i = 0; i < GameStateConstants.N_DEVCARDTYPES; i++){
			belife[i] = Integer.valueOf(this.belifeState[i]);
		}
		
		return belife;
	}
	
}
