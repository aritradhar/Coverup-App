//*************************************************************************************
//*********************************************************************************** *
//author Aritra Dhar 																* *
//PhD Researcher																  	* *
//ETH Zurich													   				    * *
//Zurich, Switzerland															    * *
//--------------------------------------------------------------------------------- * * 
///////////////////////////////////////////////// 									* *
//This program is meant to do world domination... 									* *
///////////////////////////////////////////////// 									* *
//*********************************************************************************** *
//*************************************************************************************

package com.ethz.app.env;

import java.io.File;

public class ENV {
	
	public static String DELIM;
	static
	{
		String OS = System.getProperty("os.name");
		if(OS.contains("windows"))
			DELIM = "\\";
		
		else
			DELIM = "/";
	}
	
	public static final String APP_STORAGE_LOC = "APP_DATA";
	static
	{
		File file = new File(APP_STORAGE_LOC);
		if(!file.exists())
			file.mkdir();
	}
	
	public static final String APP_STORAGE_BROWSER_COMM_DROPLET_LOC = "DROPLET";
	public static final String APP_STORAGE_BROWSER_COMM_DROPLET_BIN_LOC = "DROPLET_BIN";
	
	public static final String APP_STORAGE_COMPLETED_DROPLET_FILE = "info.txt";
	public static final String APP_STORAGE_DROPLET_URL = "dropletUrl.txt";
	public static final String APP_STORAGE_COMPLETE_DATA = "data.txt";
	public static final String APP_STORAGE_COMPLETE_DATA_AON = "data_dec.txt";
	public static final String APP_STORAGE_TABLE_DUMP = "table.json";
	public static final String APP_STORAGE_TABLE_MULTIPLE_PROVIDER_DUMP = "table";
	public static final String APP_STORAGE_KEY_FILE = "key.bin";
	public static final String APP_STORAGE_INTERACTIVE_DATA = "Interactive";
	static
	{
		File fileI = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_INTERACTIVE_DATA);
		if(!fileI.exists())
			fileI.mkdir();
	}
	
	public static final byte INTR_MARKER = (byte)0x06;
	public static final int INTR_MARKER_LEN = 8;
	public static final String MAGIC_BYTES_EXCEPTION_MESSAGE = "MAGIC";
	
	public static final int AES_KEY_SIZE = 16;
	
	public static final String APP_JSON_EXTENSION = ".table";
	public static final String APP_BIN_EXTENSION = ".bin";
	
	
	public static final int FOUNTAIN_CHUNK_SIZE = 10000;
	
	public static final String DATABASE_TABLE_COL = "BQVZ-tildem-table";
	public static final String DATABASE_DROPLET_COL = "BQVZ-tildem";
	
	public static final String BROWSER_COMM_LINK = "comm.txt";
	 
	public static final boolean MULTIPLE_PROVIDER_SUPPORT =  true;
	public static final boolean AON_SUPPORT = true;
	
	public static final boolean EXPERIMENTAL = false;
	
	
	public static final boolean COMPRESSION_SUPPORT = false;
	
	public static final char[] PROGRESS_SYMB = {'-', '\\', '|', '/'};
	
	
	public static final String ABOUT_MESSAGE = "To those who can hear me, I say - do not despair. \n The misery that is now upon us is but the passing of greed - "
			+ "\nthe bitterness of men who fear the way of human progress. \nThe hate of men will pass,"
			+ " and dictators die, and the power they took from the people \n will return to the people.\nAnd so long as men die, liberty will never perish. .....\n"
			+ "- Charlie Chaplin (The Great Dictator)";
}
