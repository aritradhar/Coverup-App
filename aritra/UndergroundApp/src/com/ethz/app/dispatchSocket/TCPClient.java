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
import java.net.Socket;
import java.util.Base64;

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
		
		connectToBrowser(null);
	}

}
