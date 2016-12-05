/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 
package smartsettlers.boardlayout;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import smartsettlers.player.*;
import smartsettlers.util.*;
import tradingPOMDPs.TradingAction;
import tradingPOMDPs.TradingUtil;
import uct.TreeNode;
import uct.UCT;
import convNNSettler.*;
import hmmUtitli.HMMUtili;


/**
 *
 * @author szityu
 */

/*Improve by Agreme@(Makara Phav)*/
// if working on HMM change the player at players[] initialization
public class BoardLayout implements HexTypeConstants, VectorConstants, GameStateConstants, ConvNNConstants
{
	//Help parameters for POMCP
	public boolean wrong = false;
	public int[] eachPlayerCardNotReveal = new int[NPLAYERS];// done
	public int[] newlyBoughtCardEachPlayer = new int[NPLAYERS];// done
	public int[][] eachPlayerCardPlaiedThisRound = new int[NPLAYERS][N_DEVCARDTYPES];// done
	public int numberCardBoughtThisRound = 0;// done
	public int card_play_this_round = 0 ;
	public boolean hiddenInfo = false;
	public int guessingRight = 0;
	public int guessingWrong = 0;
	
	//Not sure what the use of this
	public int[][][] playingCardTimeStamp = new int[NPLAYERS][N_DEVCARDTYPES][NCARDS];
	public int[][][] buyingCardTimeStamp = new int[NPLAYERS][N_DEVCARDTYPES][NCARDS];
	// Using this one to construct the data time frame for the conditional probability
	public int[][][] playingAveragingTime = new int[NPLAYERS][N_DEVCARDTYPES][NCARDS];//Storing inside the data.txt
	public int[][] cardPlayingTimetimeStamp = new int[N_DEVCARDTYPES][STATE_TIME_REPRESENTATION];
	
	// Help function for HMM Player
	// Assumption about the playing time
	// the oldest card will play first in case of duplicate file
	public int[][] keepSince = new int[NPLAYERS][NCARDS];// For tracking the time since player bought it
	public int[][] firstBought = new int[NPLAYERS][NCARDS]; // For tracking the time when it firstly bought
	public int[][] revealCardSoFar = new int[NPLAYERS][NCARDS]; // store card type that has been reveal so far
	//public int[][] playerDevCardData = new int[NPLAYERS][NCARDS];
	public int[][] trackingMyCardIndex = new int[NPLAYERS][NCARDS];
	public int winner = -1;
	// Constructing the likelihood data
	public int[][][] storeData = new int[NPLAYERS][NCARDS][N_DEVCARDTYPES]; 
	public HMMUtili hmmPredictor = new HMMUtili(this);
	
	
	// Help parameters for MM player
	public int[][] playingCardTransition = new int[N_DEVCARDTYPES][N_DEVCARDTYPES];

	
	// Help Parameters for Simulation of trading
	public int NUM_IT = 1000;
	public int MAX_HEAP = 1000;
	public int[] currentProductionNumber = new int[19];
	
	//For translating the production into ConvNN input form
	public int[][][] production_since = new int[N_PLAYER][N_VERTICES][N_RESOURCES];
	public int[][] total_production_since = new int[N_VERTICES][N_RESOURCES];
	
	//END
	
    public static final int[][] PORT_COORD = {
        { 3, 0, 1},
        { 5, 0, 2},
        { 6, 1, 2},
        { 6, 3, 3},
        { 4, 5, 4},
        { 2, 6, 4},
        { 0, 6, 5},
        { 0, 4, 0},
        { 1, 2, 0}
    };
    public static final int[][] SEA_COORD = {
        { 4, 0, -1},
        { 6, 0, -1},
        { 6, 2, -1},
        { 5, 4, -1},
        { 3, 6, -1},
        { 1, 6, -1},
        { 0, 5, -1},
        { 0, 3, -1},
        { 2, 1, -1}
    };
    public static final int[][] LAND_COORD = {
    		//Description on the land coordinate will be present in the documentation
    	    { 3, 1, -1},
    	    { 4, 1, -1},
    	    { 5, 1, -1},
    	    { 2, 2, -1},
    	    { 3, 2, -1},
    	    { 4, 2, -1},
    	    { 5, 2, -1},
    	    { 1, 3, -1},
    	    { 2, 3, -1},
    	    { 3, 3, -1},
    	    { 4, 3, -1},
    	    { 5, 3, -1},
    	    { 1, 4, -1},
    	    { 2, 4, -1},
    	    { 3, 4, -1},
    	    { 4, 4, -1},
    	    { 1, 5, -1},
    	    { 2, 5, -1},
    	    { 3, 5, -1}
    	};
    public static final int N_LAND_TILES = LAND_COORD.length;
    public static final int N_SEA_TILES  = SEA_COORD.length;
    public static final int N_PORT_TILES = PORT_COORD.length;
    public static final int N_TILES = N_LAND_TILES + N_SEA_TILES + N_PORT_TILES;
    
    public static final int LAND_START_INDEX = 0;
    public static final int SEA_START_INDEX  = LAND_START_INDEX + N_LAND_TILES;
    public static final int PORT_START_INDEX = SEA_START_INDEX + N_SEA_TILES;
    
    // The representation of the board in smart of settler is different from the Jsettler which represent
	// The board in the HEX number then convert it when needed
	
	public static final int MAXX = 7;
    public static final int MAXY = 7;
    
    
    public int[] landSequence = {
        LAND_SHEEP, LAND_SHEEP, LAND_SHEEP, LAND_SHEEP, 
        LAND_WHEAT, LAND_WHEAT, LAND_WHEAT, LAND_WHEAT, 
        LAND_CLAY, LAND_CLAY, LAND_CLAY, 
        LAND_WOOD, LAND_WOOD, LAND_WOOD, LAND_WOOD, 
        LAND_STONE, LAND_STONE, LAND_STONE,
        LAND_DESERT };
    
    public int[] portSequence = {
     PORT_MISC, PORT_MISC, PORT_MISC, PORT_MISC, 
     PORT_SHEEP,
     PORT_WOOD,
     PORT_CLAY,
     PORT_WHEAT,
     PORT_STONE
    };

    
    public int[] cardSequence = {       
        //14
        CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, 
        CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, CARD_KNIGHT, 
        // 5
        CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, CARD_ONEPOINT, 
        // 2, 2, 2
        CARD_MONOPOLY, CARD_MONOPOLY,
        CARD_FREERESOURCE, CARD_FREERESOURCE,
        CARD_FREEROAD, CARD_FREEROAD
    };
    
    public int[] hexnumberSequence = {
        2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
    };
    
    public final int MAX_TRAD_OFFER = 5;
    
    public HexTile[] hextiles;
    public Edge[] edges;
    public Vertex[] vertices;
    public int[][] hexatcoord;
    
    public int[][] neighborHexHex;
    public int[][] neighborVertexVertex;    
    public int[][] neighborHexVertex;
    public int[][] neighborHexEdge;
    public int[][] neighborVertexHex;
    public int[][] neighborVertexEdge;
    public int[][] neighborEdgeEdge;
            
    public Random rnd = new Random();

    public int screenWidth, screenHeight;
    double A[][] = {{1, 0}, {0.5, -0.86602540378443864676372317075294}};
    double offset[] = {-0.5, 6.5};
    public double scale = 20;
    
    public int[] state;
    public int[] action;
    public Player[] player;
    public GameLog gamelog; // All takend action which already decided by MCTS is stored here.
    public boolean isLoggingOn;
    public ActionList possibilities = new ActionList();
    public TradingAction tradingPossibilites = new TradingAction(); // Store all the possible trading option
    public TradingAction goodTrading = new TradingAction();
    
    public static final int MODE_RANDOM = 0;
    public static final int MODE_UCT    = 1;
    public int mode = MODE_UCT;
    
    public UCT uctTree;
    public UCT uctTradinTree;
    private int uctTime = 0;
    private int uctTradingTime = 0;
    
    //Trading Data
    
    public int tradingOffer = 0;
    public int tradingAccepte = 0;
    
    public void setState(int[] s)
    {
        int i;
        for (i=0; i<STATESIZE; i++)
            state[i] = s[i];
    }
    
    public static int[] cloneOfState(int[] s)
    {
        int[] s2 = new int[STATESIZE];
        int i;
        for (i=0; i<STATESIZE; i++)
            s2[i] = s[i];
        return s2;
    }
    
    public void initArrayCardType(int[][] cardDesk){
    	for(int i = 0; i < cardDesk.length; i++){
    		for(int j = 0; j < cardDesk[0].length; j++){
    			cardDesk[i][j] = -1;
    		}
    	}
    }
    
    public void setBoardSize(int screenWidth, int screenHeight, double scale)
    {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        //scale = Math.min(screenHeight / 8.0, screenWidth / 8.0);
        this.scale = scale;
    }

    public BoardLayout(int screenWidth, int screenHeight)
    {
        //setBoardSize(screenWidth, screenHeight);
        //InitBoard();
    }
    //Shuffle all the elements in the array so when we init it, it will be always different
    public void ShuffleIntArray(int[] a)
    {
        int firstvalue;
        int pos1, pos2=0;
        int nSteps = 1000;
        int i;
        int N = a.length;
        
        pos1 = rnd.nextInt(N);
        firstvalue = a[pos1];
        for (i=0; i<nSteps; i++)
        {
            pos2 = rnd.nextInt(N);
            a[pos1] = a[pos2];
            pos1 = pos2;
        }
        a[pos2] = firstvalue;
    }
    
