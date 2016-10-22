package pomcpUtili;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import smartsettlers.boardlayout.ActionList;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
import uct.TreeNode;
import uct.UCT;

public class UCT_POCMP implements GameStateConstants{
	
	History history;
	int NUM_SIMULATION = 1000;
	int treeDepth = 0;
	int PeakTreeDepth = 0;
	int[] state;
	VNode root;
	Random rnd;
	Statistics StateTotalReward;
	Statistics StateTreeDepth;
	BoardLayout bl;
	public UCT_POCMP(){
		
	}
	// Make function for running the simulation and action selection.
	public void mtcs_pomcp(){
		
	}
	// Update is mostly related to update the belife state accordingly
	public void mtcs_update(int[] action, int[][] observation, double reward){
		history.addNode(new Entry(action, observation));
		BelifeState belifes;
		
		QNode qnode = root.Children.get(action);
		VNode vnode = qnode.Children.get(observation);
		if(vnode != null){
			
		}
		
	}
	// for shuffle the array of action
	public int[][] shuffle_2dArray(int[][] input){

		int first_index, second_index, temp_index;
		for(int i = 0; i < input.length; i++){
			first_index = i;
			int[] action = input[first_index];
			second_index = rnd.nextInt(input.length);
			input[first_index] = input[second_index];
			input[second_index] = action;
		}
		return input;
	}
	// call when the random rollout called first to give UCT some prior background knowleagde about the method itself
	public int[] action_selection(){
		this.mtcs_uctsearch();
		return root.UCBGreedy(root);
	}
	
	// This method is called only when there a new tree need to construct in order to give uct a basic idea on what to do
	public void mtcs_randome_rollout(int pl){
		int history_depth = history.size();
		int[] bliefe_state = root.belief_state;
		int[] state = BoardLayout.cloneOfState(bliefe_state);
		int[][] action_pool;
		if(bl.possibilities.n != 0){
			action_pool = bl.possibilities.action;
			action_pool = shuffle_2dArray(action_pool);
		}
		else{
			bl.player[pl].listPossibilities(state);
			action_pool = bl.possibilities.action;
			action_pool = shuffle_2dArray(action_pool);
		}
		for(int ind_it = 0; ind_it < bl.NUM_IT; ind_it++ ){
			
			int[] action = action_pool[(ind_it % action_pool.length)];
			int[][] observation = makeObservation(state,action);
			double delayReward, totalReward, immediateResard;
			QNode q_node = root.Children.get(action);
			VNode v_node = root.Children.get(action).Children.get(observation);
			immediateResard = q_node.rewardingModel(action[0]);
			if(bl.getWinner(state) == -1){
				if(v_node == null){
					// This is basically try to extend the node from vnode which not exist to step further down
					// By further down I mean put in the hashtable
					root.Children.get(action).Children.put(QNode.convIntToInteger(observation), 
							q_node.expand_vnode(observation, history_depth));
				}
			}
			history.addNode(new Entry(action, observation));
			delayReward = q_node.rollout(state, history_depth);
			totalReward = immediateResard + POMCPConstance.DISCOUNT_FACTOR*delayReward;
			root.Children.get(action).REWARD += totalReward;
			root.Children.get(action).VISIT++;
			history.resize(history_depth);
		}
	}
	// this function is the same as the one define in QNode class
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
	//
	public void mtcs_uctsearch(){
		
		int historyDepth = history.size();
		for(int i = 0; i < NUM_SIMULATION; i++){
			
			int[] card_believe_state = root.belief_state;
			// Change the state according to the belife approximation
			treeDepth = 0;
			PeakTreeDepth = 0;
			double totalReward = root.simulation_v(state, root, treeDepth);
			StateTotalReward.add(totalReward);
			StateTreeDepth.add(totalReward);
			history.resize(historyDepth);
			
		}
	}
	public void mtcs_addSample(){
		
	}
	

}
    

class Trace_POMCP {
    public int hc;
    public int pl; 
    public int aind;
    
    public void set(int hc, int pl, int aind)
    {
        this.hc = hc;
        this.pl = pl;
        this.aind = aind;
    }
}