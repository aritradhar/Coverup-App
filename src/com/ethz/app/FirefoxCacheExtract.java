package com.ethz.app;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FirefoxCacheExtract {

	public String databaseFile;
	public String jsonData;
	
	public static String changedDBLocation = "";
	
	public String getFirefoxCacheFile(String fileName)
	{
		if(fileName == null || fileName.length() == 0)
			return this.getFirefoxCacheFile();
		
		this.databaseFile = fileName;
		return fileName;
	}
	
	public String getFirefoxCacheFile()
	{
		String fileName = null;
		
		String os = System.getProperty("os.name");
		
		String appDataLoc = null;
		if(os.contains("Win"))
		{
			appDataLoc = System.getenv("AppData");
			appDataLoc = appDataLoc.concat("\\Mozilla");
			File mozzila = new File(appDataLoc);
			
			if(!mozzila.exists())
				throw new RuntimeException("Firefox not installed");
			
			appDataLoc = appDataLoc.concat("\\Firefox\\Profiles");
			File profileLoc = new File(appDataLoc);
			File[] files = profileLoc.listFiles();
			fileName = files[0].getAbsolutePath().concat("\\webappsstore.sqlite");
			
		}
		else if(os.contains("Linux")){
			appDataLoc = System.getenv("HOME");
			appDataLoc = appDataLoc.concat("/.mozilla");
			File mozzila = new File(appDataLoc);
			
			if(!mozzila.exists())
				throw new RuntimeException("Firefox not installed");
			
			appDataLoc = appDataLoc.concat("/firefox/6p4vbecj.default/webappsstore.sqlite");
			
			fileName = appDataLoc;
		}
		else
		{
			System.err.println("code for " + os + " is still not here :p");
		}
		this.databaseFile = fileName;
		System.out.println(fileName);
		
		return fileName;
	}
	
	public void conncetDatabase(String loc, boolean flag) throws SQLException
	  {
		if(loc == null)
			this.getFirefoxCacheFile();
		else
			this.getFirefoxCacheFile(loc);
	    Connection c = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + this.databaseFile);
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    System.out.println("Opened database successfully");
	    
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'index-BBB\';" );
	
		while(rs.next())
			this.jsonData = rs.getString("value");
			
		stmt.close();
		c.close();
	  }
	
	public String conncetDatabase(String key, String loc) throws SQLException
	{
		this.getFirefoxCacheFile(loc);
		 Connection c = null;
		    try 
		    {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:" + this.databaseFile);
		    } 
		    catch ( Exception e ) 
		    {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      System.exit(0);
		    }
		    
		    System.out.println("Opened database successfully");
		    
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );
		
			while(rs.next())
				this.jsonData = rs.getString("value");
				
			stmt.close();
			c.close();
			
			return this.jsonData;
	}
	
	public String conncetDatabase(String key) throws SQLException
	  {
		this.getFirefoxCacheFile();
	    Connection c = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:" + this.databaseFile);
	    } 
	    catch ( Exception e ) 
	    {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    
	    System.out.println("Opened database successfully");
	    
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );
	
		while(rs.next())
			this.jsonData = rs.getString("value");
			
		stmt.close();
		c.close();
		
		return this.jsonData;
	  }
}
