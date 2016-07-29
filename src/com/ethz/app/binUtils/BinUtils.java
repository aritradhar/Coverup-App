package com.ethz.app.binUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
		System.arraycopy(tableBytes, fixedPacketSizeByte.length, tableLenBytes, 0, tableLenBytes.length);
		int tableLen  = ByteBuffer.wrap(tableLenBytes).getInt();
		
		byte[] tableByte = new byte[tableLen];
		System.arraycopy(tableBytes, fixedPacketSizeByte.length + tableLenBytes.length, tableByte, 0, tableLen);
		
		//System.out.println("HT : " + Base64.getUrlEncoder().encodeToString(tableByte));
		
		
		byte[] signatureBytes = new byte[64];
		System.arraycopy(tableBytes, fixedPacketSizeByte.length + tableLenBytes.length + tableLen, signatureBytes, 
				0, signatureBytes.length);
		try 
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashtableBytes = md.digest(tableByte);
			
			boolean signatureVerify = Curve25519.getInstance("best").verifySignature(serverPublicKey, hashtableBytes, signatureBytes);
			if(!signatureVerify)
				return null;
		} 

		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		String tableStr = new String(tableBytes, StandardCharsets.UTF_8);
		String signatureStr = Base64.getUrlEncoder().encodeToString(signatureBytes);
		
		JSONObject jObject = new JSONObject();
		jObject.put("table", tableStr);
		jObject.put("signature", signatureStr);
		
		return jObject.toString(2);
	}
	
	public static String dropletBinToDropletJson(byte[] dropletBytes, byte[] serverPublicKey, StringBuffer messageLog)
	{
		JSONObject jObject = new JSONObject();
		
		int tillNow = 0;
		byte[] fixedPacketLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, fixedPacketLenBytes, 0, fixedPacketLenBytes.length);
		tillNow += fixedPacketLenBytes.length;
		
		byte[] seedLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, seedLenBytes, 0, seedLenBytes.length);
		tillNow += seedLenBytes.length;
		int seedLen = ByteBuffer.wrap(seedLenBytes).getInt();
		
		byte[] seedBytes = new byte[seedLen];
		System.arraycopy(dropletBytes, tillNow, seedBytes, 0, seedLen);
		tillNow += seedLen;
		
		byte[] num_chunksBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, num_chunksBytes, 0, num_chunksBytes.length);
		tillNow += num_chunksBytes.length;
		
		int num_chunks = ByteBuffer.wrap(num_chunksBytes).getInt();
		
		byte[] dataLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, dataLenBytes, 0, dataLenBytes.length);
		tillNow += dataLenBytes.length;
		
		int dataLen = ByteBuffer.wrap(dataLenBytes).getInt();
		
		byte[] data = new byte[dataLen];
		System.arraycopy(dropletBytes, tillNow, data, 0, dataLen);
		
		tillNow += dataLen;
		
		//bind droplet json
		JSONObject dropletJson = new JSONObject();
		dropletJson.put("seed", Base64.getUrlEncoder().encodeToString(seedBytes));
		dropletJson.put("num_chunks", num_chunks);
		dropletJson.put("data", Base64.getUrlEncoder().encodeToString(data));
		
		
		byte[] urlLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, urlLenBytes, 0, urlLenBytes.length);
		tillNow += urlLenBytes.length;
		int urlLen = ByteBuffer.wrap(urlLenBytes).getInt();
		
		byte[] urlBytes = new byte[urlLen];
		System.arraycopy(dropletBytes, tillNow, urlBytes, 0, urlLen);
		tillNow += urlLen;
		String url = new String(urlBytes, StandardCharsets.UTF_8);
		
		byte[] f_idBytes = new byte[Long.BYTES];
		System.arraycopy(dropletBytes, tillNow, f_idBytes, 0, f_idBytes.length);
		tillNow += f_idBytes.length;
		
		long f_id = ByteBuffer.wrap(f_idBytes).getLong();
		
		byte[] signature = new byte[64];
		System.arraycopy(dropletBytes, tillNow, signature, 0, signature.length);
		
		
		byte[] dropletByte = new byte[seedLenBytes.length + seedBytes.length + num_chunksBytes.length + dataLenBytes.length + data.length];

		System.arraycopy(seedLenBytes, 0, dropletByte, 0, seedLenBytes.length);
		System.arraycopy(seedBytes, 0, dropletByte, seedLenBytes.length, seedBytes.length);
		System.arraycopy(num_chunksBytes, 0, dropletByte, seedLenBytes.length + seedBytes.length, num_chunksBytes.length);
		System.arraycopy(dataLenBytes, 0, dropletByte, seedLenBytes.length + seedBytes.length + num_chunksBytes.length, dataLenBytes.length);
		System.arraycopy(data, 0, dropletByte, seedLenBytes.length + seedBytes.length + num_chunksBytes.length + dataLenBytes.length, data.length);

		
		byte[] dataToSign = new byte[fixedPacketLenBytes.length + dropletByte.length + urlLenBytes.length + urlBytes.length + f_idBytes.length];

		System.arraycopy(fixedPacketLenBytes, 0, dataToSign, 0, fixedPacketLenBytes.length);
		System.arraycopy(dropletByte, 0, dataToSign, fixedPacketLenBytes.length, dropletByte.length);
		System.arraycopy(urlLenBytes, 0, dataToSign, fixedPacketLenBytes.length + dropletByte.length, urlLenBytes.length);
		System.arraycopy(urlBytes, 0, dataToSign, fixedPacketLenBytes.length + dropletByte.length + urlLenBytes.length, urlBytes.length);
		System.arraycopy(f_idBytes, 0, dataToSign, fixedPacketLenBytes.length + dropletByte.length + urlLenBytes.length + urlBytes.length, f_idBytes.length);

		String signatureBase64 = null;;
		try 
		{

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashDataToSign = md.digest(dataToSign);
			boolean verifiyResult =  Curve25519.getInstance("best").verifySignature(serverPublicKey, hashDataToSign, signature);
			
			if(!verifiyResult)
				return null;
			
			signatureBase64 = Base64.getUrlEncoder().encodeToString(signature);
		} 

		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
			return null;
		}
		
		jObject.put("url", url);
		jObject.put("f_id", f_id);
		jObject.put("droplet", dropletJson.toString());
		jObject.put("signature", signatureBase64);	
		
		
		//System.out.println(dropletJson.toString(2));
		return jObject.toString(2);
	}
	
	public static void main(String[] args) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader("binResp.txt"));
		String s = br.readLine();
		br.close();
		
		//String j = dropletBinToDropletJson(Base64.getDecoder().decode(s), Base64.getUrlDecoder().decode("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg="), new StringBuffer());
		
		//System.out.println(j);
		
		String j = tableBinToTableJson(Base64.getDecoder().decode(s), Base64.getUrlDecoder().decode("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg="));
		
		System.out.println(j);
		
	}

}
