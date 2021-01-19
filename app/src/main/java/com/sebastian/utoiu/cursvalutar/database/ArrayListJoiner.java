package com.sebastian.utoiu.cursvalutar.database;

import java.util.ArrayList;

public class ArrayListJoiner
{
	public String joinToString( String[] list, String separator )
	{
		return String.join(separator, list);
	}

	public String joinToString( ArrayList<String> list, String separator )
	{
		return String.join(separator, list);
	}
}
