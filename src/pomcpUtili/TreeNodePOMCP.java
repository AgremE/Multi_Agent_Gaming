package pomcpUtili;


import smartsettlers.boardlayout.GameStateConstants;

/**
 *
 * @author szityu
 */
public class TreeNodePOMCP implements GameStateConstants {

    public int nvisits;
    public int[][] nwins;
    public double[][] values;
    public int nactions;
    public int[] nactionvisits;
    public int timeStamp;
    public int history;
    
    public TreeNodePOMCP(int nactions)
    {
        nvisits = 0;
        nwins = new int[nactions][];
        values = new double[nactions][];
        for (int i=0; i<nactions; i++)
        {
            nwins[i] = new int[NPLAYERS];
            values[i] = new double[NPLAYERS];
        }
        this.nactions = nactions;
        nactionvisits = new int [nactions];
    }
    
    public TreeNodePOMCP(int nactions, int player, int[] virtualwins)
    {
        this(nactions);// This one call above function
        for (int i=0; i<nactions; i++)
            nwins[i][player] = virtualwins[i];
    }
    // Just to get the number of win for each action corresponding to each player
    public int[][] getNumWin(){
    	
    	return this.nwins;
    }
}