    public void InitBoard()
    {
        int i, j, k;
        HexTile t1, t2, t3;
        int ind1, ind2, ind3;

        
        // create Hex tiles, set screen coordinates,
        // place them on the coordinate system
        
        hextiles = new HexTile[N_TILES];//
        edges = new Edge[N_EDGES];//
        vertices = new Vertex[N_VERTICES];//
        hexatcoord = new int[MAXX][MAXY];//
        neighborHexHex = new int[N_TILES][6];//
        neighborHexVertex = new int[N_TILES][6];//
        neighborHexEdge = new int[N_TILES][6];//
        neighborVertexHex = new int[N_VERTICES][6];//
        neighborVertexVertex = new int[N_VERTICES][6]; //6 directions, but only 3 active: 0,2,4 or 1,3,5
        neighborVertexEdge = new int[N_VERTICES][6];//
        neighborEdgeEdge = new int[N_EDGES][6];//
        
        gamelog = new GameLog();
        state = new int[STATESIZE];
        action = new int[ACTIONSIZE];

        // <editor-fold defaultstate="collapsed" desc="init neighborhood matrices to -1">
        //Intialize all to -1 which mean there is nothing can be play there.
        for (i=0; i<MAXX; i++)
            for (j=0; j<MAXY; j++)
                hexatcoord[i][j] = -1;
        
        for (i=0; i<N_TILES; i++)
            for (j=0; j<6; j++)
            {
                neighborHexHex[i][j] = -1;
                neighborHexVertex[i][j] = -1;
                neighborHexEdge[i][j] = -1;
            }
        
        for (i=0; i<N_VERTICES; i++)
            for (j=0; j<6; j++)
            {
                neighborVertexVertex[i][j] = -1;
                neighborVertexEdge[i][j] = -1;
                neighborVertexHex[i][j] = -1;
            }
        
        for (i=0; i<N_EDGES; i++)
            for (j=0; j<6; j++)
            {
                neighborEdgeEdge[i][j] = -1;
            }
        
        // </editor-fold>
        //Start shuffle the card before assigning the boar sequence of land, port and cards
        ShuffleIntArray(landSequence);
        ShuffleIntArray(portSequence);
        ShuffleIntArray(cardSequence);
        
        // Assigning the board sequence
        //Initilized the the hex index
        for(i=0; i<N_LAND_TILES; i++)
        {
            hextiles[LAND_START_INDEX + i] = new HexTile(LAND_COORD[i][0],LAND_COORD[i][1],landSequence[i],-1);
            hexatcoord[LAND_COORD[i][0]][LAND_COORD[i][1]] = LAND_START_INDEX + i;
            //System.out.print("Land type: " + landSequence[i]);
            
        }
        System.out.println();
        
        for(i=0; i<N_SEA_TILES; i++)
        {
            hextiles[SEA_START_INDEX + i] = new HexTile(SEA_COORD[i][0],SEA_COORD[i][1],SEA,-1);
            hexatcoord[SEA_COORD[i][0]][SEA_COORD[i][1]] = SEA_START_INDEX + i;
        }
        
        for(i=0; i<N_PORT_TILES; i++)
        {
            hextiles[PORT_START_INDEX + i] = new HexTile(PORT_COORD[i][0],PORT_COORD[i][1],portSequence[i],PORT_COORD[i][2]);
            hexatcoord[PORT_COORD[i][0]][PORT_COORD[i][1]] = PORT_START_INDEX + i;
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="create neighborHexHex">
        
        //Delta is parameter which create for finding the surrounding tile of the provided position
        
        int[][] delta =        {{ 1, 0},
                                { 0, 1},
                                {-1, 1},
                                {-1, 0},
                                { 0,-1},
                                { 1,-1}};
        
        int x1, y1, x2, y2;
        //We try to construct the structure of neighbor hood of hex(x,y)
        for (i=0; i<N_TILES; i++)
        {
            x1 = (int) (hextiles[i].pos.x);
            y1 = (int) (hextiles[i].pos.y);
            for (j=0; j<6; j++)
            {
                x2 = x1 + delta[j][0];
                y2 = y1 + delta[j][1];
                
                if ((x2>=0) && (x2<MAXX) && (y2>=0) && (y2<MAXY))
                {
                	ind2 = hexatcoord[x2][y2];// Index for the tile
                }
                //outside of MaxX and MaxY coordinate it is the sea side of the board
                else
                    ind2 = -1;
                ind1 = hexatcoord[x1][y1];
                //Why assing ind1 to i not [x1][y1]
                //ind1 = i;
                if ((ind1 != -1) && (ind2 != -1))
                {
                    neighborHexHex[i][j] = ind2; // Store index of the hex 
                    //System.out.println("Neighbor Hex Tile: " + neighborHexHex[i][j]);
                }
            }
            
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="set hex tile screen coordinates">
        Point p;
        Polygon hexagon;
        
        for (j = 0; j < N_TILES; j++) 
        {
            hexagon = new Polygon();
            
            for (i = 0; i < 6; i++) 
            {
            	// HEX_EDGES is used to calculate the coordinate of of the vertex by provided the center coordinate of hex tile
                p = VectorToScreenCoord(hextiles[j].pos.Add(HEX_EDGES[i]));
                
                hexagon.addPoint(p.x, p.y);
                if(j <= N_LAND_TILES){
                	System.out.print("Hexagon coordinate: "+p.x+" "+p.y+"\n");
                }
            }
            hextiles[j].screenCoord = hexagon;
            hextiles[j].centerScreenCord = VectorToScreenCoord(hextiles[j].pos);// It is the center of each hex tile
            
        }
        // create vertices
        // With total number of vertices is 54
        int nvertices = 0;
        for (i=0; i<MAXX; i++)
        	
            for (j=0; j<MAXY; j++)
            {
                ind1 = hexatcoord[i][j];
                if (i<MAXX-1 && j<MAXY-1)
                {
                    ind2 = hexatcoord[i+1][j];
                    ind3 = hexatcoord[i][j+1];
                    if ((ind1!=-1) && (ind2!=-1) && (ind3!=-1))
                    {
                        t1 = hextiles[ind1]; // Hex Tile 1
                        t2 = hextiles[ind2]; // Hex Tile 2
                        t3 = hextiles[ind3]; // Hex Tile 3

                        if (t1.type == TYPE_LAND || t2.type == TYPE_LAND || t3.type == TYPE_LAND)
                        {
                        	// As we shown before the HEX_EDGES is used to get the specific vertex from provided hex tile
                            vertices[nvertices] = new Vertex(t1.pos.Add(HEX_EDGES[5]),ind1,0);
                            vertices[nvertices].screenCoord = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[0]));
                            //Three connected to form a complete single vertex
                            neighborHexVertex[ind1][0] = nvertices;
                            neighborHexVertex[ind2][2] = nvertices;
                            neighborHexVertex[ind3][4] = nvertices;
                            //Each vertex associate itself with only three hex tile but there are two possbile conifiguration which is shown explicitly
                            // inside document
                            neighborVertexHex[nvertices][3] = ind1;
                            neighborVertexHex[nvertices][5] = ind2;
                            neighborVertexHex[nvertices][1] = ind3;
                            
                            nvertices++;
                            
                        }
                    }
                }
                //System.out.println("nvertices = " + nvertices);
                if (i<MAXX-1 && j>0)
                {
                    ind2 = hexatcoord[i+1][j];
                    ind3 = hexatcoord[i+1][j-1];
                    if ((ind1!=-1) && (ind2!=-1) && (ind3!=-1))
                    {
                        t1 = hextiles[ind1];
                        t2 = hextiles[ind2];
                        t3 = hextiles[ind3];

                        if (t1.type == TYPE_LAND || t2.type == TYPE_LAND || t3.type == TYPE_LAND)
                        {
                        	//We used HEX_EDGES[5] it is just our choice, you can choose whatever number between 0 and 5
                            vertices[nvertices] = new Vertex(t1.pos.Add(HEX_EDGES[5]),ind1,5);
                            vertices[nvertices].screenCoord = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[5]));
                            //Three Hex tile create one complete vertex
                            neighborHexVertex[ind1][5] = nvertices;
                            neighborHexVertex[ind2][3] = nvertices;
                            neighborHexVertex[ind3][1] = nvertices;
                            //Each vertex associate itself with only three hex tile but there are two possbile conifiguration which is shown explicitly
                            // inside document
                            neighborVertexHex[nvertices][2] = ind1;
                            neighborVertexHex[nvertices][0] = ind2;
                            neighborVertexHex[nvertices][4] = ind3;
                            nvertices++;
                        }
                    }
                }
            }
        // </editor-fold>
        //System.out.println(vertices.length);
        // <editor-fold defaultstate="collapsed" desc="compute vertex-vertex neighborhoods, neighborVertexVertex">     
        //
        int v1, v2;
        for (ind1 = 0; ind1<N_TILES; ind1++)
        {
        	//The the description about the vertex neighbor is more convinient to express in picture
        	// We attach the picture with documentation on SmartSettler
            v1 = neighborHexVertex[ind1][0]; v2 = neighborHexVertex[ind1][1];
            if ((v1 != -1) && (v2 != -1))
            {
            	//Some vertex is no used for example the vertex outside the board or the sea
                neighborVertexVertex[v1][2] = v2;
                neighborVertexVertex[v2][5] = v1;
            }

            v1 = neighborHexVertex[ind1][1]; v2 = neighborHexVertex[ind1][2];
            if ((v1 != -1) && (v2 != -1))
            {
                neighborVertexVertex[v1][3] = v2;
                neighborVertexVertex[v2][0] = v1;
            }
            v1 = neighborHexVertex[ind1][2]; v2 = neighborHexVertex[ind1][3];
            if ((v1 != -1) && (v2 != -1))
            {
                neighborVertexVertex[v1][4] = v2;
                neighborVertexVertex[v2][1] = v1;
            }
            v1 = neighborHexVertex[ind1][3]; v2 = neighborHexVertex[ind1][4];
            if ((v1 != -1) && (v2 != -1))
            {
                neighborVertexVertex[v1][5] = v2;
                neighborVertexVertex[v2][2] = v1;
            }
            v1 = neighborHexVertex[ind1][4]; v2 = neighborHexVertex[ind1][5];
            if ((v1 != -1) && (v2 != -1))
            {
                neighborVertexVertex[v1][0] = v2;
                neighborVertexVertex[v2][3] = v1;
            }
            v1 = neighborHexVertex[ind1][5]; v2 = neighborHexVertex[ind1][0];
            if ((v1 != -1) && (v2 != -1))
            {
                neighborVertexVertex[v1][1] = v2;
                neighborVertexVertex[v2][4] = v1;
            }
            
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="create the edges, neighborVertexEdge, neighborHexEdge">
        //create edges
        int nedges = 0;        
        for (i=0; i<MAXX; i++)
            for (j=0; j<MAXY; j++)
            {
            	// Get the hex tile index from specific coordinate6
                ind1 = hexatcoord[i][j];
                // We can work on all the 6 edges of each hex tile but it is un-necessary as we work on the vertex as well.
                // As we work through some of the edges are being duplicate initialized
                if (i<MAXX-1)
                {
                    ind2 = hexatcoord[i+1][j];
                    
                    if ( (ind1!=-1) && (ind2!=-1) )
                    {
                        t1 = hextiles[ind1];
                        t2 = hextiles[ind2];
                        
                        if (t1.type == TYPE_LAND || t2.type == TYPE_LAND)
                        {
                        	// We begin to init with two vertex 0 and 5 to get the edge because it is formed by two vertex position
                            edges[nedges] = new Edge(t1.pos.Add(HEX_EDGES[5]),t1.pos.Add(HEX_EDGES[0]), ind1, 0);

                            edges[nedges].screenCoord[0] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[5]));
                            edges[nedges].screenCoord[1] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[0]));
                            
                            //Get index of vertex number 5 of hex index ind1
                            v1 = neighborHexVertex[ind1][5];
                            neighborVertexEdge[v1][1] = nedges;
                            //Get index of vertex number 0 from hex index ind1
                            // It is the same representation as the vertex but use for edge
                            v1 = neighborHexVertex[ind1][0];
                            neighborVertexEdge[v1][4] = nedges;
                            // Hex Share the common edge
                            neighborHexEdge[ind1][0] = nedges;
                            neighborHexEdge[ind2][3] = nedges;
                            
                            nedges++;
                        }
                            
                    }
                }

                if (j<MAXY-1)
                {
                    ind2 = hexatcoord[i][j+1];
                    if ( (ind1!=-1) && (ind2!=-1) )
                    {
                        t1 = hextiles[ind1];
                        t2 = hextiles[ind2];
                        if (t1.type == TYPE_LAND || t2.type == TYPE_LAND)
                        {
                            edges[nedges] = new Edge(t1.pos.Add(HEX_EDGES[0]),t1.pos.Add(HEX_EDGES[1]),ind1,1);
                            edges[nedges].screenCoord[0] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[0]));
                            edges[nedges].screenCoord[1] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[1]));
                            v1 = neighborHexVertex[ind1][0];
                            neighborVertexEdge[v1][2] = nedges;
                            v1 = neighborHexVertex[ind1][1];
                            neighborVertexEdge[v1][5] = nedges;
                            neighborHexEdge[ind1][1] = nedges;
                            neighborHexEdge[ind2][4] = nedges;
                            nedges++;
                        }
                    }
                }
                if (i>0 && j<MAXY-1)
                {
                    ind2 = hexatcoord[i-1][j+1];
                    if ( (ind1!=-1) && (ind2!=-1) )
                    {
                        t1 = hextiles[ind1];
                        t2 = hextiles[ind2];
                        if (t1.type == TYPE_LAND || t2.type == TYPE_LAND)
                        {
                            edges[nedges] = new Edge(t1.pos.Add(HEX_EDGES[1]),t1.pos.Add(HEX_EDGES[2]),ind1,2);
                            edges[nedges].screenCoord[0] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[1]));
                            edges[nedges].screenCoord[1] = VectorToScreenCoord(t1.pos.Add(HEX_EDGES[2]));
                            v1 = neighborHexVertex[ind1][1];
                            neighborVertexEdge[v1][3] = nedges;
                            v1 = neighborHexVertex[ind1][2];
                            neighborVertexEdge[v1][0] = nedges;
                            neighborHexEdge[ind1][2] = nedges;
                            neighborHexEdge[ind2][5] = nedges;
                            nedges++;
                        }
                    }
                }
                
            }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="neighborEdgeEdge">
        // Just init like above number
        // They run on the number of vertex because the we already init the variable neighborVertexEdge
        
        for (i=0; i<N_VERTICES; i++)
        {
            for (j=0; j<6; j++)
                for (k=0; k<6; k++)
                {
                    ind1 = neighborVertexEdge[i][j];
                    ind2 = neighborVertexEdge[i][k];
                    if ((ind1!=-1) && (ind2 !=-1))
                    {
                        neighborEdgeEdge[ind1][k] = ind2;
                        neighborEdgeEdge[ind2][j] = ind1;
                    }
                }
        }
        
        for(int t = 0; t<6;t++){
        	//System.out.println("Hex to Vertex : " + neighborHexVertex[LAND_START_INDEX+1][t]);
        	//System.out.println("Edge Num: "+" to Hex : " + neighborEdgeEdge[9][t]);
        }

        //printArray(portSequence);
        InitProductionNumbers();
        //Check the good condition of the board 
        betterBoard();
        /*
        TranslationState test = new TranslationState(action, this);
        int[][][] testState = test.getTheRestTranslationofTheBoard();
        test.getResourceandDevelopmentCardsTranslation();
        
        for(int x = 0; x < testState[1].length; x++){
        	try{
        		for(int y = 0; y < testState[1][x].length; y++){
        			System.out.print(testState[ConvNNConstants.OFS_CONVVERTECES][x][y] + " ");
            	}
        	}catch (Exception e) {
				// TODO: handle exception
        		System.err.println("The error Messeange"+e.getMessage()+" with " + testState[x][8]);
			}
        	System.out.print("\n");
        }
        
        int seqind = 0;
        for (int x=0; x<MAXX; x++)
            for (int y=0; y<MAXY; y++)
            {	
                int ind = hexatcoord[x][y];
                if ((ind != -1) && hextiles[ind].type == TYPE_LAND && hextiles[ind].subtype != LAND_DESERT)
                {
                	System.out.println("LAND at X "+ x + " and Y "+ y+ " with Production Number of " + hexnumberSequence[seqind]+" and land production is " + hextiles[hexatcoord[x][y]].subtype );
                    hextiles[ind].productionNumber = hexnumberSequence[seqind];
                    seqind++;
                }
                
            }*/
        
        NewGame(state, true); //todo: remove this.
        
        uctTree = new UCT();
        uctTradinTree = new UCT();
