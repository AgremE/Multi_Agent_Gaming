package convNNSettler;


import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.Edge;
import smartsettlers.boardlayout.GameStateConstants;
import smartsettlers.boardlayout.HexTile;
import smartsettlers.boardlayout.HexTypeConstants;
import smartsettlers.boardlayout.Vertex;

public class TranslationState implements ConvNNConstants{
	//private int[] state; // to store current state from board layout
	private int[] action;// to store action state from board game
	private int[][][] state = new int[48][23][23];
	private Coordinate[] portCoord;
	private Coordinate[] centerofHexTile;
	private BoardLayout bl;
	
	
	final public int N_VERTECESTOHEX = 6;
	
	public TranslationState( int[] action,BoardLayout bl){
		this.action = action;
		this.bl = bl;
		this.initState3D();
		this.centerofHexTile = new Coordinate[19];
		this.portCoord = new Coordinate[18];
		initCoor1D(this.portCoord);
		initCenter();
		initPort(this.portCoord);
	}
	/*
	 * Tranform the state of the game from HEX representation to 2D representation
	 * */
	void initNegOne(int[] state){
		for( int i = 0; i < state.length; i++){
			state[i] = -1;
		}
	}
	// this function use to initialize the array of two dimension with type Coordinate
	void initCoor2D(Coordinate[][] input){
		for(int i = 0; i < input.length; i++){
			for(int j=0; j< input[i].length ; j++){
				input[i][j] = new Coordinate(0,0);
			}
		}
	}
	//
	void initCoor1D(Coordinate[] input){
		for(int i = 0; i < input.length; i++){
			input[i] = new Coordinate(0,0);
		}
	}
	void initState3D(){
		for(int i = 0; i < this.state.length; i++){
			for(int j=0; j<this.state[i].length ; j++){
				for(int k=0; k<this.state[i][j].length; k++){
					this.state[i][j][k] = 0;
				}
				
			}
		}
	}
	//To set the center for the hexagona board to get the ver and edge
	void initCenter(){
		int init_x = 19, init_y = 6;// keep track of the initial position of the board hextail
		//int line = 0; // Just to keep track of the first index of the line that we begin to init
		for(int i = 0; i < this.centerofHexTile.length; i++){
			centerofHexTile[i] = new Coordinate(0,0);
		}
		for(int i = 0; i < 3; i++){
			centerofHexTile[i].setX(init_x);
			centerofHexTile[i].setY(init_y);
			init_y = init_y + 4;
			//line = i;
		}
		init_x = 15;
		init_y = 4;
		//int line2 = 0;
		for( int i = 3; i < 7 ; i++){
			centerofHexTile[i].setX(init_x);
			centerofHexTile[i].setY(init_y);
			init_y = init_y + 4;
			//line2 = i;
		}
		init_x = 11;
		init_y = 2;
		//int line3 = 0;
		for(int i = 7; i < 12; i ++){
			centerofHexTile[i].setX(init_x);
			centerofHexTile[i].setY(init_y);
			init_y = init_y + 4;
			//line3 = i;
		}
		init_x = 7;
		init_y = 2;
		for(int i = 12; i< 16; i++){
			centerofHexTile[i].setX(init_x);
			centerofHexTile[i].setY(init_y);
			init_y = init_y + 4;
			//line = i;
		}
		init_x = 3;
		init_y = 4;
		for( int i = 16; i<19 ; i++){
			centerofHexTile[i].setX(init_x);
			centerofHexTile[i].setY(init_y);
			init_y = init_y + 4;
		}
	}
	//Get the VERTECE from the provided center of the hex
	Coordinate[] getVertex(Coordinate hexCenter){
		
		Coordinate[] vertex = new Coordinate[N_VERTECESTOHEX];
		
		for(int i = 0; i < N_VERTECESTOHEX; i++){
			vertex[i] = new Coordinate(0,0);
		}
		
		vertex[0].setX(hexCenter.getX()+1);
		vertex[0].setY(hexCenter.getY()-2);
		
		vertex[1].setX(hexCenter.getX()-1);
		vertex[1].setY(hexCenter.getY()-2);
		
		vertex[2].setX(hexCenter.getX()-3);
		vertex[2].setY(hexCenter.getY());
		
		vertex[3].setX(hexCenter.getX()-1);
		vertex[3].setY(hexCenter.getY()+2);
		
		vertex[4].setX(hexCenter.getX()+1);
		vertex[4].setY(hexCenter.getY()+2);
		
		vertex[5].setX(hexCenter.getX()+3);
		vertex[5].setY(hexCenter.getY());
		
		return vertex;
	}
	
