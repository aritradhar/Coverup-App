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
package com.ethz.app.dispatchSocket;

import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class TCPClient {

	public static void connectToBrowser(byte[] data) throws Exception
	{
		Socket clientSocket = null;
		try
		{
			clientSocket = new Socket("localhost", 6789);
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ENV.EXCEPTION_BROWSER_EXTENSION_MISSING);
		}
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(Base64.getEncoder().encodeToString(data));
		clientSocket.close();
	}
	
	public static void main(String[] args) throws Exception {
		
		byte[] data = Files.readAllBytes(new File(ENV.APP_STORAGE_LOC + ENV.DELIM + 
								ENV.APP_STORAGE_SLICE_ID_FILES_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_ID_FILE).toPath());
		
		ScheduledExecutorService execService
		=	Executors.newScheduledThreadPool(50);
		
	
		execService.scheduleAtFixedRate(()->{
		try {
			connectToBrowser(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}, 0, 200L, TimeUnit.MILLISECONDS);
	}

}
