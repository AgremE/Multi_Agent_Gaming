//This package is extended from the smartsettler package from ... 
//In order to make the agent play in a more real game environment
//Using POMCP procedurec on approximating the belief state then using action selection random roll out policy
package smartsettlers.player;

import java.awt.List;
import java.util.ArrayList;
import java.util.Random;
import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class POMDPs extends smartsettlers.player.Player{
	
	private ArrayList<int []> history = new ArrayList<int []>();
	
	public POMDPs(BoardLayout bl, int position){
		
		super(bl,position);
		
	}

	@Override
	public void selectAction(int[] s, int[] a) {
		// TODO Auto-generated method stub
		int fsmlevel    = s[OFS_FSMLEVEL];
      int pl          = s[OFS_FSMPLAYER+fsmlevel];
      int i,j;
      
      int[] s2 = BoardLayout.cloneOfState(s);// we need to change that every development card that they play will be not observable  
      
      s2 = this.makeDevCarInvsible(s2);
      
      history.add(s2);
      
      if (bl.uctTree != null)
      {
          bl.player[pl].listPossibilities(s2);
          for (j=0; j<bl.possibilities.n; j++)
          {
              int[] a2 = bl.possibilities.action[j];
              System.out.printf("%2d: [%d %d %d %d %d]  w:%f\n", j, a2[0], a2[1], a2[2], a2[3], a2[4], bl.possibilities.weight[j]);
          }
          // All of the init agents are the UCT player
          // All of the miulslateGame start here
          bl.UCTsimulateGame(s2);
          // I need to approximate the belife state first then simulate it like it an actuat state of the game
          int aind = bl.uctTree.selectAction(s, pl, true);
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
				s[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j] = (Integer) null;
				s[OFS_PLAYERDATA[i] + OFS_OLDCARDS+ j] = (Integer) null;
			}
		}
		return s;
	}
}