	//Get the EDGE from the provided center of the hex
	Coordinate[] getEdge(Coordinate hexCenter){
		// There also 6 Edge within one vertex
		Coordinate[] edges = new Coordinate[N_VERTECESTOHEX];
		for(int i = 0; i < N_VERTECESTOHEX; i++){
			edges[i] = new Coordinate(0,0);
		}
		
		edges[0].setX(hexCenter.getX()+2);
		edges[0].setY(hexCenter.getY()-1);
		
		edges[1].setX(hexCenter.getX());
		edges[1].setY(hexCenter.getY()-2);
		
		edges[2].setX(hexCenter.getX()-2);
		edges[2].setY(hexCenter.getY()-1);
		
		edges[3].setX(hexCenter.getX()-2);
		edges[3].setY(hexCenter.getY()-1);
		
		edges[4].setX(hexCenter.getX());
		edges[4].setY(hexCenter.getY()+2);
		
		edges[5].setX(hexCenter.getX()+2);
		edges[5].setY(hexCenter.getY()+1);
		
		return edges;
		
	}
	//To get the state of the game in two dimensional array
	public int[][][] getstate(){
		
		return this.state;
	}
	// Translate the whole state from board layout into state of two dimensional arrays
	public int[][] translateVerHexTo2D(){
		Coordinate[] vertex = null;
		int[][] stateVert = new int[23][23];
		for(int i = 0; i < 23; i++){
			for(int j=0; j<23 ; j++){
				stateVert[i][j] = 0;
			}
		}
		for (int i = 0; i < 19; i++){
			vertex = getVertex(centerofHexTile[i]);
			for(int j = 0; j<vertex.length; j++){
				try{
					stateVert[vertex[j].getX()][vertex[j].getY()] = 1;
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("Here is the error message of "+e.getMessage() );
				} 
			}
		}
		return stateVert;
	}
	public int[][] translateEdgeHexTo2D(){
		Coordinate[] edge = null;
		int[][] stateEdge = new int[23][23];
		for(int i = 0; i < 23; i++){
			for(int j=0; j<23 ; j++){
				stateEdge[i][j] = 0;
			}
		}
		for (int i = 0; i < 19; i++){
			edge = getEdge(centerofHexTile[i]);
			for(int j = 0; j<edge.length; j++){
				try{
					stateEdge[edge[j].getX()][edge[j].getY()] = 1;
				} catch (Exception e) {
					// TODO: handle exception
					System.err.println("Here is the error with message of "+e.getMessage() );
				} 
			}
		}
		return stateEdge;
	}
	// This is hard coding translation from board of smartSettler to convNNsettler
	public void initPort(Coordinate[] ports){
		
		ports[0].setX(20);
		ports[0].setY(10);

		ports[1].setX(20);
		ports[1].setY(12);
		
		ports[2].setX(18);
		ports[2].setY(16);

		ports[3].setX(16);
		ports[3].setY(18);

		ports[4].setX(12);
		ports[4].setY(20);

		ports[5].setX(10);
		ports[5].setY(22);

		ports[6].setX(6);
		ports[6].setY(22);

		ports[7].setX(4);
		ports[7].setY(20);

		ports[8].setX(2);
		ports[8].setY(16);
		
		ports[9].setX(2);
		ports[9].setY(14);
		
		ports[10].setX(2);
		ports[10].setY(8);

		ports[11].setX(2);
		ports[11].setY(6);
		
		ports[12].setX(4);
		ports[12].setY(2);
		
		ports[13].setX(6);
		ports[13].setY(0);
		
		ports[14].setX(10);
		ports[14].setY(0);
		
		ports[15].setX(12);
		ports[15].setY(2);
		
		ports[16].setX(16);
		ports[16].setY(4);
		
		ports[17].setX(18);
		ports[17].setY(6);
		
	}
	// Verteces number is being init right here for each vertece there are three corresponding number
	public void init_vertecesNUM(){
		
	}
	int[][] translatePlayerData(int player_number){
		int[][] state = new int[23][23];
		return state;
	}
	// Translate the whole state input into three dimensional array with dimension of [23][23][16]
	//TODO: Translate all the resources with its type and player data into this 2 dimensional array
	// Change this function to no return statement
	public int[][][] translateFromHEXto2D(){
		Coordinate[] vertex = null;
		Coordinate[] edge = null;
		/*for(int i = 0 ; i < this.centerofHexTile.length; i++){
			System.out.print("X :"+this.centerofHexTile[i].getX() + " Y:"+this.centerofHexTile[i].getY());
		}*/
		// To protect the orignal state of the game that pass by bl
		int[] cloneState = this.bl.cloneOfState(bl.state);
		for (int i = 0; i < BoardLayout.N_HEXES; i++){
			
			vertex = getVertex(centerofHexTile[i]);
			edge = getEdge(centerofHexTile[i]);
			
			if( i < 18){
				try{
					this.state[OFS_CONVEDGES][this.centerofHexTile[i].getX()][this.centerofHexTile[i].getY()] = bl.hextiles[bl.LAND_START_INDEX + i].subtype;
					
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("Vertex: " + this.centerofHexTile[i].getX() + " , " + this.centerofHexTile[i].getY());
				}
				//System.out.println("index :" + i + " land type: " + bl.hexnumberSequence[i] );
			}
			
			//this.state[OFS_CONVEDGES][centerofHexTile[i].getX()][centerofHexTile[i].getY()] = bl.hexnumberSequence[i];
			for(int j = 0; j<vertex.length; j++){
				try{
					this.state[OFS_CONVVERTECES][vertex[j].getX()][vertex[j].getY()] = 1;
					this.state[OFS_CONVEDGES][edge[j].getX()][edge[j].getY()] = 1;
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println("Vertex: " + vertex[j].getX() + " , " + vertex[j].getY());
					}
				}
			
			// We can improve a bit of performent if we include the for loop inside the case
			/*
			 * WE start translate the board game representation with VERTECES and edge and what kind of production will it produce with
			 * with that specific VERTECES because each VERTECES can produce three different type of resource that why I slipt it into layer
			 * of resource
			 * */
			//Translate all resource into layer of CNN
			
			switch(bl.hextiles[BoardLayout.LAND_START_INDEX + i].subtype){
			
				case HexTypeConstants.LAND_SHEEP:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESSHEEP ][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_SHEEP;
					}
					break;
				case HexTypeConstants.LAND_WOOD:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESWOOD ][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_WOOD;}
					break;
				case HexTypeConstants.LAND_CLAY:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESCLAY ][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_CLAY;}
					break;
				case HexTypeConstants.LAND_WHEAT:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESWHEAT ][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_WHEAT;}
					break;
				case HexTypeConstants.LAND_STONE:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESSTONE ][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_STONE;}
					break;
				case HexTypeConstants.LAND_DESERT:
					for(int j = 0; j<vertex.length; j++){
						this.state[OFS_RESOURCESNOTHIN][vertex[j].getX()][vertex[j].getY()] = HexTypeConstants.LAND_DESERT;}
					break;
				default:
					System.out.println(bl.landSequence[i]);
					break;
			/*Finish translate the resource types, verteces and edges into CNN layers*/	
			/*Initialize the production number layer of Convolutional Neural network*/
				
			}

		}
		// Initialize the port coordinate and type of port from the bl
		// "NOTICE": It have to be after the array is being shuffle
		// 18 is total number of port
		for(int i = 0; i< N_PORT;i++){
    		if( i % 2 == 0){
    			int port_type = bl.hextiles[BoardLayout.PORT_START_INDEX+i/2].subtype;
        		this.state[OFS_PORT][this.portCoord[i].getX()][this.portCoord[i].getY()] = port_type;
        		this.state[OFS_PORT][this.portCoord[i+1].getX()][this.portCoord[i+1].getY()] = port_type;

    		}
    	}
		/*Finish Translate port*/
		//Translate the vertices with city or settlerment
		for(int i = 0; i < GameStateConstants.N_VERTICES; i++){
			
			int player_settlerment = bl.state[GameStateConstants.OFS_VERTICES+i];
			
			if(player_settlerment != 0){
				Vertex vertiecs = bl.vertices[i];
				int hex_index = vertiecs.hex_index;
				vertex = getVertex(centerofHexTile[hex_index]);
				int player = player_settlerment - GameStateConstants.VERTEX_HASSETTLEMENT;
				if(player < 2){
					switch (player) {
					case 0:
						this.state[OFS_PLAYER_ZERO_SETTLEMENTS]
								[vertex[vertiecs.n_vertex_to_hex].getX()]
										[vertex[vertiecs.n_vertex_to_hex].getY()] = 1;
						break;
					case 1:
						this.state[OFS_PLAYER_ONE_SETTLEMENTS]
								[vertex[vertiecs.n_vertex_to_hex].getX()]
										[vertex[vertiecs.n_vertex_to_hex].getY()] = 1;
						break;
					default:
						break;
					}
				}
				else{
					player = player_settlerment - GameStateConstants.VERTEX_HASCITY;
					switch (player) {
					case 0:
						this.state[OFS_PLAYER_ONE_CITIES]
								[vertex[vertiecs.n_vertex_to_hex].getX()]
										[vertex[vertiecs.n_vertex_to_hex].getY()] = 1;
						break;
					case 1:
						this.state[OFS_PLAYER_ONE_CITIES]
								[vertex[vertiecs.n_vertex_to_hex].getX()]
										[vertex[vertiecs.n_vertex_to_hex].getY()] = 1;
						break;
					default:
						break;
					}
				}
			}
		}
		/*Finis the translation part from city, settlement into ConvNN*/
		
		/*Translate the edge into ConvNN*/
		for(int i = 0; i < GameStateConstants.N_EDGES; i++){
			
			int player_settlerment = bl.state[GameStateConstants.OFS_EDGES+i];
			
			if(player_settlerment != GameStateConstants.EDGE_EMPTY){
				Edge get_edge = bl.edges[i];
				int hex_index = get_edge.hex_index;
				Coordinate[] edges = getEdge(centerofHexTile[hex_index]);
				int player = player_settlerment - GameStateConstants.EDGE_OCCUPIED;
				switch (player) {
					case 0:
						this.state[ConvNNConstants.OFS_PLAYER_ZERO_EDGES]
								[edges[get_edge.edge_to_hex_index].getX()]
										[edges[get_edge.edge_to_hex_index].getY()] = 1;
						break;
					case 1:
						this.state[ConvNNConstants.OFS_PLAYER_ONE_EDGES]
								[edges[get_edge.edge_to_hex_index].getX()]
										[edges[get_edge.edge_to_hex_index].getY()] = 1;
						break;
					default:
						break;
				}
			}
		}
		/*Finish*/
		
		/*Start translating resource type*/
		//TODO: Translate resource produce with vertex with also the layer with resource produce since
		/*Put all the resource into vertex center of the hex when that number produce*/
		
		/*Finish the resource translation*/
		
		/*The development card will represent into two dimensional state*/
		//TODO: Translate the development card state
		
		
		/*Start to translate the player data into Layer I decide to write another function to help out this part*/
		
 		return this.state;
	}

}
