package com.arkmusic.tamilrhymefinder.server.words;

import java.util.ArrayList;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.arkmusic.tamilrhymefinder.server.Configuration;
import com.arkmusic.tamilrhymefinder.server.Language;

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
			
			//Custom sorting order for better UX
			ArrayList<String> rhyming_words_list=new ArrayList<String>(rhyming_word_set);
			WordProcessor.sortListAccordingToSyllablesDeviationFromInputWord(word, rhyming_words_list);
			rhyming_words.put(last_n_char_index+"", new JSONArray(rhyming_words_list.toString()));
			
			rhyming_words_added_to_response_till_now.addAll(rhyming_word_set);
		}
		
		
		
		return rhyming_words;
	}
}
