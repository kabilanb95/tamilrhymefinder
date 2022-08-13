package com.arkmusic.tamilrhymefinder.server.words;

import java.util.ArrayList;
import java.util.Collections;

public class WordProcessor
{
	public static void sortListAccordingToSyllablesDeviationFromInputWord(String input_word, ArrayList<String> words_list)
	{
		Collections.sort(words_list, new SyllableComparator(getSyllables(input_word)));
	}

	public static int getSyllables(String word)
	{
		int syllables = 0;

		int token;

		for(int i = 0; i <= word.length() - 2; i++)
		{
			char current_char = word.charAt(i);
			char next_char = word.charAt(i + 1);
			if(isVowel(current_char) & isConsonant(next_char))
			{
				syllables++;
			}
		}

		if(isVowel(word.charAt(word.length() - 1)))
		{
			syllables++;
		}

		return syllables;
	}

	private static boolean isVowel(char c)
	{
		return "AEIOUaeiou".indexOf(c) != -1;
	}

	private static boolean isConsonant(char c)
	{
		return !isVowel(c);
	}

}
