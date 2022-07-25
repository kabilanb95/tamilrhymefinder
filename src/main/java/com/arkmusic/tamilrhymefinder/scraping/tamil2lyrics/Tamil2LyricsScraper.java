package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics;

import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.scraping.CommonUtil;
import com.arkmusic.tamilrhymefinder.scraping.FileUtil;
import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages.MoviesListPage;

public class Tamil2LyricsScraper 
{
	private static Logger logger=Logger.getLogger(Tamil2LyricsScraper.class.getName());
	
	public static int WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS=5;

	public static final String SCRAPED_FILE_PATH="/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/wordsset.txt";

	private static HashSet<String> word_set=null;
	
	public static boolean IS_WRITE_TO_FILE=true;
	
	public static void main(String[] args) throws IOException
	{		
		word_set=new HashSet<String>();
		
		Long start=System.currentTimeMillis();
		HashSet<String> words=getWordsByScrapingAllPages();
		logger.info("All words scraped:"+words.toString());
		Long end=System.currentTimeMillis();
		logger.info("Total Time:"+(end-start));
	}
	
	public static void writeWordsToFile(HashSet<String> words) throws IOException
	{
		if(!IS_WRITE_TO_FILE)
		{
			return;
		}
		word_set.addAll(words);
		FileUtil.writeStringToFile(SCRAPED_FILE_PATH, CommonUtil.toCommanSeperatedString(word_set));
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
				String movie_list_page_url=MoviesListPage.getListURLByIndex(i);
				document=Jsoup.connect(movie_list_page_url).get();
				logger.info("Current movie list page index : "+current_page_index);
				words.addAll(MoviesListPage.getWordsByScrapingAllMoviesInCurrentMovieListPage(document));
				logger.info("MOVIE SCRAPING COMPLETED FOR LIST PAGE INDEX : "+current_page_index);
			}
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Could not scrape this page fully : "+document.location(), e);
			}
		}		
				
		return words;
	}
}
