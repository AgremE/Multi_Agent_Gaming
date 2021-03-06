/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import smartsettlers.boardlayout.ActionList;
import smartsettlers.boardlayout.GameStateConstants;

/**
 *
 * @author szityu
 */
/*
 * Improved by Agrem_E (@Makara Phav)
 * */
// THe implementation of Monte Carlo with UCT method
public class UCT implements GameStateConstants {
    
    public Hashtable tree;
    public double max_outcome = 0.0;
    public static final int MAXNTRACES = 20000;
    int ntraces;
    private int[] winCount = new int[NPLAYERS];
    private int[] loseCount = new int[NPLAYERS];
    
    Trace[] traceList;
    Random rnd = new Random();
    
    public UCT()
    {
        tree = new Hashtable(1000000);
        ntraces = 0;
        traceList = new Trace[MAXNTRACES];
        for (int i=0; i<MAXNTRACES; i++)
            traceList[i] = new Trace();
    }
    //return ntrace number
    public int getNtrace(){
    	return this.ntraces;
    }
    
    // Hash Code of state which represent in the tree
    
    public static int getHashCode(int[] s)
    {
        int [] s2 = s.clone();
        s2[OFS_TURN] = 0;
        s2[OFS_FSMLEVEL] = 0;
        s2[OFS_DIE1] = 0;
        s2[OFS_DIE2] = 0;

        return(Arrays.hashCode(s2));
        
    }
    // Add state to the tree with list of possible action
    public void addState(int[] s, ActionList possibilities)
    {
        addState(s, getHashCode(s),possibilities);
    }
    // At state to the tree with some expected reward withing that state. Only consider the reward of building settlement and city
    //TODO: Change to add some reward to build the road and buying development card.
    public void addState(int[] s, int hc, ActionList possibilities) // s "state", hc "hashcode" 
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int state       = s[OFS_FSMSTATE+fsmlevel];
        int[] vwins     = new int[possibilities.n];
        int totalvwins  = 0;
        int i; 
        //Start Construct tree for trace the node of the monte carlo tree
        for (i=0; i<possibilities.n; i++)
        {
            switch (possibilities.action[i][0])
            {
                case A_BUILDSETTLEMENT:
                    if ((state != S_SETTLEMENT1) && (state != S_SETTLEMENT2))
                        vwins[i] = 20;
                    break;
                case A_BUILDCITY:
                    vwins[i] = 10;
                    break;
                //TODO: Change to add some reward to build the road and buying development card.
            }
            totalvwins += vwins[i];
        }
        TreeNode val = new TreeNode(possibilities.n, pl, vwins);
        tree.put(hc, val);
        
////        updateVirtualWins
//        TreeNode node;
//        Trace tr;
//        
//        for (i=0; i<ntraces; i++)
//        {
//            tr = traceList[i];
//            node = getNode(tr.hc);
//            node.nvisits+=totalvwins;
//            node.nwins[tr.aind][pl]+=totalvwins;
//            node.nactionvisits[tr.aind]+=totalvwins;
////            node.timeStamp = timeStamp;
//            
//            tree.put(tr.hc, node);
//        }
    }
    
    public TreeNode getNode(int[] s)
    {
        int hc = getHashCode(s);
        return getNode(hc);
    }

    public TreeNode getNode(int hc)
    {
        TreeNode res = (TreeNode) tree.get(hc);
        return res;
    }
    public void setReward(double reward, int traceListIndex,int hc){
    	 TreeNode node = (TreeNode) tree.get(hc);
    	 node.nodeReward += reward;
    	 node.rewardActionStep[traceListIndex] += reward;
    	 tree.put(hc, node);
    }
    public void setReward(double reward, int hc){
   	 TreeNode node = (TreeNode) tree.get(hc);
	   	 node.nodeReward += reward;
	   	 tree.put(hc, node);
   }
    public void setExpectedReward(double expectedReward, int pl, int action_ind, int hc ){
    	 TreeNode node = (TreeNode) tree.get(hc);
	   	 node.nodeReward += expectedReward;
	   	 node.expectedReward[pl][action_ind] += expectedReward;
	   	 tree.put(hc, node);
    }
    public void setFirstAction(int hc, int action_ind){
    	 TreeNode node = (TreeNode) tree.get(hc);
	   	 node.first_action = action_ind;
	   	 tree.put(hc, node);
    }
    public void clearTraces()
    {
        ntraces = 0;
    }
    
    public void addTrace(int hc, int pl, int aind)
    {
        if (ntraces >= MAXNTRACES)
            return;
        traceList[ntraces].set(hc, pl, aind);// aind is action index repected to list possibility
        ntraces++;
    }
    
    public void update(int winner, int timeStamp)
    {
        int i, k, pl;
        TreeNode node;
        Trace tr;
        
        if(winner != -1){
        	for (i=0; i<ntraces; i++)
            {
                tr = traceList[i];
                node = getNode(tr.hc);
                node.nvisits++;
                node.nwins[tr.aind][winner]++;
                node.nactionvisits[tr.aind]++;
                node.timeStamp = timeStamp;            
                tree.put(tr.hc, node);
                winCount[winner]++;
                for(int loser = 0; loser < NPLAYERS; loser++ ){
                	if(loser == winner){
                		continue;
                	}
                	loseCount[loser]++;
                }
            }
        }
        
        //TODO: Start counting only the final state to consider the trading or we should consider other options
        //if we average the outcome, it would be more efficient to consider the trading option
        
        
        
        /// SLOOOW!!! 
//        Enumeration ek = tree.keys();
//        Enumeration ee = tree.elements();
//        Object keyo;
//        //TreeNode node;
//        while( ek.hasMoreElements() )
//        {
//            keyo = ek.nextElement();
//            node = (TreeNode) ee.nextElement();
//            if (node.timeStamp+5<timeStamp)
//            {
//                tree.remove(keyo);
//            }
//        }
        
    }
        
    public static final int MINVISITS = 10;
    public static final double C0 = 2.0;
    public static final double MAXVAL = 100.0;
    
    public int selectAction(int[] s, int pl, boolean echo)
    {
        return selectAction(getHashCode(s),pl, echo);
    }
    // UCT Selection method: You can finid it easily on Wikipeadia
    public int selectAction(int hc, int pl, boolean echo)
    {
        int k;
        double v, maxv;
        int maxind=0;
        TreeNode node = getNode(hc);
        
        maxv = 0.0;
        if (node==null) 
            return 0;
        if (node.nvisits < MINVISITS)
        {
            return rnd.nextInt(node.nactions);
        }
        for (k=0; k<node.nactions; k++)
        {
            if (node.nactionvisits[k]==0)
            {
                v = MAXVAL;
                if (echo)
                {
                    System.out.printf("%2d (%d): \t %5.2f ", k, node.nactionvisits[k], v);
                    v = 0.0;
                }
            }
            else
            {
            	// Working to improve the decision making by using longterm expected reward instead
                v = ((double)node.nwins[k][pl])/(node.nactionvisits[k]) +
                        C0*Math.sqrt(Math.log(node.nactions)/node.nactionvisits[k]);
                if (echo)
                {
                    System.out.printf("%2d (%d): \t %5.2f = %5.2f + %5.2f\n", k, node.nactionvisits[k], v, 
                            ((double)node.nwins[k][pl])/(node.nactionvisits[k]), C0*Math.sqrt(Math.log(node.nactions)/node.nactionvisits[k]));
                    		// The most simplest form of UCT algorithm
                    v = ((double)node.nwins[k][pl])/(node.nactionvisits[k]);
                }
            }
            if (maxv<v)
            {
                maxv = v;
                maxind = k;
            }
        }
        if (echo)
            System.out.printf("sel:%d\n\n", maxind);
        
        this.max_outcome = maxv;
        
        return maxind;
        
    }
    public int selectAction(int[] s, int pl, boolean ucb, boolean expectedLongTermReward, ActionList possibleAction,int level)
    {
        return selectAction(getHashCode(s),pl, ucb, expectedLongTermReward,possibleAction, level);
    }
    
    public int selectAction(int hc, int pl, boolean ucb, boolean expectedLongTermReward, ActionList possibleAction, int level)
    {
        int k;
        double v, maxv;
        int maxind=0;
        Random rnd = new Random();
        ArrayList<Integer> actionList = new ArrayList<>();
        TreeNode node = getNode(hc);
        
        maxv = 0.0;
        if (node==null) 
            return 0;
        
        
        for (k=0; k<node.nactions; k++)
        {
        	
            if (ucb)
            {
            	if(node.nactionvisits[k]==0){
            		v = MAXVAL;
            	}else{
            		v = (node.expectedReward[pl][k]/(double)node.nactionvisits[k]) 
                			+ C0*Math.sqrt(Math.log(((node.nactions)/node.nactionvisits[k])+1));
            	}
                
            }
          //Safe
            else
            {
        		// greedy choosing state after running ucb
            	if(node.nactionvisits[k] == 0){
            		v = 0.0;
            	}else{
                	v = node.expectedReward[pl][k];
                    //v = ((double)node.nwins[k][pl])/(node.nactionvisits[k]) + C0*Math.sqrt(Math.log(node.nactions)/node.nactionvisits[k]);
                    
            	}
            }
           //Not safe
          //Safe
            /*There is a problem here with the how I assign the value to action*/
            if (v >= maxv)
            {
            	if(v == maxv){
            		actionList.add(k);
            	}
            	else{
            		actionList.clear();
            		actionList.add(k);
            	}
                maxv = v;
                maxind = k;
            }
        }
        
        this.max_outcome = maxv;
        
        if(level != S_NORMAL){
        	if(actionList.size() > 1){
            	return actionList.get(rnd.nextInt(actionList.size()));
            }
            else{
            	return maxind;
            }
        }else{
        	/*
        	int action = this.preferenceAction(possibleAction, actionList);
        	if(action == -1){
        		return maxind;
        	}
        	else{
        		return action;
        	}*/
        	return maxind;
        	
        }
        //return maxind;
        
        
        
    }
    public int preferenceAction(ActionList listPossibility, ArrayList actionlist){
		int ac_ind = 0;
		Random rnd = new Random();
		if(actionlist.size() == 0){
			return -1;
		}
		else{
			for(int i = 0; i < actionlist.size(); i++){
				switch(listPossibility.action[(int)actionlist.get(i)][0]){
					case A_BUILDCITY:
						return i;
					case A_BUILDSETTLEMENT:
						return i;
					default:
						continue;
				}
			}
			return (int)actionlist.get(rnd.nextInt(actionlist.size()));
		}
    }
    
    @Override
    public String toString()
    {
        String s = getClass().getName() + ": " + tree.size() + "\n";
        int hc;
        TreeNode node;
        
        Enumeration e = tree.keys();
        while( e.hasMoreElements() )
        {
            hc = (Integer) e.nextElement();
            node = (TreeNode) tree.get(hc);
            s += String.format("%X/%d: %3d", hc, node.nactions, node.nvisits);
            for (int i=0; i<node.nactions; i++)
            {
                s+= String.format(" %d[%d,%d,%d,%d]", node.nactionvisits[i],
                        node.nwins[i][0],node.nwins[i][1],node.nwins[i][2],node.nwins[i][3]);
            }
            s+= "\n";
            
        }
        return s;
    }
    // Use this function to decide whether to negotiate or not
    
    public double getMaxOutcome(){
    	
    	return this.max_outcome;
    	
    }
    
    public void clearWinner(){
    	for(int i = 0; i<NPLAYERS; i++){
    		this.winCount[i] = 0;
    	}
    }
    
    public int getWinCount(int player){
    	return this.winCount[player];
    }
    
    public int getLoseCount(int player){
    	return this.loseCount[player];
    }
    
    public float getAverageWinLose(int player){
    	return (this.getWinCount(player)/(this.getLoseCount(player) + this.getWinCount(player)));
    }
    
    public int[] getWinnersCount(){
    	return this.winCount;
    }
    final double DISCOUNT_FACTOR = 0.9;
    
    public double returnReward(int depth,boolean leadingToWin){
    	double reward =0.0;
    	double temp_reward = 0.0;
    	if(depth > 1000){
    		if(leadingToWin){
    			return 500.0;
    		}else{
    			return 0.0;
    		}
    	}
    	if(depth == ntraces){
    		return reward;
    		
    	}
    	else{

        	if(depth == 0){
        		TreeNode node = getNode(traceList[depth].hc);
            	if(node == null){
            		if(leadingToWin){
            			return 100.0;
            		}
            		else{
            			return 0.0;
            		}
            	}else{
            		if(leadingToWin){
            			reward += node.nodeReward + DISCOUNT_FACTOR*returnReward(++depth, leadingToWin) + 100.0;
            		}else{
            			reward += node.nodeReward + DISCOUNT_FACTOR*returnReward(++depth, leadingToWin);
            		}
            	}
        		
        	}else{
        		TreeNode node = getNode(traceList[depth].hc);
            	if(node == null){
            		if(leadingToWin){
            			return 100.0;
            		}
            		else{
            			return 0.0;
            		}
            	}
        		reward += node.nodeReward + DISCOUNT_FACTOR*returnReward(++depth, leadingToWin);
        	}
    	}
    	
    	return reward;
    }
}

class Trace {
    public int hc = 0;
    public int pl = -1; 
    public int aind = -1;
    
    public void set(int hc, int pl, int aind)
    {
        this.hc = hc;
        this.pl = pl;
        this.aind = aind;
    }
}

