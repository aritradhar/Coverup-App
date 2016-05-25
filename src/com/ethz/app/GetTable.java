package com.ethz.app;

import java.util.Iterator;

import org.json.JSONObject;

public class GetTable {
	
	
	public static Object[][] getTableData(String jsonString)
	{
			
		JSONObject jObject = new JSONObject(jsonString);
		int tableSize = jObject.keySet().size();
		
		Object[][] toReturn = new Object[tableSize][];
		
		Iterator<String> keys = jObject.keys();
		int i = 0;
		
		while(keys.hasNext())
		{
			String key = keys.next();
			//String value = jObject.getString(key);
			toReturn[i++] = new Object[]{key, "Select"};
		}
		
		return toReturn;
	}

}