//        int [] a1 = {1, 2, 3};
//        int [] a2 = {1, 2, 3};
//        int [] a3 = {1, 1, 1, 1};
        
//        tree.addState(a1,10);
//        System.out.printf("UCT TEST %d  %d   %d \n", tree.getValue(a1), tree.getValue(a2), tree.getValue(a3));
    }
    //Add on function: to swap the hex index in case of colliding port type and land type
    void SwapHexTile(HexTile[] hextiles, int f_ind,int s_ind){
    	
    	HexTile tem = hextiles[f_ind];
    	hextiles[f_ind]= hextiles[s_ind];
    	hextiles[s_ind] = tem;
    	
    }
    // To check whether the provided port have the same type with the provided port type 
    // port and portType not necessary the same port
    boolean checkPortLand(int portInd,int port_type){
    	
    	for(int i = 0; i <6;i++){
    		int land = neighborHexHex[portInd][i];
    		if((land != -1) && (hextiles[land].subtype == port_type)){
    			return false;
    		}
    	}
    	return true;
    	
    }
    // check the goodness condition of the board
    void betterBoard(){
    	
    	for(int port = 0; port<N_PORT_TILES;port++){
    		int port_type = hextiles[PORT_START_INDEX+port].subtype;
    		int port_index = PORT_START_INDEX+port;
    		if (port_type == PORT_MISC){
    			continue;
    		}
    		else{
    			//System.out.println("Test Port type Positive");
    			
    			for(int land_close_port = 0; land_close_port<6;land_close_port++){
    				int ind_land = neighborHexHex[port_index][land_close_port];
    				//Check type of land production and port exchange to make them different
    				// the different between port type and land type is 6
    				if((ind_land!=-1)
    						&&(port_type == hextiles[ind_land].subtype-6)){
    					
    					int produ_num = hextiles[ind_land].productionNumber;
    					if(produ_num == 6 || produ_num == 8 || produ_num == 5)
    					{
    						for(int port_2 = 0; port_2<N_PORT_TILES;port_2++)
    						{
    							int port2_ind = PORT_START_INDEX + port_2;
    							HexTile tem_port = hextiles[port2_ind];
    							if((tem_port.subtype != port_type)&&(checkPortLand(port2_ind, port_type)))
    							{
    								SwapHexTile(hextiles, PORT_START_INDEX + port, PORT_START_INDEX+ port_2);
    								break;
    							}
    						}
    					}
    				}
    			}
    		}
    		// How you connect the port with the land
    	}
    }
    
    void InitProductionNumbers()
    {
        boolean goodarrangement = false;
        int x, y, k, ind, ind2;// ind stand for index 
        int seqind, curreDesSeq;
        boolean desertapply;
        
        while (!goodarrangement)
        {
            
        
            ShuffleIntArray(hexnumberSequence);
            seqind = 0;
            curreDesSeq = 0;
            desertapply = false;
            // deal numberts to hexes
            for (x=0; x<MAXX; x++)
                for (y=0; y<MAXY; y++)
                {	
                    ind = hexatcoord[x][y];
                    if ((ind != -1) && hextiles[ind].type == TYPE_LAND && hextiles[ind].subtype != LAND_DESERT)
                    {
                    	hextiles[ind].productionNumber = hexnumberSequence[seqind];
                    	currentProductionNumber[curreDesSeq] = hexnumberSequence[seqind];
                        seqind++;
                        curreDesSeq++;
                    }
                    if((ind != -1) && (hextiles[ind].type == TYPE_LAND )&& (hextiles[ind].subtype == LAND_DESERT )&& (!desertapply)){
                    	System.out.println("Land Desert Assign");
                    	currentProductionNumber[curreDesSeq] = 1;// Number 1 represent land of desert
                    	curreDesSeq++;
                    	desertapply = true;
                    }
                }
            
            // check if the arrangement is good
            // it is good if no red numbers (6 or 8) are besides each other
            
            goodarrangement = true;
            outerloop:
            for (x=0; x<MAXX; x++)
                for (y=0; y<MAXY; y++)
                {
                    ind = hexatcoord[x][y];
                    for (k=0; k<6; k++)
                    {
                        if (ind != -1) 
                        {
                            ind2 = neighborHexHex[ind][k];
                            if ((ind2 != -1) 
                                    && (hextiles[ind].productionNumber==6 || hextiles[ind].productionNumber == 8) 
                                    && (hextiles[ind2].productionNumber==6 || hextiles[ind2].productionNumber == 8))
                            {
                            	goodarrangement = false;
                                break outerloop;
                            }
                        }
                    }
                }
            
        }
        printArray(currentProductionNumber);
    }
    /*
     * */
    public Point VectorToScreenCoord(Vector2d v)
    {
        Point p = new Point();
        
        p.setLocation(
                scale*(A[0][0]*v.x + A[1][0]*v.y + offset[0])   , 
                scale*(A[0][1]*v.x + A[1][1]*v.y + offset[1])   );
        return p;
    }

    public void NewGame(int[] s, boolean isLoggingOn)
    {
        int pl, i;
        
        this.initArrayCardType(firstBought);
        this.initArrayCardType(revealCardSoFar);
        this.initArrayCardType(trackingMyCardIndex);
        player = new Player[NPLAYERS];
        for (pl=0; pl<NPLAYERS -1   ; pl++)
        {
            player[pl] = new UctPlayer(this, pl);
//            player[pl] = new RandomPlayer(this, pl);
        }
        player[N_PLAYER - 1] = new HMMPlayer(this, N_PLAYER - 1,true);
        // POMCPPlayer player created
        //player[NPLAYERS - 1] = new POMCPPlayer(this,NPLAYERS - 1, true);
        //player[NPLAYERS-1] = new POMCPPlayer(this, NPLAYERS-1);
        
        s = new int[STATESIZE];
        s[OFS_FSMLEVEL] = 0;
        s[OFS_FSMSTATE+0] = S_GAME;
        s[OFS_FSMPLAYER+0] = 0;
        s[OFS_LARGESTARMY_AT] = -1;
        s[OFS_LONGESTROAD_AT] = -1;
        for (i=0; i<N_HEXES; i++)
        {
            if (hextiles[i].subtype == LAND_DESERT)
            {
                s[OFS_ROBBERPLACE] = i;
                break;
            }
        }
        for(int player = 0; player < NPLAYERS; player++){
			for(int ind_type = 0; ind_type < N_DEVCARDTYPES; ind_type++){
				for(int ind_card = 0; ind_card < NCARDS; ind_card++){
					playingCardTimeStamp[player][ind_type][ind_card]=0;
					buyingCardTimeStamp[player][ind_type][ind_card]=0;
					playingAveragingTime[player][ind_type][ind_card]=0;//Storing inside the data.txt
				}
			}
		}
        int[] a = new int[ACTIONSIZE];
        gamelog.size = 0;
        GameTick(s, a);
        setState(s);
        this.isLoggingOn = isLoggingOn;
       
        if (isLoggingOn)
        {
            gamelog.clear();
            gamelog.addState(s);
        }
    }
    public int[][] cardInMyEnermyHand(int myId){

		int[][] guessingDeskCard = new int[NPLAYERS][N_DEVCARDTYPES];
    	for(int pl = 0; pl<NPLAYERS; pl++){
    		if(pl == myId){
    			continue;
    		}
    		else{
    			for(int ind_card = 0; ind_card < N_DEVCARDTYPES; ind_card++){
    				if(this.state[OFS_PLAYERDATA[pl]+OFS_OLDCARDS+ind_card] > 0){
    					guessingDeskCard[pl][ind_card] = this.state[OFS_PLAYERDATA[pl]+OFS_OLDCARDS+ind_card];
    				}
    			}
    		}
    	}
    	return guessingDeskCard;
    }
    public int getTotalHiddenState(int pl){
    	int total_hidden_state = 0;
    	for(int ind_pl = 0;ind_pl < NPLAYERS; ind_pl++){
    		if(pl != ind_pl){
    			for(int ind_type = 0; ind_type<N_DEVCARDTYPES;ind_type++){
            		total_hidden_state += this.state[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS+ind_type];
            	}
    		}
    	}
    	return total_hidden_state;
    }
    public int[] getRealCardBeforePlay(int pl){
    	int totalHiddenCard = getTotalHiddenState(pl);
    	int[] cards = new int[totalHiddenCard];
    	int nextIndex = 0;
    	for(int ind_pl =0; ind_pl < NPLAYERS; ind_pl++){
    		if(pl != ind_pl){
    			for(int ind_type = 0; ind_type < N_DEVCARDTYPES; ind_type++){
    				if(this.state[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS+ind_type]!=0){
    					for(int i = 0; i <nextIndex; i++ ){
    						cards[i] = ind_type;
    					}
    				}
    				nextIndex += state[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS+ind_type];
    			}
    		}
    	}
    	return cards;
    	
    }
    public int[] guessingCorrectBeforePlaying(int[] currentGuessing, int[] realCardBeforePlay){
    	int rightGuessing = 0;
    	int guessingWrong = 0;
    	boolean correct = false;
    	int[] guessingRightAndWrong = new int[2];
    	for(int ind_real = 0; ind_real < realCardBeforePlay.length; ind_real++){
    		for(int ind_guess = 0; ind_guess < currentGuessing.length;ind_guess++){
    			if(currentGuessing[ind_guess] != -1){
    				if(currentGuessing[ind_guess] == realCardBeforePlay[ind_real]){
    					correct = true;
    					currentGuessing[ind_guess] = -1;
    					break;
    				}
    			}
    		}
    		if(correct){

				rightGuessing++;
				correct = false;
    		}
    		else{
    			
    			guessingWrong++;
    		
    		}
    	}
    	guessingRightAndWrong[CARD_GUESSING_RIGHT_INDEX] = rightGuessing;
    	guessingRightAndWrong[CARD_GUESSING_WRONG_INDEX] = guessingWrong;
    	return guessingRightAndWrong;
    }
    
    public int[] guessingCorrectAfterPlaying(int[] currentGuessing, int[] realCardAfterPlay){
    	int rightGuessing = 0;
    	int guessingWrong = 0;
    	boolean correct = false;
    	int[] guessingRightAndWrong = new int[2];
    	for(int i = 0; i < realCardAfterPlay.length; i++){
    		for(int j = 0; j < currentGuessing.length;j++){
    			if(currentGuessing[j] != -1){
    				if(currentGuessing[j] == realCardAfterPlay[i]){
    					correct = true;
    					currentGuessing[j] = -1;
    					break;
    				}
    			}
    		}
    		if(correct){

				rightGuessing++;
				correct = false;
    		}
    		else{
    			guessingWrong++;
    		}
    	}
    	guessingRightAndWrong[CARD_GUESSING_RIGHT_INDEX] = rightGuessing;
    	guessingRightAndWrong[CARD_GUESSING_WRONG_INDEX] = guessingWrong;
    	return guessingRightAndWrong;
    }
    public void GameTick(int[] s, int [] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int statlevel 	= s[OFS_FSMSTATE+ fsmlevel];
        int pl          = s[OFS_FSMPLAYER+ fsmlevel];
        int winCount = 0;
        int[] players_winningRate;
        int[] state = cloneOfState(s);
        int[] trad;
        double[][] currentGuess;
        int[] cardDeskGuessing;
        int state_fml = 0;
        int numTradOffer = 0;
        boolean offer_answer = false;
        int[] guessing = new int[2];
        int[] realCard=null;
        int totalHiddentState = 0;
        
        if(player[pl].isHMMAgent()){
        	if(this.hasHiddenInfo()){
        		// if there is or are hiddens state information about the game, we use this part of program to predict
        		
        		for(int i = 0; i < NPLAYERS; i++){
        			if(i == pl){
        				continue;
        			}
        			else
        			{
        				for(int i_devType = 0; i_devType < N_DEVCARDTYPES; i_devType++){
        					if(state[OFS_PLAYERDATA[i]+OFS_OLDCARDS+i_devType] != 0){
        						totalHiddentState += state[OFS_PLAYERDATA[i]+OFS_OLDCARDS+i_devType];
        					}
        				}
        			}
        		}
        		realCard = new int[totalHiddentState];
        		int index = 0;
        		for(int i = 0; i < NPLAYERS; i++){
        			if(i == pl){
        				continue;
        			}
        			else
        			{
        				for(int i_devType = 0; i_devType < N_DEVCARDTYPES; i_devType++){
        					for(int num_card = 0; num_card< state[OFS_PLAYERDATA[i]+OFS_OLDCARDS+i_devType]; num_card++){
    							realCard[index] = i_devType;
    							index++;
    						}
        				}
        			}
        		}
        		hmmPredictor.updateHMMGuessing(this.gamelog.getSize(),pl,totalHiddentState);

            	currentGuess = hmmPredictor.getCurrentGuess();
            	cardDeskGuessing = hmmPredictor.getCurrentProCardGuess(totalHiddentState);
            	guessing = this.guessingCorrectBeforePlaying(cardDeskGuessing,
            															realCard);
            	this.guessingRight += guessing[CARD_GUESSING_RIGHT_INDEX];
            	this.guessingWrong += guessing[CARD_GUESSING_WRONG_INDEX];
            	state = hideState(pl, state);
            	state = constructHMMGuessState(pl,state,totalHiddentState);
        	}
        	player[pl].listPossibilities(state);
            player[pl].selectAction(state,a);
             
            if (isLoggingOn)
                 gamelog.addAction(a);
             
            player[pl].performAction(s, a);
            stateTransition(s, a);
        }else{
        
        player[pl].listPossibilities(s);
        player[pl].selectAction(s,a);
        
        if (isLoggingOn)
            gamelog.addAction(a);
        
        player[pl].performAction(s, a);
        stateTransition(s, a); 
       }
   
        
       
        //System.out.println("System start trading");
        /*
        if(statlevel == S_NORMAL){
        	
        	//System.out.println("System start trading");
        	state = hideState(pl, state);
        	UCTsimulateTrading(state);
        	player[pl].listTradingOption(s);
        	winCount = uctTradinTree.getWinCount(pl);
        	players_winningRate = uctTradinTree.getWinnersCount();
        	outerloop:
            for(int i =0 ; i < this.tradingPossibilites.n; i++){
            	
                //this.UCTsimulateTrading(state);
            	//System.out.println("Considering Offer");
            	trad = tradingPossibilites.trad[i];
            	for(int ind =0 ; ind < this.tradingPossibilites.n; ind++){
            		System.out.printf("Trading Option: [%d %d %d %d %d %d]\n", tradingPossibilites.trad[ind][0], 
            												   tradingPossibilites.trad[ind][1], 
            												   tradingPossibilites.trad[ind][2], 
            												   tradingPossibilites.trad[ind][3], 
            												   tradingPossibilites.trad[ind][4], 
            												   tradingPossibilites.trad[ind][5]);
            	}
            	try {
					TimeUnit.SECONDS.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	int[] state_trad_simulation = cloneOfState(s);
            	// Chaning a to action of trading posibility
            	changeState(state_trad_simulation, trad);
            	UCTsimulateTrading(state_trad_simulation);
            	TradingUtil tradutil = new TradingUtil(this);
            	int traind = i;
            	
            	if(winCount < uctTradinTree.getWinCount(pl)){
            		tradingOffer++;
            		//System.out.println("Offering");
            		// TODO: Start the trading offer
            		// Wait for offer
            		// Start trading offer accepted otherwise reject
            		// and Start normal simulation with monte carlo
            		// Add list of trading here
            		// It is complete different action from player.performaction
            		// Therefore, we need to make complete different thinking mechanisim for agent to make
            		// decision
            		//trad_action.considerOffer(state, orignalStateChance, pl);
            		
            		for(int player_ind = 0; player_ind < N_PLAYER; player_ind++){
            			
            			if(player_ind != pl){
            				offer_answer = tradutil.consdierOffer(trad,player_ind,players_winningRate[player_ind]);
            				if(offer_answer){
                    			//System.out.println("Accepted");
                    			s = tradutil.applyTrad(s, trad);
                    			tradingAccepte++;
                    			break outerloop; // break the whole nested loop with this line
                    		}
            			}
                		
            		}
            		numTradOffer++;
            		// There is a limit number of offers because the speed of the playing the game
            		
            		if(numTradOffer > MAX_TRAD_OFFER){
            			break;
            		}
            	}
            	
            }
        }*/
        // doing stuff
        // We copy the state from here
        
        // Consider the trade at the beginning first.
        // Loop through the list of possible trading
        
        // Start offer to that specific player:
        
        // We always clean up the list before we continues to work on building up the new list of possibility
        // When we are in the pay tax state that program will perform really bad and the outcomes are unreliable
        /* player[pl].listPossibilities(s);
        player[pl].selectAction(s,a);
        
        if (isLoggingOn)
            gamelog.addAction(a);
        
        player[pl].performAction(s, a);
        stateTransition(s, a);
        
        
        fsmlevel    = s[OFS_FSMLEVEL];
        // Assumming that POMCP is agent number 4 in the list
        if(fsmlevel == 0){
        	if(wrong){
        		try {
					TimeUnit.MINUTES.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	else{
        		wrong = true;
        	}
        }
        if(player[pl].isPOMCP()){
        	clearHELPPOMCP();
        }*/
    }
    
    private int[] constructHMMGuessState(int pl, int[] state2, int totalHiddenState) {
		// TODO Auto-generated method stub
    	int[] currentGuessing = hmmPredictor.getCurrentProCardGuess(totalHiddenState);
    	for(int ind_pl = 0; ind_pl < NPLAYERS; ind_pl++){
    		if(ind_pl == pl){
    			continue;
    		}
    		for(int ind_card = 0; ind_card<totalHiddenState; ind_card++){
    			int type = currentGuessing[ind_card];
    			if(type != -1){
    				state2[OFS_PLAYERDATA[ind_pl]+OFS_OLDCARDS+type]++;
    			}
    		}
    	}
		return state2;
	}

	public static void printArray(int[] s)
    {
        for (int i=0; i<s.length; i++)
            System.out.print(s[i]+" ");
        System.out.println();
    }
    
    public void GameTickUCT(int[] s, int [] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL];
        int pl          = s[OFS_FSMPLAYER+fsmlevel];
        
        if (pl == 0)
        {
            int[] s2 = cloneOfState(s);
            UCTsimulateGame(s2);
            s2=null;
            player[pl].listPossibilities(s);
            int aind = uctTree.selectAction(s, pl, true);
            int i;
            for (i=0; i<a.length; i++)
                a[i] = possibilities.action[aind][i];
        }
        else
        {
            player[pl].listPossibilities(s);
            player[pl].selectAction(s,a); 
        }
        if (isLoggingOn)
            gamelog.addAction(a);
        //TODO: NEED to check whether state transition between those medium is finish
        player[pl].performAction(s, a);

        stateTransition(s, a);
    }

    public int stateTransition(int[] s, int[] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL]; // To access the level of game state
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];// To access the state step
        int pl          = s[OFS_FSMPLAYER+fsmlevel];// To access the player number
        int motherstate;
        if (fsmlevel>0) 
            motherstate = s[OFS_FSMSTATE+fsmlevel-1]; 
        else
            motherstate = -1;
        //System.out.println(fsmlevel);
        switch (fsmstate)
        {
            case S_GAME:
                fsmlevel++;  
                s[OFS_FSMLEVEL] = fsmlevel;
                s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT1;
                break;
            case S_SETTLEMENT1:
                s[OFS_FSMSTATE+fsmlevel] = S_ROAD1;
                break;
            case S_ROAD1:
                if (pl==NPLAYERS-1)
                {
                    pl = NPLAYERS-1;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT2;
                }
                else
                {
                    pl++;
                    s[OFS_FSMPLAYER+fsmlevel] = pl;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT1;
                }
                break;
            case S_SETTLEMENT2:
                s[OFS_FSMSTATE+fsmlevel] = S_ROAD2;
                break;
            case S_ROAD2:
                if (pl==0)
                {
                    pl = 0;
                    s[OFS_FSMSTATE+fsmlevel] = S_BEFOREDICE;
                }
                else
                {
                    pl--;
                    s[OFS_FSMPLAYER+fsmlevel] = pl;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT2;
                }
                break;
            case S_BEFOREDICE:
                if ((a[0]==A_THROWDICE) && (s[OFS_DIE1]+s[OFS_DIE2] != 7) )
                {
                    s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;
                }
                else if ((a[0]==A_THROWDICE) && (s[OFS_DIE1]+s[OFS_DIE2] == 7) )
                {
                    //TODO: place robber, pay tax
                    // s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;
                	
                    fsmlevel++;  
                    s[OFS_FSMLEVEL] = fsmlevel;
                    s[OFS_FSMSTATE + fsmlevel] = S_PAYTAX;
                    s[OFS_FSMPLAYER + fsmlevel] = 0;                    
                }
                break;
                // There is an error in pay tax state of the game
                // I think it should be fine
                /*
                 * It works as follow:
                 * 	Put the child state into S_PAYTAX
                 * 	Each player pay tax
                 * 	Init current player
                 *  Put child state into S_ROBBERAT7
                 *  Decrease State Step to Motherstep
                 *  Put Motherstep or current state step into S_NORMAL
                 * */
            case S_PAYTAX:
                pl++;
                if (pl<NPLAYERS)
                {
                    s[OFS_FSMPLAYER + fsmlevel] = pl;// Each of the four player start to pay tax
                    s[OFS_FSMSTATE + fsmlevel] = S_PAYTAX;                    
                }
                else
                {
                    s[OFS_FSMPLAYER + fsmlevel] = s[OFS_FSMPLAYER + fsmlevel-1];// Current player
                    s[OFS_FSMSTATE + fsmlevel] = S_ROBBERAT7;// Type of current state
                }
                break;
            case S_ROBBERAT7:
                fsmlevel--;  
                s[OFS_FSMLEVEL] = fsmlevel;
                s[OFS_FSMSTATE + fsmlevel] = S_NORMAL;
                break;
            case S_NORMAL:
                switch (a[0])
                {
                    case A_ENDTURN:
                        pl++;
                        if (pl>=NPLAYERS) pl=0; 
                        s[OFS_FSMPLAYER+fsmlevel] = pl;
                        s[OFS_FSMSTATE+fsmlevel] = S_BEFOREDICE;                        
                        break;
                    case A_PLAYCARD_FREEROAD:
                        s[OFS_FSMSTATE+fsmlevel] = S_FREEROAD1;                        
                        break;
                }
                break;
            case S_FREEROAD1:
                    s[OFS_FSMSTATE+fsmlevel] = S_FREEROAD2;                 
                break;
            case S_FREEROAD2:
                    s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;                 
                break;
                    
        }
        recalcScores(s);
        if (getWinner(s) != -1)
        {
            s[OFS_FSMSTATE+fsmlevel] = S_FINISHED;
        }
        
        if (isLoggingOn)
            gamelog.addState(s);
        return fsmstate; 
    }
    /*
     * Change the information of the state according to trading offer
     * */
    public int[] changeState(int[] state_info, int[] chang_info){
    	
    	int[] state_after;
    	int pl = chang_info[0];
    	int other_pl = chang_info[3];
    	state_after = cloneOfState(state_info);
    	state_after[OFS_PLAYERDATA[pl] + OFS_RESOURCES + chang_info[1]] -= chang_info[2];
    	state_after[OFS_PLAYERDATA[pl] + OFS_RESOURCES + chang_info[4]] += chang_info[5];
    	state_after[OFS_PLAYERDATA[other_pl] + OFS_RESOURCES + chang_info[4]] -= chang_info[5];
    	state_after[OFS_PLAYERDATA[other_pl] + OFS_RESOURCES + chang_info[1]] += chang_info[2];
    	return state_after;
    	
    }
    //TODO:
    /*
     * Upon:
     * - S_MAKEOFFER
     * - Make all possible list offer with listpossibleoffer() in Player
     * - Limited offer time is 5 in case of rejection if there any one accepted we just approve it directly
     * - Check respond from JSettler with makeOffer in Jsettler
     * - Wait from respond with function considerOffer in Jsettler
     * */
    public void simulateExistingOptionTrading()
    {
    	int[] clone_stae = this.cloneOfState(this.state);
    	int fsmlevel    = clone_stae[OFS_FSMLEVEL];
        //System.out.println("FSM LEVEL"+fsmlevel);
        int pl          = clone_stae[OFS_FSMPLAYER+fsmlevel];
    	UCTsimulateTrading(clone_stae);
    	double curr_winLoss = uctTradinTree.getAverageWinLose(pl);
    	
    }
    // """Important Function to explain explicitly"""
    // Stimulate about 1000 game step to choose which one give the best result of movement
    // Store data and state from this part for training the neural network
    //TODO: Need to work independence Simulation to work with the tradning
    // Step of the simulation:
    // Clone the state
    // Change the state
    // Simulate it
    //Choose the best
    // This function will use inside the GameTick function to simulate for every trading possible situation under current assumption
    
    public void UCTsimulateTrading(int[] s2){
    	
    	int[] s = null;    
        int[] a = new int[TradingAction.ACTIONSIZE];// ACTIONSIZE is 5

        TreeNode node;
        boolean isKnownState = true;
        int winner;
        int it;
        //s2 with the side of 269
        // Keep trace of this variable because it might hang the process
        boolean oldIsLoggingOn = isLoggingOn;
        isLoggingOn = false;
        uctTradingTime ++;
        uctTradinTree.clearWinner();
        if (uctTradinTree.tree.size()>MAX_HEAP)
        	uctTradinTree.tree.clear();
        
        int fsmlevel    = s2[OFS_FSMLEVEL];
        //System.out.println("FSM LEVEL"+fsmlevel);
        int pl          = s2[OFS_FSMPLAYER+fsmlevel];
//        System.out.printf("!1");
        
        //Calculate all the possible option of action like dev_card, building, or whatever
        player[pl].listPossibilities(s2);
//        System.out.println("OFS_FSMLEVEL"+ OFS_FSMLEVEL +" "+fsmlevel);
//        System.out.printf("!2");
        int N_IT = NUM_IT;
        // Only one action left there is nothing to stimulate there
        if (possibilities.n == 1)
            N_IT = 1;
        
        for(it=0; it<N_IT; it++)
        {
//            if (it%10 == 0)
//                System.out.printf(".");
            isKnownState = true;
            //Close state s2 in order to protect the original state
            s = cloneOfState(s2);
            uctTradinTree.clearTraces();
            int round = 0;
            while (true)
            {
                int hc = UCT.getHashCode(s);
                node = uctTradinTree.getNode(hc);
                
                //System.out.print(node+" ");
                fsmlevel    = s[OFS_FSMLEVEL];
                pl          = s[OFS_FSMPLAYER+fsmlevel];
                //System.out.println("OFS_FSMLEVEL"+ OFS_FSMLEVEL +" "+fsmlevel);
                player[pl].listPossibilities(s);
                int nactions = possibilities.n; //Number of action available
                int aind; //action index (ind is stand for index)

                if ((isKnownState) && (node!=null))
                {
                    // known states
                    //aind = possibilities.randomInd();                
                    aind = uctTradinTree.selectAction(hc,pl,false);
                    uctTradinTree.addTrace(hc, pl, aind);
//        System.out.printf("!7");

                }
                else if ((isKnownState) && (node==null))
                {
                    // first unknown state
                    isKnownState = false;
                    aind = possibilities.randomInd();                
                    uctTradinTree.addState(s,hc, possibilities);
                    uctTradinTree.addTrace(hc, pl, aind);
//        System.out.printf("!8");
                }
                else
                {
                    // further unknown states
                    aind = possibilities.randomInd();                
//        System.out.printf("!9");
                }

                a = possibilities.action[aind];
//        System.out.printf("!5");
                player[pl].performAction_simulation(s, a);
                // Changing state
                // It also changing tht player number at the same time as we use the state transition
                stateTransition(s, a);
                //Initi the winner and loser count here
                //TODO: init the player who win and who lose here from UTC tree
                winner = getWinner(s);
                round++;
                if(round == 1000){
                	break;
                }
                
                if (winner !=-1)
                    break;
            }
            uctTradinTree.update(winner, uctTradingTime);
        }
        // !!! printing takes LOTS of time
        //System.out.println(uctTree);
//        s2[3] = 8;
//        printArray(s);
//        printArray(s2);
        isLoggingOn = oldIsLoggingOn;
        s=null;
        a=null;
//        System.out.printf("!1");
        // Doing the trading before start simulating the game and compare the average outcome
        // after trading if this.max > trading.max doing nothing else call on player.perform
    }
    public void UCTsimulateGame(int[] s2)
    {
        int[] s = null;    
        int[] a = new int[ACTIONSIZE];// ACTIONSIZE is 5

        TreeNode node;
        boolean isKnownState = true;
        int winner;
        int it;
        //s2 with the side of 269
        boolean oldIsLoggingOn = isLoggingOn;
        isLoggingOn = false;
        uctTime ++;
        
        if (uctTree.tree.size()>MAX_HEAP)
            uctTree.tree.clear();
        
        int fsmlevel    = s2[OFS_FSMLEVEL];
        int pl          = s2[OFS_FSMPLAYER+fsmlevel];
        
        //Calculate all the possible option of action like dev_card, building, or whatever
        player[pl].listPossibilities(s2);
        int N_IT = NUM_IT;
        // Only one action left there is nothing to stimulate there
        if (possibilities.n == 1)
            N_IT = 1;
        
        for(it=0; it<N_IT; it++)
        {
            isKnownState = true;
            //Close state s2 in order to protect the original state
            s = cloneOfState(s2);
            uctTree.clearTraces();
            int round = 0;
            while (true)
            {
                int hc = UCT.getHashCode(s);
                node = uctTree.getNode(hc);
                
                //System.out.print(node+" ");
                fsmlevel    = s[OFS_FSMLEVEL];
                pl          = s[OFS_FSMPLAYER+fsmlevel];
                //System.out.println("OFS_FSMLEVEL"+ OFS_FSMLEVEL +" "+fsmlevel);
                player[pl].listPossibilities(s);
                int nactions = possibilities.n; //Number of action available
                int aind; //action index (ind is stand for index)
                // using UCT to improve the greedy in action selection  if the node is not in the tree yet
                if ((isKnownState) && (node!=null))
                {
                    // known states
                    //aind = possibilities.randomInd();                
                    aind = uctTree.selectAction(hc,pl,false);
                    uctTree.addTrace(hc, pl, aind);

                }
                //Add state into the UCT just one time because at the end of the second stage only one onde is added and
                // it is history
                else if ((isKnownState) && (node==null))
                {
                    // first unknown state
                    isKnownState = false;
                    // They doing random rollout policy which might lead to bad outcome
                    aind = possibilities.randomInd();                
                    uctTree.addState(s,hc, possibilities);
                    uctTree.addTrace(hc, pl, aind);
                }
              //Random rollout version of policy
                else
                {
                    // further unknown states
                    aind = possibilities.randomInd(); 
                }

                a = possibilities.action[aind];
                // Sometime the simulation hang in this state
                player[pl].performAction_simulation(s, a);
                // Changing state
                // It also changing tht player number at the same time as we use the state transition
                // We need to define the optimal simulation round so it able to do it job properly
                stateTransition(s, a);
                winner = getWinner(s);
                round++;
                
                if(round == 600){
                	break;
                }
                if (winner !=-1)
                    break;
            }
            uctTree.update(winner, uctTime);
        }
// !!! printing takes LOTS of time
//System.out.println(uctTree);
//        s2[3] = 8;
//        printArray(s);
//        printArray(s2);
        isLoggingOn = oldIsLoggingOn;
        s=null;
        a=null;
    }
    // Need to do POMCP simulation one more as an option for POMCP
    public void POMCPSimulation(int[] state){
    	
    }
    
    
    // Get the winner for the current state
    public int getWinner(int[] s)
    {
        int pl;
        int retval = -1;
        for (pl=0; pl<NPLAYERS;pl++)
        {
            if (s[OFS_PLAYERDATA[pl] + OFS_SCORE] >= 10)
                retval = pl;
        }
        return retval;
    }
          
    int[] auxScoreArray = new int[4];
    // No need to change the recalScores because it is only called in the state transition which need to perform the
    // score calculation later so if we hide the hidden variable at the beginning of simulation it would be fine
    public void recalcScores(int[] s)
    {
        int pl;
        for (pl=0; pl<NPLAYERS; pl++)
        {
            auxScoreArray[pl] = 0;
            auxScoreArray[pl] += s[OFS_PLAYERDATA[pl] + OFS_NSETTLEMENTS];
            auxScoreArray[pl] += s[OFS_PLAYERDATA[pl] + OFS_NCITIES]*2;
            auxScoreArray[pl] += s[OFS_PLAYERDATA[pl] + OFS_OLDCARDS + CARD_ONEPOINT];
        }
        for (pl=0; pl<NPLAYERS; pl++)
        {
            s[OFS_PLAYERDATA[pl] + OFS_SCORE] = auxScoreArray[pl];
        }
        
        pl = s[OFS_LARGESTARMY_AT];
        if (pl!= -1)
            s[OFS_PLAYERDATA[pl] + OFS_SCORE] += 2;
        
        pl = s[OFS_LONGESTROAD_AT];
        if (pl!= -1)
            s[OFS_PLAYERDATA[pl] + OFS_SCORE] += 2;
    }
    
    public void recalcScores()
    {
        recalcScores(state);
    }
    
    
    private static final int LR_EMPTY = 0;
    private static final int LR_UNCHECKED = 1;
    private static final int LR_CHECKED1 = 2;
    private static final int LR_CHECKED2 = 3;
    private static final int LR_CHECKED3 = 4;
    private static final int LR_MAXROUTE = 5;


    boolean isOpponentPresentAtVertex(int []s, int pl, int ind)
    {
        boolean returnval;
        int val = s[OFS_VERTICES + ind];
        if ((val == VERTEX_EMPTY) || (val == VERTEX_TOOCLOSE) || 
                (val == VERTEX_HASSETTLEMENT+pl) || (val == VERTEX_HASCITY+pl))
            returnval = false;
        else returnval = true;
        for (int j = 0; j < 6; j++) 
        {
            val = neighborVertexEdge[ind][j];
            if ((val!=-1) && (s[OFS_EDGES+val] != EDGE_EMPTY)) //opponent has road
                returnval = true;
        }
        return returnval;
    }
    
