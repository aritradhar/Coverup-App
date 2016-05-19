package com.ethz.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

public class App {
	
	public String fileName;
	public byte[] ServerpublicKey;
	
	String version, message, signatureString;
	
	public App(String fileName)
	{
		this.fileName = fileName;
	}
	
	public void extractMessage() throws IOException, NoSuchAlgorithmException
	{
		BufferedReader br = new BufferedReader(new FileReader(this.fileName));
		
		StringBuffer sb = new StringBuffer();
		String st = null;
		
		while((st = br.readLine()) != null)
		{
			sb.append(st);
		}
		br.close();
		
		JSONObject jObject = new JSONObject(sb);
		this.version = jObject.getString("version");
		this.message = jObject.getString("message");
		this.signatureString = jObject.getString("signature");
		
		byte[] messageBytes = this.message.getBytes();
		byte[] messageHash = MessageDigest.getInstance("sha-512").digest(messageBytes);
		
		byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureString);
		
		if(!Curve25519.getInstance("best").verifySignature(ServerpublicKey, messageHash, signatureBytes))
		{
			throw new RuntimeException("SIgnature is not verified");
		}
	}
	
	public static void main(String[] args) {
		
	}

}
