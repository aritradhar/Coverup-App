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
	public static final String APP_STORAGE_COMPLETED_DROPLET_FILE = "info.txt";
	public static final String APP_STORAGE_DROPLET_URL = "dropletUrl.txt";
	public static final String BROWSER_COMM_LINK = "comm.txt";
	 
	
	
	static
	{
		File file = new File(APP_STORAGE_LOC);
		if(!file.exists())
			file.mkdir();
	}
	
	public static final boolean COMPRESSION_SUPPORT = false;
	
}
