package com.arkmusic.tamilrhymefinder.server;

import java.util.Arrays;
import java.util.List;

public class Configuration 
{
	public static List<Integer> getSupportedLastNChars()
	{
		//entries to the array should be in descending order ONLY
		Integer[] supported_last_n_chars=new Integer[] {5,4,3};
		return Arrays.asList(supported_last_n_chars);
	}
}
