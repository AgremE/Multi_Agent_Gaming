package pomcpUtili;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import smartsettlers.boardlayout.ActionList;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
//After new root node selection we just start over
public class QNode implements POMCPConstance{

	int VISIT = 0;
	double REWARD = 0;
	int[] action;
	Random rnd;
	BoardLayout bl;
	public Hashtable<Integer, VNode> Children;// key is observation as an array of integer
	/*
	 * Observation is in the form of the state with some guessing of the card at random in which its card's desk is updated accordingly 
	 * due to the number of reminding cards
	 * */
	public QNode(BoardLayout bl, int[] action){
		this.action = action;
		this.bl = bl;
		this.rnd = new Random();
		Children = new Hashtable<>();
		this.setValue(0.0, 0);
		
	}
	
	
	// randomizing the observation in the hope of getting the best guess from the pool
	public int observation(){
		return rnd.nextInt();
	}
	
	// For converting the two dimensional array of int into Integer
	public static Integer[][] convIntToInteger(int[][] obser){
		Integer[][] new_obser = new Integer[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
		for(int i=0; i < obser.length; i++){
			for(int j = 0; j < obser[i].length; j++){
				new_obser[i][j] = (Integer)obser[i][j];
			}
		}
		return new_obser;
	}
	
	// Simulate Q value function
	public double simulate_q(QNode q_node, int[] state, int depth){
		
		
		double immReward, delayReward;
		
		int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
	    int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
        //Selected action in greedy manner to see whether it good to selection capability
       
		if(bl.player[pl].isPOMCP()){
			immReward = rewardingModel(q_node.action[0]);
			 q_node.VISIT++;
		}
		else{
			immReward = 0;
			if( q_node.VISIT == 0){
				 q_node.VISIT++;
			}
			
		}
		
		int winner = bl.getWinner(state);

		if(winner != -1 ){
			if(bl.player[pl].isPOMCP()){
				return 50;
			}
			return 0;
		}
		double total_reward = 0;
		VNode node;
		
		// Making an observation for this round base on current belife state
		
		int[] observation_array = makeObservation(q_node.action);
		int hash_code_observation = getHashCodeFromArray(observation_array);
		
		node = q_node.Children.get(hash_code_observation);
		
		if(q_node.Children.get(hash_code_observation) == null){
			node = expand_vnode(observation_array, depth);
		}
		
		if(node != null){
			depth++;
			
			Children.put(hash_code_observation, node);
			bl.player[pl].performAction_simulation(state, q_node.action);
			bl.stateTransition(state, q_node.action);
			delayReward = node.simulation_v(state, node, depth);
		}
		else{
			depth++;
			delayReward = rollout(state, depth);
			//doing the random rollout from here
		}
		depth--;
		
		total_reward = immReward + delayReward*DISCOUNT_FACTOR;
		return total_reward;
	}
	
	
	// Make a randomized card selection for the observation overhead0
	// Let change the observation method to define in term of number of card in each other player hand and guess it
	public int[] makeObservation(int[] action){
		
		int fsmlevel    = bl.state[GameStateConstants.OFS_FSMLEVEL];
        int pl          = bl.state[GameStateConstants.OFS_FSMPLAYER+ fsmlevel];
        int[] state_clone = BoardLayout.cloneOfState(bl.state);
        int[] gussingObservation = bl.hideState(pl, state_clone);
        
		for(int ind_player = 0; ind_player < GameStateConstants.NPLAYERS; ind_player++ ){
			// TODO: Check this function to update accordingly
			int lenght = bl.eachPlayerCardNotReveal[ind_player];
			for(int ind_card = 0; ind_card < lenght; ind_card++){
				
				gussingObservation[GameStateConstants.OFS_PLAYERDATA[pl]+
				                   GameStateConstants.OFS_OLDCARDS
				                   +cardSequence[rnd.nextInt(cardSequence.length)]]++;
				
			}
		}
		gussingObservation = bl.stateActionObservation(gussingObservation, action);
		state_clone = null;
		return gussingObservation;
	}
	
	// get hash code for each of the observation state
	
	public int getHashCodeFromArray(int[] state){
		{
	        int [] s2 = BoardLayout.cloneOfState(state);
	        
	        state[GameStateConstants.OFS_TURN] = 0;
	        state[GameStateConstants.OFS_FSMLEVEL] = 0;
	        state[GameStateConstants.OFS_DIE1] = 0;
	        state[GameStateConstants.OFS_DIE2] = 0;

	        return(Arrays.hashCode(s2));
	        
	    }
	}
	
	// Random rollout policy which needed to run before running the UCT algorithm
	public double rollout(int[] state, int depth){
		
		
		double total_reward = 0.0;
		double discount = 1.0;
		int numSteps;
		int[] state_clone  = BoardLayout.cloneOfState(state);
		// TODO: check the condition of MAX_DEPTH
		for ( numSteps = 0; numSteps +  depth < 500; numSteps++){
			
	        double reward;
	        
	        int fsmlevel    = state_clone[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
	        int pl          = state_clone[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
	        
	        //state = bl.hideState(pl, state);
	        bl.player[pl].listPossibilities(state_clone);
	        int[] action = bl.possibilities.action[rnd.nextInt(bl.possibilities.n)];
	        bl.player[pl].performAction_simulation(state_clone, action);
	        reward = rewardingModel(action[0]);
	        
            // Changing state
            // It also changing tht player number at the same time as we use the state transition
            // We need to define the optimal simulation round so it able to do it job properly
	        
            bl.stateTransition(state_clone, action);
            int winner = bl.getWinner(state_clone);
            
            total_reward += reward*discount;
            if(winner != -1){
            	break;
            }
		}
		state_clone =  null;
		return total_reward;
	}
	
	
	// Expending the node from provided observation (in case it is not in the hash table)
	public VNode expand_vnode(int[] observation, int depth){
		VNode node;
		if(bl.factory.checkVNodeFactoryEmpty()){
			node = new VNode(this.bl,BelifeState.getStateBeforeHashCompare(observation));
		}
		else{
			node = bl.factory.popVnode(BelifeState.getStateBeforeHashCompare(observation));
		}
		return node;
	}
	
	
	// Get action with this specific node
	public int[] getAction(){
		
		return this.action;
	}
	
	public void setAction(int[] action){
		this.action = action;
	}
	
	// Set value into this specific node
	public void setValue(double reward, int count){
		this.VISIT = count;
		this.REWARD = reward;
	}
	// 
	
	// Give some potential rewarding system
	public double rewardingModel(int action){
		switch(action){
			case GameStateConstants.A_BUILDCITY:
				return 5;
			case GameStateConstants.A_BUILDROAD:
				return 2;
			case GameStateConstants.A_BUILDSETTLEMENT:
				return 5;
			case GameStateConstants.A_BUYCARD:
				return 1;
			case GameStateConstants.A_PAYTAX:
				return 0.5;
			case GameStateConstants.A_NOTHING:
				return 0.5;
			case GameStateConstants.A_PLACEROBBER:
				return 2;
			case GameStateConstants.A_PLAYCARD_FREERESOURCE:
				return 2;
			case GameStateConstants.A_PLAYCARD_FREEROAD:
				return 2;
			case GameStateConstants.A_PLAYCARD_KNIGHT:
				return 2;
			case GameStateConstants.A_PLAYCARD_MONOPOLY:
				return 2;
			case GameStateConstants.A_THROWDICE:
				return 0;
			default:
				return 0;
		}
	}
}
