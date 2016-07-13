package com.ethz.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

import com.ethz.app.env.ENV;

public class TableChecker 
{
	
	//this map has to be alive through out application life cycle
	public static Map<String, JSONObject> URL_JSON_TABLE_MAP = new HashMap<>();;
	
	public String tableJson;
	public String signature;

	public String publicKeyString;
	public byte[] ServerpublicKey;

	public boolean verifyResult;


	
	public void loadtableData() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		ffce.conncetDatabase(ENV.DATABASE_TABLE);

		JSONObject jObject = new JSONObject(ffce.jsonData);

		this.tableJson = jObject.getString("table");
		this.signature = jObject.getString("signature");
		this.setMapFromtableJSON();
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
}
