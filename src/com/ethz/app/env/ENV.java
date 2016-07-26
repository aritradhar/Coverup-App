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
	
	public static final int FOUNTAIN_CHUNK_SIZE = 10000;
	
	public static final String DATABASE_TABLE_COL = "BQVZ-tildem-table";
	public static final String DATABASE_DROPLET_COL = "BQVZ-tildem";
	
	public static final String BROWSER_COMM_LINK = "comm.txt";
	 
	public static final boolean EXPERIMENTAL = false;
	
	static
	{
		File file = new File(APP_STORAGE_LOC);
		if(!file.exists())
			file.mkdir();
	}
	
	public static final boolean COMPRESSION_SUPPORT = false;
	
}
