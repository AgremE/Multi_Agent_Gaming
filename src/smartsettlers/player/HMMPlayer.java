package smartsettlers.player;

import smartsettlers.boardlayout.BoardLayout;

public class HMMPlayer extends Player{

	public HMMPlayer(BoardLayout bl, int position, boolean HMM) {
		super(bl, position, HMM);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void selectAction(int[] s, int[] a) {
		// TODO Auto-generated method stub
		int fsmlevel    = s[OFS_FSMLEVEL];
		int fsmstate    = s[OFS_FSMSTATE+fsmlevel];// To access the state step
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i,j;
        
        int[] s2 = BoardLayout.cloneOfState(s);  
        if (bl.uctTree != null)
        {
        	// Only when we use uct to selection action this condition will satify
        	//s2 = bl.hideState(pl, s2);
            bl.player[pl].listPossibilities(s2);
            for (j=0; j<bl.possibilities.n; j++)
            {
                int[] a2 = bl.possibilities.action[j];
                System.out.printf("%2d: [%d %d %d %d %d]  w:%f\n", j, a2[0], a2[1], a2[2], a2[3], a2[4], bl.possibilities.weight[j]);
            }
            bl.UCTsimulateGame(s2);
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

}
