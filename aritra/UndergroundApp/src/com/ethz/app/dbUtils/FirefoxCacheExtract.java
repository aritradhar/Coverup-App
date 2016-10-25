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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.sqlite.SQLiteConfig;

import com.ethz.app.AppMain;
import com.ethz.app.env.ENV;

public class FirefoxCacheExtract {

	public static String databaseFile;
	public static List<String> chromeDatabaseFiles;
	public String jsonData;

	public static String changedDBLocation = "";

	public String getFirefoxCacheFile(String fileName)
	{
		if(fileName == null || fileName.length() == 0)
			return this.getFirefoxCacheFile();

		databaseFile = fileName;
		return fileName;
	}

	public String getFirefoxCacheFile()
	{
		if(databaseFile != null)
			return databaseFile;

		String fileName = null;

		String os = System.getProperty("os.name");

		String appDataLoc = null;
		if(os.contains("Win"))
		{

			if(AppMain.selectedPrimaryBrowser.equals(ENV.BROWSER_FIREFOX))
			{
				appDataLoc = System.getenv("AppData");
				appDataLoc = appDataLoc.concat("\\Mozilla");
				File mozzila = new File(appDataLoc);

				if(!mozzila.exists())
					throw new RuntimeException("Firefox not installed");

				appDataLoc = appDataLoc.concat("\\Firefox\\Profiles");
				File profileLoc = new File(appDataLoc);
				File[] files = profileLoc.listFiles();

				if(files.length > 0 )
				{
					Set<String> databaseList = new HashSet<>();
					for(File file : files)
						databaseList.add(file.getName());

					try
					{	
						JFileChooser chooser = new JFileChooser(); 
						chooser.setCurrentDirectory(new java.io.File(appDataLoc));
						chooser.setDialogTitle("Multiple profiles found. Choose one");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

						if (chooser.showOpenDialog(AppMain.frame) == JFileChooser.APPROVE_OPTION) 
							fileName = chooser.getSelectedFile().getAbsolutePath().concat("\\webappsstore.sqlite");	
						else
						{
							JOptionPane.showMessageDialog(AppMain.frame, "No valid file Chosen. Exiting", "Error", JOptionPane.ERROR_MESSAGE);
							System.exit(1);
						}
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				else
				{
					fileName = files[0].getAbsolutePath().concat("\\webappsstore.sqlite");
				}
			}
			
			else
			{
				appDataLoc = System.getenv("LocalAppData").concat("\\Google\\Chrome\\User Data\\Default\\Local Storage");
				File chrome = new File(appDataLoc);

				if(!chrome.exists())
					throw new RuntimeException("Chrome not installed");
				
				try {
					FirefoxCacheExtract.chromeDatabaseFiles = this.chromeLocalStorageDetection(appDataLoc);
				} catch (ClassNotFoundException | IOException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else if(os.contains("Linux"))
		{
			if(AppMain.selectedPrimaryBrowser.equals(ENV.BROWSER_FIREFOX))
			{
				appDataLoc = System.getenv("HOME");
				appDataLoc = appDataLoc.concat("/.mozilla");
				File mozzila = new File(appDataLoc);

				if(!mozzila.exists())
					throw new RuntimeException("Firefox not installed");


				JFileChooser chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(new java.io.File(appDataLoc));
				chooser.setDialogTitle("Choose Firefox profile dir");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (chooser.showOpenDialog(AppMain.frame) == JFileChooser.APPROVE_OPTION) 
					fileName = chooser.getSelectedFile().getAbsolutePath().concat("\\webappsstore.sqlite");	
			}
			else
			{
				appDataLoc = "~/.config/google-chrome/Default";
				File chrome = new File(appDataLoc);

				if(!chrome.exists())
					throw new RuntimeException("Chrome not installed");
				
				try {
					FirefoxCacheExtract.chromeDatabaseFiles = this.chromeLocalStorageDetection(appDataLoc);
				} catch (ClassNotFoundException | IOException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		else
		{
			if(AppMain.selectedPrimaryBrowser.equals(ENV.BROWSER_FIREFOX))
			{
				JFileChooser chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(new java.io.File(appDataLoc));
				chooser.setDialogTitle("<MAC> Choose Firefox profile dir");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if (chooser.showOpenDialog(AppMain.frame) == JFileChooser.APPROVE_OPTION) 
					fileName = chooser.getSelectedFile().getAbsolutePath().concat("\\webappsstore.sqlite");	
			}
			else
			{
				appDataLoc = "~/Library/Application Support/Google/Chrome/Default";
				File chrome = new File(appDataLoc);

				if(!chrome.exists())
					throw new RuntimeException("Chrome not installed");
				
				try {
					FirefoxCacheExtract.chromeDatabaseFiles = this.chromeLocalStorageDetection(appDataLoc);
				} catch (ClassNotFoundException | IOException | SQLException e) {
					e.printStackTrace();
				}
			}
		}
		databaseFile = fileName;
		//System.out.println(fileName);

		return fileName;
	}

	public void connectDatabase(String loc, boolean flag) throws SQLException
	{
		if(loc == null)
			this.getFirefoxCacheFile();
		else
			this.getFirefoxCacheFile(loc);
		Connection c = null;
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true); 

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile, config.toProperties());
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

	public String connectDatabase(String key, String loc) throws SQLException
	{
		//this.getFirefoxCacheFile(loc);
		Connection c = null;
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true); 
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile, config.toProperties());
		} 
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}

		//System.out.println("Opened database successfully");

		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );

		while(rs.next())
			this.jsonData = rs.getString("value");

		stmt.close();
		c.close();

		return this.jsonData;
	}

	/**Experomental.
	 * For multiple provider
	 * @param key
	 * @param loc
	 * @param flag
	 * @return
	 * @throws SQLException
	 */

	public List<String[]> connectDatabaseMultipleProvider(String key, String loc) throws SQLException
	{
		//this.getFirefoxCacheFile(loc);
		Connection c = null;
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true); 

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile, config.toProperties());
		} 
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}

		//System.out.println("Opened database successfully");

		Statement stmt = c.createStatement();
		ResultSet rs = null;
		boolean isChrome = false;
		try
		{
			rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );
		}
		catch(SQLException ex)
		{
			if(ex.getMessage().equals("[SQLITE_ERROR] SQL error or missing database (no such table: webappsstore2)"))
			{
				try
				{
					rs = stmt.executeQuery( "SELECT * FROM ItemTable WHERE key = \'" + key + "\';" );
					isChrome = true;
				}
				catch (SQLException e) {
					throw new SQLException("Not a firefox/chrome database file");
				}
			}

		}
		List<String[]> out = new ArrayList<>();

		while(rs.next())
		{
			String[] arr = new String[2];
			if(!isChrome)
			{
				arr[0] = rs.getString("value");
				arr[1] = rs.getString("originKey");
			}
			else
			{
				arr[0] = rs.getString("value");
				arr[1] = databaseFile;
			}
			out.add(arr);
		}	
		stmt.close();
		c.close();

		return out;
	}

	/**
	 * Experimental
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public List<String[]> connectDatabaseMultipleProvider(String key) throws SQLException
	{
		//this.getFirefoxCacheFile();
		Connection c = null;
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true); 
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile, config.toProperties());
		} 
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}

		//System.out.println("Opened database successfully");

		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );

		List<String[]> out = new ArrayList<>();

		while(rs.next())
		{
			String[] arr = new String[2];
			arr[0] = rs.getString("value");
			arr[1] = rs.getString("originKey");
			out.add(arr);
		}	
		stmt.close();
		c.close();

		return out;
	}


	public String connectDatabase(String key) throws SQLException
	{
		//this.getFirefoxCacheFile();
		Connection c = null;
		try 
		{
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true); 

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + databaseFile, config.toProperties());
		} 
		catch ( Exception e ) 
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			return null;
		}

		//System.out.println("Opened database successfully");

		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT * FROM webappsstore2 WHERE key = \'" + key + "\';" );

		while(rs.next())
			this.jsonData = rs.getString("value");

		stmt.close();
		c.close();

		return this.jsonData;
	}
	
	/**
	 *  Get List of sqlite files where tildem data exists
	 * @param baseDir
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public List<String> chromeLocalStorageDetection(String baseDir) throws IOException, SQLException, ClassNotFoundException
	{
		File[] files = new File(baseDir).listFiles();
		
		SQLiteConfig config = new SQLiteConfig();
		config.setReadOnly(true); 
		
		List<String> toRet = new ArrayList<>();
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
			
			int rowCounter = 0;
			while(rs.next())
				rowCounter++;
			
			if(rowCounter > 0)
			{
				System.out.println(file.toString());
				toRet.add(file.getCanonicalPath());
			}
		}
		
		return toRet;
	}
}
