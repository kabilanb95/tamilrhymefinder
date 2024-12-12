package com.arkmusic.tamilrhymefinder.controller.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arkmusic.tamilrhymefinder.algorithms.WordProcessor;
import com.arkmusic.tamilrhymefinder.configuration.AppConfig;
import com.arkmusic.tamilrhymefinder.models.Word;

public class RhymingWordsResponse
{
	private Map<Integer, List<String>> rhymingWords;
	private String word; // Input word for rhyming
	private String language;

	// Constructor that initializes the rhyming words based on the input word and
	// language
	public RhymingWordsResponse(String word, String language, List<Word> words)
	{
		this.word = word; // The input word to find rhymes for
		this.language = language;
		this.rhymingWords = new HashMap<>();

		// Categorize and sort words based on suffix lengths
		categorizeAndSortRhymingWords(words);		
	}

	// Method to categorize and sort words based on their suffix lengths
	private void categorizeAndSortRhymingWords(List<Word> words)
	{
		// Fetch the supported rhyme lengths from configuration (e.g., "3,4,5")
		String supportedRhymeLengths = AppConfig.getProperty("supported_rhymes");
		String[] rhymeLengths = supportedRhymeLengths.split(",");

		// Convert the rhyme lengths into integers and sort them in descending order
		List<Integer> sortedSuffixLengths = new ArrayList<>();
		for(String lengthStr : rhymeLengths)
		{
			int suffixLength = Integer.parseInt(lengthStr);
			sortedSuffixLengths.add(suffixLength);
		}
		Collections.sort(sortedSuffixLengths, Collections.reverseOrder()); // Sorting suffix lengths from highest to lowest

		// Set to track already categorized words to avoid duplicates
		Set<String> categorizedWords = new HashSet<>();

		// Iterate over each suffix length starting from the longest to the shortest
		for(int suffixLength : sortedSuffixLengths)
		{
			// List to store words that rhyme with the current suffix length
			List<String> rhymingWordsForLength = new ArrayList<>();

			// Iterate through all words and check if they match the current suffix length
			for(Word candidateWord : words)
			{
				String candidateWordText = candidateWord.getWord(); // Extract the word text from the Word object

				// Skip the word if it's too short to match the current suffix length
				if(candidateWordText.length() < suffixLength || word.length() < suffixLength)
				{
					continue; // Skip this word, as it can't match this suffix length
				}

				// Extract the suffix of the input word to match
				String wordSuffix = word.substring(word.length() - suffixLength);

				// Check if the candidate word ends with the extracted suffix and has not been
				// categorized
				if(candidateWordText.endsWith(wordSuffix) && !categorizedWords.contains(candidateWordText))
				{
					// Add the word to the rhyme group for this suffix length
					rhymingWordsForLength.add(candidateWordText);
					// Mark this word as categorized to avoid adding it again
					categorizedWords.add(candidateWordText);
				}
			}

			// If any rhyming words were found for this suffix length, add them to the
			// result map
			if(!rhymingWordsForLength.isEmpty())
			{
				// Sort the rhyming words for this suffix length based on syllables deviation
				// from the input word
				WordProcessor.sortListAccordingToSyllablesDeviationFromInputWord(word, (ArrayList<String>) rhymingWordsForLength);

				// Add the sorted list to the rhymingWords map
				rhymingWords.put(suffixLength, rhymingWordsForLength);
			}
		}
	}

	// Getter for the categorized rhyming words
	public Map<Integer, List<String>> getRhymingWords()
	{
		return rhymingWords;
	}

	// Getter for the input word
	public String getWord()
	{
		return word;
	}

	// Getter for the language
	public String getLanguage()
	{
		return language;
	}
}
