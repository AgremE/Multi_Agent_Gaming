package pomcpUtili;

import java.rmi.activation.ActivationInstantiator;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
	int NUM_SIMULATION = 500;
	int treeDepth = 0;
	int PeakTreeDepth = 0;
	int[] state;
	VNode root;
	Random rnd;
	Statistics StateTotalReward;
	Statistics StateTreeDepth;
	BoardLayout bl;
	
	/*
	 * Check again the part that need to work on *Refer to the paper*
	 * */ 
	public UCT_POCMP(BoardLayout bl, int position){
		
		rnd = new Random();
		this.bl = bl;
		StateTotalReward = new Statistics();
		StateTreeDepth = new Statistics();
		root = new VNode(bl);
		history = new History();
		
	}
	
	// Make function for running the simulation and action selection.
	public void mtcs_update_particle(){

		// Need to work
		if(bl.numberCardBoughtThisRound != 0){
			root.belief_state.updateBoughtCard(bl.newlyBoughtCardEachPlayer);
		}
		if(bl.card_play_this_round != 0){
			root.belief_state.updateRevealCard(bl.eachPlayerCardPlaiedThisRound);
		}
		
	}

	//update particle in side the belife particle pool
	
	
	// Update is mostly related to update the belife state accordingly
	public boolean mtcs_update(int[] action, int[]observation){
		
		history.addNode(new Entry(action, observation));
		int[] belifes;
		QNode qnode = root.Children.get(getHashCodeFromActionArray(action));
		VNode vnode = qnode.Children.get(getHashCodeFromObservationArray(observation));
		
		if(vnode != null){
			
			belifes = vnode.getBelifeState();
			
			if(belifes == null){
				
				for(int num_particle = 0 ; num_particle < POMCPConstance.TOTAL_PARTICLE; num_particle++){
					
					vnode.belief_state.samplingParticle(bl.eachPlayerCardPlaiedThisRound);
					
				}
				
			}
		}
		// fixing the particle deprivation problems
		else{
			vnode = new VNode(bl);
			
			for(int num_particle = 0 ; num_particle < POMCPConstance.TOTAL_PARTICLE; num_particle++){
				
				vnode.belief_state.samplingParticle(bl.eachPlayerCardPlaiedThisRound);
				
			}

		}
		if(root.belief_state.getBelifeState() == null){
			return false;
		}
		
		this.state = vnode.getBelifeState();
		// Change the root from here
		this.root = vnode;
		vnode = null;
		return true;
	}
	
	// get hash code for each of the observation state
	public int getHashCodeFromActionArray(int[] action){
		{
		     return(Arrays.hashCode(action));
		}
	}
	
	// get hash code for each of the observation state
	public int getHashCodeFromObservationArray(int[] state){
		{
		    int [] s2 = state.clone();
		        
		    state[GameStateConstants.OFS_TURN] = 0;
		    state[GameStateConstants.OFS_FSMLEVEL] = 0;
		    state[GameStateConstants.OFS_DIE1] = 0;
		    state[GameStateConstants.OFS_DIE2] = 0;

		    return(Arrays.hashCode(s2));
		        
		}
	}
	
	// for shuffle the array of action
	public int[][] shuffle_2dArray(int[][] input){

		int first_index, second_index;
		
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
		return root.UCBGreedy(root,false);
	
	}
	
	// make a random observation based on number of card other player hand
	// this function is the same as the one define in QNode class
	// Dont use this function
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
			int[] bliefe_state = root.belief_state.getBelifeState();
			int[] state = BoardLayout.cloneOfState(vnode.getBelifeState());
			int[][] action_pool;

			int fsmlevel    = bl.state[GameStateConstants.OFS_FSMLEVEL]; // To access the level of game state
	        int pl          = bl.state[GameStateConstants.OFS_FSMPLAYER + fsmlevel];// To access the player number
	        // init the possibility state for this user
	        bl.player[pl].listPossibilities(root.getBelifeState());
	        
	        bl.possibilities.action = shuffle_2dArray(bl.possibilities.action);
			
	        initChild();
	        
			for(int ind_it = 0; ind_it < bl.NUM_IT; ind_it++ ){
				
				int[] action = bl.possibilities.action[(ind_it % bl.possibilities.action.length)];
				// we can pass the state here because it is only the hide the state already in the beginning
				int[] observation = bl.stateActionObservation(state,action);
				
				double delayReward, totalReward, immediateResard = 0.0;
				//root is null
				int action_hash  = getHashCodeFromActionArray(action);
				
				QNode q_node = root.Children.get(action_hash);
				if(q_node == null){
					
					QNode node = new QNode(bl, action);
					node.setValue(0, 0);
					int action_hash_code = root.getHashCodeFromArray(action);
					root.Children.put(action_hash_code, node);
					
					
					q_node = root.Children.get(action_hash);
				}
				if(q_node.Children == null){
					System.out.println("Childrean Null");
				}
				// TODO: Checking the observation status
				// At the first step all the childs are not init properly yet.
				//BoardLayout.printArray(observation);
				VNode v_node;
				int observation_hash = getHashCodeFromObservationArray(observation);
				if(bl.getWinner(state) == -1){
					
					if(q_node.Children.contains(observation_hash)){
						
						v_node = q_node.Children.get(observation_hash);
						
					}
					
					else{
						
						root.Children.get(action_hash).Children.put(getHashCodeFromObservationArray(observation), 
								q_node.expand_vnode(observation, history_depth));
						v_node = root.Children.get(action_hash).Children.get(observation_hash);
					}
					
					immediateResard = q_node.rewardingModel(action[0]);
					
				}
				
				history.addNode(new Entry(action, observation));
				delayReward = q_node.rollout(state, history_depth);
				totalReward = immediateResard + POMCPConstance.DISCOUNT_FACTOR*delayReward;
				root.Children.get(action_hash).REWARD += totalReward;
				root.Children.get(action_hash).VISIT++;
				history.resize(history_depth);
				vnode.stage = SEARCH_STAGE.UCT_ROLLOUT;
				
			}
		}
		
		
	//performing the UCT search 
	public void mtcs_uctsearch(){
		
		int historyDepth = history.size();
		for(int i = 0; i < NUM_SIMULATION; i++){
			
			int[] card_believe_state = root.belief_state.getBelifeState();
			// Change the state according to the belife approximation
			treeDepth = 0;
			PeakTreeDepth = 0;
			double totalReward = root.simulation_v(root.belief_state.getBelifeState(), root, treeDepth);
			StateTotalReward.add(totalReward);
			StateTreeDepth.add(totalReward);
			history.resize(historyDepth);
			/*try {
				System.out.println(root.Children.size()+"\n");
				TimeUnit.SECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
		/*
		for(int key: root.Children.keySet()){
			System.out.println(root.Children.lenght+"\n");
			System.out.println("Rewarding Node:"+key+" Rewarding Value"+root.Children.get(key).REWARD + "\n");
			
		}
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	

	
	public int[] getBliefState(){
		return root.getBelifeState();
	}
	
	public VNode getRoot(){
		return this.root;
	}
	
	// To get the observation based on the belief state
	public int[] getRootObservation(){
		return this.root.observed;
	}
	
	// Handle particle deprivation issues within mtcs_update belief_pool
	public void particleDeprivationHandler(){
		
		
		for(int ind_particle = 0; ind_particle < POMCPConstance.TOTAL_PARTICLE; ind_particle++){
			
			int[] particle = root.belief_state.samplingParticle(bl.eachPlayerCardPlaiedThisRound);
			if(root.belief_state.particleExist(particle)){
				int tmp_value = root.belief_state.getValueForParticle(particle);
				tmp_value++;
				root.belief_state.putParticleIntoBeliefPool(particle, tmp_value);
			}
			else{
				root.belief_state.addParticle(particle, 1);
			}
			
			
		}
	}
	
	//init child for the root node
	public void initChild(){
		
	    root.NUM_ACTION = bl.possibilities.n;
		root.possibilities_list = bl.possibilities;
		
		// put all the result of the possible action inside the list of children
		for(int i = 0; i < root.NUM_ACTION; i++){
			
			int[] action = bl.possibilities.action[i];
			QNode node = new QNode(bl, action);
			node.setValue(0, 0);
			int action_hash_code = root.getHashCodeFromArray(action);
			root.Children.put(action_hash_code, node);
			
		}
	}
	

}