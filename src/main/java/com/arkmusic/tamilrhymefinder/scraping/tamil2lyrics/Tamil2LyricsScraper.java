package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics;

import java.io.IOException;
import java.util.TreeSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.scraping.FileUtil;
import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages.MoviesListPage;

public class Tamil2LyricsScraper 
{
	private static Logger logger=Logger.getLogger(Tamil2LyricsScraper.class.getName());
	
	public static int WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS=4;

	public static final String SCRAPED_FILE_PATH="/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/tamil.txt";

	private static TreeSet<String> word_set=null;
	
	//config variables
	public static boolean IS_WRITE_TO_FILE=true;//set as false if you are not scraping and storing to file
	public static boolean IS_CONTINUE_SCRAPING=false;//set as true if scraping stopped in the middle and we are continuing it for the remaining pages alone

	public static void main(String[] args) throws IOException
	{		
		if(IS_CONTINUE_SCRAPING)
		{
			String word_set_str=FileUtil.getFileAsString(SCRAPED_FILE_PATH);
			word_set=CommonUtil.commanSeperatedStringToTreeSet(word_set_str);
			logger.info("Word set was loaded from file with "+word_set.size()+" words");
		}
		else 
		{
			word_set=new TreeSet<String>();			
		}
		
		Long start=System.currentTimeMillis();
		TreeSet<String> words=getWordsByScrapingAllPages();
		logger.info("All words scraped:"+words.toString());
		Long end=System.currentTimeMillis();
		logger.info("Total Time:"+(end-start));
	}
	
	public static void writeWordsToFile(TreeSet<String> words) throws IOException
	{
		if(!IS_WRITE_TO_FILE)
		{
			return;
		}
		
		word_set.addAll(words);
		FileUtil.writeStringToFile(SCRAPED_FILE_PATH, CommonUtil.toCommanSeperatedString(word_set));
		logger.info("Writing to file success!!");
	}
		
	public static TreeSet<String> getWordsByScrapingAllPages() throws IOException
	{
		TreeSet<String> words=new TreeSet<String>();
		
		Document document=Jsoup.connect(MoviesListPage.URL).get();
		
		int last_page_index=MoviesListPage.getLastPageIndex(document);
		
		logger.info("Total of "+last_page_index+" pages were found, Going to scrape them one by one");
		
		for(int i=1;i<=last_page_index;i++)
		{			
			try 
			{
				String movie_list_page_url=MoviesListPage.getListURLByIndex(i);
				document=Jsoup.connect(movie_list_page_url).get();
				int current_page_index=MoviesListPage.getCurrentPageIndex(document);
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
