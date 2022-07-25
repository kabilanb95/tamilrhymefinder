package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.Tamil2LyricsScraper;

public class MoviePage 
{
	private static Logger logger=Logger.getLogger(MoviePage.class.getName());

	public static HashSet<String> getAllSongURLS(Document document)
	{
		HashSet<String> song_urls=new HashSet<>();
		
		Elements song_link_tags=document.select(".container").select("a[href*='/lyrics/']");
		
		for(Element song_link_tag : song_link_tags)
		{
			song_urls.add(song_link_tag.attr("href"));
		}
		
		return song_urls;
	}
	
	public static HashSet<String> getWordsByScrapingAllSongsInCurrentMoviePage(Document document) throws IOException, InterruptedException
	{
		HashSet<String> words=new HashSet<>();
		
		HashSet<String> song_urls=MoviePage.getAllSongURLS(document);
		for(String song_url : song_urls)
		{
			document=Jsoup.connect(song_url).get();
			logger.info("Current song url page :"+song_url);
			words.addAll(SongPage.getWordsFromSongPage(document));
			Thread.sleep(Tamil2LyricsScraper.WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS*1000);//To avoid DDOS blocking
		}
		
		return words;
	}

}
