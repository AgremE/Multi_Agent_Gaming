/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package smartsettlers.boardlayout;

import java.awt.*;
import smartsettlers.util.*;

/**
 *
 * @author szityu
 */
public class Edge implements HexTypeConstants 
{

    public Vector2d[] endpoints; // It from the smart settler.util
    public Point[] screenCoord;
    public int hex_index;
    public int edge_to_hex_index;
    public boolean isPartOfLongestRoad;
    
    public Edge(Vector2d p1, Vector2d p2, int hex_index, int edge_to_hex_index)
    {
        endpoints  = new Vector2d[2];
        endpoints[0] = p1;
        endpoints[1] = p2;
        this.hex_index = hex_index;
        this.edge_to_hex_index = edge_to_hex_index;
        screenCoord = new Point[2];
        // screenCoord values are set externally by BoardLayout!
    }
}
