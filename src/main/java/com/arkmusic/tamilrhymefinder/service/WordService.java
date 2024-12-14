package com.arkmusic.tamilrhymefinder.service;

import java.util.List;

import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.models.Word;
import com.arkmusic.tamilrhymefinder.repository.WordRepository;
import com.arkmusic.tamilrhymefinder.repository.WordRepository.PhoneticPropertyCriteria;

public class WordService
{
	private static WordService instance;
	private final WordRepository wordRepository;

	private WordService()
	{
		this.wordRepository = WordRepository.getInstance();
	}

	public static WordService getInstance()
	{
		if(instance == null)
		{
			synchronized(WordService.class)
			{
				if(instance == null)
				{
					instance = new WordService();
				}
			}
		}
		return instance;
	}

	// Fetch raw list of rhyming words based on last 3, 4, and 5 characters
	public List<Word> getRhymingWords(String word, Language language)
	{
		return wordRepository.findRhymingWords(word, language);
	}

	// Fetch the Word document for a given word and language
	public Word findByWordAndLanguage(String word, Language language)
	{
		return wordRepository.findByWordAndLanguage(word, language);
	}

	public List<String> findByPhoneticFeatures(String word, Language language, PhoneticPropertyCriteria criteria)
	{
		// Retrieve the Word object for the given word and language
		Word wordObj = findByWordAndLanguage(word, language);

		if(wordObj == null)
		{
			throw new IllegalArgumentException("Word not found: " + word + " for language: " + language);
		}

		// Delegate to the repository method with the PhoneticFeatureCriteria object
		return wordRepository.findByPhoneticFeatures(wordObj, language, criteria);
	}
}
