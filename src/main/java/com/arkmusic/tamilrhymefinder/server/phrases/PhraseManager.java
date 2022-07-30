package com.arkmusic.tamilrhymefinder.server.phrases;

import java.io.IOException;

import org.json.JSONObject;

import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.ResourceUtil;

public class PhraseManager 
{
	private static JSONObject phrases_by_words_tamil;

	public static void init() throws IOException
	{
		phrases_by_words_tamil=new JSONObject(ResourceUtil.getFileAsStringFromResourcesFolder(Language.TAMIL.phrasejson_filepath));
	}
	
	public static JSONObject getPhrasesJSONByLanguage(Language language)
	{
		if(language==Language.TAMIL)
		{
			return phrases_by_words_tamil;
		}
		
		throw new RuntimeException("Unsupported language : "+language);
	}
}
