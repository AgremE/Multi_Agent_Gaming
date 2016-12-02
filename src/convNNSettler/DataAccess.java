package convNNSettler;

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
	long file_id = -1;
    long dataspace_id = -1;
    long dataset_board_id = -1;
    long dataset_card_id = -1;
    long dataset_action_id = -1;
    long group_game_board = -1;
    long group_game_card  = -1;
    long group_game_action = -1;
    long group_game_num = -1;
    long group_game_step = -1;
    long[] board_dataset_dim;
    long[] card_dataset_dim;
    long[] action_dataset_dim;
    String FILENAME = "SettlerDataset1000GamePlay.h5";
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
    public DataAccess(){
    	board_dataset_dim = new long[]{BOARD_DIM_X,BOARD_DIM_Y,BOARD_DIM_Z};
        card_dataset_dim = new long[]{CARD_DIM_X, CARD_DIM_Y};
        action_dataset_dim = new long[]{ACTION_DIM};
	}
    // Create file assuming it is only create file and group that are essential for file heirachy (Assuming that after create we close it directly)
    public void createFile(){
    	
    	try {
			file_id = H5.H5Fcreate(FILENAME, HDF5Constants.H5F_ACC_TRUNC, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
			if(file_id >=0){
				this.closeFile();
			}
		} catch (HDF5LibraryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    //Assuming that it is close right after it is created
    public void createGroupGameNum(int GameNum){
    	if(file_id < 0){
    		try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
				group_game_num = H5.H5Gcreate((int)file_id, "/" + GROUP_GAME_NUM + GameNum, HDF5Constants.H5P_DEFAULT,
                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >=0){
					this.closeGroup((int)group_game_num);
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
    public void createGroupGameStep(int GameNum, int GameStep){
    	if(file_id < 0){
    		try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
				group_game_num = H5.H5Gopen((int)file_id, "/"+GROUP_GAME_NUM+GameNum, HDF5Constants.H5P_DEFAULT);
				if(group_game_num >= 0 ){
	    			group_game_step = H5.H5Gcreate((int)file_id, "/" + GROUP_GAME_NUM +GameNum + "/"+ GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT,
	                        HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	    			if(group_game_step >=0){
	    				this.closeGroup((int)group_game_step);
	    			}
	    		}
				if(group_game_num >= 0){
					this.closeGroup((int)group_game_num);
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
        		group_game_step = H5.H5Gopen((int)file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
        	}
        }catch (Exception e) {
            e.printStackTrace();
        }
        // Open dataset inside group of Board Game
        try {
            if ((file_id >= 0) && (group_game_step >= 0))
            	dataset_board_id = H5.H5Dopen((int)group_game_step, DATASET_BOARD_INFO, HDF5Constants.H5P_DEFAULT);
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
        
        return board_dset;
        
    }
	// Read card game info at specific game number and game step
	public int[][] readCardData(int GameNum, int GameStep){
		int[][] card_dataset = new int[CARD_DIM_X][CARD_DIM_Y];
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
        	if(dataset_board_id >= 0){
        		H5.H5Dread((int)dataset_card_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, card_dataset);
        	}
        }catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
		}
        
        return card_dataset;
	}

	public int[] readActionDataSet(int GameNum, int GameStep){
		int[] action_dataset = new int[ACTION_DIM];
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
        	if(dataset_board_id >= 0){
        		H5.H5Dread((int)dataset_action_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, action_dataset);
        	}
        }catch (Exception e) {
			// TODO: handle exception
        	 e.printStackTrace();
		}
        
        return action_dataset;
	}
	
	//write data into board dataset assuming that we need to create new dataset within this function
	public int writeBoardDataset(int[][][] input, int GameNum, int GameStep){

		int status = -1;
		long dataspace_id = -1;
		if(!validateBoardData(input)){
			System.out.println("Wrong Dimension");
			return status = -1;
		}
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}/*
		if(file_id >= 0){
			try{
				group_game_step = H5.H5Gopen((int)file_id,"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep, HDF5Constants.H5P_DEFAULT);
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}*/
		try {
			dataspace_id = H5.H5Screate_simple(3, board_dataset_dim, null);
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (HDF5Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if ((file_id >= 0) && (dataspace_id >=0)){
			try {
				dataset_board_id = H5.H5Dcreate((int)file_id, 
										"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep+ "/" + DATASET_BOARD_INFO, 
												HDF5Constants.H5T_STD_I32LE, (int)dataspace_id, 
															HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, 
																	HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if(dataset_board_id >=0){
			try {
				status = H5.H5Dwrite((int)dataset_board_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
				this.closeDataSet((int)dataset_board_id);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HDF5Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	
	//Write data into card data set
	public int writeCardDataSet(int[][] input, int GameNum, int GameStep){

		int status = -1;
		int dataspace_id = -1;
		
		if(!validateCardData(input)){
			System.out.println("Wrong Dimension");
			return status=-1;
		}
		
		//Create dataspace for dataset with dim of 2
		try {
			dataspace_id = H5.H5Screate_simple(2,card_dataset_dim,null);
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (HDF5Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if ((file_id >= 0) && (dataspace_id >= 0)){
			try {
				dataset_card_id = H5.H5Dcreate((int)file_id, 
									"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep+ "/" + DATASET_CARD_INFO,
										HDF5Constants.H5T_STD_I32LE, (int)dataspace_id, 
											HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if(dataset_card_id >=0){
			try {
				status = H5.H5Dwrite((int)dataset_card_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL,
									HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
				this.closeDataSet((int)dataspace_id);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HDF5Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	public int writeActionDataSet(int[] input, int GameNum, int GameStep){

		int status = -1;
		int dataspace_id = -1;
		if(!validateActionData(input)){
			System.out.println("Wrong Dimension");
			return status=-1;
		}
		if(file_id < 0){
			try {
				file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDWR, HDF5Constants.H5P_DEFAULT);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
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
		} catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (HDF5Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if ((file_id >= 0) && (dataspace_id >= 0)){
			try {
				dataset_action_id = H5.H5Dcreate((int)file_id, 
						"/"+GROUP_GAME_NUM+GameNum+"/"+GROUP_GAME_STEP+GameStep + "/" + DATASET_ACITON_INFO, 
								HDF5Constants.H5T_STD_I32LE, (int)dataspace_id, HDF5Constants.H5P_DEFAULT, 
										HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		if(dataset_action_id >=0){
			try {
				status = H5.H5Dwrite((int)dataset_action_id, HDF5Constants.H5T_NATIVE_INT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, input);
				this.closeDataSet((int)dataset_action_id);
				
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HDF5Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return status;
	}
	public int closeDataSet(int id){
		int status = -1;
		if(id >=0 ){
			try {
				status=H5.H5Dclose(id)
;
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}else{
			status = 1;
		}
		return status;
	}
	// close group board dataset
	public int closeGroup(int group_id){
		int status = -1;
		if(group_id < 0){
			return status = 1;
		}else{
			try {
				return status = H5.H5Gclose(group_id);
			} catch (HDF5LibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return status;
			}
		}
	}
	// close board dataset
	public int closeFile(){
		int status = -1;
		try{
			if(this.file_id >= 0){
				status = H5.H5Dclose((int)file_id);
			}else{
				status = 1;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return status;
	}
		
}
