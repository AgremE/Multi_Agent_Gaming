package smartsettlers.util;

import smartsettlers.boardlayout.BoardLayout;

public class TradingUtil {
	
	private int[] trad;
	private int[] state;
	BoardLayout bl;
	public TradingUtil(int[] s,int[] trad, BoardLayout bl){
		this.state = s;
		this.trad = trad;
		this.bl = bl;
	}
	//TODO: consider trading offer with simulation
	public boolean considerOffer(int[] state_change, int orignalStateChance, int pl){
		
		boolean trad = false;
		int[] state_clone = BoardLayout.cloneOfState(state_change);
		//TODO:
		// Check on whether it is good idea to use uctTree within the boardlayout instead of using a new game tree
		bl.UCTsimulateGame(state_clone);
		if(bl.uctTree.getAverageWinLose(pl) > orignalStateChance){
			trad = true;
		}
		return trad;
	}
	
}
