package com.ethz.app.binUtils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;

public class BinUtils {
	
	
	public static String tableBinToTableJson(byte[] tableBytes, byte[] serverPublicKey)
	{
		/*
		 * P = fixed packet size
		 table-> P (4) | table_len (4) | table (n) | signature (64) | padding (P - 72 - n) |</p><p>
		 */
		
		byte[] fixedPacketSizeByte = new byte[Integer.BYTES];
		System.arraycopy(tableBytes, 0, fixedPacketSizeByte, 0, fixedPacketSizeByte.length);
		//int fixePacketSize = ByteBuffer.wrap(fixedPacketSizeByte).getInt();
		
		byte[] tableLenBytes = new byte[Integer.BYTES];
		System.arraycopy(tableBytes, 0, tableLenBytes, fixedPacketSizeByte.length, tableLenBytes.length);
		int tableLen  = ByteBuffer.wrap(tableLenBytes).getInt();
		
		byte[] tableByte = new byte[tableLen];
		System.arraycopy(tableBytes, 0, tableByte, fixedPacketSizeByte.length + tableLenBytes.length, tableLen);
		
		byte[] signatureBytes = new byte[64];
		System.arraycopy(tableBytes, 0, signatureBytes, 
				fixedPacketSizeByte.length + tableLenBytes.length + tableLen, signatureBytes.length);
		try 
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashtableBytes = md.digest(tableLenBytes);
			boolean signatureVerify = Curve25519.getInstance("best").verifySignature(serverPublicKey, hashtableBytes, signatureBytes);
			if(!signatureVerify)
				return null;
		} 

		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		String tableStr = new String(tableBytes);
		String signatureStr = Base64.getUrlEncoder().encodeToString(signatureBytes);
		
		JSONObject jObject = new JSONObject();
		jObject.put("table", tableStr);
		jObject.put("signature", signatureStr);
		
		return jObject.toString(2);
	}
	
	public static String dropletBinToDropletJson(byte[] jsonBytes, byte[] serverPublicKey)
	{
		
		return null;
	}

}
