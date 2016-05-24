package com.ethz.app;

import java.util.HashSet;
import java.util.Set;

public class QueryMake {
	
	public static Set<String> query = new HashSet<>();
	
	public static void insert(String qry)
	{
		query.add(qry);
	}

}
