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

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Aritra
 *
 */
public class ProxyServer {

	public static void setProxy(int port)
	{
		System.setProperty("http.proxySet", "true");
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", new Integer(port).toString());
		System.setProperty("https.proxyHost", "localhost");
		System.setProperty("https.proxyPort", new Integer(port).toString());
	}
	private boolean stop;
	private int port;
	public ProxyServer(int port) throws IOException 
	{
		this.port = port;	
		this.stop = true;
		//this.startServer();
	}
	private Thread serverThread;
	private ServerSocket serverSocket;
	public void startServer() { 
		
		Runnable serverTask = new Runnable() {
			@Override
			public void run() 
			{
				try 
				{
					serverSocket = new ServerSocket(port);
					System.out.println("Waiting for clients to connect...");
					while (stop) 
					{
						Socket clientSocket = serverSocket.accept();
						System.out.println("here");
						OutputStream os = clientSocket.getOutputStream();
						os.write(10);
						os.flush();
						os.close();
					}
					serverSocket.close();
					System.out.println("Server stopped");

				} catch (IOException e) {
					System.err.println("Server closed");
					return;
				}
			}
		};
		this.serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public void stopServer() throws IOException
	{
		if(serverSocket != null)
		{
			stop = false;
			serverSocket.close();
			System.err.println("Server closed");
		}
	}

}
