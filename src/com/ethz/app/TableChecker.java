package com.ethz.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

import com.ethz.app.env.ENV;

public class TableChecker 
{
	
	//this map has to be alive through out application life cycle
	public static Map<String, JSONObject> URL_JSON_TABLE_MAP = new HashMap<>();
	//url -> source key
	public static Map<String, String> URL_SOURCE_TABLE_MAP = new HashMap<>();
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

		JSONObject jObject = new JSONObject(ffce.jsonData);

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
			JSONObject jObject = new JSONObject(row[0]);

			this.tableJson = jObject.getString("table");
			this.signature = jObject.getString("signature");
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
			URL_SOURCE_TABLE_MAP.put(key, sourceKey);
		}
	}
	

	public String[] getURLsFromTable()
	{
		String[] toReturn = TableChecker.URL_JSON_TABLE_MAP.keySet().toArray(new String[0]);
		
		return toReturn;
	}

	public void setPK(String publicKey)
	{
		this.publicKeyString = publicKey;
		this.ServerpublicKey = Base64.getUrlDecoder().decode(this.publicKeyString);
	}

	public void verifyMessage() throws NoSuchAlgorithmException
	{
		byte[] theTableBytes = tableJson.getBytes(StandardCharsets.UTF_8);


		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashtableBytes = md.digest(theTableBytes);
		byte[] signatureBytes = Base64.getUrlDecoder().decode(this.signature);
		
		this.verifyResult = Curve25519.getInstance("best").verifySignature(this.ServerpublicKey, hashtableBytes, signatureBytes);
	}
	public void verifyMessageMultipleProvider() throws NoSuchAlgorithmException
	{
		
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
