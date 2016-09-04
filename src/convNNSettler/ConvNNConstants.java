package convNNSettler;

public interface ConvNNConstants {
	
	public final int N_PLAYER = 2;
	public final int N_PORT = 18;
	//TODO: Finalize all the number
	public final int OFS_CONVVERTECES = 0;
	public final int OFS_CONVEDGES = 1;
	public final int OFS_PORT = 2;
	
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
	//Layer of ConvNN translate data from the board for player zeor
	public final int OFS_PLAYER_ZERO_VER = 8;
	public final int OFS_PLAYER_ZERO_EDGES = 9;
	public final int OFS_PLAYER_ZERO_RESOURCESSHEEP = 10;
	public final int OFS_PLAYER_ZERO_RESOURCESWOOD = 11;
	public final int OFS_PLAYER_ZERO_RESOURCESCLAY = 12;
	public final int OFS_PLAYER_ZERO_RESOURCESWHEAT = 13;
	public final int OFS_PLAYER_ZERO_RESOURCESSTONE = 14;
	public final int OFS_PLAYER_ZERO_SETTLEMENTS = 15;
	public final int OFS_PLAYER_ZERO_CITIES = 15;
	public final int OFS_PLAYER_ZERO_DEVCARD = 16;
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
	//TODO: Make the data layer for player 3 and 4
	
}
