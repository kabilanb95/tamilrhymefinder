package com.arkmusic.tamilrhymefinder.algorithms;

import java.util.Comparator;

public class SyllableComparator implements Comparator<String>
{
	private int ideal_syllable_count;

	public SyllableComparator(int ideal_syllable_count)
	{
		this.ideal_syllable_count = ideal_syllable_count;
	}

	public int compare(String word_a, String word_b)
	{
		int word_a_syllables = WordProcessor.getSyllables(word_a);
		int word_b_syllables = WordProcessor.getSyllables(word_b);

		int word_a_syllables_deviation_from_ideal = Math.abs(this.ideal_syllable_count - WordProcessor.getSyllables(word_a));
		int word_b_syllables_deviation_from_ideal = Math.abs(this.ideal_syllable_count - WordProcessor.getSyllables(word_b));

		return word_a_syllables_deviation_from_ideal - word_b_syllables_deviation_from_ideal;
	}
}
