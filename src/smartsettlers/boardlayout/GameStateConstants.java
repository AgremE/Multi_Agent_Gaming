/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package smartsettlers.boardlayout;

import java.awt.Color;

/**
 *
 * @author szityu
 */
public interface GameStateConstants extends HexTypeConstants {

    int NPLAYERS = 2;// Change into semi zero sum game
    
    int N_VERTICES              = 54;// Number of vertice
    int N_HEXES                 = 19;// Number of hex
    int N_EDGES                 = 72;// Number of Edge
    int N_RESOURCES             = 5; // Number of resources
    int N_DEVCARDTYPES          = 5; // Number Development Cards
    
    // OFS is stand for offset
    // It is just to set the position in the array to specify where to get those information
    // like the represent of the turn and where is the robber place in which hex
    // all of them should be constances
    int OFS_TURN                = 0; // get which turn we are in like rolling dice turn or trading turn but we wont use it anyway
    int OFS_FSMLEVEL            = OFS_TURN          +1 ;// 1 to get the settlement level
    int OFS_FSMSTATE            = OFS_FSMLEVEL      +1 ;// 2 starting position for getting level of state
    int OFS_FSMPLAYER           = OFS_FSMSTATE      +3 ;// 5 starting position for getting the player
    int OFS_NCARDSGONE          = OFS_FSMPLAYER     +3 ;// 8 Starting position of getting the card gone
    int OFS_DIE1                = OFS_NCARDSGONE    +1 ;// 9 to get the result of dice number 1
    int OFS_DIE2                = OFS_DIE1          +1 ;// 10 to get the result of dice number 2
    int OFS_ROBBERPLACE         = OFS_DIE2          +1 ;// 11 to get the place where is the robber is
    int OFS_LONGESTROAD_AT      = OFS_ROBBERPLACE   +1 ;// 12 to get the longest road position
    int OFS_LARGESTARMY_AT      = OFS_LONGESTROAD_AT   +1 ;//13 to get the largest army
    int OFS_LASTVERTEX          = OFS_LARGESTARMY_AT   +1 ;//14 to get the lastvertex
    int OFS_EDGES               = OFS_LASTVERTEX    +1 ;//15 starting position to get the egde
    int OFS_VERTICES            = OFS_EDGES         +N_EDGES ;// Start position for the vertex number
    
    //int OFS_EDGEACCESSIBLE      = OFS_VERTICES      +N_VERTICES;
    //int OFS_VERTEXACCESSIBLE    = OFS_EDGEACCESSIBLE+N_EDGES;

    int OFS_SCORE               = 0; // get the score from OFS_PLAYERDATA
    int OFS_NSETTLEMENTS        = 1; // get Number of 
    int OFS_NCITIES             = 2;
    int OFS_NROADS              = 3;
    int OFS_PLAYERSLONGESTROAD  = 4;
    int OFS_HASPLAYEDCARD       = 5;
    int OFS_RESOURCES           = OFS_HASPLAYEDCARD   +1;
    int OFS_ACCESSTOPORT        = OFS_RESOURCES     +NRESOURCES;
    int OFS_USEDCARDS           = OFS_ACCESSTOPORT  +(NRESOURCES+1);
    int OFS_OLDCARDS            = OFS_USEDCARDS     +N_DEVCARDTYPES;// Need to hide from other players
    int OFS_NEWCARDS            = OFS_OLDCARDS      +N_DEVCARDTYPES;
    int PLAYERSTATESIZE         = OFS_NEWCARDS      +N_DEVCARDTYPES;
    int[] OFS_PLAYERDATA        = { OFS_VERTICES+N_VERTICES,//WHole game state of the board presenting + player one
                                    OFS_VERTICES+N_VERTICES + PLAYERSTATESIZE,// 
                                    //OFS_VERTICES+N_VERTICES + 2*PLAYERSTATESIZE,
                                    //OFS_VERTICES+N_VERTICES + 3*PLAYERSTATESIZE
                                    };// Change it to semi zero sum game between two players    
    
 // To specific the state information for the game play
    
    int STATESIZE = OFS_VERTICES+N_VERTICES + 2*PLAYERSTATESIZE;// Change it to semi zero sum game between two players
    
 // Number of action you can do in one turn
 // There are 5 action to follow or 5 turns:
    /*
     * 1- Rolling Dice
     * 2- Trading
     * 3- Building
     * 4- PlayDevelopment Card
     * 5- End Turn
     * 
     * */
    int ACTIONSIZE = 15;
    
    // todo: who has longest road, biggest army, where is the robber
    // State of the game is represent in constant number
    int S_GAME                  =  0;
    int S_START                 =  1;
    int S_SETTLEMENT1           =  2;
    int S_ROAD1                 =  3;
    int S_SETTLEMENT2           =  4;
    int S_ROAD2                 =  5;
    //int S_THROWDICE             =  6;
    int S_BEFOREDICE            =  6;
    int S_NORMAL                = 100;
    int S_BUYSETTLEMENT         =  7;
    int S_BUYROAD               =  8;
    int S_BUYCARD               =  9;
    int S_BUYCITY               = 10;
    int S_PAYTAX                = 11;
    int S_KNIGHT                = 12;
    int S_FREEROAD1             = 13;
    int S_FREEROAD2             = 14;
    int S_ROBBERAT7             = 15;
    int S_FINISHED              = 101;
    
            
//    int S_                      = 
    
 // Representing action according to constant number
    
    int A_NOTHING               = 0; 
    int A_BUILDSETTLEMENT       = 1;
    int A_BUILDROAD             = 2;
    int A_BUILDCITY             = 3;
    int A_THROWDICE             = 4;
    int A_ENDTURN               = 5;
    int A_PORTTRADE             = 6;
    int A_BUYCARD               = 7;
    int A_PLAYCARD_KNIGHT       = 8;
    int A_PLAYCARD_FREEROAD     = 9;
    int A_PLAYCARD_FREERESOURCE = 10;
    int A_PLAYCARD_MONOPOLY     = 11;
    int A_PAYTAX                = 12;
    int A_PLACEROBBER           = 13;
    int A_TRADING				= 14;
    
    
    
    int VERTEX_EMPTY            = 0;
    int VERTEX_TOOCLOSE         = 1;
    int VERTEX_HASSETTLEMENT    = 2; // Vertex already have settlement+player number
    int VERTEX_HASCITY          = 6; //Vertex has city already+player number
    
    int EDGE_EMPTY              = 0;
    int EDGE_OCCUPIED           = 1; // Edge occupied by road+player number
    
    
    int CARD_KNIGHT             = 0;
    int CARD_ONEPOINT           = 1;
    int CARD_FREEROAD           = 2;
    int CARD_FREERESOURCE       = 3;
    int CARD_MONOPOLY           = 4;
    
    int NCARDTYPES              = 5;
    int NCARDS                  = 25; // Number of development card
    
    String[] resourceNames = {"sheep", "wood", "clay", "wheat", "stone"};
    String[] cardNames = {"knight", "+1 point", "+2 road", "+2 res.", "monopoly"};
    // 
    public final static Color[] playerColor = 
    {
        Color.BLUE,
        Color.RED,
        //Color.WHITE,
        //Color.ORANGE,
    };

}
