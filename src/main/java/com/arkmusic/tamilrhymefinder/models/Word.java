package com.arkmusic.tamilrhymefinder.models;

import java.util.TreeSet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // No-argument constructor for MongoDB to instantiate
@RequiredArgsConstructor
public class Word
{
	@NonNull
	Language language;
	@NonNull
	String word;
	@NonNull
	TreeSet<String> phrases;

	// Phonetic features
	@NonNull
	String consonantVowelPattern; // Consonant-Vowel pattern (CVCV)
	@NonNull
	String soundexCode; // Soundex code
	@NonNull
	String metaphoneCode; // Metaphone code
	@NonNull
	Integer syllableCount; // Syllable count
	@NonNull
	String stressPattern; // Stress pattern (101)
}