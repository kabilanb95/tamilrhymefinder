package com.arkmusic.tamilrhymefinder;

import java.util.Arrays;
import java.util.TreeSet;

public class CommonUtil 
{
	public static String toCommanSeperatedString(TreeSet<String> hashset)
	{
		String str=hashset.toString().replace("[", "").replace("]", "").trim();
		return removeAllWhiteSpaces(str);
	}

	public static TreeSet<String> commanSeperatedStringToTreeSet(String comma_seperated_string)
	{
		return new TreeSet<String>(Arrays.asList(comma_seperated_string.split(",")));
	}

	public static String removeAllWhiteSpaces(String str)
	{
		return str.replaceAll("\\s+","");
	}

	//removes all multiple whitespaces with single space
	public static String trim(String str)
	{
		return str.replaceAll("\\s+"," ");
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNReturnNull(String str,int n)
	{
		return str.length() > n ? str.substring(str.length() - n) : null;
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNReturnInput(String str,int n)
	{
		return str.length() > n ? str.substring(str.length() - n) : str;
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNThrowException(String str,int n)
	{
		if(str.length() > n)
		{
			return str.substring(str.length() - n);
		}
		else
		{	
			throw new RuntimeException("This should not have happened, check the core logic of entire code. str:"+str+" n:"+n);
		}
	}
}
