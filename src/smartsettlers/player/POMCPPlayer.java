//This package is extended from the smartsettler package from ... 
//In order to make the agent play in a more real game environment
//Using POMCP procedurec on approximating the belief state then using action selection random roll out policy
package smartsettlers.player;

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;

import pomcpUtili.UCT_POCMP;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
import tradingPOMDPs.TradingUtil;

public class POMCPPlayer extends smartsettlers.player.Player{
	
	private ArrayList<int []> history = new ArrayList<int []>();
	UCT_POCMP pomcp_settler;
	
	public POMCPPlayer(BoardLayout bl, int position, boolean trues){
		
		super(bl,position,true);
		pomcp_settler  = new UCT_POCMP(bl,position);
	}

	@Override
	public void selectAction(int[] s, int[] a) {
		
	  // TODO Auto-generated method stub
		
	  //int fsmlevel    = s[OFS_FSMLEVEL];
      /*
	  int pl          = s[OFS_FSMPLAYER+fsmlevel];
      int statlevel 	= s[OFS_FSMSTATE+ fsmlevel];
      int numTradOffer = 0;
      int winCount = 0;
      int[] trad;
      boolean offer_answer = false;
      int i,j;
      int MAX_TRAD_OFFER = 5;
      */
      //int[] s2 = BoardLayout.cloneOfState(s);// we need to change that every development card that they play will be not observable  
     
      //Call POMCP instead
      //history.add(s2);
      // Add back later
      
      // No trading involve yet
      // running normal UCT for trading
	  /*
	   * Just assuming for now that the beliefState from POMCP is the current actual state
	   * and start offer the trading options
	   * Noted: Should change the condition of deciding whether to trade with the providing option all not is need to consider
	   * with Virtual win as well
	   * */
      int[] state;
      if(bl.hasHiddenInfo()){
    
    	  // update particle for the simulation belief state
    	  pomcp_settler.mtcs_update_particle();
    	  // Simulate is already in the function action_selection
    	  state = pomcp_settler.getBliefState();
    	 
    	  // action selection
    	  a =  pomcp_settler.action_selection();

      }
      else{
    	  
    	  // Clear up the particle and update according the full state information
    	  pomcp_settler.getRoot().getBelife().updateFullyObservable();
    	  state = BoardLayout.cloneOfState(s);
    	  // using normal UCT search with UCT tree
    	  a = pomcp_settler.action_selection();
      }
      state = bl.stateActionObservation(state, a);
      pomcp_settler.mtcs_update(a, state);
      //s2=null;
	}
	public void pomcpUpdate(int[] action, int[] observation){
		this.pomcp_settler.mtcs_update(action, observation);
	}

	@Override
	public int selectMostUselessResourceInHand(int pl, int[] s) {
		// TODO Auto-generated method stub
		return selectRandomResourceInHand(pl, s);
	}
	
	// Hide the information to POMDPs agent if the card is not play yet
	// Assuming that POMDPs is the first player
	public int [] makeDevCarInvsible(int[] s){
		int[] state_clone = BoardLayout.cloneOfState(s);
		for(int i = 1; i < GameStateConstants.NPLAYERS; i++){
			for(int j = 0; j<GameStateConstants.N_DEVCARDTYPES;i++){
				state_clone[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j]=(Integer) null;
				state_clone[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j]=(Integer) null;
			}
		}
		return state_clone;
	}
}

// Add back content
/*
if(statlevel == S_NORMAL){
  	
  	//System.out.println("System start trading");
  	s2 = bl.hideState(pl, s2);
  	bl.UCTsimulateTrading(s2);
  	bl.player[pl].listTradingOption(s2);
  	winCount = bl.uctTradinTree.getWinCount(pl);
  	
  	outerloop:
  		
      for(i =0 ; i < bl.tradingPossibilites.n; i++){
    	 for(int ind =0 ; ind < bl.tradingPossibilites.n; ind++){
      		System.out.printf("Trading Option: [%d %d %d %d %d %d]\n", bl.tradingPossibilites.trad[ind][0], 
      				bl.tradingPossibilites.trad[ind][1], 
      				bl.tradingPossibilites.trad[ind][2], 
      				bl.tradingPossibilites.trad[ind][3], 
      				bl.tradingPossibilites.trad[ind][4], 
      				bl.tradingPossibilites.trad[ind][5]);
      	}
      	bl.tradingOffer++;
      	trad = bl.tradingPossibilites.trad[i];
      	int[] state_trad_simulation = bl.cloneOfState(s);
      	// Chaning a to action of trading posibility
      	bl.changeState(state_trad_simulation, trad);
      	bl.UCTsimulateTrading(state_trad_simulation);
      	TradingUtil tradutil = new TradingUtil(bl);
      	int traind = i;
      	
      	if(winCount < bl.uctTradinTree.getWinCount(pl)){
      		//System.out.println("Offering");
      		// TODO: Start the trading offer
      		// Wait for offer
      		// Start trading offer accepted otherwise reject
      		// and Start normal simulation with monte carlo
      		// Add list of trading here
      		// It is complete different action from player.performaction
      		// Therefore, we need to make complete different thinking mechanisim for agent to make
      		// decision
      		//trad_action.considerOffer(state, orignalStateChance, pl);
      		
      		for(int player_ind = 0; player_ind < GameStateConstants.NPLAYERS; player_ind++){
      			
      			offer_answer = tradutil.consdierOffer(trad,player_ind,winCount);
      			
          		if(offer_answer){
          			//System.out.println("Accepted");
          			s = tradutil.applyTrad(s, trad);
          			bl.tradingAccepte++;
          			break outerloop; // break the whole nested loop with this line
          		}
      		}
      		numTradOffer++;
      		// There is a limit number of offers because the speed of the playing the game
      		
      		if(numTradOffer > MAX_TRAD_OFFER){
      			break;
      		}
      	}
      	
      }
  }
*/
