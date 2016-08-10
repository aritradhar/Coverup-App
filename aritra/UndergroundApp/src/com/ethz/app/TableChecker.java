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

package com.ethz.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

import com.ethz.app.binUtils.BinUtils;
import com.ethz.app.env.ENV;
import com.ethz.app.rep.RepeatedDatabaseCheck;

public class TableChecker 
{
	
	//this map has to be alive through out application life cycle
	public static Map<String, JSONObject> URL_JSON_TABLE_MAP = new HashMap<>();
	//url -> source keys
	public static Map<String, Set<String>> URL_SOURCE_TABLE_MAP = new HashMap<>();
	//sourceKey ->url
	public static Map<String,Set<String>> SOURCE_KEY_URL_MAP = new HashMap<>();
	//source key -> table | signature
	public static Map<String, String[]> SOURCE_KEY_TABLE_SIGNATURE_MAP = new HashMap<>();
	//source key -> signature verification result
	public static Map<String, Boolean> SOURCE_KEY_SIGNATURE_VERIFY_MAP = new HashMap<>();
	
	public String tableJson;
	public String signature;

	public String publicKeyString;
	public byte[] ServerpublicKey;

	public boolean verifyResult;
	public String tableDumpJson;

	
	public void loadtableData() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		ffce.conncetDatabase(ENV.DATABASE_TABLE_COL);

		JSONObject jObject = null;
		
		try
		{
			jObject = new JSONObject(ffce.jsonData);
		}
		catch(JSONException ex)
		{
			String binTableConvertedJson = BinUtils.tableBinToTableJson(Base64.getDecoder().decode(ffce.jsonData), RepeatedDatabaseCheck.ServerPublickey);
			if(binTableConvertedJson != null)
				jObject = new JSONObject(binTableConvertedJson);
		}
		
