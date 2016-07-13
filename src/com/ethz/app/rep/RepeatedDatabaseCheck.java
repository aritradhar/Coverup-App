package com.ethz.app.rep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

import com.ethz.app.FirefoxCacheExtract;
import com.ethz.app.TableChecker;
import com.ethz.app.env.ENV;

public class RepeatedDatabaseCheck {
	
	public static byte[] ServerPublickey;
	StringBuffer messaage;
	
	public static String changedDBLoc = "";
	public String modifiedDatabaseLocation;
	
	
	public RepeatedDatabaseCheck(String modifiedDatabaseLocation) throws Exception 
	{
		this.modifiedDatabaseLocation = modifiedDatabaseLocation;
		System.out.println("Run");
		
		this.messaage = new StringBuffer();
		this.doTableCheck();
		this.doDataBaseCheck();
	}
	
	
	private void doTableCheck() throws SQLException
	{
		TableChecker tabCheck = new TableChecker();
		tabCheck.loadtableData();
	}
	
	
	private void doDataBaseCheck() throws SQLException, IOException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile(this.modifiedDatabaseLocation);
		ffce.conncetDatabase(ENV.DATABASE_DROPLET, this.modifiedDatabaseLocation);

		JSONObject jObject = new JSONObject(ffce.jsonData);
		
		System.err.println(jObject.toString(2));
		
		String droplet = jObject.getString("droplet");
		String dropletUrl = jObject.getString("url");
		
		String dropletStr = droplet.concat(dropletUrl);
		
		byte[] dropletByte = dropletStr.getBytes(StandardCharsets.UTF_8);
		String signatureString = jObject.getString("signature");
		byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureString);
		
		this.messaage.append("Droplet length : ").append(dropletByte.length);
		
		byte[] hashtableBytes = null;
		try 
		{
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			hashtableBytes = md.digest(dropletByte);
			
			//System.out.println("hash " + Base64.getUrlEncoder().encodeToString(hashtableBytes));
			
			boolean check = Curve25519.getInstance("best").verifySignature(RepeatedDatabaseCheck.ServerPublickey, hashtableBytes, signatureBytes);
			
			if(!check)
			{
				this.messaage.append("\nSignature verification failed");
				throw new RuntimeException("Signature verification failed");
			}
			else
			{
				this.messaage.append("\nSignature verification success");
			}
		} 
		
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		
		JSONObject tableJSONData = TableChecker.URL_JSON_TABLE_MAP.get(dropletUrl);
		String dropletLocation =  tableJSONData.get("dropletLoc").toString();
		
		this.messaage.append("\ndroplet location : ").append(dropletLocation);
		
		String fileName = ENV.APP_STORAGE_LOC + ENV.DELIM + dropletLocation + ENV.DELIM + Base64.getUrlEncoder().encodeToString(hashtableBytes).concat(".txt");
		String dropletUrlFileName =  ENV.APP_STORAGE_LOC + ENV.DELIM + dropletLocation + ENV.DELIM + ENV.APP_STORAGE_DROPLET_URL;
		
		if(!new File(ENV.APP_STORAGE_LOC + ENV.DELIM + dropletLocation).exists())
		{
			new File(ENV.APP_STORAGE_LOC + ENV.DELIM + dropletLocation).mkdir();
		}
		
		File file = new File(fileName);
		File dropletUrlFile = new File(dropletUrlFileName);
		
		if(!file.exists())
		{
			FileWriter fw = new FileWriter(fileName);
			fw.write(droplet);
			this.messaage.append("\n Droplet dumped in local storage");
			this.messaage.append("\n Droplet id : " + Base64.getUrlEncoder().encodeToString(hashtableBytes));
			fw.close();
		}
		else
		{
			this.messaage.append("\n Droplet id : " + Base64.getUrlEncoder().encodeToString(hashtableBytes));
			this.messaage.append("\n Droplet exists in loal Storage. Skipped...");
		}
		
		if(!dropletUrlFile.exists())
		{
			FileWriter fwUrl = new FileWriter(dropletUrlFile);
			fwUrl.append(dropletUrl);
			fwUrl.close();
		}
		
		this.messaage.append("\n---------------------------------\n");
	}
	
	
}
