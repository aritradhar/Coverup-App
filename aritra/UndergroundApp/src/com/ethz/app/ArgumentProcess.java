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
package com.ethz.app;

import com.ethz.app.env.ENV;
import com.ethz.app.poll.DataBasePollPresetPK;

/**
 * @author Aritra
 *
 */
public class ArgumentProcess {

	
	public static String profileLoc;
	/**
	 * 
	 * @param args Arguments from main command arguments
	 * @return 0 for exit, 1 for continue
	 */
	public int processArgument(String[] args)
	{
		if(args[0].equalsIgnoreCase("help"))
		{
			System.out.println("argument format \n"
					+ "help or \n"
					+ "firefox <pathToProfile> <pollingrate>");
			
			return 0;
		}
		
		else
		{
			ENV.AUTO_PILOT = true;
			if(args[0].equalsIgnoreCase("firefox"))
				AppMain.selectedPrimaryBrowser = ENV.BROWSER_FIREFOX;
			else if(args[0].equalsIgnoreCase("chrome"))
				AppMain.selectedPrimaryBrowser = ENV.BROWSER_CHROME;
			else
			{
				System.err.println("Wrong browser argument");
				return 0;
			}
			
			profileLoc = args[1];
			DataBasePollPresetPK.pollingRate = Integer.parseInt(args[2]);
		}
		
		return 1;
	}
	
}
