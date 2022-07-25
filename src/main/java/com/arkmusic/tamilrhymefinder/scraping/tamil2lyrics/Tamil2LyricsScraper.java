package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages.MoviesListPage;

public class Tamil2LyricsScraper 
{
	private static Logger logger=Logger.getLogger(Tamil2LyricsScraper.class.getName());
	
	public static void main(String[] args) throws IOException
	{
		Long start=System.currentTimeMillis();
		logger.info(getWordsByScrapingAllPages().toString());
		Long end=System.currentTimeMillis();
		logger.info("Total Time:"+(end-start));
	}
	
	public static HashSet<String> getWordsByScrapingAllPages() throws IOException
	{
		HashSet<String> words=new HashSet<String>();
		
		Document document=Jsoup.connect(MoviesListPage.URL).get();
		
		int last_page_index=MoviesListPage.getLastPageIndex(document);
		
		logger.info("Total of "+last_page_index+" pages were found, Going to scrape them one by one");
		
		for(int i=1;i<=last_page_index;i++)
		{
			int current_page_index=MoviesListPage.getCurrentPageIndex(document);
			
			try 
			{
				logger.info("Current movie list page index : "+current_page_index);
				
				HashSet<String> all_movie_urls=new HashSet<String>();
				
				logger.info("All movie URLS in this page : "+all_movie_urls.toString());
				
			}
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Could not scrape this page fully : "+document.location(), e);
			}
			
		}		
				
		return words;
	}
}
