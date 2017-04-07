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
package com.ethz.app.nativeMessageListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class NativeMessageListenerService extends Thread {

	public static ServerSocket serverSocket;
	
	@Override
	public void run(){
		String clientSentence;
		try {
			serverSocket = new ServerSocket(ENV.NATIVE_MESSAGE_LISTER_SERVER_PORT);
		} 
		catch (IOException e) {
			System.err.println("---- error at creating message listener service");
			return;
		}

		System.err.println("--------------------------------------");
		System.err.println("       Listner service started        ");
		System.err.println("--------------------------------------");
		while(true)
		{
			if(Thread.currentThread().isInterrupted())
				return;
			try
			{
				Socket connectionSocket = serverSocket.accept();
				BufferedReader messageFromClient =
						new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				clientSentence = messageFromClient.readLine();
				System.err.println(">> Listener service : Received: " + clientSentence);	
			}
			catch(Exception ex)
			{
				continue;
			}
		}
	}

	
	public static void stopServer()
	{
		try {
			serverSocket.close();
		} 
		catch (IOException e) {
			System.err.println(">> Listener service : Problem in closing the message listner service");
		}
	}
	
	public static void main(String[] args) throws IOException {
		NativeMessageListenerService service = new NativeMessageListenerService();
		service.run();
	}
}
