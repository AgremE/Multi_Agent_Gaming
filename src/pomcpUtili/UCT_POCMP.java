package pomcpUtili;

import java.rmi.activation.ActivationInstantiator;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import pomcpUtili.VNode.SEARCH_STAGE;
import smartsettlers.boardlayout.ActionList;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
import uct.TreeNode;
import uct.UCT;
/*
 * Makara Phav @ Agreme
 * */
/*
 * First if the tree is not extended working on the random rollout to give some background knowleadge to use UCT search
 * if the tree is searching state already, just using the UCT for searching
 * 
 */
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
	
	
	public UCT_POCMP(BoardLayout bl){
		
		rnd = new Random();
		this.bl = bl;
		StateTotalReward = new Statistics();
		StateTreeDepth = new Statistics();
		root = new VNode(bl);
		
	}
	
	// Make function for running the simulation and action selection.
	public void mtcs_update_particle(){
		
		int[][] observation;
		int[] action;
		double reward;
		// Need to work
		if(bl.numberCardBoughtThisRound != 0){
			root.belief_state.updateBoughtCard(bl.newlyBoughtCardEachPlayer);
		}
		if(bl.card_play_this_round != 0){
			root.belief_state.updateRevealCard(bl.eachPlayerCardPlaiedThisRound, bl.eachPlayerCardNotReveal);
		}
		
	}

	//update particle in side the belife particle pool
	
	
	// Update is mostly related to update the belife state accordingly
	public boolean mtcs_update(int[] action, int[][] observation){
		
		history.addNode(new Entry(action, observation));
		int[] belifes;
		QNode qnode = root.Children.get(action);
		VNode vnode = qnode.Children.get(observation);
		
		if(vnode != null){
			
			belifes = vnode.getBelifeState();
			
			if(belifes == null){
				
				for(int num_particle = 0 ; num_particle < POMCPConstance.TOTAL_PARTICLE; num_particle++){
					
					vnode.belief_state.addParticle(bl.eachPlayerCardPlaiedThisRound, bl.eachPlayerCardNotReveal);
					
				}
				
			}
		}
		
		if(root.belief_state.getBelifeState(bl) == null){
			return false;
		}
		
		this.state = vnode.getBelifeState();
		// Change the root from here
		this.root = vnode;
		return true;
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
	// Change this function to be independence of vnode
	public int[] action_selection(){
		// Should change root to Vnode
		if(root.stage == SEARCH_STAGE.RANDOME_ROLLOUT){
			this.mtcs_randome_rollout(root);
		}
		
		this.mtcs_uctsearch();
		return root.UCBGreedy(root);
	
	}
	
	// make a random observation based on number of card other player hand
	// this function is the same as the one define in QNode class
	public int[][] makeRandomObservation(int[] state, int[] action){
		
		int[][] current_guess = new int[NPLAYERS][N_DEVCARDTYPES];
		
		for(int ind_player = 0; ind_player < NPLAYERS; ind_player++ ){
			
			int lenght = bl.eachPlayerCardNotReveal[ind_player];
			
			for(int ind_card = 0; ind_card < lenght; ind_card++){
				current_guess[ind_card][POMCPConstance.cardSequence[rnd.nextInt(POMCPConstance.cardSequence.length)]]++;
			}
		}
		
		return current_guess;
	}
	
	// This method is called only when there a new tree need to construct in order to give uct a basic idea on what to do
		public void mtcs_randome_rollout(VNode vnode){
			
			int history_depth = history.size();
			int[] bliefe_state = root.belief_state.getBelifeState(bl);
			int[] state = BoardLayout.cloneOfState(bliefe_state);
			int[][] action_pool;
			
			if(bl.possibilities.n != 0){
				
				action_pool = bl.possibilities.action;
				action_pool = shuffle_2dArray(action_pool);
				
			}
			
			else{
				
				int fsmlevel    = state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
		        int pl          = state[GameStateConstants.OFS_FSMPLAYER + fsmlevel];// To access the player number
				bl.player[pl].listPossibilities(state);
				action_pool = bl.possibilities.action;
				action_pool = shuffle_2dArray(action_pool);
				
			}
			
			for(int ind_it = 0; ind_it < bl.NUM_IT; ind_it++ ){
				
				int[] action = action_pool[(ind_it % action_pool.length)];
				int[][] observation = makeRandomObservation(state,action);
				double delayReward, totalReward, immediateResard;
				QNode q_node = root.Children.get(action);
				VNode v_node = root.Children.get(action).Children.get(observation);
				immediateResard = q_node.rewardingModel(action[0]);
				
				if(bl.getWinner(state) == -1){
					if(v_node == null){
						// This is basically try to extend the node from vnode which not exist to step further down
						// By further down I mean put in the hashtable
						root.Children.get(action).Children.put(observation, 
								q_node.expand_vnode(observation, history_depth));
					}
				}
				
				history.addNode(new Entry(action, observation));
				delayReward = q_node.rollout(state, history_depth);
				totalReward = immediateResard + POMCPConstance.DISCOUNT_FACTOR*delayReward;
				root.Children.get(action).REWARD += totalReward;
				root.Children.get(action).VISIT++;
				history.resize(history_depth);
				vnode.stage = SEARCH_STAGE.UCT_ROLLOUT;
				
			}
		}
		
		
	//performing the UCT search 
	public void mtcs_uctsearch(){
		
		int historyDepth = history.size();
		for(int i = 0; i < NUM_SIMULATION; i++){
			
			int[] card_believe_state = root.belief_state.getBelifeState(bl);
			// Change the state according to the belife approximation
			treeDepth = 0;
			PeakTreeDepth = 0;
			double totalReward = root.simulation_v(root.belief_state.getBelifeState(bl), root, treeDepth);
			StateTotalReward.add(totalReward);
			StateTreeDepth.add(totalReward);
			history.resize(historyDepth);
			
		}
	}
	
	public void mtcs_addSample(VNode node, int[] state){
		
		int[] state_clone = bl.cloneOfState(state);
		node.belief_state.addBelife(state_clone);
		
	}
	
	public int[] getBliefState(){
		return root.getBelifeState();
	}
	
	public VNode getRoot(){
		return this.root;
	}
	
	// To get the observation based on the belief state
	public int[][] getRootObservation(){
		return this.root.observed;
	}

}