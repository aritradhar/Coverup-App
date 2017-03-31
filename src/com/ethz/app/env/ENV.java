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

package com.ethz.app.env;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.JSONObject;

@SuppressWarnings("unused")
public class ENV {

	public static String DELIM;
	static
	{
		String OS = System.getProperty("os.name");
		if(OS.contains("windows"))
			DELIM = "\\";

		else
			DELIM = "/";
	}

	public static final String APP_STORAGE_LOC = "APP_DATA";
	static
	{
		File file = new File(APP_STORAGE_LOC);
		if(!file.exists())
			file.mkdir();
	}
	
	
	public static boolean AUTO_PILOT = false;

	public static final String APP_STORAGE_BROWSER_COMM_DROPLET_LOC = "DROPLET";
	public static final String APP_STORAGE_BROWSER_COMM_DROPLET_BIN_LOC = "DROPLET_BIN";

	public static final String APP_STORAGE_COMPLETED_DROPLET_FILE = "info.txt";
	public static final String APP_STORAGE_DROPLET_URL = "dropletUrl.txt";
	public static final String APP_STORAGE_COMPLETE_DATA = "data.txt";
	public static final String APP_STORAGE_COMPLETE_DATA_AON = "data_dec.txt";
	public static final String APP_STORAGE_TABLE_DUMP = "table.json";
	public static final String APP_STORAGE_TABLE_MULTIPLE_PROVIDER_DUMP = "table";
	public static final String APP_STORAGE_KEY_FILE = "key.bin";
	public static final String APP_STORAGE_INTERACTIVE_DATA = "Interactive";
	public static final String APP_STORAGE_SLICE_TABLE_LOC = "SLICE_TABLE";
	public static final String APP_STORAGE_SLICE_TABLE = "sliceTable.txt";
	public static final String APP_STORAGE_COVERT_BROWSER_START_PAGE = "sliceTableHTML.htm";
	public static final String APP_STORAGE_SLICE_ID_FILES_LOC = "SLICE_ID";
	public static final String APP_STORAGE_SLICE_ID_FILE = "slice_id.bin";
	public static final String APP_STORAGE_SLICE_FILE_FORMAT = ".slice";
	
	public static final String APP_STORATE_DROPLET_STORE_LOCATION = "DROPLET";
	//chat stuff
	public static final String APP_STORAGE_CHAT_LOC = "Chat";
	public static final String APP_STORAGE_CHAT_DISPATCH_LOC = "Dispatch";
	public static final String APP_STORAGE_CHAT_LOG_LOC = "LOGS";
	public static final String APP_STORAGE_CHAT_REPO_FILE = "CHATLOG.log";

	public static final String APP_STORAGE_CHAT_DISPATCH_FILE = "CHAT.bin";
	public static final String APP_STORAGE_ENC_CHAT_DISPATCH_FILE = "CHAT_ENC.bin";

	public static final String APP_STORAGE_INCOMING_CHAT_DATABASE_FILE = APP_STORAGE_LOC + DELIM
			+ APP_STORAGE_CHAT_LOC + DELIM 
			+ "INCOMING_CHAT.db";

	public static final String REPLICATED_CHROME_DB = APP_STORAGE_LOC + DELIM + "webappsstore.sqlite";

	public static final String BROWSER_FIREFOX = "BROWSER_FIREFOX";
	public static final String BROWSER_CHROME = "BROWSER_CHROME";

	//magic bytes
	public static final byte INTR_MAGIC_BYTE = (byte)0x06;
	public static final int INTR_MAGIC_BYTES_LEN = 8;

	public static final byte CHAT_MAGIC_BYTES = (byte)0x0A;
	public static final int CHAT_MAGIC_BYTES_LEN = 8;

	public static final byte BROADCAST_CHAT_MAGIC_BYTES = (byte)0x0B;
	public static final int BROADCAST_CHAT_MAGIC_BYTES_LEN = 16;
	//magic byte ends

