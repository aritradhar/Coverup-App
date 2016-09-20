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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import com.ethz.app.env.ENV;

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
	private int port;
	private FileWriter fw;
	public ProxyServer(int port) throws IOException 
	{
		this.port = port;
		this.fw = new FileWriter(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_ID_FILES_LOC + ENV.DELIM + new Random().nextLong() + "txt");
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
					while (true) 
					{
						Socket clientSocket = serverSocket.accept();
						System.err.println("here");
						BufferedReader readRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


						while(true) 
						{
							String s = readRequest.readLine();
							if(s.contains("GET"))
							{
								s = s.trim();
								String[] reqHead = s.split(" ");
								if(reqHead[0].equalsIgnoreCase("GET"))
								{
									String getReq = reqHead[1];
									getReq = getReq.split("=")[1];
									CovertBrowser.sliceIdSet.add(getReq);
									System.out.println(getReq);
								}
								break;
							}
							if(s == null || s.trim().length() == 0) 
								break;			       
						}

						readRequest.close();

					/*	OutputStream os = clientSocket.getOutputStream();
						String s = "<html><body>bla</body></html>";
						String response = "HTTP/1.1 200 OK\r\n" +
			                    "Server: Server\r\n" +
			                    "Content-Type: text/html\r\n" +
			                    "Content-Length: " + s.length() + "\r\n" +
			                    "Connection: close\r\n\r\n";
			            String result = response + s;
			            os.write(result.getBytes());
			            os.flush();
			            os.close();*/
			            
						/*OutputStream os = clientSocket.getOutputStream();
						os.write(10);
						os.flush();
						os.close();*/
					}

				} 
				catch(SocketException ex)
				{
					
					if(ex.getMessage().equalsIgnoreCase("Connection reset"))
					{
						System.err.println("Connection reset, restarting the server...");
						try {
							stopServer();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						startServer();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
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
			//stop = false;
			serverSocket.close();
			this.fw.close();
			System.err.println("Server closed");
		}
	}

}
