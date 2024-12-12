package com.arkmusic.tamilrhymefinder.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.arkmusic.tamilrhymefinder.configuration.AppConfig;
import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.models.Word;
import com.arkmusic.tamilrhymefinder.mongodb.MongoDBConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import lombok.Data;

public class WordRepository
{
	private static WordRepository instance;
	private final MongoCollection<Word> wordCollection;

	private WordRepository()
	{
		MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
		this.wordCollection = database.getCollection("words", Word.class);
	}

	public static WordRepository getInstance()
	{
		if(instance == null)
		{
			synchronized(WordRepository.class)
			{
				if(instance == null)
				{
					instance = new WordRepository();
				}
			}
		}
		return instance;
	}

	@Data
	public static class PhoneticPropertyCriteria
	{
		private boolean isMatchingConsonantVowelPattern = false;
		private boolean isMatchingSoundexCode = false;
		private boolean isMatchingMetaphoneCode = false;
		private boolean isMatchingSyllableCount = false;
		private boolean isMatchingStressPattern = false;

		// Check if any feature is set to true
		public boolean hasAnyMatchingFeature()
		{
			return isMatchingConsonantVowelPattern || isMatchingSoundexCode || isMatchingMetaphoneCode || isMatchingSyllableCount || isMatchingStressPattern;
		}
	}

	public List<Word> findRhymingWords(String word, Language language)
	{
		if(word == null || language == null)
		{
			throw new IllegalArgumentException("Invalid parameters.");
		}

		// Fetch the supported rhyme lengths from configuration
		String supportedRhymeLengths = AppConfig.getProperty("supported_rhymes"); // "3,4,5"
		String[] rhymeLengths = supportedRhymeLengths.split(",");

		// Prepare the list to hold the words that match the rhyming criteria
		List<Word> matchingWords = new ArrayList<>();

		// Iterate through each suffix length
		for(String lengthStr : rhymeLengths)
		{
			int suffixLength = Integer.parseInt(lengthStr);

			// If the word is shorter than the suffix length, skip the query for this length
			if(word.length() < suffixLength)
			{
				continue; // Skip to the next suffix length
			}

			// Prepare the regex pattern for the current suffix length
			String suffix = word.substring(word.length() - suffixLength); // Get the suffix of the word
			String regex = "(" + suffix + ")$"; // Regex to match words that end with this suffix

			// Create the MongoDB query with the regex filter for word matching and language
			Bson query = Filters.and(Filters.eq("language", language), Filters.regex("word", regex));

			// Execute the query and collect the matching words
			List<Word> words = wordCollection.find(query).into(new ArrayList<>());

			// If words are found, add them to the result list
			if(!words.isEmpty())
			{
				matchingWords.addAll(words);
			}
		}
		
		// removing input word itself to avoid duplication
		matchingWords.remove(word);

		// Return the list of matching words, or an empty list if no matches were found
		return matchingWords;
	}

	// Method to find a word by word and language (for Requirement 2)
	public Word findByWordAndLanguage(String word, Language language)
	{
		if(word == null || language == null)
		{
			throw new IllegalArgumentException("Both word and language must be provided.");
		}

		Bson query = Filters.and(Filters.eq("word", word), Filters.eq("language", language));
		return wordCollection.find(query).first();
	}

	public List<String> findByPhoneticFeatures(Word word, PhoneticPropertyCriteria criteria)
	{
		if(word == null)
		{
			throw new IllegalArgumentException("Word must be provided.");
		}

		// Ensure at least one feature is enabled
		if(!criteria.hasAnyMatchingFeature())
		{
			throw new IllegalArgumentException("At least one phonetic feature filter must be specified.");
		}

		List<Bson> filters = new ArrayList<>();

		// Apply filters based on the provided criteria
		if(criteria.isMatchingConsonantVowelPattern())
		{
			filters.add(Filters.eq("consonantVowelPattern", word.getConsonantVowelPattern()));
		}
		if(criteria.isMatchingSoundexCode())
		{
			filters.add(Filters.eq("soundexCode", word.getSoundexCode()));
		}
		if(criteria.isMatchingMetaphoneCode())
		{
			filters.add(Filters.eq("metaphoneCode", word.getMetaphoneCode()));
		}
		if(criteria.isMatchingSyllableCount())
		{
			filters.add(Filters.eq("syllableCount", word.getSyllableCount()));
		}
		if(criteria.isMatchingStressPattern())
		{
			filters.add(Filters.eq("stressPattern", word.getStressPattern()));
		}

		Bson query = Filters.and(filters);
		
		List<String> matchingWords = wordCollection.find(query).map(wordObj -> wordObj.getWord()).into(new ArrayList<>());
		
		
		// removing input word itself to avoid duplication
		matchingWords.remove(word.getWord());

		// Return the 'word' property of matching documents
		return matchingWords;
	}
}
