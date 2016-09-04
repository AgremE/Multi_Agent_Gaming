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
public class Vertex implements HexTypeConstants 
{

    public Vector2d centerpoint;
    public Point screenCoord;
    
    public int debugLRstatus;
    //Help variable to get coordinate of vertex from SmartSettler to ConvNNSettler
    public int hex_index;
    public int n_vertex_to_hex;
    public Vertex(Vector2d p, int hex_index, int n_vertex_to_hex)
    {
        centerpoint = p;
        this.hex_index = hex_index;
        this.n_vertex_to_hex = n_vertex_to_hex;
//        screenCoord = new Point[2];
        // screenCoord values are set externally by BoardLayout!
    }
}
