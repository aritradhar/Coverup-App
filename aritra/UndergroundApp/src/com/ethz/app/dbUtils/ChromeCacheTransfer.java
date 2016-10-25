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
package com.ethz.app.dbUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.sqlite.SQLiteConfig;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class ChromeCacheTransfer {
	
	String baseDir;
	
	public ChromeCacheTransfer(String baseDir) {	
		this.baseDir = baseDir;
	}

	public void transfer() throws IOException, ClassNotFoundException, SQLException
	{
		File[] files = new File(baseDir).listFiles();
		
		SQLiteConfig config = new SQLiteConfig();
		config.setReadOnly(true); 
		
		Connection replicatedFileconn = DriverManager.getConnection("jdbc:sqlite:" + ENV.REPLICATED_CHROME_DB);
		for(File file : files)
		{
			if(file.getCanonicalPath().contains("localstorage-journal") || file.toString().contains("__0.localstorage"))
				continue;		

			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + file.getCanonicalPath(), config.toProperties());
			
			Statement stmt = c.createStatement();
			ResultSet rs = null;
			try
			{
				rs = stmt.executeQuery( "SELECT * FROM itemTable WHERE key = \'" + ENV.DATABASE_TABLE_COL + "\';" );
			}
			catch(SQLException ex)
			{
				continue;
			}
			if(rs == null)
				continue;
			
			while(rs.next())
			{
				String key = rs.getString("key");
				String value = rs.getString("value");
				
				Statement innerStatement = replicatedFileconn.createStatement();
				
				//rs = innerStatement.executeQuery("SELECT * from webappsstore2 where originKey ");
				
				int a = innerStatement.executeUpdate("INSERT OR REPLACE INTO webappsstore2 (originKey, scope, key, value) "
						+ "VALUES ((SELECT originKey from webappsstore2 WHERE scope = '" + file.getName() + "') , '" + 
						file.getName() + "','" + key + "','" +  value + "');");
				System.out.println(a);
			}
		}
	}
	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		
		new ChromeCacheTransfer("C:\\Users\\Aritra\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Local Storage").transfer();
	}
}
