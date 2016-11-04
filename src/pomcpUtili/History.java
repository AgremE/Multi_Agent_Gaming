package pomcpUtili;

import java.util.Vector;
/*
 * Makara Phav @ Agreme
 * */
public class History {

	Vector<Entry> history;
	
	public History(Entry node){
		history = new Vector<>();
		history.add(node);
		
	}
	public History(){
		history = new Vector<>();
	}
	public void addNode(Entry node){
		history.add(node);
	}
	public void resize(int size){
		history.setSize(size);
	}
	public void clear(){
		history.clear();
	}
	public int size(){
		return history.size();
	}
	public Entry back(){
		if(history.size() == 0){
			return null;
		}
		return history.lastElement();
	}
	public Entry pop(){
		Entry node = history.lastElement();
		history.remove(node);
		return node;
	}
}
class Entry{
	int[] action;
	int[] observation;
	
	public Entry(int[] action, int[] observation){
		
		this.action = action;
		this.observation = observation;
		
	}
}
