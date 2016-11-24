package hmmUtitli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import smartsettlers.boardlayout.BoardLayout;
import smartsettlers.boardlayout.GameStateConstants;

/*
 * Author: Agreme@MakaraPhav
 * */
public class HMMUtili implements HMMConstance, GameStateConstants{

	BoardLayout bl;
	
	// Current guess will store all the data 
	int[][] currentGuessing;
	// Model the HMM (transitional matrix)
	int[][] HMM_TRANSITIONALMATRIX;
	// Model the HMM (hiddent State guessing)
	int[][] HMM_CONDITIONALPRO;
	
	// Model the prior
	int[] prior;
	
	public HMMUtili(BoardLayout bl){
		
		this.bl = bl;
		currentGuessing = new int[NPLAYERS][N_DEVCARDTYPES];
		HMM_TRANSITIONALMATRIX = new int[N_DEVCARDTYPES][N_DEVCARDTYPES];
		HMM_CONDITIONALPRO = new int[N_DEVCARDTYPES][22];
		prior = new int[N_DEVCARDTYPES];
		
	}
	
	public void readDataHMM(){
		try{
			File file = new File("C:\\Users\\AILAB\\Documents\\gameStatistic.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileReader fw = new FileReader(file.getAbsoluteFile());
			while(){
				
			}
			fw.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("There is an error file reading from HMM agent: " + e.getMessage());
		}
	}
	
	public void updateHMMGuessing(){
		
	}
	
	public void updatePrior(){
		
	}
	
	public void updateHMMB(){
		
	}
}
