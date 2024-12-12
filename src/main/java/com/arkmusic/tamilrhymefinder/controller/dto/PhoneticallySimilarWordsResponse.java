package com.arkmusic.tamilrhymefinder.controller.dto;

import java.util.List;

import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.repository.WordRepository.PhoneticPropertyCriteria;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PhoneticallySimilarWordsResponse
{
	String word;
	Language language;
	PhoneticPropertyCriteria criteria;
	List<String> similarWords;
}
