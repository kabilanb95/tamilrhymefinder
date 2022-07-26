package com.arkmusic.tamilrhymefinder.server.words;

import java.util.HashSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.arkmusic.tamilrhymefinder.server.Configuration;

public class RhymingWords 
{	
	public static JSONObject getRhymingWords(String word,Language language)
	{
		TreeSet<String> rhyming_words_added_to_response_till_now=new TreeSet<String>();

		RhymeCacher rhyme_cacher=RhymeCacherManager.getRhymeCacherByLanguage(language);
		
		JSONObject rhyming_words=new JSONObject();
		
		for(int last_n_char_index : Configuration.getSupportedLastNChars())
		{
			TreeSet<String> rhyming_word_set=rhyme_cacher.getRhymingWordsByNLastChars(word, last_n_char_index);
			rhyming_word_set.removeAll(rhyming_words_added_to_response_till_now);
			rhyming_words.put(last_n_char_index+"_letter_rhyme", new JSONArray(rhyming_word_set.toString()));
			rhyming_words_added_to_response_till_now.addAll(rhyming_word_set);
		}
		
		return rhyming_words;
	}
}