		this.tableJson = jObject.getString("table");
		this.signature = jObject.getString("signature");
		this.setMapFromtableJSON();
		this.tableDumpJson = jObject.toString(2);
	}
	
	public void loadtableData(String loc) throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile(loc);
		ffce.conncetDatabase(ENV.DATABASE_TABLE_COL, loc);

		JSONObject jObject = new JSONObject(ffce.jsonData);

		this.tableJson = jObject.getString("table");
		this.signature = jObject.getString("signature");
		this.setMapFromtableJSON();
		this.tableDumpJson = jObject.toString(2);
	}
	
	
	public List<String[]> multipleProviderRows; 
	
	/**
	 * Experimental 
	 * @throws SQLException
	 */
	public void loadtableDataMultipleProvider() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		this.multipleProviderRows = ffce.conncetDatabaseMultipleProvider(ENV.DATABASE_TABLE_COL);

		
		for(String[] row : this.multipleProviderRows)
		{
			JSONObject jObject_local = null;
			
			try
			{
				jObject_local = new JSONObject(row[0]);
			}
			catch(JSONException ex)
			{
				//handle as binary
				String binTableConvertedJson = BinUtils.tableBinToTableJson(
						Base64.getDecoder().decode(ffce.jsonData), 
						RepeatedDatabaseCheck.ServerPublickey);
				
				if(binTableConvertedJson != null)
					jObject_local = new JSONObject(binTableConvertedJson);
			}
			this.tableJson = jObject_local.getString("table");
			this.signature = jObject_local.getString("signature");
			SOURCE_KEY_TABLE_SIGNATURE_MAP.put(row[1], new String[]{this.tableJson, this.signature});
			this.setMapFromtableJSONMultipleProvider(new JSONObject(this.tableJson), row[1]);
		}
	}
	
	/**
	 * Experimental
	 * @param loc
	 * @throws SQLException
	 */
	public void loadtableDataMultipleProvider(String loc) throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile(loc);
		this.multipleProviderRows = ffce.conncetDatabaseMultipleProvider(ENV.DATABASE_TABLE_COL, loc);

		for(String[] row : this.multipleProviderRows)
		{
			JSONObject jObject = new JSONObject(row[0]);			
			
			this.tableJson = jObject.getString("table");
			this.signature = jObject.getString("signature");
			SOURCE_KEY_TABLE_SIGNATURE_MAP.put(row[1], new String[]{this.tableJson, this.signature});
			
			this.setMapFromtableJSONMultipleProvider(new JSONObject(this.tableJson), row[1]);
		}
	}
	
	private void setMapFromtableJSON()
	{
		JSONObject jObject = new JSONObject(this.tableJson);
		JSONArray tabelDataArray = jObject.getJSONArray("table");
		
		for(int i = 0; i < tabelDataArray.length(); i++)
		{
			JSONObject jObIn = tabelDataArray.getJSONObject(i);	
			String key = jObIn.getString("key");
			String value = jObIn.getString("value");
			JSONObject tableRowJSONObject = new JSONObject(value);
			
			URL_JSON_TABLE_MAP.put(key, tableRowJSONObject);
		}
	}
	
	/**
	 * Experimental for multiple providers
	 * @param jObject
	 */
	private void setMapFromtableJSONMultipleProvider(JSONObject jObject, String sourceKey)
	{
		JSONArray tabelDataArray = jObject.getJSONArray("table");
		
		for(int i = 0; i < tabelDataArray.length(); i++)
		{
			JSONObject jObIn = tabelDataArray.getJSONObject(i);	
			String key = jObIn.getString("key");
			String value = jObIn.getString("value");
			JSONObject tableRowJSONObject = new JSONObject(value);
			
			URL_JSON_TABLE_MAP.put(key, tableRowJSONObject);
			
			Set<String> sourceKeys = new HashSet<>();
			if(!URL_SOURCE_TABLE_MAP.containsKey(key))
			{			
				sourceKeys.add(sourceKey);
				URL_SOURCE_TABLE_MAP.put(key, sourceKeys);
			}
			else
			{
				sourceKeys = URL_SOURCE_TABLE_MAP.get(key);
				sourceKeys.add(sourceKey);
			}
			
			Set<String> urls = new HashSet<>();
			if(!SOURCE_KEY_URL_MAP.containsKey(sourceKey))
			{
				urls.add(key);
				SOURCE_KEY_URL_MAP.put(sourceKey, urls);
			}
			else
			{
				urls = SOURCE_KEY_URL_MAP.get(sourceKey);
				urls.add(key);
			}
		}
	}
	
	public int getRowCount()
	{
		int toReturn = 0;
		
		for(String sourcekey : SOURCE_KEY_URL_MAP.keySet())
			toReturn += SOURCE_KEY_URL_MAP.get(sourcekey).size();
		
		return toReturn;
	}

	public String[] getURLsFromTable()
	{
		String[] toReturn = TableChecker.URL_JSON_TABLE_MAP.keySet().toArray(new String[0]);
		
		return toReturn;
	}
	
	public String[] getOriginKeysFromTable()
	{
		String[] toReturn = TableChecker.SOURCE_KEY_URL_MAP.keySet().toArray(new String[0]);
		
		return toReturn;
	}

	public void setPK(String publicKey)
	{
		this.publicKeyString = publicKey;
		this.ServerpublicKey = Base64.getUrlDecoder().decode(this.publicKeyString);
	}

	public boolean verifyMessage() throws NoSuchAlgorithmException
	{
		if(this.ServerpublicKey == null)
			return false;
		
		byte[] theTableBytes = tableJson.getBytes(StandardCharsets.UTF_8);


		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashtableBytes = md.digest(theTableBytes);
		byte[] signatureBytes = Base64.getUrlDecoder().decode(this.signature);
		
		this.verifyResult = Curve25519.getInstance("best").verifySignature(this.ServerpublicKey, hashtableBytes, signatureBytes);
		
		return true;
	}
	public boolean verifyMessageMultipleProvider() throws NoSuchAlgorithmException
	{
		if(this.ServerpublicKey == null)
			return false;
		
		for(String sourceKey : SOURCE_KEY_TABLE_SIGNATURE_MAP.keySet())
		{
			String tableJsonFromMap = SOURCE_KEY_TABLE_SIGNATURE_MAP.get(sourceKey)[0];
			String signatureFromMap = SOURCE_KEY_TABLE_SIGNATURE_MAP.get(sourceKey)[1];
			
			byte[] theTableBytes = tableJsonFromMap.getBytes(StandardCharsets.UTF_8);

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashtableBytes = md.digest(theTableBytes);
			byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureFromMap);

			boolean _verifyResult = Curve25519.getInstance("best").verifySignature(this.ServerpublicKey, hashtableBytes, signatureBytes);
			SOURCE_KEY_SIGNATURE_VERIFY_MAP.put(sourceKey, _verifyResult);
		}
		
		return true;
	}
	
	public static List<String> verifyMessageList()
	{
		List<String> failedSigOriginKeys = new ArrayList<>();
		
		for(String sourceKey : SOURCE_KEY_SIGNATURE_VERIFY_MAP.keySet())
		{
			if(!SOURCE_KEY_SIGNATURE_VERIFY_MAP.get(sourceKey))
				failedSigOriginKeys.add(sourceKey);
		}
		
		return failedSigOriginKeys;
	}
}
