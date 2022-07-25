package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;

import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.Tamil2LyricsScraper;

public class SongPage 
{
	private static Logger logger=Logger.getLogger(SongPage.class.getName());

	public static final String
	MUSIC_BY_TEXT="(?i)Music by",
	MALE_SINGER_DESCRIPTION="Male : ",
	FEMALE_SINGER_DESCRIPTION="Female : "
	;
	
	public static HashSet<String> getWordsFromSongPage(Document document) throws IOException
	{
		HashSet<String> words=new HashSet<String>();
		
		String lyrics=getLyrics(document);
		lyrics=lyrics.replace("\n", " ").replace("\r", " ");
		
		for(String word : lyrics.split(" "))
		{
			word=word.toLowerCase();
			word=word.replaceAll("[^a-zA-Z]+", "");//replace all non alphabetics
			word=word.trim();
			if(!word.trim().equals(""))
			{
				words.add(word);				
			}
		}
		
		logger.info("Got words from the page "+document.location()+" -->"+words.toString());
		
		//nothing will happen if Tamil2LyricsScraper.IS_WRITE_TO_FILE equals false
		Tamil2LyricsScraper.writeWordsToFile(words);
		
		return words;
	}
	
	public static String getLyrics(Document document)
	{
		return document.select("#English").first().text().split(MUSIC_BY_TEXT)[1].replace(MALE_SINGER_DESCRIPTION, "").replace(FEMALE_SINGER_DESCRIPTION, "").trim();
	}
}
