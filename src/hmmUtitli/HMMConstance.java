package hmmUtitli;
/*
 * Author: Agreme@MakaraPhav
 * */
public interface HMMConstance{
	
	// Represent card in index of the matrix form 
	public final int CARD_STATE = 0;
	
	// Represent time stem in index of matrix form
	/* from State 1 of 0->4
	 *  	State 2 of 5
	 * 		State 3 of 6
	 * 		State 4 of 7
	 * 		State 5 of 8
	 * 		until State 12 of 15
	 * 		State 12 of 15 -> 19
	 * 		State 13 of 20 -> 24
	 * 		State 14 of 25 -> 30
	 * 		State 15 of 30 -> 40
	 * 		State 16 of 40 -> 50	
 	 */
	public final int TIMESTATE = 0;// 0->4
	final int TIMESTATESTEP = 16;
	
}
