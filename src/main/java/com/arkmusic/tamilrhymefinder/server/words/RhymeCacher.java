package com.arkmusic.tamilrhymefinder.server.words;

import java.util.TreeMap;
import java.util.TreeSet;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.server.Configuration;
import com.arkmusic.tamilrhymefinder.server.Language;

public class RhymeCacher 
{
	TreeMap<Integer, TreeMap<String, TreeSet<String>>> n_char_to_n_char_rhyme_map;
	TreeSet<String> all_words_of_language;
	
	public RhymeCacher(Language language)
	{
		this.all_words_of_language=WordsManager.getWordsOfLanguage(language);
		setWordsMappedByNLastChars();
	}
	
	public TreeSet<String> getRhymingWordsByNLastChars(String word,int no_of_last_chars_to_consider)
	{		
		if(!Configuration.getSupportedLastNChars().contains(no_of_last_chars_to_consider))
		{
			throw new RuntimeException("Unsupported 'no_of_last_chars_to_consider' : "+no_of_last_chars_to_consider);	
		}
		
		String rhyme=CommonUtil.getLastNCharsIfInputStringIsSmallerThanNReturnNull(word,no_of_last_chars_to_consider);
		
		if(rhyme==null)
		{
			return new TreeSet<>();			
		}
		
		if(isRhymeMapContainsRhyme(rhyme, word, n_char_to_n_char_rhyme_map.get(no_of_last_chars_to_consider)))
		{
			TreeSet<String> rhyming_words=n_char_to_n_char_rhyme_map.get(no_of_last_chars_to_consider).get(rhyme);
			rhyming_words.remove(word);
			return rhyming_words;
		}
		
		return new TreeSet<>();
	}
	
	private void setWordsMappedByNLastChars()
	{		
		n_char_to_n_char_rhyme_map=new TreeMap<Integer, TreeMap<String, TreeSet<String>>>();
		
		for(int last_n_char_index : Configuration.getSupportedLastNChars())
		{
			n_char_to_n_char_rhyme_map.put(last_n_char_index, new TreeMap<String, TreeSet<String>>());
		}

		for(String word : this.all_words_of_language)
		{
			word=word.toLowerCase();
			
			for(int last_nth_char : Configuration.getSupportedLastNChars())
			{				
				TreeMap<String, TreeSet<String>> rhyme_map=n_char_to_n_char_rhyme_map.get(last_nth_char);
				
				String rhyme=CommonUtil.getLastNCharsIfInputStringIsSmallerThanNReturnNull(word,last_nth_char);

				if(rhyme==null)
				{
					continue;
				}
				
				if(word.endsWith(rhyme))
				{
					if(rhyme_map.containsKey(rhyme)==false)
					{
						rhyme_map.put(rhyme, new TreeSet<String>());
					}
					rhyme_map.get(rhyme).add(word);
				}				
			}
		}
	}
	
	private static void addToRhymeMap(String rhyme,String word,TreeMap<String, TreeSet<String>> rhyme_map)
	{
		if(rhyme_map.containsKey(rhyme)==false)
		{
			rhyme_map.put(rhyme, new TreeSet<String>());
		}
		rhyme_map.get(rhyme).add(word);
	}
	
	private static boolean isRhymeMapContainsRhyme(String rhyme,String word,TreeMap<String, TreeSet<String>> rhyme_map)
	{
		return rhyme_map.containsKey(rhyme);
	}

}
