package pomcpUtili;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import org.apache.poi.poifs.filesystem.NPOIFSDocument;

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
	Random rnd = new Random();
	BoardLayout bl;
	public Hashtable<Integer, VNode> Children;// key is observation as an array of integer
	/*
	 * Observation is in the form of the state with some guessing of the card at random in which its card's desk is updated accordingly 
	 * due to the number of reminding cards
	 * */
	public QNode(BoardLayout bl, int[] action){
		this.action = action;
		this.bl = bl;
		Children = new Hashtable<>();
		
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
		
		
		double immReward = 0, delayReward = 0;
		immReward = rewardingModel(q_node.action[0]);
		
		int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL];
        int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];
        
        //Selected action in greedy manner to see whether it good to selection capability
        q_node.VISIT++;
		
		int winner = bl.getWinner(state);

		if(winner != -1 ){
			return 0;
		}
		double total_reward = 0;
		VNode node;
		
		// Making an observation for this round base on current belife state
		
		int[] observation_array = makeObservation(q_node.action);
		int hash_code_observation = getHashCodeFromArray(observation_array);
		
		node = q_node.Children.get(hash_code_observation);
		
		if(node == null){
			node = expand_vnode(observation_array, depth);
		}
		
		if(node != null){
			depth++;
			Children.put(hash_code_observation, node);
			bl.player[pl].performAction_simulation(state, q_node.action);
            bl.stateTransition(state, q_node.action);
            winner = bl.getWinner(state);
            immReward = rewardingModel(action[0]);
            if(winner != -1){
            	total_reward = immReward + delayReward*DISCOUNT_FACTOR;
            	return total_reward;
            }
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
        int[] gussingObservation = bl.hideState(pl,bl.state);
        
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
		return gussingObservation;
	}
	
	// get hash code for each of the observation state
	
	public int getHashCodeFromArray(int[] state){
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
	
	// Random rollout policy which needed to run before running the UCT algorithm
	public double rollout(int[] state, int depth){
		
		
		double total_reward = 0.0;
		double discount = 1.0;
		int numSteps;
		
		// TODO: check the condition of MAX_DEPTH
		for ( numSteps = 0; numSteps +  depth < MAX_DEPTH; numSteps++){
			
	        double reward;
	        
	        int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
	        int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
	        
	        bl.player[pl].listPossibilities(state);
	        int[] action = bl.possibilities.action[rnd.nextInt(bl.possibilities.n)];
	        bl.player[pl].performAction_simulation(state, action);
	        reward = rewardingModel(action[0]);
	        
            // Changing state
            // It also changing tht player number at the same time as we use the state transition
            // We need to define the optimal simulation round so it able to do it job properly
	        
            bl.stateTransition(state, action);
            int winner = bl.getWinner(state);
            
            total_reward += reward*discount;
            if(winner != -1){
            	break;
            }
		}
		return total_reward;
	}
	
	
	// Expending the node from provided observation (in case it is not in the hash table)
	public VNode expand_vnode(int[] observation, int depth){
		
		VNode node = new VNode(this.bl,BelifeState.getStateBeforeHashCompare(observation));
		node.setValue(0.0, 0);
		return node;
	}
	
	
	// Get action with this specific node
	public int[] getAction(){
		
		return this.action;
	}
	
	
	// Set value into this specific node
	public void setValue(double reward, int count){
		this.VISIT = count;
		this.REWARD = reward;
	}
	
	
	// Give some potential rewarding system
	public double rewardingModel(int action){
		switch(action){
			case GameStateConstants.A_BUILDCITY:
				return 10;
			case GameStateConstants.A_BUILDROAD:
				return 1;
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
			case GameStateConstants.A_PORTTRADE:
				return 2;
			default:
				return 0;
		}
	}
}
