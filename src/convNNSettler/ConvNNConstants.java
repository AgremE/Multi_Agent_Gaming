package convNNSettler;

/*
 * Makara Phav @ Agreme
 * */

public interface ConvNNConstants {
	
	final int N_PLAYER = 2;
	final int N_PORT = 18;
	//TODO: Finalize all the number
	final int OFS_CONVVERTECES = 0;
	final int OFS_CONVEDGES = 1;
	final int OFS_PORT = 2;
	final int TOTAL_LAND_TILE = 19;
	final int NUM_VER_TO_HEX = 6;
	final int NUM_EDGE_TO_HEX = 6;
	
	int COVN_N_VERTICES              = 54;// Number of vertice
	int COVN_N_EDGES                 = 72;// Number of Edge
	int COVN_N_RESOURCES             = 5; // Number of resources
	int COVN_N_DEVCARDTYPES          = 5; // Number Development Cards
	
	/* There are six types of ports we can have on the board present int the constant below
	int PORT_SHEEP	: 
	int PORT_WOOD 	:
	int PORT_CLAY   :
	int PORT_WHEAT	:
	int PORT_STONE	:	
	int PORT_MISC   :
*/
	public final int OFS_RESOURCESSHEEP = 3;
	public final int OFS_RESOURCESWOOD = 4;
	public final int OFS_RESOURCESCLAY = 5;
	public final int OFS_RESOURCESWHEAT = 6;
	public final int OFS_RESOURCESSTONE = 7;
	public final int OFS_RESOURCESNOTHIN = 8;
	public final int OFS_VERTECES_NUMONE = 8;
	public final int OFS_VERTECES_NUMTWO = 9;
	public final int OFS_VERTECES_NUMFOUR = 11;
	public final int OFS_VERTECES_NUMFIVE = 10;
	public final int OFS_VERTECES_NUMSIX = 10;
	public final int OFS_VERTECES_NUMSEVEN = 10;
	public final int OFS_VERTECES_NUMEIGHT = 10;
	public final int OFS_VERTECES_NUMNINE = 10;
	public final int OFS_VERTECES_NUMTEN = 10;
	public final int OFS_VERTECES_NUMELEVEN = 10;
	public final int OFS_VERTECES_NUMTWELVE= 10;
	public final int OFS_RESOURCE_PRODUCE_SINCE = 10;
	
	//Translate the whole player data into ConvNN form of input
	
    int OFS_COVN_VER = 8;
	int OFS_COVN_EDGE = OFS_COVN_VER + 1;
	int OFS_COVN_RES = OFS_COVN_EDGE + COVN_N_RESOURCES;
	int OFS_COVN_VERPRODUCTION_SINCE = OFS_COVN_RES + COVN_N_RESOURCES;
	int OFS_COVN_ROAD = OFS_COVN_VERPRODUCTION_SINCE + 1;
	int OFS_COVN_SETTLEMENTS = OFS_COVN_ROAD + 1;
	int OFS_COVN_CITIES = OFS_COVN_SETTLEMENTS + 1;
	int OFS_COVN_DEVCARD = OFS_COVN_CITIES + 1;
	int COVN_PLAYERSTATESIZE         = OFS_COVN_DEVCARD + COVN_N_DEVCARDTYPES;
    
    
    int[] OFS_COVN_PLAYERDATA        = { COVN_PLAYERSTATESIZE,//Whole game state of the board presenting + player one
    								2*COVN_PLAYERSTATESIZE,// 
                                    //OFS_VERTICES+N_VERTICES + 2*PLAYERSTATESIZE
                                    //OFS_VERTICES+N_VERTICES + 3*PLAYERSTATESIZE
                                    };// Change it to semi zero sum game between two players
}
    /*
	
	//Layer of ConvNN translate data from the board for player 1
	//TODO: Think about how can you represent the development card in this kind of state
	public final int OFS_PLAYER_ONE_VER = 8;
	public final int OFS_PLAYER_ONE_EDGES = 9;
	public final int OFS_PLAYER_ONE_RESOURCESSHEEP = 10;
	public final int OFS_PLAYER_ONE_RESOURCESWOOD = 11;
	public final int OFS_PLAYER_ONE_RESOURCESCLAY = 12;
	public final int OFS_PLAYER_ONE_RESOURCESWHEAT = 13;
	public final int OFS_PLAYER_ONE_RESOURCESSTONE = 14;
	public final int OFS_PLAYER_ONE_SETTLEMENTS = 15;
	public final int OFS_PLAYER_ONE_CITIES = 15;
	public final int OFS_PLAYER_ONE_DEVCARD = 16;
	//Layer of ConvNN translate data from the board for player 2
	public final int OFS_PLAYER_TWO_VER = 15;
	public final int OFS_PLAYER_TWO_EDGES = 16;
	public final int OFS_PLAYER_TWO_RESOURCESSHEEP = 17;
	public final int OFS_PLAYER_TWO_RESOURCESWOOD = 18;
	public final int OFS_PLAYER_TWO_RESOURCESCLAY = 19;
	public final int OFS_PLAYER_TWO_RESOURCESWHEAT = 20;
	public final int OFS_PLAYER_TWO_RESOURCESSTONE = 21;
	public final int OFS_PLAYER_TWO_SETTLEMENTS = 15;
	public final int OFS_PLAYER_TWO_CITIES = 15;
	public final int OFS_PLAYER_TWO_DEVCARD = 16;
	//TODO: Make the data layer for player 3
	public final int OFS_PLAYER_THREE_VER = 15;
	public final int OFS_PLAYER_THREE_EDGES = 16;
	public final int OFS_PLAYER_THREE_RESOURCESSHEEP = 17;
	public final int OFS_PLAYER_THREE_RESOURCESWOOD = 18;
	public final int OFS_PLAYER_THREE_RESOURCESCLAY = 19;
	public final int OFS_PLAYER_THREE_RESOURCESWHEAT = 20;
	public final int OFS_PLAYER_THREE_RESOURCESSTONE = 21;
	public final int OFS_PLAYER_THREE_SETTLEMENTS = 15;
	public final int OFS_PLAYER_THREE_CITIES = 15;
	public final int OFS_PLAYER_THREE_DEVCARD = 16;
	*/

