package tradingPOMDPs;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

public class TradingUtil implements  GameStateConstants{

		BoardLayout bl;
		
		public TradingUtil(BoardLayout bl){
			this.bl = bl;
		}
		
		public boolean consdierOffer(int[] trad,int pl,float current_win_lose){
			
			float tem_win_lose = 0;
			// Simulate the game after trading
			// if give better result than current state reuturn true
			// other wise return false
			
			int[] state_clone = bl.changeState(bl.state, trad, trad[0], trad[3]);
			bl.UCTsimulateTrading(state_clone);
			
			tem_win_lose = bl.uctTradinTree.getAverageWinLose(pl);
			if(tem_win_lose > current_win_lose){
				return true;
			}
			else{
				return false;
			}
		}
		public int[] applyTrad(int[] s, int[] a,int pl){
			// apply trading action here and update the state before process with monte carlo
			int otherplayer = a[4];
        	s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] -= a[3];
            s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[5]] += a[6];
            s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[5]] -= a[6];
            s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[2]] += a[3];
            
            return s;
		}
		// Need perform viturall rollout before trading decided.
}