// It is the function use to searhc the tree for calculating the longest or largest army
void lrDepthFirstSearch(int[] s, int pl, int ind, int []lrVertices, boolean[] lrOpponentPresent,  boolean[] lrPlayerPresent,
        int UNCHECKEDVALUE, int CHECKEDVALUE, int[] returnvalues)
{
    int cind, cpos, i, j;
    int[] lrStack = new int[N_VERTICES];
    int[] lrStackPos = new int[N_VERTICES];
    int lrStacklen;
    boolean foundnext, isstartind;
    int maxlen = 0, maxStartInd = 0;
    int nextind, nextedge;

    nextind = 0; // unnecessary, but otherwise "uninitialized" error

    lrStacklen = 0;
    cind = ind;
    cpos = 0;
    isstartind = true;
    //lrStack[0] = ind;
    do
    {
        //System.out.printf("(%d,%d,%d) ",cind,cpos,lrStacklen);
        foundnext = false;
        //System.out.printf("*");
        isstartind = false;
        lrVertices[cind] = CHECKEDVALUE;
        vertices[cind].debugLRstatus = CHECKEDVALUE;
        // TODO: if search starts in a "broken" OFS_RESOURCESSHEEP, the algorithm believes that it is connected
        if ((cind==ind) || (lrPlayerPresent[cind]) || (!lrOpponentPresent[cind]) )
        {
            for (j=cpos; j<6; j++)
            {
                //System.out.printf(".");
                nextind = neighborVertexVertex[cind][j];
                nextedge = neighborVertexEdge[cind][j];
                if (nextind==-1) 
                    continue;
                if (s[OFS_EDGES + nextedge] != EDGE_OCCUPIED+pl)
                    continue;
                if (lrVertices[nextind]!=UNCHECKEDVALUE)
                    continue;
                foundnext = true;
                lrStack[lrStacklen] = cind;
                lrStackPos[lrStacklen] = j+1;
                lrStacklen++;
                if (lrStacklen>maxlen)
                {
                    maxlen = lrStacklen;
                    maxStartInd = nextind;
                }
                if ((CHECKEDVALUE == LR_CHECKED3) && (maxlen==returnvalues[0]))
                {
                    for (i=0; i<lrStacklen; i++)
                    {
                        vertices[lrStack[i]].debugLRstatus = CHECKEDVALUE;
                        // TODO: implement this correctly
//                        edges[neighborVertexEdge[lrStack[i]][lrStackPos[i]-1]].isPartOfLongestRoad 
//                                = true;
                    }
                    vertices[nextind].debugLRstatus = CHECKEDVALUE;
                    break;
                }
                break;
            }
        }
        if (foundnext)
        {
            cind = nextind;
            cpos = 0;
        }
        else
        {
            if (lrStacklen==0)
                break;
            lrStacklen--;
            cind = lrStack[lrStacklen];
            cpos = lrStackPos[lrStacklen];
        }
        //System.out.printf("x");                    
    } while (lrStacklen>=0);
    returnvalues[0] = maxlen;
    returnvalues[1] = maxStartInd;
    
    lrStack=null;
    lrStackPos=null;
}

