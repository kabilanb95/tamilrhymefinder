package com.arkmusic.tamilrhymefinder.server.words;

import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.MapDBUtil;
import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.ResourceUtil;

public class WordsManager 
{
	private static TreeSet<String> TAMIL_WORDS;

	public static void init() throws IOException
	{
		TAMIL_WORDS=MapDBUtil.getAllWords(Language.TAMIL);
	}
	
	public static TreeSet<String> getWordsOfLanguage(Language language)
	{
		if(language==Language.TAMIL)
		{
			return TAMIL_WORDS;
		}
		
		throw new RuntimeException("Unsupported language : "+language);
	}
}
