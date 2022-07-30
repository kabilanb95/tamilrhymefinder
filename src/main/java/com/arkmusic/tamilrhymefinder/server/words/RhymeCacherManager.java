package com.arkmusic.tamilrhymefinder.server.words;

import java.util.TreeMap;

import com.arkmusic.tamilrhymefinder.server.Language;

public class RhymeCacherManager 
{
	private static TreeMap<Language, RhymeCacher> rhyme_cache_manager;
	
	public static void init()
	{
		rhyme_cache_manager=new TreeMap<Language, RhymeCacher>();
		
		for(Language language : Language.values())
		{
			rhyme_cache_manager.put(language,new RhymeCacher(language));
		}
	}
	
	public static RhymeCacher getRhymeCacherByLanguage(Language language)
	{
		return rhyme_cache_manager.get(language);
	}
}
