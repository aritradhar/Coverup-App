package com.ethz.app.rep;

import java.sql.SQLException;

import org.json.JSONObject;

import com.ethz.app.FirefoxCacheExtract;
import com.ethz.app.TableChecker;

public class RepeatedDatabaseCheck {
	
	public static byte[] ServerPublickey;
	
	
	public RepeatedDatabaseCheck() throws SQLException 
	{
		System.out.println("Run");
		this.doTableCheck();
		this.doDataBaseCheck();
	}
	
	private void doTableCheck() throws SQLException
	{
		TableChecker tabCheck = new TableChecker();
		tabCheck.loadtableData();
	}
	
	
	private void doDataBaseCheck() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		ffce.conncetDatabase("tildem");

		JSONObject jObject = new JSONObject(ffce.jsonData);
		
		String droplet = jObject.getString("droplet");
		String dropletUrl = jObject.getString("url");
		
	}
	
	
}
