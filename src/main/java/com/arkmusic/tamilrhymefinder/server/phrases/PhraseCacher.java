package com.arkmusic.tamilrhymefinder.server.phrases;

import java.util.TreeSet;

import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.mapdb.MapDBUtil;

public class PhraseCacher
{	
	Language language;
	
	public PhraseCacher(Language language)
	{
		this.language=language;
	}
	
	public TreeSet<String> getPhrasesByWord(String word)
	{
		return MapDBUtil.getPhrases(this.language,word);
	}
}
