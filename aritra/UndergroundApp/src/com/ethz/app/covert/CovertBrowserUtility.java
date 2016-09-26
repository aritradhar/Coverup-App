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
package com.ethz.app.covert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class CovertBrowserUtility {

	public static String[] getLocalSliceIds()
	{
		File sliceFileLoc = new File(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_INTERACTIVE_DATA);
		File[] sliceFiles = sliceFileLoc.listFiles();
		String[] ret = new String[sliceFiles.length];
		int i = 0;
		for(File sliceDoc : sliceFiles)
			ret[i++] = sliceDoc.getName();
		return ret;
		
	}
	
	public static byte[] assembleSlices(long sliceId) throws IOException
	{
		String sliceDirLocation = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_INTERACTIVE_DATA + ENV.DELIM + sliceId;
		
		File[] files = new File(sliceDirLocation).listFiles();
		
		byte[] ret = null;
		int i = 0;
		for(File file: files)
		{
			String fileName = file.getName().split("\\.")[0];
			byte[] sliceData = Files.readAllBytes(file.toPath());
			//Initialize the big byte array at the first look 
			if(i == 0)
				ret = new byte[sliceData.length * files.length];
			int startIndex = Integer.parseInt(fileName);
			System.arraycopy(sliceData, 0, ret, startIndex * sliceData.length, sliceData.length);
			i++;
		}
		
		return ret;
	}
	
}
