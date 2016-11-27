/*List of possible trade is in listNormalPossibilities
 * */

package smartsettlers.player;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import smartsettlers.boardlayout.*;
import smartsettlers.util.*;

/**
 *
 * @author szityu
 */
/*
 * Improve by Makara Phav
 * */
public abstract class Player implements GameStateConstants
{
    int position;
    int type;
    
    //int lastvertex; // last vertex where a settlement has been placed. 
                    // needed for initial road placement
    
    boolean POMCP = false;
    BoardLayout bl;
    Random rnd;
    
    public Player(BoardLayout bl, int position)
    {
        this.bl = bl;
        this.position = position;
        rnd = new Random();
    }
    
    public Player(BoardLayout bl, int position, boolean isPOMCP)
    {
        this.bl = bl;
        this.position = position;
        rnd = new Random();
        POMCP = isPOMCP;
    }
    
    public boolean isPOMCP(){
    	return this.POMCP;
    }
    public boolean isHMMAgent(){
    	if((position == NPLAYERS - 1)){
    		return true;
    	}
    	return false;
    }
    public void listMonopolyPossibilities(int []s)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i;
        if (s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] != 0)
            return;
        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_MONOPOLY] >= 1)
        {
            for (i=0; i<NRESOURCES; i++)
                bl.possibilities.addAction(1.0,A_PLAYCARD_MONOPOLY, i);                    
//                bl.possibilities.addAction(1000.0,A_PLAYCARD_MONOPOLY, i);                    
        }
        
    }
    
    public void listInitSettlementPossibilities(int[] s) 
    {
       int i;

       for (i=0; i<N_VERTICES; i++)
        {
            if (s[OFS_VERTICES+i]==0)
            {
                bl.possibilities.addAction(1.0,A_BUILDSETTLEMENT, i);
            }
        }

    }
    
    public void listInitRoadPossibilities(int[] s) 
    {
        int i, ind;
        int lastvertex = s[OFS_LASTVERTEX];
        for (i=0; i<6; i++)
        {
            ind = bl.neighborVertexEdge[lastvertex][i];
            if ((ind != -1) && (s[OFS_EDGES+ind]==0))
            {
                bl.possibilities.addAction(1.0,A_BUILDROAD, ind);
            }
        }        
    }
    //
    public void listTradingOption(int[] s){
    	
    	int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, pl2, val;
        
        bl.tradingPossibilites.Clear();

    	for ( i=0; i<NRESOURCES; i++)
        {
            for ( j = 0; j<NRESOURCES; j++)
            {
                if (i==j) continue;
                double w = 1.0;
                for(int player = 0; player < NPLAYERS ;player++){
                	
                	if (pl == player){continue;}
                	
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] == 1) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,1,player,j,1);
                		
                	}
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] > 1) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,2,player,j,1);
                		
                		
                	}
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] > 2) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,3,player,j,1);
                		
                		
                	}
                }
                
            }
        }
    }
    public void listNormalPossibilities(int[] s) 
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, pl2, val;
        boolean hasneighbor;
        
        bl.possibilities.addAction(1.0,A_ENDTURN);
