package com.arkmusic.tamilrhymefinder.server.phrases;

import org.json.JSONArray;

import com.arkmusic.tamilrhymefinder.server.Language;

public class Phrases
{
	public static JSONArray getPhrases(String word, Language language)
	{
		return new JSONArray(PhraseCacherManager.getPhraseCacherByLanguage(language).getPhrasesByWord(word).toString());
	}
}
