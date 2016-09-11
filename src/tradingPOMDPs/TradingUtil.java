package tradingPOMDPs;

import smartsettlers.boardlayout.BoardLayout;

public class TradingUtil implements TradingConstance{

		BoardLayout bl;
		
		public TradingUtil(BoardLayout bl){
			this.bl = bl;
		}
		
		public bool consdierOffer(int action){
			
			int[] state_clone  = bl.cloneOfState(bl.state);
			// Simulate the game after trading
			// if give better result than current state reuturn true
			// other wise return false
			
		}
		public int[] applyTrad(int[] state, int action){
			// apply trading action here and update the state before process with monte carlo
			
		}
		// Need perform viturall rollout before trading decided.
}