//        bl.possibilities.addAction(10.0,A_ENDTURN);

        // buy road
        if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WOOD] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_CLAY] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_NROADS] < 15) )
        {
            listRoadPossibilities(s);
            
//            for (i=0; i<N_EDGES; i++)
//            {
//                if (s[OFS_EDGES+i]==EDGE_EMPTY)
//                {
//                    hasneighbor = false;
//                    for (j=0; j<6; j++)
//                    {
//                        ind = bl.neighborEdgeEdge[i][j];
//                        if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
//                        {
//                            hasneighbor = true;
//                        }
//                    }
//                    if (hasneighbor)
//                        bl.possibilities.addAction(A_BUILDROAD,i);                                
//                }
//            }
        }

        // buy settlement
        if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WOOD] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_CLAY] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_SHEEP] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS] <= 5) )
        {
        	for (i=0; i<N_VERTICES; i++)
            {
                if (s[OFS_VERTICES+i]==VERTEX_EMPTY) 
                {
                    hasneighbor = false;
                    for (j=0; j<6; j++)
                    {
                        ind = bl.neighborVertexEdge[i][j];
                        if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
                            hasneighbor = true;
                    }
                    if (hasneighbor)
                        bl.possibilities.addAction(1.0,A_BUILDSETTLEMENT, i);
//                        bl.possibilities.addAction(10000.0,A_BUILDSETTLEMENT, i);
                }
            }
        }

        // buy city
        if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_STONE] >= 3) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 2) &&
               (s[OFS_PLAYERDATA[pl]+OFS_NCITIES] <= 4) )
        {
        	for (i=0; i<N_VERTICES; i++)
            {
                if (s[OFS_VERTICES+i]==VERTEX_HASSETTLEMENT + pl) 
                {
                    bl.possibilities.addAction(1.0,A_BUILDCITY, i);
//                    bl.possibilities.addAction(10000.0,A_BUILDCITY, i);
                }
            }
        }

        // buy devcard
        if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_STONE] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_SHEEP] >= 1) &&
               s[OFS_NCARDSGONE] < NCARDS  )
        {
            bl.possibilities.addAction(1.0,A_BUYCARD, bl.cardSequence[s[OFS_NCARDSGONE]]);
            // the type of next card is added. It is added only for logging,
            // may not be peeked by the player
        }


        listDevCardPossibilities(s);

        double w = 10.0;
        
        // trade with ports or bank
        // The point that we need to improve by using ANN to do negotiation with other agent
        
        for (i=0; i<NRESOURCES; i++)
        {
            for (j = 0; j<NRESOURCES; j++)
            {
                if (i==j) continue;
//                if (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+j]==0)
//                    w = 100.0;
//                else
//                    w = 10.0;
                w = 1.0;
                //double weight = Math.pow(10, nresources-4);
                // specific port
                if (    (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 2) &&
                        (s[OFS_PLAYERDATA[pl]+OFS_ACCESSTOPORT+i] == 1) )
                    bl.possibilities.addAction(w,A_PORTTRADE, 2, i, 1, j);
                // misc port
                else if (    (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 3) &&
                        (s[OFS_PLAYERDATA[pl]+OFS_ACCESSTOPORT+NRESOURCES] == 1) )
                    bl.possibilities.addAction(w,A_PORTTRADE, 3, i, 1, j);                        
                // bank
                else if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 4) )
                    bl.possibilities.addAction(w,A_PORTTRADE, 4, i, 1, j);
            }
        }
        // Trade with another agent
        // Let assuming that agent cannot trade with card more than 4
        // TODO need to list of all the possible action in trading here
        // We need to do rollout one more time with trading then get the reward from there.
        // I think I should put it with the normal list of possibilities
        /*
        for (i=0; i<NRESOURCES; i++)
        {
            for (j = 0; j<NRESOURCES; j++)
            {
                if (i==j) continue;
                w = 1.0;
                for(int player = 0; player < NPLAYERS ;player++){
                	
                	if (pl == player){continue;}
                	
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] == 1) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,1,player,j,1);
                		
                	}
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] > 1) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,2,player,j,1);
                		
                		
                	}
                	else if((s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i] > 2) && 
                			(s[OFS_PLAYERDATA[player] + OFS_RESOURCES + j] >= 1)){
                		
                		bl.tradingPossibilites.addTradOption(w,pl,i,3,player,j,1);
                		
                		
                	}
                }
                
            }
        }*/
        
    }
    // When we stimulate the possible action we only consider the card that we have and ignore the other player possibility
    public void listDevCardPossibilities(int []s)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, pl2, val;

        // play devcards
        if (s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] != 0)
            return;
        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREERESOURCE] >= 1)
        {
            for (i=0; i<NRESOURCES; i++)
                for (j=i; j<NRESOURCES; j++)
//                    bl.possibilities.addAction(100.0,A_PLAYCARD_FREERESOURCE, i, j);
                    bl.possibilities.addAction(1.0,A_PLAYCARD_FREERESOURCE, i, j);
        }

        listMonopolyPossibilities(s);
        
        if ((s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREEROAD] >= 1) &&
               (s[OFS_PLAYERDATA[pl]+OFS_NROADS] < 15-1) )                
        {
//            bl.possibilities.addAction(100.0,A_PLAYCARD_FREEROAD);                    
            bl.possibilities.addAction(1.0,A_PLAYCARD_FREEROAD);                    
        }

        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_KNIGHT] >= 1)
        {
            listRobberPossibilities(s, A_PLAYCARD_KNIGHT);
        }
    }

    public void listRoadPossibilities(int[] s)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind;
        boolean hasneighbor;
        double ratio = ((double)s[OFS_PLAYERDATA[pl]+OFS_NROADS])/(s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS]+s[OFS_PLAYERDATA[pl]+OFS_NCITIES]);
        double weight = Math.pow(10, -ratio+1);
        weight = 1.0;

            for (i=0; i<N_VERTICES; i++)
            {
                
                if ((s[OFS_VERTICES+i] == VERTEX_EMPTY) || 
                        (s[OFS_VERTICES+i] == VERTEX_TOOCLOSE) ||
                        (s[OFS_VERTICES+i] == VERTEX_HASSETTLEMENT+pl) ||
                        (s[OFS_VERTICES+i] == VERTEX_HASCITY+pl))
                {
                    hasneighbor = false;
                    for (j=0; j<6; j++)
                    {
                        ind = bl.neighborVertexEdge[i][j];
                        if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
                        {
                            hasneighbor = true;
                        }
                    }
                    if (hasneighbor)
                    {
                        for (j=0; j<6; j++)
                        {
                            ind = bl.neighborVertexEdge[i][j];
                            if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_EMPTY))
                                bl.possibilities.addAction(weight,A_BUILDROAD,ind);                                
                        }
                    }
                }
            }
//        for (i=0; i<N_EDGES; i++)
//        {
//            if (s[OFS_EDGES+i]==EDGE_EMPTY)
//            {
//                hasneighbor = false;
//                for (j=0; j<6; j++)
//                {
//                    ind = bl.neighborEdgeEdge[i][j];
//                    if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
//                        hasneighbor = true;
//                }
//                if (hasneighbor)
//                    bl.possibilities.addAction(A_BUILDROAD,i);                                
//            }
//        }
        
    }
    
    public void listRobberPossibilities(int[] s, int action)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];// Current Player
        int i, j, ind, pl2, val;
        boolean hasneighbor;

        for (i=0; i<N_HEXES; i++)
        {
            if (bl.hextiles[i].type != TYPE_LAND)
                continue;
            if (i == s[OFS_ROBBERPLACE])
                continue;
            //!!!!! bl.possibilities.addAction(A_PLACEROBBER, i, -1);
            // TODO: if opponent player has multiple buildings, multipe actions are added
            for (j=0; j<6; j++)
            {
                ind = bl.neighborHexVertex[i][j];
                int res = 0;
                if (ind==-1)
                    continue;
                val = s[OFS_VERTICES + ind];
                if ((val >= VERTEX_HASSETTLEMENT) && (val< VERTEX_HASSETTLEMENT + NPLAYERS))
                    pl2 = val - VERTEX_HASSETTLEMENT;
                else if ((val >= VERTEX_HASCITY) && (val< VERTEX_HASCITY + NPLAYERS))
                    pl2 = val - VERTEX_HASCITY;
                else
                    pl2 = -1;
                if ((pl2!=-1) && (pl2!=pl))
                	//TODO: Hide the state of other player stealing card in the state environment from other players except the stealer and one who got robbed 
                    res = selectRandomResourceInHand(pl2, s);
                	if(res != -1 ){
                    	bl.possibilities.addAction(1.0,action, i, pl2, res);
                    }
                	else{
                		bl.possibilities.addAction(0.1,action,i,pl2,-1);
                	}
            }
        }
        
    }
    
    
    public void listPossibilities(int[] s)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, pl2, val;
        boolean hasneighbor;

        bl.possibilities.Clear();
        bl.tradingPossibilites.Clear();
        
        switch (fsmstate)
        {
            case S_SETTLEMENT1:
            case S_SETTLEMENT2:
                listInitSettlementPossibilities(s);
                break;
            case S_ROAD1:
            case S_ROAD2:
                listInitRoadPossibilities(s);
                break;
            case S_BEFOREDICE:
                bl.possibilities.addAction(1.0,A_THROWDICE);                
                //listDevCardPossibilities(s);
                if ((s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_KNIGHT] >= 1)
                        && (s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD]==0))
                {
                    listRobberPossibilities(s, A_PLAYCARD_KNIGHT);
                }                
                break;
            case S_FREEROAD1:// This does not execute so FREEROAD2 is execute
            				// It is the same as asking freeroad 1 to build one that free road2 to build another one
            				// But it is simpler this way
            case S_FREEROAD2:
                listRoadPossibilities(s);
                break;
            case S_PAYTAX:
            	// This is for just simulation that there is a possibility that we might get cut down by pay tax
                val = 0;
                for (i=0; i<NRESOURCES; i++)
                    val += s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i];
                if (val>7)
                    val = val/2;
                else
                    val = 0;
                bl.possibilities.addAction(1.0,A_PAYTAX,val);
                break;
            case S_ROBBERAT7:
                listRobberPossibilities(s, A_PLACEROBBER);
                break;
            case S_NORMAL:
                listNormalPossibilities(s);
                break;
            case S_MAKEOFFER:
            	listTradingOption(s);
            	break;
            	
        }
        if (bl.possibilities.n==0)
        {
            bl.possibilities.addAction(1.0,A_NOTHING);
        }
       
    }

    public abstract void selectAction(int[] s, int [] a);

    
