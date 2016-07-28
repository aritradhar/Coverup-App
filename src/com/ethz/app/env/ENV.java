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
	public static final String APP_STORAGE_BROWSER_COMM_DROPLET_LOC = "DROPLET";
	
	public static final String APP_STORAGE_COMPLETED_DROPLET_FILE = "info.txt";
	public static final String APP_STORAGE_DROPLET_URL = "dropletUrl.txt";
	public static final String APP_STORAGE_COMPLETE_DATA = "data.txt";
	public static final String APP_STORAGE_TABLE_DUMP = "table.json";
	public static final String APP_STORAGE_TABLE_MULTIPLE_PROVIDER_DUMP = "table";
	
	public static final String APP_JSON_EXTENSION = ".json";
	
	
	public static final int FOUNTAIN_CHUNK_SIZE = 10000;
	
	public static final String DATABASE_TABLE_COL = "BQVZ-tildem-table";
	public static final String DATABASE_DROPLET_COL = "BQVZ-tildem";
	
	public static final String BROWSER_COMM_LINK = "comm.txt";
	 
	public static final boolean EXPERIMENTAL =  true;
	
	static
	{
		File file = new File(APP_STORAGE_LOC);
		if(!file.exists())
			file.mkdir();
	}
	
	public static final boolean COMPRESSION_SUPPORT = false;
	
	
	
	
	public static final String ABOUT_MESSAGE = "To those who can hear me, I say - do not despair. \n The misery that is now upon us is but the passing of greed - "
			+ "\nthe bitterness of men who fear the way of human progress. \nThe hate of men will pass,"
			+ " and dictators die, and the power they took from the people \n will return to the people.\nAnd so long as men die, liberty will never perish. .....\n"
			+ "- Charlie Chaplin (he Great Dictator)";
}
