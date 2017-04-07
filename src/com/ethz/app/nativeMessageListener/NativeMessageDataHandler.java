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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 * This handle the transfer of messages received from the native message listener 
 * service and transfer it to the replicated database identical to that of the firefox 
 * browser cache database
 */
public class NativeMessageDataHandler {

	JSONObject messageJSON;

	public NativeMessageDataHandler(JSONObject messageJSON)
	{
		this.messageJSON = messageJSON;
	}

	public void insertToDB()
	{
		try
		{
			String origin = this.messageJSON.getString("origin");
			String key = this.messageJSON.getString("key");
			String value = this.messageJSON.getString("value");

			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + ENV.REPLICATED_NATIVE_MESSGAE_DB);
			Statement insertStatement = connection.createStatement();
			insertStatement.executeUpdate("INSERT INTO webappsstore2 (originKey, scope, key, value) "
					+ "VALUES ('" + origin + "' , '" + origin + "','" + key + "','" +  value + "');");

			insertStatement.close();
			connection.close();
		}
		catch(JSONException ex)
		{
			System.err.println(">> Data handler service : json parsing error");
		}
		catch(Exception es)
		{
			System.err.println(">> Data handler service : Database write error");
		}
	}
}
