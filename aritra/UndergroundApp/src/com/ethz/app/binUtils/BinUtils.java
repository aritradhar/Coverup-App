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

package com.ethz.app.binUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;

import com.ethz.app.AppMain;
import com.ethz.app.env.ENV;

public class BinUtils {
	
	
	public static String tableBinToTableJson(byte[] tableBytes, byte[] serverPublicKey) throws RuntimeException
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
				throw new RuntimeException("Signature could not be verified");
		} 

		catch (NoSuchAlgorithmException e) 
		{
			throw new RuntimeException("SHA-256 provider not found");
		}
		
		String tableStr = new String(tableBytes, StandardCharsets.UTF_8);
		String signatureStr = Base64.getUrlEncoder().encodeToString(signatureBytes);
		
		JSONObject jObject = new JSONObject();
		jObject.put("table", tableStr);
		jObject.put("signature", signatureStr);
		
		return jObject.toString(2);
	}
	
	public static String dropletBinToDropletJson(byte[] dropletBytes, byte[] serverPublicKey, StringBuffer messageLog)
	throws RuntimeException
	{
		JSONObject jObject = new JSONObject();
		
		//try to decrypt 
		//packet len(4) | seedlen (4) ->0 | Magic (8) | Data | Padding
		//Data -> slice id (8) | slice index (4) | slice_data_len (4) | slice data (n) | padding|
		
		if(AppMain.cipher != null)
		{
			try 
			{
				byte[] decBytes = AppMain.cipher.doFinal(dropletBytes);
				int tillNow = 0;
				byte[] fixedPacketLenBytes = new byte[Integer.BYTES];
				System.arraycopy(decBytes, tillNow, fixedPacketLenBytes, 0, fixedPacketLenBytes.length);
				tillNow += fixedPacketLenBytes.length;

				int fixedPacketLen = ByteBuffer.wrap(fixedPacketLenBytes).getInt();
				byte[] seedLenBytes = new byte[Integer.BYTES];
				System.arraycopy(decBytes, tillNow, seedLenBytes, 0, seedLenBytes.length);
				tillNow += seedLenBytes.length;
				int seedLen = ByteBuffer.wrap(seedLenBytes).getInt();

				if(seedLen == 0)
				{
					if(fixedPacketLen != decBytes.length)
						throw new RuntimeException(ENV.EXCEPTION_MESSAGE_MISMATCHED_INTR_PACKET_SIZE);
					
					byte[] magicBytes = new byte[ENV.INTR_MAGIC_BYTES_LEN];
					System.arraycopy(decBytes, tillNow, magicBytes, 0, magicBytes.length);
					byte[] idealInteractiveMagicBytes = new byte[ENV.INTR_MAGIC_BYTES_LEN];
					Arrays.fill(idealInteractiveMagicBytes, ENV.INTR_MAGIC_BYTE);
					
					byte[] idealChatMagicBytes = new byte[ENV.CHAT_MAGIC_BYTES_LEN];
					Arrays.fill(idealChatMagicBytes, ENV.CHAT_MAGIC_BYTES);
					
					//magic byte for interactive data found!
					if(Arrays.equals(idealInteractiveMagicBytes, magicBytes))
						throw new RuntimeException(ENV.EXCEPTION_INTR_MESSAGE_MAGIC_BYTES);
					else if(Arrays.equals(idealChatMagicBytes, magicBytes))
						throw new RuntimeException(ENV.EXCEPTION_CHAT_MESSAGE_MAGIC_BYTES);
				}	
			}
			catch (IllegalBlockSizeException | BadPaddingException e1) {
				throw new RuntimeException(ENV.EXCEPTION_MESSAGE_CIPHER_FAILURE);
			}
		}
		
		int tillNow = 0;
		byte[] fixedPacketLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, fixedPacketLenBytes, 0, fixedPacketLenBytes.length);
		tillNow += fixedPacketLenBytes.length;
		
		int fixedPacketLen = ByteBuffer.wrap(fixedPacketLenBytes).getInt();
		
		//System.out.println(fixedPacketLen);
		
		byte[] seedLenBytes = new byte[Integer.BYTES];
		System.arraycopy(dropletBytes, tillNow, seedLenBytes, 0, seedLenBytes.length);
		tillNow += seedLenBytes.length;
		int seedLen = ByteBuffer.wrap(seedLenBytes).getInt();
		
		if(seedLen != 32 && fixedPacketLen != dropletBytes.length)
			throw new RuntimeException(ENV.EXCEPTION_MESSAGE_GARBAGE_PACKET);
		
		else if(fixedPacketLen != dropletBytes.length)
			throw new RuntimeException(ENV.EXCEPTION_MESSAGE_MISMATCHED_PACKET_SIZE);
		
		//packet len(4) | seedlen (4) ->0 | Magic (16) | Data | Padding
		
		
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
				throw new RuntimeException("Droplet Signbature not verified");
							
			signatureBase64 = Base64.getUrlEncoder().encodeToString(signature);
		} 

		catch (NoSuchAlgorithmException e) 
		{
			throw new RuntimeException("SHA-256 provider missing");
		}
		
		jObject.put("url", url);
		jObject.put("f_id", f_id);
		jObject.put("droplet", dropletJson.toString());
		jObject.put("signature", signatureBase64);	
		
		
		//System.out.println(dropletJson.toString(2));
		return jObject.toString(2);
	}
	
	/**
	 * 
	 * @param dropletBytes
	 * @param messageLog
	 * @return Object array
	 * 1. slice id in long
	 * 2. slice index in integer
	 * 3. slice data in byte array
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static Object[] intrBinProcess(byte[] dropletBytes, StringBuffer messageLog) throws IllegalBlockSizeException, BadPaddingException
	{
		//packet len(4) | seedlen (4) ->0 | Magic (8) | Data | Padding
		//Data -> slice id (8) | slice index (4) | slice_data_len (4) | slice data (n) | padding|
		
		byte[] decBytes = AppMain.cipher.doFinal(dropletBytes);
		byte[] sliceIdBytes = new byte[8];
		System.arraycopy(decBytes, 16, sliceIdBytes, 0, 8);
		long sliceId = ByteBuffer.wrap(sliceIdBytes).getLong();
		byte[] sliceIndexBytes = new byte[4];
		System.arraycopy(decBytes, 24, sliceIndexBytes, 0, 4);
		int sliceIndex = ByteBuffer.wrap(sliceIndexBytes).getInt();
		byte[] sliceDataLenBytes = new byte[4];
		System.arraycopy(decBytes, 28, sliceDataLenBytes, 0, 4);
		int sliceDatalen = ByteBuffer.wrap(sliceDataLenBytes).getInt();
		byte[] sliceDataBytes = new byte[sliceDatalen];
		System.arraycopy(dropletBytes, 32, sliceDataBytes, 0, sliceDatalen);
		
		return new Object[]{sliceId, sliceIndex, sliceDataBytes};
	}
	
	/**
	 * 
	 * @param data
	 * @return Array of string.
	 * index 0 -> sender public address
	 * index 1 -> decrypted chat data
	 * index 2 -> data hash for databse indexing
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	
	public static String[] chatMessageBinProcess(byte[] _data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		//packet len(4) | seedlen (4) ->0 | Magic (8) | Data | Padding
		//	   0		  1		 	 2	   	  3			 4
		//R_adder (8)| S_addr(8) | iv(16) | len(4) | enc_Data (n) | sig (64) (on 0|1|2|3|4)
		byte[] data = AppMain.cipher.doFinal(_data);
			
		int tillNow = 16;
		byte[] receiverAddress = new byte[ENV.PUBLIC_ADDRESS_LEN]; //holy shit this is me.
		System.arraycopy(data, tillNow, receiverAddress, 0, receiverAddress.length);
		tillNow += receiverAddress.length;
		
		byte[] senderAddress = new byte[ENV.PUBLIC_ADDRESS_LEN]; //who send this to me.
		System.arraycopy(data, tillNow, senderAddress, 0, senderAddress.length);
		tillNow += senderAddress.length;
		
		String sernderAddressStr = Base64.getUrlEncoder().encodeToString(senderAddress);
		byte[] senderPublicKey = BinUtils.addresskeyMap.get(sernderAddressStr);
		if(senderPublicKey == null)
			return null;
		
		int datalenStart = ENV.PUBLIC_ADDRESS_LEN * 2 + 16 + 16; //next 16 is for the preamble stuff
		byte[] encDataLenBytes = new byte[4];
		System.arraycopy(data, datalenStart, encDataLenBytes, 0, 4);
		int encDataLen = ByteBuffer.wrap(encDataLenBytes).getInt();
		int lenToVerify = ENV.PUBLIC_ADDRESS_LEN * 2 + 16 + 4 + encDataLen;
		byte[] toVerify = new byte[lenToVerify];
		System.arraycopy(data, 16, toVerify, 0, toVerify.length);
		//byte[] toVerify = Arrays.copyOf(data, lenToVerify);
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedToVerify = md.digest(toVerify);
		
		byte[] signature = new byte[64];
		System.arraycopy(data, lenToVerify + 16, signature, 0, 64);
		boolean res = Curve25519.getInstance(Curve25519.BEST).verifySignature(senderPublicKey, hashedToVerify, signature);
			
		if(res == false)
			throw new RuntimeException(ENV.EXCEPTION_CHAT_SIGNATURE_ERROR);
		
		
		byte[] sharedSecret = Curve25519.getInstance(Curve25519.BEST).calculateAgreement(senderPublicKey, BinUtils.myPrivateKey);
		
		md.reset();
		byte[] hashedSharedSecret = md.digest(sharedSecret);
		byte[] aesKey = new byte[hashedSharedSecret.length / 2];
		byte[] aesIV = new byte[hashedSharedSecret.length / 2];
		
		System.arraycopy(hashedSharedSecret, 0, aesKey, 0, aesKey.length);
		System.arraycopy(data, tillNow, aesIV, 0, aesIV.length);
		tillNow += aesIV.length;
		
		tillNow += 4;
		
		byte[] encryptedData = new byte[encDataLen];
		System.arraycopy(data, tillNow, encryptedData, 0, encDataLen);
		
		SecretKey key = new SecretKeySpec(aesKey, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(aesIV));
        
        byte[] decryptedChat = cipher.doFinal(encryptedData);
        
		return new String[]{sernderAddressStr, new String(decryptedChat, StandardCharsets.UTF_8), 
				Base64.getUrlEncoder().encodeToString(hashedToVerify)};
	}
	
	
	
	//-------------------------------------------------------------------------------------------------------------------------
	//necessary only to process chat data
	public static Map<String, byte[]> addresskeyMap = new HashMap<>();

	public static void initializeChatData() throws Exception
	{
		BinUtils.populateAddressKey();
		BinUtils.keyFileGenChat();
	}
	
	private static void populateAddressKey() throws Exception
	{
		
		BufferedReader br = new BufferedReader(new FileReader(ENV.APP_STORAGE_PUBLIC_KEY_LIST));
		String str = null;
		while((str = br.readLine()) != null)
		{
			if(str.length() == 0)
				continue;
				
			byte[] pk = Base64.getUrlDecoder().decode(str);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedPk = md.digest(pk);
			byte[] publicAddressBytes = Arrays.copyOf(hashedPk, ENV.PUBLIC_ADDRESS_LEN);
			String address = Base64.getUrlEncoder().encodeToString(publicAddressBytes);
			
			addresskeyMap.put(address, pk);
		}
		br.close();
	}
	
	public static byte[] myPublicKey;
	private static byte[] myPrivateKey;
	public static String myPublicAddress;
	
	private static void keyFileGenChat() throws IOException, NoSuchAlgorithmException
	{
		File keyFile = new File(ENV.APP_STORAGE_CHAT_KEY_FILE);
		if(!keyFile.exists())
		{
			Curve25519KeyPair keyPair = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();
			myPrivateKey = keyPair.getPrivateKey();
			myPublicKey = keyPair.getPublicKey();

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hasheddPk = md.digest(myPublicKey);
			byte[] publicAddressBytes = Arrays.copyOf(hasheddPk, ENV.PUBLIC_ADDRESS_LEN);
			myPublicAddress = Base64.getUrlEncoder().encodeToString(publicAddressBytes);
			
			JSONObject jObject = new JSONObject();
			jObject.put("pk", Base64.getUrlEncoder().encodeToString(myPublicKey));
			jObject.put("sk", Base64.getUrlEncoder().encodeToString(myPrivateKey));
			jObject.put("address", myPublicAddress);

			FileWriter fw = new FileWriter(ENV.APP_STORAGE_CHAT_KEY_FILE);
			fw.write(jObject.toString(2));
			fw.close();
		}
		else
		{
			BufferedReader br = new BufferedReader(new FileReader(ENV.APP_STORAGE_CHAT_KEY_FILE));
			StringBuffer stb = new StringBuffer();
			String str = null;

			while((str = br.readLine()) != null)
				stb.append(str);
			br.close();

			JSONObject jObject = new JSONObject(stb.toString());
			myPublicKey = Base64.getUrlDecoder().decode(jObject.getString("pk"));
			myPrivateKey = Base64.getUrlDecoder().decode(jObject.getString("sk"));
			myPublicAddress = jObject.getString("address");

		}
	}
	//chat stuff ends here
	//----------------------------------------------------------------------------------------------------------------------------------
	
	//test
	public static void main(String[] args) throws Exception {
		
		//byte[] b = Files.readAllBytes(new File("C:\\Users\\Aritra\\workspace_Mars\\UndergroundApp\\APP_DATA\\DROPLET_BIN\\134211151\\5.bin").toPath());
		//System.out.println(b.length);
		
		
		BufferedReader br = new BufferedReader(new FileReader("binResp.txt"));
		String s = br.readLine();
		br.close();
		
		String j = dropletBinToDropletJson(Base64.getDecoder().decode(s), Base64.getUrlDecoder().decode("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg="), new StringBuffer());
		
		System.out.println(j);
		
		//String j = tableBinToTableJson(Base64.getDecoder().decode(s), Base64.getUrlDecoder().decode("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg="));
		
		//System.out.println(j);
		
	}

}
