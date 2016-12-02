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
	int OFS_COVN_VER = 0;
	int OFS_COVN_EDGE = OFS_COVN_VER + 1;
	int OFS_COVN_RES = 2;
	int OFS_RESOURCESSHEEP = 3;
	int OFS_RESOURCESWOOD = 4;
	int OFS_RESOURCESCLAY = 5;
	int OFS_RESOURCESWHEAT = 6;
	int OFS_RESOURCESSTONE = 7;
	int OFS_RESOURCESNOTHIN = 8;
	/* There are six types of ports we can have on the board present int the constant below
	int PORT_SHEEP	: 
	int PORT_WOOD 	:
	int PORT_CLAY   :
	int PORT_WHEAT	:
	int PORT_STONE	:	
	int PORT_MISC   :
*/
	
	
	//Translate the whole player data into ConvNN form of input
	
    
	int PLAYERDATA_START_INDEX = 9;
	int OFS_COVN_ROAD = PLAYERDATA_START_INDEX + 1;
	int OFS_COVN_SETTLEMENTS = OFS_COVN_ROAD + 1;
	int OFS_COVN_CITIES = OFS_COVN_SETTLEMENTS + 1;
	int OFS_COVN_DEVCARD = OFS_COVN_CITIES + 1;
	int OFS_TILEFIRSTNUMBER = OFS_COVN_DEVCARD + 1;
	int OFS_TILESECONDNUMBER = OFS_TILEFIRSTNUMBER + 1;
	int OFS_TILETHIRDNUMBER = OFS_TILESECONDNUMBER + 1; 
	int OFS_RESOURCE_PRODUCE_SINCE = OFS_TILETHIRDNUMBER + 1;
	int OFS_CONV_PORT = OFS_RESOURCE_PRODUCE_SINCE + 1;
	int COVN_PLAYERSTATESIZE         = OFS_CONV_PORT + 1; // 20 for first player for two player it is 31
    
    int[] OFS_COVN_PLAYERDATA        = { PLAYERDATA_START_INDEX,PLAYERDATA_START_INDEX + COVN_PLAYERSTATESIZE//Whole game state of the board presenting + player one
    								// 
                                    //OFS_VERTICES+N_VERTICES + 2*PLAYERSTATESIZE
                                    //OFS_VERTICES+N_VERTICES + 3*PLAYERSTATESIZE
                                    };// Change it to semi zero sum game between two players
    int CONV_DATASIZE = PLAYERDATA_START_INDEX + 2*COVN_PLAYERSTATESIZE;
    // if want to know the detail on the implementation please refer to code in traslation
    final int BOARD_DIM_X  = 47;
    final int BOARD_DIM_Y = 23;
    final int BOARD_DIM_Z = 23;
    
    final int CARD_DIM_X = 2; // number of player
    final int CARD_DIM_Y = 10;// number of both dev card and res card 
    
    final int ACTION_DIM = 20;
    
    // You can refer to the structure of the file in the documentation
    //final String GROUP_GAME_DATA = "GROUP_GMAE_DATA";
    
    final String GROUP_GAME_NUM = "GROUP_GAME_NUM_";
    
    final String GROUP_GAME_STEP = "GROUP_GAME_SETP_NUM_";
    
    final String GROUP_BOARD_INFO = "GROUP_BOARD_INFO";
  
    final String DATASET_BOARD_INFO = "DATASET_BOARD_INFO_STEP_";// already translate and will store in 3 dimensional array 
   
    final String GROUP_CARD_INFO = "GROUP_CARD_INFO";
   
    final String DATASET_CARD_INFO = "DATASET_CARD_INFO_STEP_"; // already translate and will store in 2 dimensional array
    
    final String GROUP_OUTCOME_INFO = "GROUP_OUTCOME_INFO";
    
    final String DATASET_OUTCOME = "DATASET_OUTCOME_GAMES";// 1 represent win and 0 represent lose for example if store about 1000 game this will len(DATASET) = 1000
    //Dataset in HDF5
    
    final String GROUP_ACTION_INFO = "GROUP_ACTION_INPUT_SETP_";
    
    final String DATASET_ACITON_INFO = "DATASET_ACTION_INFO";// will be in one dimensional array of len = 
    
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

