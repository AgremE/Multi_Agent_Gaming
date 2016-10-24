package convNNSettler;
/*
 * Makara Phav @ Agreme
 * */
public class Coordinate {
	private int x;
	private int y;
	private int type;
	public Coordinate(){
		this.x = 0;
		this.y = 0;
		this.type = 0;
	}
	public Coordinate(int x, int y, int type){
		this.x = x;
		this.y = y;
		this.type = type;
	}
	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public int getType(){
		return this.type;
	}
	public void setX(int x){
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	public void setType(int type){
		this.type = type;
	}
	
}
