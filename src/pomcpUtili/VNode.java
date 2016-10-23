package pomcpUtili;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;

import org.apache.poi.poifs.property.Child;

import smartsettlers.boardlayout.ActionList;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
/*
 * Makara Phav @ Agreme
 * */
// After new root node selection we just start over
public class VNode implements POMCPConstance{

	int action_index = 0; // This is for keep track of action index to get the immediate reward
	int STATUS = -1; // Represent the state of the game
	int VISIT = 0;
	double REWARD  = 0;
	int NUM_ACTION;
	int[][] observed;
	boolean ACTION_LIST_EMPTY = false;
	final double EXPLORATION_CONSTANCE = 1.5;
	BelifeState belief_state;// need to approximate by unweighted particle filter
	BoardLayout bl;
	Random rnd = new Random();
	ActionList possibilities_list;
	
	
	public Hashtable<int[], QNode> Children = new Hashtable<>();// Key is action which pick in random manner
	
	
	
	
	public VNode(BoardLayout bl, int[][] observation){
		
		this.bl = bl;
		NUM_ACTION = bl.possibilities.n;
		possibilities_list = bl.possibilities;
		this.observed = observation;
		
		// put all the result of the possible action inside the list of children
		for(int i = 0; i < this.NUM_ACTION; i++){
			
			int[] action = bl.possibilities.action[i];
			QNode node = new QNode(bl, bl.possibilities.action[i]);
			node.setValue(0, 0);
			Children.put(action, node);
			
		}
	}
	
	public static Integer[] changeINT2INTEGER(int[] action){
		Integer[] action_cov = new Integer[action.length];
		for(int i = 0 ; i< action.length; i++){
			action_cov[i] = (Integer) action[i];
		}
		return action_cov;
	}
	
	// Simulated this VNode 
	public double simulation_v(int[] state,VNode v_node, int treeDepth){
		
		int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
        int fsmstate    = state[GameStateConstants.OFS_FSMSTATE+fsmlevel];// To access the state step
        int pl          = state[GameStateConstants.OFS_FSMPLAYER+fsmlevel];// To access the player number
        
        //Selected action in greedy manner to see whether it good to selection capability
        v_node.VISIT++;
        int[] action = UCBGreedy(v_node);
        
		int[] state_clone = bl.cloneOfState(state);
		
		int winner = bl.getWinner(state_clone);
		if(treeDepth > MAX_DEPTH){
			return 0;
		}
		if(winner != -1 ){
			STATUS = TERMINATE_STATE;
			return 0;
		}
		else{
			STATUS = NORMAL_STATE;
		}
		
		QNode q_node = Children.get(action);
		
		double total_reward = q_node.simulate_q(q_node, state_clone, treeDepth);
		v_node.REWARD = total_reward;
		return total_reward;
		
	}

	//For helping in UCT Searching
	public int[] UCBGreedy(VNode v_node){
		ArrayList<int[]> action_pool = new ArrayList<>();
		double bestq = Double.NEGATIVE_INFINITY;
		int v_visit = v_node.VISIT;
		double logN = Math.log(v_visit + 1);
		//TODO: Will put alpha value later
		for(int ind_action = 0; ind_action < NUM_ACTION; ind_action++){
			double q_value;
			int q_visit;
			QNode q_node = v_node.Children.get(possibilities_list.action[ind_action]);
			q_visit = q_node.VISIT;
			q_value = q_node.REWARD;
			q_value += this.UCB_FAST(v_visit, q_visit, logN);
			if(bestq <= q_value){
				if(q_value > bestq){
					action_pool.clear();
				}
				bestq = q_value;
				action_pool.add(possibilities_list.action[ind_action]);
			}
		}
		if(action_pool.isEmpty()){
			
			v_node.ACTION_LIST_EMPTY = true;
			return new int[5];
			
		}
		else{
			v_node.ACTION_LIST_EMPTY = false;
			return action_pool.get(rnd.nextInt(action_pool.size()));
		}
		
	}
	
	// This fucntion used to help in GREEDYUCB action selection
	public double UCB_FAST(int N, int n, double logN){
		if(n == 0){
			return Double.POSITIVE_INFINITY;
		}
		else{
			return EXPLORATION_CONSTANCE*(Math.sqrt(logN / n));
		}
	}
	
	// Set specific value to this VNode
	public void setValue(double value, int count){
		this.REWARD = value;
		this.VISIT = count;
	}
	
	// get belife state withing this VNode
	public int[] getBelifeState(){
		return belief_state.getBelifeState(bl);
	}
	
}