public void recalcLongestRoad(int[] s, int pl)
    {
        int ind, cind, cpos, j, k;
        int[] lrVertices = new int[N_VERTICES];
        boolean[] lrOpponentPresent = new boolean[N_VERTICES]; // get the longest road from the opponent
        boolean[] lrPlayerPresent = new boolean[N_VERTICES];
        int[] returnvalues = new int[2];
        int maxlen, maxStartInd = 0;
        int val;
        //int pl;
                
        
        for (ind=0; ind<N_VERTICES; ind++)
            vertices[ind].debugLRstatus = 0;
        for (ind=0; ind<N_EDGES; ind++)
            edges[ind].isPartOfLongestRoad = false;
//        for (pl = 0; pl < NPLAYERS; pl++) 
        {
            for (ind=0; ind<N_VERTICES; ind++)
            {
                //System.out.printf("/%d/",ind);       
                
                lrVertices[ind] = LR_EMPTY;
                lrOpponentPresent[ind] = false;
                val = s[OFS_VERTICES + ind];
                if ((val == VERTEX_EMPTY) || (val == VERTEX_TOOCLOSE))
                    ;
                else if ((val == VERTEX_HASSETTLEMENT+pl) || (val == VERTEX_HASCITY+pl))
                    lrPlayerPresent[ind] = true;
                else 
                    lrOpponentPresent[ind] = true;
                for (j = 0; j < 6; j++) 
                {
                    val = neighborVertexEdge[ind][j];
                    if ((val!=-1) && (s[OFS_EDGES+val] == EDGE_OCCUPIED+pl)) //player has road
                        lrVertices[ind] = LR_UNCHECKED;
//                    else if ((val!=-1) && (s[OFS_EDGES+val] != EDGE_EMPTY)) //opponent has road
//                        lrOpponentPresent[ind] = true;     
                }
            }
            
            
            
            // TODO!!! 6-length cycles counts only as a 5 !!!
            maxlen = 0;
            for (ind=0; ind<N_VERTICES; ind++)
            {
                if (lrVertices[ind]!=LR_UNCHECKED)
                    continue;
                lrDepthFirstSearch(s, pl, ind, lrVertices, lrOpponentPresent, lrPlayerPresent, 
                        LR_UNCHECKED, LR_CHECKED1, returnvalues);
                lrDepthFirstSearch(s, pl, returnvalues[1], lrVertices, lrOpponentPresent, lrPlayerPresent, 
                        LR_CHECKED1, LR_CHECKED2, returnvalues);
                if (maxlen<returnvalues[0])
                {
                    maxlen = returnvalues[0];
                    maxStartInd = returnvalues[1];
                }
            }
//            if (maxlen>0)
//                vertices[maxStartInd].isPartOfLongestRoad = LR_MAXROUTE;
//            maxlen = returnvalues[0];
            
            // the purpose of this call to DFS is to mark the longest road.
            lrDepthFirstSearch(s, pl, maxStartInd, lrVertices, lrOpponentPresent, lrPlayerPresent, 
                        LR_CHECKED2, LR_CHECKED3, returnvalues);
            s[OFS_PLAYERDATA[pl] + OFS_PLAYERSLONGESTROAD] = maxlen;
        }
        
        int maxpl = s[OFS_LONGESTROAD_AT]; // current player with longest road;
        if (maxpl!=-1)
            maxlen = s[OFS_PLAYERDATA[maxpl] + OFS_PLAYERSLONGESTROAD];
        else
            maxlen = 0;
        for (pl=0; pl<NPLAYERS; pl++)
        {
            if (s[OFS_PLAYERDATA[pl] + OFS_PLAYERSLONGESTROAD] > maxlen)
            {
                maxlen = s[OFS_PLAYERDATA[pl] + OFS_PLAYERSLONGESTROAD];
                maxpl = pl;
            }
        }
        if (maxlen>=5)
        {
            s[OFS_LONGESTROAD_AT] = maxpl;
        }
        lrVertices=null;
        lrOpponentPresent=null;
        lrPlayerPresent =null;
        returnvalues =null;
    }
    
    public void recalcLargestArmy(int[]s)
    {
        int pl;
        int largestpl = s[OFS_LARGESTARMY_AT];
        int current;
        
        for (pl=0; pl<NPLAYERS; pl++)
        {
            current = s[OFS_PLAYERDATA[pl] + OFS_USEDCARDS + CARD_KNIGHT];
            if ((largestpl==-1) && (current>=3))
                s[OFS_LARGESTARMY_AT] = pl;
            if ((largestpl!=-1) && (current> s[OFS_PLAYERDATA[largestpl] + OFS_USEDCARDS + CARD_KNIGHT]))
                s[OFS_LARGESTARMY_AT] = pl;            
        }
    }
    
    FileWriter logfile = null;
    
    public void writeLog(int[] st,int[] a, int pl, String gameName, String event)
    {
        System.out.println("log:  " + event);
        if (logfile == null) {
            try {
                String s;
                logfile = new FileWriter(gameName + ".txt");
                s = String.format("I %d \n", st.length);
                logfile.write(s);
            } catch (IOException ex) {
                Logger.getLogger(BoardLayout.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    logfile.close();
                } catch (IOException ex) {
                    Logger.getLogger(BoardLayout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        if (logfile != null) {
            try {
                String s;
                int i;

                logfile = new FileWriter(gameName + ".txt",true);
                logfile.append("C " + event + "\n");
                System.out.println("C " + event);
                s = "A " + pl;
                for (i=0; i<a.length; i++)
                    s = s + " " + a[i];
                logfile.append(s + "\n");
                System.out.println(s);
                s = "S";
                for (i=0; i<st.length; i++)
                    s = s + " " + st[i];
                logfile.append(s + "\n");
                System.out.println(s);
                logfile.close();              
            //D.ebugPrintln("WROTE |"+event+"|");
            } catch (Exception e) {
                System.out.println("log: ERROR "+e);
            }
        }
        
    }
    
    //Get data of how many cards other players have bought
    public int getNumberOfCardPurchase(int pl){
    	
    	int total = 0;
    	
    	for(int i = 0; i < NPLAYERS; i++){
    		
    		if(i == pl){
    			continue;
    		}
    		total += newlyBoughtCardEachPlayer[i];
    	
    	}
    	return total;
    }
    
    //Clear data for POMCP which use to help POMCP player
    public void clearHELPPOMCP(){
    	for(int ind_pl = 0; ind_pl < N_PLAYER; ind_pl++){
    		
    		eachPlayerCardNotReveal[ind_pl] = 0;
    		newlyBoughtCardEachPlayer[ind_pl] = 0;
    		for(int ind_card = 0 ; ind_card < N_DEVCARDTYPES; ind_card++){
    			
    			eachPlayerCardPlaiedThisRound[ind_pl][ind_card] = 0;
    			
    		}
    	}
    	numberCardBoughtThisRound = 0;
    }
    
    //TODO: to return the appropriate state of the game due to player number:
    public int[] getState(int pl, int[] s2){
    	
    	int[] s = s2.clone(); // Clone the state of the environment to protect the original state
    	
    	for(int in_pl = 0; in_pl < NPLAYERS; in_pl++){
    		
    		for( int in_resType = 0; in_resType < N_RESOURCES; in_resType++){
    			
    			s[OFS_FSMLEVEL+OFS_FSMPLAYER] = -1;
    			
    		}
    	}
    	return s;
    }
    
    // checking whether there is hidden information inside the game or not ir not I will just used the UCT state for simulation
    public boolean hasHiddenInfo(){
    	
    	int numberCardinotherPlayerHand = 0;
    	for(int i = 0; i < NPLAYERS ; i++){
    		
    		if(player[i].isHMMAgent()){
    			continue;
    		}
    		for(int ind_card = 0; ind_card < N_DEVCARDTYPES; ind_card++){
    			numberCardinotherPlayerHand += state[OFS_PLAYERDATA[i] + OFS_OLDCARDS + ind_card];
    		}
    	}
    	if(numberCardinotherPlayerHand > 0){
    		return true;
    	}
    	return false;
    	
    }
    
    /*
     * Criterial to hide all the data from other player:
     *  1- Development Card Need to hide
     *  2- Need to hide score of development card from other player
     *  3- init to put number of card into to Total number of Card into total number index of state data
     *  // Only using during the simulation to simulate the real life senario
     * */
    public int[] hideState(int pl, int[] state){
    	
    	int[] s = BoardLayout.cloneOfState(state);
    	
    	for(int i =0; i< N_PLAYER;i++){
    		
    		if(i != pl){
    			//Hide victory point card
    			
    			// Hide other development card
    			for(int DEV_IND = 0; DEV_IND < N_DEVCARDTYPES; DEV_IND ++){
    				s[OFS_PLAYERDATA[i] + OFS_TOTALNDEVCARD] += s[OFS_PLAYERDATA[i] + OFS_NEWCARDS + DEV_IND];
    				s[OFS_PLAYERDATA[i] + OFS_NEWCARDS + DEV_IND] = 0;
    				s[OFS_PLAYERDATA[i] + OFS_TOTALNDEVCARD] += s[OFS_PLAYERDATA[i] + OFS_OLDCARDS + DEV_IND];
        			s[OFS_PLAYERDATA[i] + OFS_OLDCARDS + DEV_IND] = 0;
    			}
    		}
    		
    	}
    	return s;
    	
    }
    public int[] stateActionObservation(int[] s, int[] a)
    {
        int fsmlevel    = s[OFS_FSMLEVEL]; // To access the level of game state
        int fsmstate    = s[OFS_FSMSTATE+fsmlevel];// To access the state step
        int pl          = s[OFS_FSMPLAYER+fsmlevel];// To access the player number
        int motherstate;
        if (fsmlevel>0) 
            motherstate = s[OFS_FSMSTATE+fsmlevel-1]; 
        else
            motherstate = -1;
        //System.out.println(fsmlevel);
        switch (fsmstate)
        {
            case S_GAME:
                fsmlevel++;  
                s[OFS_FSMLEVEL] = fsmlevel;
                s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT1;
                break;
            case S_SETTLEMENT1:
                s[OFS_FSMSTATE+fsmlevel] = S_ROAD1;
                break;
            case S_ROAD1:
                if (pl==NPLAYERS-1)
                {
                    pl = NPLAYERS-1;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT2;
                }
                else
                {
                    pl++;
                    s[OFS_FSMPLAYER+fsmlevel] = pl;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT1;
                }
                break;
            case S_SETTLEMENT2:
                s[OFS_FSMSTATE+fsmlevel] = S_ROAD2;
                break;
            case S_ROAD2:
                if (pl==0)
                {
                    pl = 0;
                    s[OFS_FSMSTATE+fsmlevel] = S_BEFOREDICE;
                }
                else
                {
                    pl--;
                    s[OFS_FSMPLAYER+fsmlevel] = pl;
                    s[OFS_FSMSTATE+fsmlevel] = S_SETTLEMENT2;
                }
                break;
            case S_BEFOREDICE:
                if ((a[0]==A_THROWDICE) && (s[OFS_DIE1]+s[OFS_DIE2] != 7) )
                {
                    s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;
                }
                else if ((a[0]==A_THROWDICE) && (s[OFS_DIE1]+s[OFS_DIE2] == 7) )
                {
                    //TODO: place robber, pay tax
                    // s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;
                	
                    fsmlevel++;  
                    s[OFS_FSMLEVEL] = fsmlevel;
                    s[OFS_FSMSTATE + fsmlevel] = S_PAYTAX;
                    s[OFS_FSMPLAYER + fsmlevel] = 0;                    
                }
                break;
                // There is an error in pay tax state of the game
                // I think it should be fine
                /*
                 * It works as follow:
                 * 	Put the child state into S_PAYTAX
                 * 	Each player pay tax
                 * 	Init current player
                 *  Put child state into S_ROBBERAT7
                 *  Decrease State Step to Motherstep
                 *  Put Motherstep or current state step into S_NORMAL
                 * */
            case S_PAYTAX:
                pl++;
                if (pl<NPLAYERS)
                {
                    s[OFS_FSMPLAYER + fsmlevel] = pl;// Each of the four player start to pay tax
                    s[OFS_FSMSTATE + fsmlevel] = S_PAYTAX;                    
                }
                else
                {
                    s[OFS_FSMPLAYER + fsmlevel] = s[OFS_FSMPLAYER + fsmlevel-1];// Current player
                    s[OFS_FSMSTATE + fsmlevel] = S_ROBBERAT7;// Type of current state
                }
                break;
            case S_ROBBERAT7:
                fsmlevel--;  
                s[OFS_FSMLEVEL] = fsmlevel;
                s[OFS_FSMSTATE + fsmlevel] = S_NORMAL;
                break;
            case S_NORMAL:
                switch (a[0])
                {
                    case A_ENDTURN:
                        pl++;
                        if (pl>=NPLAYERS) pl=0; 
                        s[OFS_FSMPLAYER+fsmlevel] = pl;
                        s[OFS_FSMSTATE+fsmlevel] = S_BEFOREDICE;                        
                        break;
                    case A_PLAYCARD_FREEROAD:
                        s[OFS_FSMSTATE+fsmlevel] = S_FREEROAD1;                        
                        break;
                }
                break;
            case S_FREEROAD1:
                    s[OFS_FSMSTATE+fsmlevel] = S_FREEROAD2;                 
                break;
            case S_FREEROAD2:
                    s[OFS_FSMSTATE+fsmlevel] = S_NORMAL;                 
                break;
                    
        }
        recalcScores(s);
        if (getWinner(s) != -1)
        {
            s[OFS_FSMSTATE+fsmlevel] = S_FINISHED;
        }
        return s; 
    }
    public int[] clearCard(int[] cardStore){
    	for(int i = 0; i < cardStore.length; i++){
    		cardStore[i] = 0;
    	}
    	return cardStore;
    }
    
    //Return the state representation
    public int stateRepresentation(int timeFrame){
    	int state = 0;
    	if((timeFrame >= 0)&&(timeFrame <= 4)){
    		state = 0;
    	}else if((timeFrame >= 5)&&(timeFrame <= 6)){
    		state = 1;
    	}else if((timeFrame >= 6)&&(timeFrame <=7)){
    		state = 2;
    	}else if((timeFrame >= 8)&&(timeFrame <= 9)){
    		state = 3;
    	}else if((timeFrame >= 10)&&(timeFrame <=11)){
    		state = 4;
    	}else if((timeFrame >= 12)&&(timeFrame <= 13)){
    		state = 5;
    	}else if((timeFrame >= 14)&&(timeFrame <= 15)){
    		state = 6;
    	}else if((timeFrame >= 16)&&(timeFrame <= 20)){
    		state = 7;
    	}else if((timeFrame >= 21) && (timeFrame <= 25)){
    		state = 8;
    	}else if((timeFrame >= 26)&&(timeFrame <= 30)){
    		state = 9;
    	}else if((timeFrame >= 31)&&(timeFrame <=40)){
    		state = 10;
    	}else if((timeFrame >= 41)&&(timeFrame<=50)){
    		state = 11;
    	}else if((timeFrame > 50)&&(timeFrame <= 60)){
    		state = 12;
    	}
    	else if((timeFrame > 60)&&(timeFrame <= 70)){
    		state = 13;
    	}else if((timeFrame > 70)&&(timeFrame <= 80)){
    		state = 14;
    	}else if(timeFrame > 80){
    		state = 15;
    	}
    	return state;
    		
    }
    
    public void HMM_MonteCarloSimulation(int[] state){
    	 
    	int[] s = null;    
        int[] a = new int[ACTIONSIZE];// ACTIONSIZE is 5

        TreeNode node;
        boolean isKnownState = true;
        int winner;
        int it;
        //s2 with the side of 269
        boolean oldIsLoggingOn = isLoggingOn;
        isLoggingOn = false;
        uctTime ++;
         
        if (uctTree.tree.size()>MAX_HEAP)
            uctTree.tree.clear();
         
        int fsmlevel    = state[OFS_FSMLEVEL];
        int pl          = state[OFS_FSMPLAYER+fsmlevel];
         
        //Calculate all the possible option of action like dev_card, building, or whatever
        player[pl].listPossibilities(state);
        int current_player = pl;
        int N_IT = NUM_IT;
        // Only one action left there is nothing to stimulate there
        if (possibilities.n == 1)
            N_IT = 1;
        // Divide the state into two step 1- UCB and 2- UCT
        for(it=0; it<N_IT; it++)
        {
            isKnownState = true;
            //Close state s2 in order to protect the original state
            s = cloneOfState(state);
            uctTree.clearTraces();
            int round = 0;
            int first_action = 0;
            boolean random = false;
            while (true)
            {
            	double immReward = 0;
                int hc = UCT.getHashCode(s);
                node = uctTree.getNode(hc);
                 
                //System.out.print(node+" ");
                fsmlevel    = s[OFS_FSMLEVEL];
                int fsmstate    = s[OFS_FSMSTATE+fsmlevel];// To access the state step
                pl          = s[OFS_FSMPLAYER+fsmlevel];
                //System.out.println("OFS_FSMLEVEL"+ OFS_FSMLEVEL +" "+fsmlevel);
                player[pl].listPossibilities(s);
                int nactions = possibilities.n; //Number of action available
                int aind; //action index (ind is stand for index)
                
                //Noted that I change the to add just list of action of current player who doing the simulation
                if ((isKnownState) && (node!=null))
                {             
                    aind = uctTree.selectAction(hc,pl,true,true,possibilities,fsmstate);
                    if(pl == current_player){
                    	uctTree.addTrace(hc, pl, aind);
                    }
                    random = false;
                }
                else if ((isKnownState) && (node==null))
                {
                    // first unknown state
                    isKnownState = false;
                    // They doing random rollout policy which might lead to bad outcome
                    aind = possibilities.randomInd();                
                    if(pl == current_player){
                    	uctTree.addState(s,hc, possibilities);
                        uctTree.addTrace(hc, pl, aind);
                        hc = UCT.getHashCode(s);
                        node = uctTree.getNode(hc);
                    }
                    random = false;
                }
                else
                {
                    // further unknown states
                    aind = possibilities.randomInd();
                    random = true;
                }
                hc = UCT.getHashCode(s);
                a = possibilities.action[aind];
                if(round == 0){
                	uctTree.setFirstAction(hc, aind);
                	first_action = aind;
                }
                if(node != null){
                	immReward = rewardingModel(a[0]);
                    if(uctTree.getNtrace()>0){
                    	try{
                    		uctTree.setFirstAction(hc, aind);
                    		//node.rewardActionStep[uctTree.getNtrace()-1] = immReward;
                    		uctTree.setReward(immReward, uctTree.getNtrace()-1, hc);
                    		//node.nodeReward = immReward;
                    	}catch (Exception e) {
							// TODO: handle exception
                    		System.out.println(node.rewardActionStep.length);
                    		System.out.println(uctTree.getNtrace()-1);
						}
                    }
                    else{
                    	uctTree.setReward(immReward, uctTree.getNtrace(), hc);
                    }
                }
                player[pl].performAction_simulation(s, a);
                stateTransition(s, a);
                winner = getWinner(s);
                
                round++;
                
                if(round == 1000){
                	break;
                }
                if (winner !=-1)
                    break;
             }
            int[] s2 = cloneOfState(state);
            int hc = UCT.getHashCode(s2);
            node = uctTree.getNode(hc);
        	// Update expected reward accordingly
            if(winner == current_player){
            	double expected_reward = uctTree.returnReward(0, true);
            	//node.setExpectedReward(current_player,first_action, expected_reward);
            	uctTree.setExpectedReward(expected_reward, current_player, first_action, hc);
            	//node.nodeReward += expected_reward;
            	uctTree.setReward(expected_reward, hc);
            }
            else{
            	double expected_reward = uctTree.returnReward(0, false);
            	//node.setExpectedReward(current_player, first_action, expected_reward);
            	uctTree.setExpectedReward(expected_reward, current_player, first_action, hc);
            	//node.nodeReward += expected_reward; 
            	uctTree.setReward(expected_reward, hc);
            }
            uctTree.update(winner, uctTime);
            first_action = 0;
        }
        isLoggingOn = oldIsLoggingOn;
        s=null;
        a=null;
    }
    
    
    public double rewardingModel(int action){
		switch(action){
			case GameStateConstants.A_BUILDCITY:
				return 500;
			case GameStateConstants.A_BUILDROAD:
				return 1;
			case GameStateConstants.A_BUILDSETTLEMENT:
				return 200;
			case GameStateConstants.A_BUYCARD:
				return 2;
			case GameStateConstants.A_PAYTAX:
				return -0.5;
			case GameStateConstants.A_NOTHING:
				return 0;
			case GameStateConstants.A_PLACEROBBER:
				return 0;
			case GameStateConstants.A_PLAYCARD_FREERESOURCE:
				return 2;
			case GameStateConstants.A_PLAYCARD_FREEROAD:
				return 2;
			case GameStateConstants.A_PLAYCARD_KNIGHT:
				return 2;
			case GameStateConstants.A_PLAYCARD_MONOPOLY:
				return 2;
			case GameStateConstants.A_THROWDICE:
				return 0;
			default:
				return 0;
		}
	}
    
    // Using in 
    public void final_update_conditionalpro(){
    	for(int ind_card = 0; ind_card < NCARDS; ind_card++){
    		for(int ind_pl = 0; ind_pl < N_PLAYER; ind_pl++){
    			for(int ind_type = 0; ind_type < N_DEVCARDTYPES;ind_type++){
    				if(this.buyingCardTimeStamp[ind_pl][ind_type][ind_card]!=0){
        				this.cardPlayingTimetimeStamp[ind_type]
        													[this.stateRepresentation(this.gamelog.getSize() 
        															- this.buyingCardTimeStamp[ind_pl][ind_type][ind_card])]+=1 ;
        			}
    			}
    		}
    	}
    }
    /*
    public boolean writeDataIntoExecl(int[] data){
    	
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Data_Generation_on_Trading");
        outputFileName = outPut.getAbsolutePath();
        int rownum = 0;
        for (int i = 0; i < dataList.size(); i++) {
            Object[] objArr = dataList.get(i);
            HSSFRow row = sheet.createRow(rownum++);

            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                sheet.autoSizeColumn((short) cellnum);
                if (obj instanceof Date) {
                    cell.setCellValue((Date) obj);
                } else if (obj instanceof Boolean) {
                    cell.setCellValue((Boolean) obj);
                } else if (obj instanceof String) {
                    cell.setCellValue((String) obj);
                } else if (obj instanceof Double) {
                    cell.setCellValue((Double) obj);
                }
            }
        }
        if (outPut.exists()) {
            outPut.delete();
        }
        FileOutputStream out =
                new FileOutputStream(outPut);
        workbook.write(out);
    }*/
}
