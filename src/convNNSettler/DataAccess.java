package convNNSettler;

import java.lang.reflect.Field;

import hdf.hdf5lib.H5;
import hdf.hdf5lib.HDF5Constants;
import hdf.hdf5lib.exceptions.HDF5Exception;
import hdf.hdf5lib.exceptions.HDF5LibraryException;
import smartsettlers.boardlayout.BoardLayout;

/*
 * Aurthor Agreme(@Makara Phav)
 * */

// will store all the data in the form of HDF5 file
public class DataAccess implements ConvNNConstants {

	BoardLayout bl;
    long[] board_dataset_dim;
    long[] card_dataset_dim;
    long[] action_dataset_dim;
    String FILENAME = "SettlerDatasetGamePlay.h5";
    
    public DataAccess(int GameNum){
    	board_dataset_dim = new long[]{BOARD_DIM_X,BOARD_DIM_Y,BOARD_DIM_Z};
        card_dataset_dim = new long[]{CARD_DIM_X, CARD_DIM_Y};
        action_dataset_dim = new long[]{ACTION_DIM};
        this.createFile(GameNum);
	}
    
    // check the validity of the board dimension before store it in HDF file
	private boolean validateBoardData(int[][][] input){
		if(input.length != BOARD_DIM_X){
			return false;
		}else if(input[0].length!=BOARD_DIM_Y){
			return false;
		}else if(input[0][0].length != BOARD_DIM_Z){
			return false;
		}
		return true;
	}
	// Check the validity of the card dimension before store it in HDF file
	private boolean validateCardData(int[][] input){
		if(input.length!=CARD_DIM_X){
			return false;
		}else if(input[0].length!=CARD_DIM_Y){
			return false;
		}
		return true;
	}
	// chekc the validity of the action dimension before store it in HDF file
	private boolean validateActionData(int[] input){
		if(input.length != ACTION_DIM){
			return false;
		}
		return true;
	}
    // Create file assuming it is only create file and group that are essential for file heirachy (Assuming that after create we close it directly)
    public void createFile(int GameNum){
    	
    	int file_id = -1;
    	try {
			file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			if(file_id>=0){
				this.createGroupGameNum(GameNum);
				H5.H5Fclose(file_id);
				return;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
    	try {
			file_id = H5.H5Fcreate(FILENAME, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			if(file_id >=0){
				this.createGroupGameNum(GameNum);
				H5.H5Fclose(file_id);
				//System.out.print("testeing");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    }
    //Assuming that it is close right after it is created
    public void createGroupGameNum(int GameNum){
    	int file_id = -1;
    	int group_game_num = -1;
    	if(file_id < 0){
    		try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
				group_game_num = H5.H5Gcreate(file_id, "/" + GROUP_GAME_NUM + GameNum, HDF5Constants.H5P_DEFAULT,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >=0){
					H5.H5Gclose(group_game_num);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		
    	}
    	else{
    		try {
				group_game_num = H5.H5Gcreate(file_id, "/" + GROUP_GAME_NUM + GameNum, HDF5Constants.H5P_DEFAULT,
				        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);

				if(group_game_num >=0){
					H5.H5Gclose(group_game_num);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    	}
    }
    public void createGroupGameStep(int GameNum, int GameStep){
    	int file_id = -1;
    	int group_game_num = -1;
    	int group_game_step = -1;
    	if(file_id < 0){
    		try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
				group_game_num = H5.H5Gopen(file_id, "/"+GROUP_GAME_NUM+GameNum, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >= 0 ){
	    			group_game_step = H5.H5Gcreate(file_id, "/" + GROUP_GAME_NUM +GameNum + "/"+ GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT,
	                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	    			if(group_game_step >=0){
	    				H5.H5Gclose(group_game_step);
	    			}
	    		}
				if(group_game_num >= 0){
					H5.H5Gclose(group_game_num);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		
    	}else{
    		try {
				group_game_num = H5.H5Gopen(file_id, "/"+GROUP_GAME_NUM+GameNum, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >= 0 ){
					String groupPath = "/" + GROUP_GAME_NUM +GameNum + "/"+ GROUP_GAME_STEP+GameStep;
	    			group_game_step = H5.H5Gcreate(file_id, groupPath, HDF5Constants.H5P_DEFAULT,
	                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	    			if(group_game_step >=0){
	    				H5.H5Gclose(group_game_step);
	    			}
	    		}
				if(group_game_num >= 0){
					H5.H5Gclose(group_game_num);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    	}
    }
    /*
    public void createGroupGameBoardInfo(int GameNum, int GameStep){
    	if(file_id < 0){
    		try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
				group_game_num = H5.H5Gopen((int)file_id, "/"+GROUP_GAME_NUM+GameNum, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >= 0 ){
	    			group_game_step = H5.H5Gcreate((int)file_id, "/" + GROUP_GAME_NUM +GameNum + "/"+ GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT,
	                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	    		}
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	}
    }
    public void createGroupGameCardInfo(){
    	
    }
    public void createGroupGameActionInfo(){
    	
    }*/
    
	public int[][][] readBoardDataSet(int GameNum, int GameStep) {

        int[][][] board_dset = new int[BOARD_DIM_X][BOARD_DIM_Y][BOARD_DIM_Z];
        int file_id = -1;
        int group_game_step = -1;
        int dataset_board_id = -1;
        // Open a file.
        try {
        	if(file_id < 0){
        		file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Open a Group of game step from specific game number
        try{
        	if(file_id >= 0){
        		group_game_step = H5.H5Gopen(file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
        	}
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Open dataset inside group of Board Game
        try {
            if ((file_id >= 0) && (group_game_step >= 0))
            	dataset_board_id = H5.H5Dopen(group_game_step, DATASET_BOARD_INFO, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        
        try{
        	if(dataset_board_id >= 0){
        		H5.H5Dread((int)dataset_board_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, board_dset);
        	}
        }catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
		}
        try {
			H5.H5Fclose(file_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Gclose(group_game_step);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Dclose(dataset_board_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return board_dset;
        
    }
	// Read card game info at specific game number and game step
	public int[][] readCardData(int GameNum, int GameStep){
		int[][] card_dataset = new int[CARD_DIM_X][CARD_DIM_Y];
		int file_id = -1;
		int group_game_step = -1;
		int dataset_card_id = -1;
        // Open a file.
        try {
        	if(file_id < 0){
        		file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Open a Group of game step from specific game number
        try{
        	if(file_id >= 0){
        		group_game_step= H5.H5Gopen((int)file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
        	}
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Open dataset inside group of card game
        try {
            if ((file_id >= 0) && (group_game_step >= 0))
            	dataset_card_id = H5.H5Dopen((int)group_game_step, DATASET_CARD_INFO, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try{
        	if(dataset_card_id >= 0){
        		H5.H5Dread((int)dataset_card_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, card_dataset);
        	}
        }catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
		}

        try {
			H5.H5Fclose(file_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Gclose(group_game_step);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Dclose(dataset_card_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return card_dataset;
	}

	public int[] readActionDataSet(int GameNum, int GameStep){
		int[] action_dataset = new int[ACTION_DIM];
		int file_id = -1;
		int group_game_step = -1;
		int dataset_action_id = -1;
        // Open a file.
        try {
        	if(file_id < 0){
        		file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
        	}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Open a Group of game step from specific game number
        /*
        try{
        	if(file_id >= 0){
        		group_game_step = H5.H5Gopen((int)file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
        	}
        }catch (Exception e) {
            e.printStackTrace();
        }*/
        // Open dataset inside group of card game
        try {
            if ((file_id >= 0) && (group_game_step >= 0))
            	dataset_action_id = H5.H5Dopen((int)group_game_step, DATASET_CARD_INFO, HDF5Constants.H5P_DEFAULT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try{
        	if(dataset_action_id >= 0){
        		H5.H5Dread((int)dataset_action_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, action_dataset);
        	}
        }catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
		}
        
        try {
			H5.H5Fclose(file_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Gclose(group_game_step);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Dclose(dataset_action_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return action_dataset;
	}
	
	//write data into board dataset assuming that we need to create new dataset within this function
	public int writeBoardDataset(int[][][] input, int GameNum, int GameStep){

		int status = -1;
		int dataspace_id = -1;
		int file_id = -1;
		int group_game_step = -1;
		int dataset_board_id = -1;
		if(!validateBoardData(input)){
			System.out.println("Wrong Dimension Board");
			return status = -1;
		}
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) {
				System.out.println("File Open Error Board");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(file_id >= 0){
			try{
				group_game_step = H5.H5Gopen(file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		try {
			dataspace_id = H5.H5Screate_simple(3, board_dataset_dim, null);
		} catch (Exception e1) {
			System.out.println("Dataspace Create Error Board");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ((file_id >= 0) && (dataspace_id >=0)){
			try {
				dataset_board_id = H5.H5Dcreate(group_game_step, DATASET_BOARD_INFO, HDF5Constants.H5T_STD_I32BE,
                		dataspace_id, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	        	System.out.println("Dataset Create Error Board");
	            e.printStackTrace();
	        }
		}
		if(dataset_board_id >=0){
			try {
				status = H5.H5Dwrite(dataset_board_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        try {
			H5.H5Fclose(file_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Gclose(group_game_step);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Dclose(dataset_board_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	//Write data into card data set
	public int writeCardDataSet(int[][] input, int GameNum, int GameStep){

		int status = -1;
		int dataspace_id = -1;
		int file_id= -1;
		int group_game_step = -1;
		int dataset_card_id = -1;
		if(!validateCardData(input)){
			System.out.println("Wrong Card Dimension");
			return status=-1;
		}
		
		//Create dataspace for dataset with dim of 2
		
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		try {
			dataspace_id = H5.H5Screate_simple(2,card_dataset_dim,null);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(file_id >= 0){
			try{
				group_game_step = H5.H5Gopen(file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if ((file_id >= 0) && (dataspace_id >= 0)){
			try {
				dataset_card_id = H5.H5Dcreate(group_game_step, 
									 DATASET_CARD_INFO,
										HDF5Constants.H5T_STD_I32BE, dataspace_id, 
											HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	        	System.out.println("Dataset Create Error Card");
	            e.printStackTrace();
	        }
		}
		if(dataset_card_id >=0){
			try {
				status = H5.H5Dwrite(dataset_card_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL,
									HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
			} catch (Exception e) {
				System.out.println("Dataset Write Error Board");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		 try {
				H5.H5Fclose(file_id);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				H5.H5Gclose(group_game_step);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				H5.H5Dclose(dataset_card_id);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return status;
	}
	public int writeActionDataSet(int[] input, int GameNum, int GameStep){

		int status = -1;
		int dataspace_id = -1;
		int file_id = -1;
		int dataset_action_id = -1;
		int group_game_step = -1;
		
		if(!validateActionData(input)){
			System.out.println("Wrong Dimension Action");
			return status=-1;
		}
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (Exception e) {
				System.out.println("File Open Error Action");
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		/*
		if(file_id >= 0){
			try{
				group_game_step = H5.H5Gopen((int)file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}*/
		
		try {
			dataspace_id = H5.H5Screate_simple(1, action_dataset_dim, null);
		} catch (Exception e1) {
			System.out.println("Dataspace Create Error Action");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(file_id >= 0){
			try{
				group_game_step = H5.H5Gopen(file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if ((file_id >= 0) && (dataspace_id >= 0)){
			try {
				dataset_action_id = H5.H5Dcreate(group_game_step, 
						DATASET_ACITON_INFO, 
								HDF5Constants.H5T_STD_I32BE, dataspace_id, HDF5Constants.H5P_DEFAULT, 
										HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	        	System.out.println("Dataset Create Error Action");
	            e.printStackTrace();
	        }
		}
		if(dataset_action_id >=0){
			try {
				status = H5.H5Dwrite((int)dataset_action_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
				
			} catch (Exception e) {
				System.out.println("Write Data Error Action");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			H5.H5Fclose(file_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Gclose(group_game_step);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			H5.H5Dclose(dataset_action_id);
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
		
}
