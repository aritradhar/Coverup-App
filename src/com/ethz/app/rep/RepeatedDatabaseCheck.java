package com.ethz.app.rep;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

import com.ethz.app.FirefoxCacheExtract;
import com.ethz.app.TableChecker;

public class RepeatedDatabaseCheck {
	
	public static byte[] ServerPublickey;
	
	
	public RepeatedDatabaseCheck() throws Exception 
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
		
		String dropletStr = droplet.concat(dropletUrl);
		
		byte[] dropletByte = dropletStr.getBytes(StandardCharsets.UTF_8);
		String signatureString = jObject.getString("signature");
		byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureString);
		
		try 
		{
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashtableBytes = md.digest(dropletByte);
			boolean check = Curve25519.getInstance("best").verifySignature(RepeatedDatabaseCheck.ServerPublickey, hashtableBytes, signatureBytes);
			
			if(!check)
				throw new RuntimeException("Signature verification failed");
		} 
		
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	
}
