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
package com.ethz.app.chatApp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class IncomingChatPoll {


	public static void pollChat()
	{
		Connection c = null;
		try 
		{					
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + ENV.APP_STORAGE_INCOMING_CHAT_DATABASE_FILE);
		} 
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return;
		}
		Statement stmt;
		try {
			stmt = c.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM incoming_chat;" );
			//read all the rows from the database and store them in the proper chat location
			while(rs.next())
			{
				String senderAddress = rs.getString("sender");
				String data = rs.getString("data");
				
				String saveLocStr = ENV.APP_STORAGE_CHAT_LOC + ENV.DELIM + 
						ENV.APP_STORAGE_CHAT_LOG_LOC + ENV.DELIM + senderAddress;
				File saveLoc = new File(saveLocStr);
				if(!saveLoc.exists())
					saveLoc.mkdir();
				
				FileWriter fw = new FileWriter(saveLocStr + ENV.DELIM + ENV.APP_STORAGE_CHAT_REPO_FILE, true);
				fw.append("---- Received start ----\n" + data + "\n ---- Received end ----\n");
				fw.close();
			}
			
			//delete all the rows from the datebase
			stmt.executeUpdate("DELETE FROM incoming_chat;");
			rs.close();
			stmt.close();
			c.close();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
		

	}
}
