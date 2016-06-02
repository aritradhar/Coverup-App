package com.ethz.app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

public class TableChecker 
{
	public String tableJson;
	public String signature;

	public String publicKeyString;
	public byte[] ServerpublicKey;

	public boolean verifyResult;

	public void loadtableData() throws SQLException
	{
		FirefoxCacheExtract ffce = new FirefoxCacheExtract();
		ffce.getFirefoxCacheFile();
		ffce.conncetDatabase("tildem-table");

		JSONObject jObject = new JSONObject(ffce.jsonData);

		this.tableJson = jObject.getString("table");
		this.signature = jObject.getString("signature");
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
