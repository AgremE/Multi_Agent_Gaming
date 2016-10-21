package pomcpUtili;

import java.awt.List;
import java.util.ArrayList;
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
	public Hashtable<Integer[][], VNode> Children = new Hashtable<>();// key is action as an array of integer
	public QNode(BoardLayout bl, int[] action){
		this.action = action;
		this.bl = bl;
		
	}
	// randomizing the observation in the hope of getting the best guess from the pool
	public int observation(){
		return rnd.nextInt();
	}
	public Integer[][] convIntToInteger(int[][] obser){
		Integer[][] new_obser = new Integer[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
		for(int i=0; i < obser.length; i++){
			for(int j = 0; j < obser[i].length; j++){
				new_obser[i][j] = (Integer)obser[i][j];
			}
		}
		return new_obser;
	}
	public double simulate_q(QNode q_node, int[] state, int depth){
		
		
		double immReward, delayReward;
		immReward = rewardingModel(q_node.action[0]);
		int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
        int fsmstate    = state[GameStateConstants.OFS_FSMSTATE+fsmlevel];// To access the state step
        int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
        
        //Selected action in greedy manner to see whether it good to selection capability
        q_node.VISIT++;
		int[] state_clone = bl.cloneOfState(state);
		
		// TODO: Need to apply action to get observation(Guessing card by randomizing)
		
		int winner = bl.getWinner(state_clone);

		if(winner != -1 ){
			return 0;
		}
		double total_reward = 0;
		VNode node;
		
		// Making an observation for this round base on current belife state
		
		int[][] observation = makeObservation(state, q_node.action);
		node = q_node.Children.get(observation);
		
		if(q_node.Children.get(observation) != null){
			node = expand_vnode(observation, depth);
		}
		if(node != null){
			depth++;
			Children.put(convIntToInteger(observation), node);
			delayReward = node.simulation_v(state_clone, node, depth);
		}
		else{
			depth++;
			delayReward = rollout(state_clone, depth);
			//doing the random rollout from here
		}
		
		total_reward = immReward + delayReward*DISCOUNT_FACTOR;
		return total_reward;
	}
	public int[][] makeObservation(int[] state, int[] action){
		int[][] current_guess = new int[GameStateConstants.NPLAYERS][GameStateConstants.N_DEVCARDTYPES];
		if(bl.numberCardBoughtThisRound == 0){
			
		}
		for(int ind_player = 0; ind_player < GameStateConstants.NPLAYERS; ind_player++ ){
			int lenght = bl.eachPlayerNewCard[ind_player];
			for(int ind_card = 0; ind_card < lenght; ind_card++){
				current_guess[ind_card][cardSequence[rnd.nextInt(cardSequence.length)]]++;
			}
		}
		return current_guess;
	}
	public double rollout(int[] state, int depth){
		
		int[] state_clone = bl.cloneOfState(state);
		double total_reward = 0.0;
		double discount = 1.0;
		boolean terminal = false;
		int numSteps;
		
		for ( numSteps = 0; numSteps +  depth < MAX_DEPTH; numSteps++){
			
			int observation;
	        double reward;
	        
	        int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
	        int fsmstate    = state[GameStateConstants.OFS_FSMSTATE+fsmlevel];// To access the state step
	        int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
	        
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
		return total_reward;
	}
	// Expending the node from provided observation (in case it is not in the hashtable)
	public VNode expand_vnode(int[][] observation, int depth){
		
		if(depth < MAX_DEPTH){
			return null;
		}
		VNode node = new VNode(this.bl, observation);
		node.setValue(0.0, 0);
		return node;
	}
	
	public int[] getAction(){
		
		return this.action;
	}
	public void setValue(double reward, int count){
		this.VISIT = count;
		this.REWARD = reward;
	}
	// Give some potential rewarding system
	public double rewardingModel(int action){
		switch(action){
			case GameStateConstants.A_BUILDCITY:
				return 7;
			case GameStateConstants.A_BUILDROAD:
				return 3;
			case GameStateConstants.A_BUILDSETTLEMENT:
				return 5;
			case GameStateConstants.A_BUYCARD:
				return 4;
			case GameStateConstants.A_PAYTAX:
				return 0.5;
			case GameStateConstants.A_NOTHING:
				return 0.5;
			case GameStateConstants.A_PLACEROBBER:
				return 1;
			case GameStateConstants.A_PLAYCARD_FREERESOURCE:
				return 2;
			case GameStateConstants.A_PLAYCARD_FREEROAD:
				return 2;
			case GameStateConstants.A_PLAYCARD_KNIGHT:
				return 2;
			case GameStateConstants.A_PLAYCARD_MONOPOLY:
				return 2;
			case GameStateConstants.A_THROWDICE:
				return 0.5;
			default:
				return 0;
		}
	}
}
