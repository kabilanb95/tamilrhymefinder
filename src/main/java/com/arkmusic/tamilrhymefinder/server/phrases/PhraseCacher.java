package com.arkmusic.tamilrhymefinder.server.phrases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONObject;

import com.arkmusic.tamilrhymefinder.server.Language;
import com.google.gson.Gson;

public class PhraseCacher
{	
	HashMap<String, HashSet<String>> phrases_by_words;
	
	public PhraseCacher(Language language)
	{
		setPhrasesMap(getPhrasesByWordsJSON(language));
	}
	
	private static JSONObject getPhrasesByWordsJSON(Language language)
	{
		return PhraseManager.getPhrasesJSONByLanguage(language);
	}
	
	private void setPhrasesMap(JSONObject phrases_by_words_json)
	{
		phrases_by_words=new HashMap<String, HashSet<String>>();
		
		Iterator<String> words=phrases_by_words_json.keys();

		String word=null;
		
		Gson gson=new Gson();
		
		while(words.hasNext())
		{
			word=words.next();			
			phrases_by_words.put(word.toLowerCase(), gson.fromJson(phrases_by_words_json.getJSONArray(word).toString(), HashSet.class));
		}
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
