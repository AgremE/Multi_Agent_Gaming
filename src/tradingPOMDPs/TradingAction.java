package tradingPOMDPs;

import java.util.Random;
import smartsettlers.boardlayout.GameStateConstants;

public class TradingAction implements GameStateConstants{
	
	public static final int MAX_ACTIONLISTSIZE = 100;
	public static final int [] NOOP = {A_NOTHING, 0, 0, 0, 0};
	public final int TRADINGSIZE = 8;
    public int[][] trad;
    Random rnd = new Random();
    public int n = 0;
    double[] weight;
    double totalweight = 0.0;
	public TradingAction(){
        trad = new int[MAX_ACTIONLISTSIZE][TRADINGSIZE];
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
            return trad[rnd.nextInt(n)];
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
            return trad[randomInd()];
    }
    public void addTradOption(double w, int player_offer, int resource_type1, int number_resource1, 
    									int player_recieve, int resource_type2, int number_resource2){
    	weight[n] = 1.0;
        totalweight += 1.0;
        //!!! a can be shorter than actionsize
        trad[n][0] = player_offer;
        trad[n][1] = resource_type1;
        trad[n][2] = number_resource1;
        trad[n][3] = player_recieve;
        trad[n][4] = resource_type2;
        trad[n][5] = number_resource2;
        n++;
    }
}