//    public void PlaceSettlement(int[] s)
//    {
//        int i; 
//        
//        // possibilities are already listed...
//        i = bl.possibilities.RandomInd();
//        performAction(s, bl.possibilities.action[i], bl.possibilities.par1[i], bl.possibilities.par2[i]);
//    }
//
//    public void PlaceRoad(int[] s)
//    {
//        // todo: place road
//        // s has information whether first, second or general
//    }
    //Important to understand mote carlo implementation
    public int selectRandomResourceInHand(int pl, int[] s)
    {
    	// There is something wrong here which always show player_sth have -sth card in the simulation stage
        int i, ind, j;
        int ncards = 0;
        for (i=0; i<NRESOURCES; i++)
            ncards += s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i];
        if (ncards == 0)
            return -1;
        
        if (ncards<= 0)
        {
            String str = "";
            System.out.printf("Player %d has %d cards\n", pl, ncards);
            for (i=0; i<s.length; i++)
                str = str + " " + s[i];
            System.out.flush();
        }
        
        if(ncards > 0){
        	ind = rnd.nextInt(ncards)+1;
            j = 0;
            //Should find a better way of selecting the card
            for (i=0; i<NRESOURCES; i++)
            {
                j += s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i];
                if (j>=ind)
                    break;
            }
            return Math.min(i, NRESOURCES-1);
        }
        else{
        	return -1;
        }
        
    }
    //It have to be implement by sub-class
    public abstract int selectMostUselessResourceInHand(int pl, int []s);
    //TODO: Working on implement action that allow trading
    public void performAction(int[] s, int [] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, val, ind2, k, ncards;
        int another_player = 0;
        // We need to perform to translate every action from this point
        switch (a[0])
        {
            case A_BUILDSETTLEMENT: 
                s[OFS_VERTICES+a[1]] =  VERTEX_HASSETTLEMENT+ pl;// At vertices index a[1] already have settlement with player number pl
                s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS]++;
                s[OFS_LASTVERTEX] = a[1];
                boolean[] hasOpponentRoad = new boolean[NPLAYERS];
                for (j=0; j<6; j++)
                {
                    ind = bl.neighborVertexVertex[a[1]][j];
                    if (ind != -1)
                    {
                        s[OFS_VERTICES+ind] = VERTEX_TOOCLOSE;
                    }
                    ind = bl.neighborVertexEdge[a[1]][j];
                    if ((ind != -1) && (s[OFS_EDGES+ind] != EDGE_EMPTY))
                    {
                        hasOpponentRoad[s[OFS_EDGES+ind]-EDGE_OCCUPIED] = true;
                    }
                }
                hasOpponentRoad[pl] = false;
                for (j=0; j<6; j++)
                {
                    ind = bl.neighborVertexHex[a[1]][j];
                    if ((ind != -1) && (bl.hextiles[ind].type == TYPE_PORT))
                    {
                        val = bl.hextiles[ind].subtype - PORT_SHEEP;
                        k = j-2; if (k<0) k+=6;
                        if (k==bl.hextiles[ind].orientation)
                            s[OFS_PLAYERDATA[pl] + OFS_ACCESSTOPORT + val] = 1;
                        k = j-3; if (k<0) k+=6;
                        if (k==bl.hextiles[ind].orientation)
                            s[OFS_PLAYERDATA[pl] + OFS_ACCESSTOPORT + val] = 1;
                    }
                }
                for (int pl2=0; pl2<NPLAYERS; pl2++)
                {
                    if (hasOpponentRoad[pl2])
                        bl.recalcLongestRoad(s,pl2);
                }
                if (fsmstate == S_SETTLEMENT2)
                {
                    int resource;
                    for (j=0; j<6; j++)
                    {
                        ind = bl.neighborVertexHex[a[1]][j];
                        if (ind !=-1)
                        {
                            resource = bl.hextiles[ind].yields();
                            if (resource != -1)
                            {
                                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + resource]++;
                            }
                        }
                    }
                }
                else if (fsmstate == S_NORMAL)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WOOD]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_CLAY]--;                    
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_SHEEP]--;                    
                }

                break;
            case A_BUILDCITY: 
                s[OFS_VERTICES+a[1]] =  VERTEX_HASCITY+ pl;
                s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS]--;
                s[OFS_PLAYERDATA[pl]+OFS_NCITIES]++;

                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_STONE]-= 3;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]-= 2;
                break;
            case A_BUILDROAD:
                s[OFS_EDGES + a[1]] = EDGE_OCCUPIED + pl;
                //System.out.println(pl);
                s[OFS_PLAYERDATA[pl]+OFS_NROADS]++;
                if (fsmstate == S_NORMAL)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WOOD]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_CLAY]--;                    
                }
                bl.recalcLongestRoad(s,pl);
                break;
            //
            case A_THROWDICE:
                s[OFS_DIE1] = rnd.nextInt(6)+1;
                s[OFS_DIE2] = rnd.nextInt(6)+1;
                val = s[OFS_DIE1] + s[OFS_DIE2];
                for (ind=0; ind<N_HEXES; ind++)
                {
                    if ( (val == bl.hextiles[ind].productionNumber)
                            && (s[OFS_ROBBERPLACE]!=ind) )
                    {
                        for (j = 0; j<6; j++)
                        {
                            ind2 = bl.neighborHexVertex[ind][j];
                            if (ind2 != -1)
                            {
                                k = s[OFS_VERTICES + ind2];
                                // production for settlement
                                if ((k>=VERTEX_HASSETTLEMENT) && (k<VERTEX_HASSETTLEMENT+NPLAYERS))
                                {
                                    pl = k-VERTEX_HASSETTLEMENT;
                                    s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+bl.hextiles[ind].yields()] ++;
                                }
                                // production for city
                                if ((k>=VERTEX_HASCITY) && (k<VERTEX_HASCITY+NPLAYERS))
                                {
                                    pl = k-VERTEX_HASCITY;
                                    s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+bl.hextiles[ind].yields()] += 2;
                                }
                          }
                        }
                    }
                }
                break;
                
            case A_PORTTRADE:
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] -= a[1];
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[4]] += a[3];
                break;
                
            case A_BUYCARD:
            	
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]--;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_SHEEP]--;                    
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_STONE]--;
                
                val = bl.cardSequence[s[OFS_NCARDSGONE]];
                
                if (val==CARD_ONEPOINT)
                    s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + val]++;
                else
                    s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + val]++;
                // HMM player Helper
                bl.buyingCardTimeStamp[pl][val][s[OFS_NCARDSGONE]] = bl.gamelog.getSize();
                bl.firstBought[pl][s[OFS_NCARDSGONE]] = bl.gamelog.getSize();
                bl.trackingMyCardIndex[pl][s[OFS_NCARDSGONE]] = val;
                s[OFS_NCARDSGONE] ++;
                break;
            case A_PLAYCARD_FREERESOURCE:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREERESOURCE]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_FREERESOURCE]++;
                // HMM player Helper
                for(int indCard = 0; indCard < NCARDS; indCard++){
                	if(bl.buyingCardTimeStamp[pl][CARD_FREERESOURCE][indCard] != 0){
                		
                		bl.playingCardTimeStamp[pl][CARD_FREERESOURCE][indCard] = bl.gamelog.getSize();
                		
                		bl.playingAveragingTime[pl][CARD_FREERESOURCE][indCard] = 
                				bl.playingCardTimeStamp[pl][CARD_FREERESOURCE][indCard] 
                						- bl.buyingCardTimeStamp[pl][CARD_FREERESOURCE][indCard];
                		bl.cardPlayingTimetimeStamp[CARD_FREERESOURCE][bl.stateRepresentation(bl.playingAveragingTime[pl][CARD_FREERESOURCE][indCard])]+=1;
                		
                		bl.buyingCardTimeStamp[pl][CARD_FREERESOURCE][indCard] = 0;
                		bl.firstBought[pl][indCard] = 0;
                		bl.revealCardSoFar[pl][indCard] = CARD_FREERESOURCE;
                	}
                }
                // POMCP player helper
                bl.eachPlayerCardPlaiedThisRound[pl][CARD_FREERESOURCE]++;
                playedCard(CARD_FREERESOURCE, pl);
                
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[1]] ++;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] ++;
                break;
            case A_PLAYCARD_MONOPOLY:
            	
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_MONOPOLY]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_MONOPOLY]++;
                // HMM player Helper
                for(int indCard = 0; indCard < NCARDS; indCard++){
                	if(bl.buyingCardTimeStamp[pl][CARD_MONOPOLY][indCard] != 0){
                		
                		bl.playingCardTimeStamp[pl][CARD_MONOPOLY][indCard] = bl.gamelog.getSize(); 
                		bl.playingAveragingTime[pl][CARD_MONOPOLY][indCard] = 
                				bl.playingCardTimeStamp[pl][CARD_MONOPOLY][indCard] 
                						- bl.buyingCardTimeStamp[pl][CARD_MONOPOLY][indCard];
                		bl.cardPlayingTimetimeStamp[CARD_MONOPOLY][bl.stateRepresentation(bl.playingAveragingTime[pl][CARD_MONOPOLY][indCard])]+=1;
                		bl.buyingCardTimeStamp[pl][CARD_MONOPOLY][indCard] = 0;
                		bl.firstBought[pl][indCard] = 0;
                		bl.revealCardSoFar[pl][indCard] = CARD_MONOPOLY;
                	}
                }
                // POMCP player helper
                bl.eachPlayerCardPlaiedThisRound[pl][CARD_MONOPOLY]++;
                playedCard(CARD_MONOPOLY, pl);
                
                for (ind = 0; ind<NPLAYERS; ind++)
                {
                    if (ind==pl)
                        continue;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[1]] += s[OFS_PLAYERDATA[ind] + OFS_RESOURCES + a[1]];                    
                    s[OFS_PLAYERDATA[ind] + OFS_RESOURCES + a[1]] = 0;
                }
                break;
            case A_PLAYCARD_FREEROAD:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREEROAD]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_FREEROAD]++;
                // HMM player Helper
                for(int indCard = 0; indCard < NCARDS; indCard++){
                	if(bl.buyingCardTimeStamp[pl][CARD_FREEROAD][indCard] != 0){
                		
                		bl.playingCardTimeStamp[pl][CARD_FREEROAD][indCard] = bl.gamelog.getSize(); 
                		bl.playingAveragingTime[pl][CARD_FREEROAD][indCard] = 
                				bl.playingCardTimeStamp[pl][CARD_FREEROAD][indCard] 
                						- bl.buyingCardTimeStamp[pl][CARD_FREEROAD][indCard];
                		bl.cardPlayingTimetimeStamp[CARD_FREEROAD][bl.stateRepresentation(bl.playingAveragingTime[pl][CARD_FREEROAD][indCard])]+=1;
                		bl.buyingCardTimeStamp[pl][CARD_FREEROAD][indCard] = 0;
                		bl.firstBought[pl][indCard] = 0;
                		bl.revealCardSoFar[pl][indCard] = CARD_FREEROAD;
                	}
                }
                // POMCP player helper
                bl.eachPlayerCardPlaiedThisRound[pl][CARD_FREEROAD]++;
                playedCard(CARD_FREEROAD, pl);
                break;
            case A_PLAYCARD_KNIGHT:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_KNIGHT]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_KNIGHT]++;
                // HMM player Helper
                for(int indCard = 0; indCard < NCARDS; indCard++){
                	if(bl.buyingCardTimeStamp[pl][CARD_KNIGHT][indCard] != 0){
                		bl.playingCardTimeStamp[pl][CARD_KNIGHT][indCard] = bl.gamelog.getSize(); 
                		bl.playingAveragingTime[pl][CARD_KNIGHT][indCard] = 
                				bl.playingCardTimeStamp[pl][CARD_KNIGHT][indCard] 
                						- bl.buyingCardTimeStamp[pl][CARD_KNIGHT][indCard];
                        bl.cardPlayingTimetimeStamp[CARD_KNIGHT][bl.stateRepresentation(bl.playingAveragingTime[pl][CARD_KNIGHT][indCard])]++;
                		bl.buyingCardTimeStamp[pl][CARD_KNIGHT][indCard] = 0;
                		bl.firstBought[pl][indCard] = 0;
                		bl.revealCardSoFar[pl][indCard] = CARD_KNIGHT;
                	}
                }
                // POMCP player helper
                bl.eachPlayerCardPlaiedThisRound[pl][CARD_KNIGHT]++;
                playedCard(CARD_KNIGHT, pl);
                bl.recalcLargestArmy(s);
                
            // flow to next case! 
            case A_PLACEROBBER:
                s[OFS_ROBBERPLACE] = a[1];
                if ((a[2]!=-1) && a[3]!=-1)
                {
                    if(s[OFS_PLAYERDATA[a[2]] + OFS_RESOURCES + a[3]] > 0){
                    	s[OFS_PLAYERDATA[a[2]] + OFS_RESOURCES + a[3]]--;
                        s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[3]]++;
                    }
                }
                break;
            case A_PAYTAX:
            	// If we don't check every time we use selectMostUselessResourceInHand sometime it return -1;
            	// Tax Paying in the orginal game of SmartSettler is wrong
            	
            	for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
            		
            		int totalCardForEachPlayer = this.getTotalResourceCount(ind_player);
            		//System.out.printf("Total Resource Card: %d\n", totalCardForEachPlayer);
            		if(totalCardForEachPlayer > 7){
            			
            			int reduceCard = totalCardForEachPlayer/2;
            			
            			for(int ind_cut=0; ind_cut < reduceCard; ind_cut++){
            				
            				int res_card = this.selectMostUselessResourceInHand(ind_player, s);
            				
            				if(res_card >= 0){
            					
            					if(s[OFS_PLAYERDATA[ind_player] + OFS_RESOURCES + res_card] > 0){
            						s[OFS_PLAYERDATA[ind_player] + OFS_RESOURCES + res_card]--;
            						
            					}
            				}
            			}
            		}
            	}
                
            	
            	/*ind = selectMostUselessResourceInHand(pl, s);
                if(ind < 0){
                	break;
                }
                else{
                	s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + ind]--;
                }
            	
            	for (i=0; i<a[1]; i++)
                {
                    
                }*/
            	break;
            case A_ENDTURN:
                // new cards become old cards
            	
            	int cardBought = 0;
            	int cardAvailable = 0;
            	
                for (ind=0; ind<NCARDTYPES; ind++)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + ind] += s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind];
                    if(s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind]!=0){
                    	CardTimeStamp cardTime = new CardTimeStamp(ind, fsmlevel);
                    }
                    cardBought += s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind];
                    s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind] = 0;
                    cardAvailable += s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + ind];
                    
                }
                
                bl.newlyBoughtCardEachPlayer[pl] = cardBought;
                bl.eachPlayerCardNotReveal[pl] = cardAvailable;
                this.keepSinceAssign(bl.keepSince, bl.firstBought);
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 0;
                break;
                
            /*case A_TRADING:
            	
            	//Trading with other player with only one at a time
            	// This one should be offer accepted
            	int otherplayer = a[4];
            	s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] -= a[3];
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[5]] += a[6];
                s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[5]] -= a[6];
                s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[2]] += a[3];
                break;
            case A_CONSIDEROFFER:
            	// Call to trading simulation again here to see whether it is good idea to trade
            	
            	break;*/
        }
    }
    public void performAction_simulation(int[] s, int [] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        int i, j, ind, val, ind2, k, ncards;
        int another_player = 0;
        // We need to perform to translate every action from this point
        switch (a[0])
        {
            case A_BUILDSETTLEMENT: 
                s[OFS_VERTICES+a[1]] =  VERTEX_HASSETTLEMENT+ pl;
                s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS]++;
                s[OFS_LASTVERTEX] = a[1];
                boolean[] hasOpponentRoad = new boolean[NPLAYERS];
                for (j=0; j<6; j++)
                {
                    ind = bl.neighborVertexVertex[a[1]][j];
                    if (ind != -1)
                    {
                        s[OFS_VERTICES+ind] = VERTEX_TOOCLOSE;
                    }
                    ind = bl.neighborVertexEdge[a[1]][j];
                    if ((ind != -1) && (s[OFS_EDGES+ind] != EDGE_EMPTY))
                    {
                        hasOpponentRoad[s[OFS_EDGES+ind]-EDGE_OCCUPIED] = true;
                    }
                }
                hasOpponentRoad[pl] = false;
                for (j=0; j<6; j++)
                {
                    ind = bl.neighborVertexHex[a[1]][j];
                    if ((ind != -1) && (bl.hextiles[ind].type == TYPE_PORT))
                    {
                        val = bl.hextiles[ind].subtype - PORT_SHEEP;
                        k = j-2; if (k<0) k+=6;
                        if (k==bl.hextiles[ind].orientation)
                            s[OFS_PLAYERDATA[pl] + OFS_ACCESSTOPORT + val] = 1;
                        k = j-3; if (k<0) k+=6;
                        if (k==bl.hextiles[ind].orientation)
                            s[OFS_PLAYERDATA[pl] + OFS_ACCESSTOPORT + val] = 1;
                    }
                }
                for (int pl2=0; pl2<NPLAYERS; pl2++)
                {
                    if (hasOpponentRoad[pl2])
                        bl.recalcLongestRoad(s,pl2);
                }
                if (fsmstate == S_SETTLEMENT2)
                {
                    int resource;
                    for (j=0; j<6; j++)
                    {
                        ind = bl.neighborVertexHex[a[1]][j];
                        if (ind !=-1)
                        {
                            resource = bl.hextiles[ind].yields();
                            if (resource != -1)
                            {
                                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + resource]++;
                            }
                        }
                    }
                }
                else if (fsmstate == S_NORMAL)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WOOD]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_CLAY]--;                    
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_SHEEP]--;                    
                }

                break;
            case A_BUILDCITY: 
                s[OFS_VERTICES+a[1]] =  VERTEX_HASCITY+ pl;
                s[OFS_PLAYERDATA[pl]+OFS_NSETTLEMENTS]--;
                s[OFS_PLAYERDATA[pl]+OFS_NCITIES]++;

                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_STONE]-= 3;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]-= 2;
                break;
            case A_BUILDROAD:
                s[OFS_EDGES + a[1]] = EDGE_OCCUPIED + pl;
                //System.out.println(pl);
                s[OFS_PLAYERDATA[pl]+OFS_NROADS]++;
                if (fsmstate == S_NORMAL)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WOOD]--;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_CLAY]--;                    
                }
                bl.recalcLongestRoad(s,pl);
                break;
            //
            case A_THROWDICE:
                s[OFS_DIE1] = rnd.nextInt(6)+1;
                s[OFS_DIE2] = rnd.nextInt(6)+1;
                val = s[OFS_DIE1] + s[OFS_DIE2];
                for (ind=0; ind<N_HEXES; ind++)
                {
                    if ( (val == bl.hextiles[ind].productionNumber)
                            && (s[OFS_ROBBERPLACE]!=ind) )
                    {
                        for (j = 0; j<6; j++)
                        {
                            ind2 = bl.neighborHexVertex[ind][j];
                            if (ind2 != -1)
                            {
                                k = s[OFS_VERTICES + ind2];
                                // production for settlement
                                if ((k>=VERTEX_HASSETTLEMENT) && (k<VERTEX_HASSETTLEMENT+NPLAYERS))
                                {
                                    pl = k-VERTEX_HASSETTLEMENT;
                                    s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+bl.hextiles[ind].yields()] ++;
                                }
                                // production for city
                                if ((k>=VERTEX_HASCITY) && (k<VERTEX_HASCITY+NPLAYERS))
                                {
                                    pl = k-VERTEX_HASCITY;
                                    s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+bl.hextiles[ind].yields()] += 2;
                                }
                          }
                        }
                    }
                }
                break;
            case A_PORTTRADE:
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] -= a[1];
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[4]] += a[3];
                break;
            case A_BUYCARD:
            	
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_WHEAT]--;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_SHEEP]--;                    
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + RES_STONE]--;
                
                if(s[OFS_NCARDSGONE] <= 24){
                	val = bl.cardSequence[s[OFS_NCARDSGONE]];
                	if (val==CARD_ONEPOINT)
                        s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + val]++;
                    else
                        s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + val]++;
                    
                    s[OFS_NCARDSGONE] ++;
                }
                
                break;
            case A_PLAYCARD_FREERESOURCE:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREERESOURCE]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_FREERESOURCE]++;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[1]] ++;
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] ++;
                break;
            case A_PLAYCARD_MONOPOLY:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_MONOPOLY]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_MONOPOLY]++;
                for (ind = 0; ind<NPLAYERS; ind++)
                {
                    if (ind==pl)
                        continue;
                    s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[1]] += s[OFS_PLAYERDATA[ind] + OFS_RESOURCES + a[1]];                    
                    s[OFS_PLAYERDATA[ind] + OFS_RESOURCES + a[1]] = 0;
                }
                break;
            case A_PLAYCARD_FREEROAD:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREEROAD]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_FREEROAD]++;
                break;
            case A_PLAYCARD_KNIGHT:
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 1;
                s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_KNIGHT]--;
                s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_KNIGHT]++;
                bl.recalcLargestArmy(s);
            // flow to next case! 
            case A_PLACEROBBER:
                s[OFS_ROBBERPLACE] = a[1];
                if ((a[2]!=-1) && a[3]!=-1)
                {
                    if(s[OFS_PLAYERDATA[a[2]] + OFS_RESOURCES + a[3]] > 0)
                    {
                    	s[OFS_PLAYERDATA[a[2]] + OFS_RESOURCES + a[3]]--;
                        s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[3]]++;
                    }
                }
                break;
            case A_PAYTAX:
            	// If we don't check every time we use selectMostUselessResourceInHand sometime it return -1;
            	// Tax Paying in the orginal game of SmartSettler is wrong
            	/*for(int ind_player = 0; ind_player < NPLAYERS; ind_player++){
            		
            		int totalCardForEachPlayer = this.getTotalResourceCount(ind_player);
            		//System.out.printf("Total Resource Card: %d\n", totalCardForEachPlayer);
            		if(totalCardForEachPlayer > 7){
            			int reduceCard = totalCardForEachPlayer/2;
            			for(int ind_cut=0; ind_cut < reduceCard; ind_cut++){
            				int res_card = this.selectMostUselessResourceInHand(ind_player, s);
            				if(res_card >= 0){
            					s[OFS_PLAYERDATA[a[2]] + OFS_RESOURCES + res_card]--;
            				}
            			}
            		}
            	}*/
                
            	
            	ind = selectMostUselessResourceInHand(pl, s);
                if(ind < 0){
                	break;
                }
                else{
                	if(s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + ind] > 0){
                		s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + ind]--;
                	}
                }
            	break;
            case A_ENDTURN:
                // new cards become old cards
                for (ind=0; ind<NCARDTYPES; ind++)
                {
                    s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + ind] += s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind];
                    s[OFS_PLAYERDATA[pl] + OFS_NEWCARDS + ind] = 0;
                }
                s[OFS_PLAYERDATA[pl] + OFS_HASPLAYEDCARD] = 0;
                break;
            /*case A_TRADING:
            	
            	//Trading with other player with only one at a time
            	// This one should be offer accepted
            	int otherplayer = a[4];
            	s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[2]] -= a[3];
                s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + a[5]] += a[6];
                s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[5]] -= a[6];
                s[OFS_PLAYERDATA[otherplayer] + OFS_RESOURCES + a[2]] += a[3];
                break;
            case A_CONSIDEROFFER:
            	// Call to trading simulation again here to see whether it is good idea to trade
            	
            	break;*/
        }
    }

    public int getTotalResourceCount(int player){
    	int total = 0;
    	for(int ind_res = 0; ind_res < N_RESOURCES; ind_res++){
    		total += bl.state[OFS_PLAYERDATA[player] + OFS_RESOURCES + ind_res];
    	}
    	return total;
    }
    
    // Assuming the card the come first play first
    public void playedCard(int cardType,int pl){
    	
    	for(int j = 0; j < bl.revealCardSoFar[0].length; j++){
			if(bl.revealCardSoFar[pl][j] == cardType){
				bl.keepSince[pl][j] = -1;
				bl.revealCardSoFar[pl][j] = cardType;
			}
		}
    }
    // keep data of time log since when the player keep that particular card
    public int[][] keepSinceAssign(int[][] keepSinceData, int[][] firstlyBought){
    	for(int i = 0; i < keepSinceData.length; i++){
    		for(int j = 0; j < keepSinceData[0].length; j++){
    			if(keepSinceData[i][j] != -1){
    				keepSinceData[i][j] = bl.gamelog.getSize() - firstlyBought[i][j];
    			}
    		}
    	}
    	return keepSinceData;
    }
    
    // only for the player of HMM agent
    public void updateRevealCardFromMyCard(int pl){
    	
    	for(int ind_card = 0; ind_card < NCARDS; ind_card++){
			if(bl.trackingMyCardIndex[pl][ind_card]!= -1){
				bl.revealCardSoFar[pl][ind_card] = bl.trackingMyCardIndex[pl][ind_card] ;
			}
		}
    }

