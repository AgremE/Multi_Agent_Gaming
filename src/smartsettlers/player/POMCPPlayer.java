//This package is extended from the smartsettler package from ... 
//In order to make the agent play in a more real game environment
//Using POMCP procedurec on approximating the belief state then using action selection random roll out policy
package smartsettlers.player;

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;
import tradingPOMDPs.TradingUtil;

public class POMCPPlayer extends smartsettlers.player.Player{
	
	private ArrayList<int []> history = new ArrayList<int []>();
	
	public POMCPPlayer(BoardLayout bl, int position){
		
		super(bl,position);
		
	}

	@Override
	public void selectAction(int[] s, int[] a) {
		// TODO Auto-generated method stub
	  int fsmlevel    = s[OFS_FSMLEVEL];
      int pl          = s[OFS_FSMPLAYER+fsmlevel];
      int statlevel 	= s[OFS_FSMSTATE+ fsmlevel];
      int numTradOffer = 0;
      int winCount = 0;
      int[] trad;
      boolean offer_answer = false;
      int i,j;
      int MAX_TRAD_OFFER = 5;
      int[] s2 = BoardLayout.cloneOfState(s);// we need to change that every development card that they play will be not observable  
      
      //history.add(s2);
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
      
      if (bl.uctTree != null)
      {
    	  s2 = bl.hideState(pl, s2);
          bl.player[pl].listPossibilities(s2);
          for (j=0; j<bl.possibilities.n; j++)
          {
              int[] a2 = bl.possibilities.action[j];
              System.out.printf("%2d: [%d %d %d %d %d]  w:%f\n", j, a2[0], a2[1], a2[2], a2[3], a2[4], bl.possibilities.weight[j]);
          }
          // All of the init agents are the UCT player
          // All of the miulslateGame start here
          // Hide all the state development card
          bl.UCTsimulateGame(s2);
          // I need to approximate the belife state first then simulate it like it an actuat state of the game
          int aind = bl.uctTree.selectAction(s, pl, true);// action index of the maximun return
          bl.player[pl].listPossibilities(s);
          for (i=0; i<a.length; i++)
              a[i] = bl.possibilities.action[aind][i];
      
      }
      else
      {
      // possibilities are already listed...
      // select an action randomly
      
          bl.player[pl].listPossibilities(s);
          int[] a2 = bl.possibilities.randomAction();
          
          for (i=0; i<a2.length; i++)
              a[i] = a2[i];
      }
      s2=null;
		
	}

	@Override
	public int selectMostUselessResourceInHand(int pl, int[] s) {
		// TODO Auto-generated method stub
		return selectRandomResourceInHand(pl, s);
	}
	
	// Hide the information to POMDPs agent if the card is not play yet
	// Assuming that POMDPs is the first player
	public int [] makeDevCarInvsible(int[] s){
		for(int i = 1; i < GameStateConstants.NPLAYERS; i++){
			for(int j = 0; j<GameStateConstants.N_DEVCARDTYPES;i++){
				s[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j]=(Integer) null;
				s[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j]=(Integer) null;
			}
		}
		return s;
	}
}
