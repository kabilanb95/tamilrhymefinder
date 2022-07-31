package com.arkmusic.tamilrhymefinder.server.phrases;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.ResourceUtil;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;

public class PhraseCacher
{	
	HashMap<String, HashSet<String>> phrases_by_words;
	
	public PhraseCacher(Language language) throws IOException
	{
		setPhrasesMap(language);
	}
		
	private void setPhrasesMap(Language language) throws IOException
	{
		phrases_by_words=GsonUtil.getHugeJSONAsHashMap(ResourceUtil.getFilePath(Language.TAMIL.phrasejson_filepath));
	}
	
	public HashSet<String> getPhrasesByWord(String word)
	{
		if(phrases_by_words.containsKey(word))
		{
			return phrases_by_words.get(word);
		}
		
		throw new PhraseNotFoundException(word);
	}
}