	public static final int CHAT_PUBLIC_ADDRESS_LEN = 8;
	public static final int CHAT_POLLING_RATE = 775;
	public static final boolean CHAT_ACCEPT_UNKOWN_PUBLIC_KEY = true;
	
		
	public static final int FIXED_CHAT_LEN = 512;
	public static final int FIXED_ENC_CHAT_PACK_LEN = FIXED_CHAT_LEN - 64 - 16 -16; //64 for signature and 16 for IV (pad the plain text to make it this size), last 16 is for the AES padding
	public static final int FIXED_CHAT_TYPE_LEN = FIXED_ENC_CHAT_PACK_LEN - CHAT_PUBLIC_ADDRESS_LEN - BROADCAST_CHAT_MAGIC_BYTES_LEN - 4 - 4; //last 4 is to make it divisible by 16
	
	

	public static final String APP_STORAGE_CHAT_KEY_FILE = APP_STORAGE_LOC + DELIM + APP_STORAGE_CHAT_LOC + DELIM + "KeyFile.key";
	public static final String APP_STORAGE_PUBLIC_KEY_LIST = APP_STORAGE_LOC + DELIM + APP_STORAGE_CHAT_LOC + DELIM + "pkList.txt";
	public static final String CHROME_BASE_DIR_LOCATION_CONFIG_FILE = APP_STORAGE_LOC + DELIM + "chromeBaseDirConfigFile.conf";
	public static final String FIREFOX_BASE_DIR_LOCATION_CONFIG_FILE = APP_STORAGE_LOC + DELIM + "firefoxBaseDirConfigFile.conf";
	public static boolean BROWSER_BASE_DIR_LOCATION_CONFIG_FILE_EXISTS = false;

	public static String _CHROME_BASE_FILE;
	public static String _FIREFOX_BASE_FILE;

