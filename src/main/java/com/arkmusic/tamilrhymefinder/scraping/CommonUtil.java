package com.arkmusic.tamilrhymefinder.scraping;

import java.util.HashSet;

public class CommonUtil 
{
	public static String toCommanSeperatedString(HashSet<String> hashset)
	{
		String str=hashset.toString().replace("[", "").replace("]", "").trim();
		return removeAllWhiteSpaces(str);
	}
	
	public static String removeAllWhiteSpaces(String str)
	{
		return str.replaceAll("\\s+","");
	}
}
