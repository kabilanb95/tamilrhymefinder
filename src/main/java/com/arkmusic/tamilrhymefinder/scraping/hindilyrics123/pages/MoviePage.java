package com.arkmusic.tamilrhymefinder.scraping.hindilyrics123.pages;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.arkmusic.tamilrhymefinder.scraping.hindilyrics123.HindiLyrics123Scraper;

public class MoviePage
{
	private static Logger logger=Logger.getLogger(MoviePage.class.getName());

	public static void scrapeLyricsOfAllSongsInCurrentMoviePage(Document document) throws IOException, InterruptedException
	{
		LinkedHashSet<String> song_urls=getAllSongURLS(document);
		for(String song_url : song_urls)
		{
			try
			{
				document=Jsoup.connect(song_url).get();
				logger.info("Current song url page :"+song_url);
				String lyrics=SongPage.getLyricsWithFormatting(document);
				
				HindiLyrics123Scraper.writeLyricsToFile(lyrics);
				Thread.sleep(HindiLyrics123Scraper.WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS*1000);//To avoid DDOS blocking
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE,"Unable to get scrape song from movie",e);
			}
		}
	}
	
	public static LinkedHashSet<String> getAllSongURLS(Document document)
	{
		LinkedHashSet<String> song_urls=new LinkedHashSet<>();
		
		Elements song_link_tags=document.select("a[href*='in-hindi'][href$='.html']");
		
		for(Element song_link_tag : song_link_tags)
		{
			song_urls.add(song_link_tag.absUrl("href"));
		}
		
		return song_urls;
	}
}
