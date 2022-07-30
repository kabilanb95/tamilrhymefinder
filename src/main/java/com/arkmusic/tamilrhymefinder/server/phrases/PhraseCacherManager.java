package com.arkmusic.tamilrhymefinder.server.phrases;

import java.util.HashMap;

import com.arkmusic.tamilrhymefinder.server.Language;

public class PhraseCacherManager 
{
	private static HashMap<Language, PhraseCacher> phrase_cacher_manager;
	
	public static void init()
	{
		phrase_cacher_manager=new HashMap<Language, PhraseCacher>();
		
		for(Language language : Language.values())
		{
			phrase_cacher_manager.put(language,new PhraseCacher(language));
		}
	}
	
	public static PhraseCacher getPhraseCacherByLanguage(Language language)
	{
		return phrase_cacher_manager.get(language);
	}
}
