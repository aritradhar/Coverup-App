package com.ethz.app;

import java.sql.SQLException;

public class TableChecker 
{
	public String tableJson;
	public String signature;
	
	public void loadtableData() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		ffce.conncetDatabase("tildem-table");
		
		
	}
}