	static
	{

		File fileI = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_INTERACTIVE_DATA);
		if(!fileI.exists())
			fileI.mkdir();
		File fileSlice = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_SLICE_TABLE_LOC);
		if(!fileSlice.exists())
			fileSlice.mkdir();
		File fileSliceID = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_SLICE_ID_FILES_LOC);
		if(!fileSliceID.exists())
			fileSliceID.mkdir();

		File FileChatLoc = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_CHAT_LOC);
		if(!FileChatLoc.exists())
			FileChatLoc.mkdir();
		File FileChatDispatch = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_CHAT_LOC + DELIM + APP_STORAGE_CHAT_DISPATCH_LOC);
		if(!FileChatDispatch.exists())
			FileChatDispatch.mkdir();
		File FileChatLog = new File(APP_STORAGE_LOC + DELIM + APP_STORAGE_CHAT_LOC + DELIM + APP_STORAGE_CHAT_LOG_LOC);
		if(!FileChatLog.exists())
			FileChatLog.mkdir();

		File chatFIle = new File(APP_STORAGE_PUBLIC_KEY_LIST);
		if(chatFIle.exists())
			try {
				chatFIle.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		//Initialization
		//db execution
		if(!new File(REPLICATED_CHROME_DB).exists())
		{
			try {
				Class.forName("org.sqlite.JDBC");
				Connection c = DriverManager.getConnection("jdbc:sqlite:" + REPLICATED_CHROME_DB);
				c.createStatement().executeUpdate("CREATE TABLE webappsstore2 (originAttributes TEXT, originKey TEXT, scope TEXT, key TEXT, value TEXT)");
				c.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if(!new File(APP_STORAGE_INCOMING_CHAT_DATABASE_FILE).exists())
		{
			try {
				Class.forName("org.sqlite.JDBC");
				Connection c = DriverManager.getConnection("jdbc:sqlite:" + APP_STORAGE_INCOMING_CHAT_DATABASE_FILE);
				c.createStatement().executeUpdate("CREATE TABLE incoming_chat ('sender'	TEXT, 'data' TEXT, 'signature' TEXT NOT NULL UNIQUE, PRIMARY KEY(signature))");
				c.close();
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}


		if(new File(CHROME_BASE_DIR_LOCATION_CONFIG_FILE).exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(CHROME_BASE_DIR_LOCATION_CONFIG_FILE));
				String st = null;
				while((st = br.readLine()) != null)
					_CHROME_BASE_FILE = st;

				br.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}	
		}

		if(new File(FIREFOX_BASE_DIR_LOCATION_CONFIG_FILE).exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(FIREFOX_BASE_DIR_LOCATION_CONFIG_FILE));
				String st = null;
				while((st = br.readLine()) != null)
					_FIREFOX_BASE_FILE = st;

				br.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}	
		}
	}

	//Specific exception messages for exception handling
	public static final String EXCEPTION_INTR_MESSAGE_MAGIC_BYTES = "EXCEPTION_INTR_MESSAGE_MAGIC_BYTES";
	public static final String EXCEPTION_CHAT_MESSAGE_MAGIC_BYTES = "EXCEPTION_CHAT_MESSAGE_MAGIC_BYTES";
	public static final String EXCEPTION_MESSAGE_MISMATCHED_PACKET_SIZE = "EXCEPTION_MESSAGE_MISMATCHED_PACKET_SIZE";
	public static final String EXCEPTION_MESSAGE_MISMATCHED_INTR_PACKET_SIZE = "EXCEPTION_MESSAGE_MISMATCHED_INTR_PACKET_SIZE";
	public static final String EXCEPTION_MESSAGE_CIPHER_FAILURE = "EXCEPTION_MESSAGE_CIPHER_FAILURE";
	public static final String EXCEPTION_MESSAGE_GARBAGE_PACKET = "EXCEPTION_MESSAGE_GARBAGE_PACKET";
	public static final String EXCEPTION_CHAT_SIGNATURE_ERROR = "EXCEPTION_CHAT_SIGNATURE_ERROR";
	public static final String EXCEPTION_BROWSER_EXTENSION_MISSING = "EXCEPTION_BROWSER_EXTENSION_MISSING";

	public static final String EXCEPTION_MESSAGE_EMPTY_TABLE = "EMPTY_TABLE";
	//////////////////////////////////////////////////////

	public static final int DISPACTH_REQUEST_THRESHOLD = 0;

	public static final int AES_KEY_SIZE = 16;

	public static final String APP_JSON_EXTENSION = ".table";
	public static final String APP_BIN_EXTENSION = ".bin";


	public static final int FOUNTAIN_CHUNK_SIZE = 10000;

	public static final String DATABASE_TABLE_COL = "BQVZ-tildem-table";
	public static final String DATABASE_DROPLET_COL = "BQVZ-tildem";

	public static final String BROWSER_COMM_LINK = "comm.txt";

	public static final boolean MULTIPLE_PROVIDER_SUPPORT =  true;
	public static final boolean AON_SUPPORT = true;

	public static final boolean EXPERIMENTAL = false;
	//native messaging port
	public static final int NATIVE_MESSAGE_PORT = 43457;

	public static final boolean COMPRESSION_SUPPORT = false;

	public static final char[] PROGRESS_SYMB = {'-', '\\', '|', '/'};

	//if false then broadcast mode
	public static final boolean CHAT_MODE_RELAY = true;

	public static final String ABOUT_MESSAGE = "To those who can hear me, I say - do not despair. \n The misery that is now upon us is but the passing of greed - "
			+ "\nthe bitterness of men who fear the way of human progress. \nThe hate of men will pass,"
			+ " and dictators die, and the power they took from the people \n will return to the people.\nAnd so long as men die, liberty will never perish. .....\n"
			+ "- Charlie Chaplin (The Great Dictator)";
	
	public static boolean isAdmin() {
	    String groups[] = (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
	    for (String group : groups) {
	        if (group.equals("S-1-5-32-544"))
	            return true;
	    }
	    return false;
	}
	
	//cryptographic params
	public static final String CRYPTO_HASH_ALGORITHM = "SHA-256";
	public static final String CRYPTO_SYMMETRIC_ALGORITHM = "AES";
	public static final int CRYPTO_SYMMETRIC_KEY_SIZE = 128;
	public static final String CRYPTO_SYMMETRIC_MODE_OF_OPERATION = "AES/CTR/PKCS5Padding";
}