//    public void listPossibleTrade(int[] s){
//    	
//    }
//    private void listDevCardPossibilities(int []s)
//    {
//        int fsmlevel    = s[OFS_FSMLEVEL];
//        int pl          = s[OFS_FSMPLAYER+fsmlevel];
//        int i, j, ind, pl2, val;
//
//        // play devcards
//        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREERESOURCE] >= 1)
//        {
//            i = rnd.nextInt(NRESOURCES);
//            j = rnd.nextInt(NRESOURCES);
//            bl.possibilities.addAction(10.0,A_PLAYCARD_FREERESOURCE, i, j);
//        }
//
//        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_MONOPOLY] >= 1)
//        {
//            i = rnd.nextInt(NRESOURCES);
//            bl.possibilities.addAction(10.0,A_PLAYCARD_MONOPOLY, i);                    
//        }
//
//        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_FREEROAD] >= 1)
//        {
//            bl.possibilities.addAction(10.0,A_PLAYCARD_FREEROAD);                    
//        }
//
//        if (s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_KNIGHT] >= 1)
//        {
//            while (true)
//            {
//                i = rnd.nextInt(N_HEXES);
//                if (bl.hextiles[i].type != TYPE_LAND)
//                    continue;
//                if (i == s[OFS_ROBBERPLACE])
//                    continue;
//                j = rnd.nextInt(6);
//                bl.possibilities.addAction(1.0,A_PLAYCARD_KNIGHT, i, -1);
//                ind = bl.neighborHexVertex[i][j];
//                if (ind!=-1)
//                {
//                    val = s[OFS_VERTICES + ind];
//                    if ((val >= VERTEX_HASSETTLEMENT) && (val< VERTEX_HASSETTLEMENT + NPLAYERS))
//                        pl2 = val - VERTEX_HASSETTLEMENT;
//                    else if ((val >= VERTEX_HASCITY) && (val< VERTEX_HASCITY + NPLAYERS))
//                        pl2 = val - VERTEX_HASCITY;
//                    else
//                        pl2 = -1;
//                    if ((pl2!=-1) && (pl2!=pl))
//                        bl.possibilities.addAction(10.0,A_PLAYCARD_KNIGHT, i, pl2, selectRandomResourceInHand(pl2, s));                                
//                }
//                break;
//            }
//        }
//        
//    }
//
//    public static final int MAXITER = 10;
//
//    public void listPossibilities(int[] s)
//    {
//        int fsmlevel    = s[OFS_FSMLEVEL];
//        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];
//        int pl          = s[OFS_FSMPLAYER+fsmlevel];
//        int i, j, ind, pl2, val;
//        boolean hasneighbor;
//        int iter;
//
//        bl.possibilities.Clear();
//        switch (fsmstate)
//        {
//            case S_SETTLEMENT1:
//            case S_SETTLEMENT2:
//                while (true)
//                {
//                    i = rnd.nextInt(N_VERTICES);
//                    if (s[OFS_VERTICES+i]==0)
//                    {
//                        bl.possibilities.addAction(1.0,A_BUILDSETTLEMENT, i);
//                        break;
//                    }
//                }
//                break;
//            case S_ROAD1:
//            case S_ROAD2:
//                while (true)
//                {
//                    i = rnd.nextInt(6);
//                    ind = bl.neighborVertexEdge[lastvertex][i];
//                    if ((ind != -1) && (s[OFS_EDGES+ind]==0))
//                    {
//                        bl.possibilities.addAction(1.0,A_BUILDROAD, ind);
//                        break;
//                    }
//                }
//                break;
//            case S_BEFOREDICE:
//                bl.possibilities.addAction(10.0,A_THROWDICE);                
//                //TODO: play cards
//                //!!! listDevCardPossibilities(s);
//                break;
//            case S_FREEROAD1:
//            case S_FREEROAD2:
//                for (iter=0; iter<MAXITER; iter++)
//                {
//                    i = rnd.nextInt(N_EDGES);
//                    if (s[OFS_EDGES+i]==EDGE_EMPTY)
//                    {
//                        hasneighbor = false;
//                        for (j=0; j<6; j++)
//                        {
//                            ind = bl.neighborEdgeEdge[i][j];
//                            if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
//                                hasneighbor = true;
//                        }
//                        if (hasneighbor)
//                        {
//                            bl.possibilities.addAction(1.0,A_BUILDROAD,i);                                
//                            break;
//                        }
//                    }
//                }
//                break;
//            case S_PAYTAX:
//                val = 0;
//                for (i=0; i<NRESOURCES; i++)
//                    val += s[OFS_PLAYERDATA[pl] + OFS_RESOURCES + i];
//                if (val>7)
//                    val = val/2;
//                else
//                    val = 0;
//                bl.possibilities.addAction(1.0,A_PAYTAX,val);
//                break;
//            case S_ROBBERAT7:
//                // !!! the same as for playing a knight card, with a different action name
//                while (true)
//                {
//                    i = rnd.nextInt(N_HEXES);
//                    if (bl.hextiles[i].type != TYPE_LAND)
//                        continue;
//                    if (i == s[OFS_ROBBERPLACE])
//                        continue;
//                    bl.possibilities.addAction(1.0,A_PLACEROBBER, i, -1);
//                    j = rnd.nextInt(6);
//                    ind = bl.neighborHexVertex[i][j];
//                    if (ind!=-1)
//                    {
//                        val = s[OFS_VERTICES + ind];
//                        if ((val >= VERTEX_HASSETTLEMENT) && (val< VERTEX_HASSETTLEMENT + NPLAYERS))
//                            pl2 = val - VERTEX_HASSETTLEMENT;
//                        else if ((val >= VERTEX_HASCITY) && (val< VERTEX_HASCITY + NPLAYERS))
//                            pl2 = val - VERTEX_HASCITY;
//                        else
//                            pl2 = -1;
//                        if ((pl2!=-1) && (pl2!=pl))
//                            bl.possibilities.addAction(10.0,A_PLACEROBBER, i, pl2, selectRandomResourceInHand(pl2, s));                                
//                    }
//                    break;
//                }
//                break;
//            case S_NORMAL:
//                bl.possibilities.addAction(1.0,A_ENDTURN);
//                
//                // buy road
//                // TODO: only 13 roads can be built
//                if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WOOD] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_CLAY] >= 1))
//                {
//                    
//                    for (iter=0; iter<MAXITER; iter++)
//                    {
//                        i = rnd.nextInt(N_EDGES);
//                        if (s[OFS_EDGES+i]==EDGE_EMPTY)
//                        {
//                            hasneighbor = false;
//                            for (j=0; j<6; j++)
//                            {
//                                ind = bl.neighborEdgeEdge[i][j];
//                                if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
//                                {
//                                    hasneighbor = true;
//                                }
//                            }
//                            if (hasneighbor)
//                            {
//                                bl.possibilities.addAction(1.0,A_BUILDROAD,i);                                
//                                break;
//                            }
//                        }
//                    }
//                }
//                
//                // buy settlement
//                // TODO: only 5 settlements can be built
//                if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WOOD] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_CLAY] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_SHEEP] >= 1))
//                {
//                    for (iter=0; iter<MAXITER; iter++)
//                    {
//                        i = rnd.nextInt(N_VERTICES);
//                        if (s[OFS_VERTICES+i]==VERTEX_EMPTY) 
//                        {
//                            hasneighbor = false;
//                            for (j=0; j<6; j++)
//                            {
//                                ind = bl.neighborVertexEdge[i][j];
//                                if ((ind!=-1) && (s[OFS_EDGES+ind]==EDGE_OCCUPIED+pl))
//                                    hasneighbor = true;
//                            }
//                            if (hasneighbor)
//                            {
//                                bl.possibilities.addAction(10.0,A_BUILDSETTLEMENT, i);
//                                break;
//                            }
//                        }
//                    }
//                }
//                
//                // buy city
//                if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_STONE] >= 3) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 2) )
//                {
//                    for (iter=0; iter<MAXITER; iter++)
//                    {
//                        i = rnd.nextInt(N_VERTICES);
//                        if (s[OFS_VERTICES+i]==VERTEX_HASSETTLEMENT + pl) 
//                        {
//                            bl.possibilities.addAction(10.0,A_BUILDCITY, i);
//                            break;
//                        }
//                    }
//                }
//
//                // buy devcard
//                if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_STONE] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_WHEAT] >= 1) &&
//                       (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+RES_SHEEP] >= 1) &&
//                       s[OFS_NCARDSGONE] < NCARDS  )
//                {
//                    bl.possibilities.addAction(5.0,A_BUYCARD, bl.cardSequence[s[OFS_NCARDSGONE]]);
//                    // the type of next card is added. It is added only for logging,
//                    // may not be peeked by the player
//                }
//                 
//                
//                listDevCardPossibilities(s);
//                
//                // trade with ports or bank
//                for (iter=0; iter<MAXITER; iter++)
//                {
//                    i = rnd.nextInt(NRESOURCES);
//                    j = rnd.nextInt(NRESOURCES);
//                    if (i==j) continue;
//                    // specific port
//                    if (    (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 2) &&
//                            (s[OFS_PLAYERDATA[pl]+OFS_ACCESSTOPORT+i] == 1) )
//                    {
//                        bl.possibilities.addAction(1.0,A_PORTTRADE, 2, i, 1, j);
//                        break;
//                    }
//                    // misc port
//                    else if (    (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 3) &&
//                            (s[OFS_PLAYERDATA[pl]+OFS_ACCESSTOPORT+NRESOURCES] == 1) )
//                    {
//                        bl.possibilities.addAction(1.0,A_PORTTRADE, 3, i, 1, j);                        
//                        break;
//                    }
//                    // bank
//                    else if (   (s[OFS_PLAYERDATA[pl]+OFS_RESOURCES+i] >= 4) )
//                    {
//                        bl.possibilities.addAction(1.0,A_PORTTRADE, 4, i, 1, j);
//                        break;
//                    }
//                }
//                break;
//        }
//                        
//       
//    }
    
}
