package tradingPOMDPs;

import java.util.Random;
import smartsettlers.boardlayout.GameStateConstants;

public class TradingAction implements GameStateConstants{
	
	public static final int MAX_ACTIONLISTSIZE = 200;
	public static final int [] NOOP = {A_NOTHING, 0, 0, 0, 0};
	
    public int[][] action;
    Random rnd = new Random();
    public int n = 0;
    double[] weight;
    double totalweight = 0.0;
	public TradingAction(){
        action = new int[MAX_ACTIONLISTSIZE][ACTIONSIZE];
	}
	public void Clear()
    {
        n = 0;
        totalweight = 0.0;
    }
    
    public int uniformRandomInd()
    {
        if (n==0)
            return -1;
        else
            return rnd.nextInt(n);
    }
    
    public int[] uniformRandomAction()
    {
        if (n==0)
            return NOOP;
        else
            return action[rnd.nextInt(n)];
    }
    
    public int randomInd()
    {
        if (n==0)
            return -1;
        
        double wlim = rnd.nextFloat()*totalweight;//wlim is weight limit
        double w = 0;
        int i;
        for (i=0; i<n; i++)
        {
            w += weight[i];
            if (w>= wlim)
            {
                break;
            }
        }
        if (i==n) i--;
        return i;
    }
    
    public int[] randomAction()
    {
        if (n==0)
            return NOOP;
        else
            return action[randomInd()];
    }
}
