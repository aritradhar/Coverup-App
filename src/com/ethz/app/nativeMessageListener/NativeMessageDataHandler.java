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
import java.sql.SQLException;

import org.json.JSONObject;
import org.sqlite.SQLiteConfig;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class NativeMessageDataHandler {

	JSONObject messageJSON;
	
	public NativeMessageDataHandler(JSONObject messageJSON)
	{
		this.messageJSON = messageJSON;
	}
	
	public void insertToDB() throws SQLException
	{
		SQLiteConfig config = new SQLiteConfig();
		config.setReadOnly(true); 

		Connection replicatedNativeMessageFileconn = DriverManager.getConnection("jdbc:sqlite:" + ENV.REPLICATED_NATIVE_MESSGAE_DB);
	}
}
