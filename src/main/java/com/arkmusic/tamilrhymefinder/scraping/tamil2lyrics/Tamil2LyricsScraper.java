package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics;

import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.scraping.FileUtil;
import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages.MoviesListPage;

public class Tamil2LyricsScraper 
{
	private static Logger logger=Logger.getLogger(Tamil2LyricsScraper.class.getName());
	
	public static int WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS=4;

	public static final String RESOURCE_FOLDER_PATH="/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/";
	public static final String ALL_WORDS_SCRAPED_FILE_PATH=RESOURCE_FOLDER_PATH+"tamil.txt";
	public static final String ALL_PHRASES_SCRAPED_JSON_FILE_PATH=RESOURCE_FOLDER_PATH+"phrases_tamil.json";

	private static TreeSet<String> word_set=null;
	private static JSONObject word_to_phrase_json=null;
	
	//config variables
	public static boolean IS_WRITE_TO_FILE=true;//set as false if you are not scraping and storing to file
	public static boolean IS_CONTINUE_SCRAPING=true;//set as true if scraping stopped in the middle and we are continuing it for the remaining pages alone

	public static void main(String[] args) throws IOException
	{
		Long start=System.currentTimeMillis();
		scrapeAndStoreAllPhrases();
		Long end=System.currentTimeMillis();
		logger.info("Total Time:"+(end-start));
	}

	//Scraper
	public static void scrapeAndStoreAllPhrases() throws IOException
	{
		if(IS_CONTINUE_SCRAPING)
		{
			String word_to_phrase_json_str=FileUtil.getFileAsString(ALL_PHRASES_SCRAPED_JSON_FILE_PATH);
			word_to_phrase_json=new JSONObject(word_to_phrase_json_str);
			logger.info("Phrases JSON was loaded from file with "+word_to_phrase_json.keySet().size()+" words");
		}
		else
		{
			word_to_phrase_json=new JSONObject();
		}

		Document document=Jsoup.connect(MoviesListPage.URL).get();
		
		int last_page_index=MoviesListPage.getLastPageIndex(document);
		
		logger.info("Total of "+last_page_index+" pages were found, Going to scrape them one by one");
		
		for(int i=1;i<=last_page_index;i++)
		{		
			boolean is_run=i>162;
			if(!is_run)
			{
				continue;
			}
			
			try 
			{
				String movie_list_page_url=MoviesListPage.getListURLByIndex(i);
				document=Jsoup.connect(movie_list_page_url).get();
				int current_page_index=MoviesListPage.getCurrentPageIndex(document);
				logger.info("Current movie list page index : "+current_page_index);
				MoviesListPage.scrapeLyricsOfAllMoviesInCurrentMovieListPage(document);
				logger.info("MOVIE SCRAPING COMPLETED FOR LIST PAGE INDEX : "+current_page_index);
			}
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Could not scrape this page fully : "+document.location(), e);
			}
		}		
	}
	
	//Scraper
	public static void scrapeAndStoreAllWords() throws IOException
	{		
		if(IS_CONTINUE_SCRAPING)
		{
			String word_set_str=FileUtil.getFileAsString(ALL_WORDS_SCRAPED_FILE_PATH);
			word_set=CommonUtil.commanSeperatedStringToTreeSet(word_set_str);
			logger.info("Word set was loaded from file with "+word_set.size()+" words");
		}
		else 
		{
			word_set=new TreeSet<String>();			
		}
		
		TreeSet<String> words=getWordsByScrapingAllPages();
		logger.info("All words scraped:"+words.toString());
	}

	public static void writeLyricsToFile(String lyrics) throws IOException
	{
		if(Tamil2LyricsScraper.IS_WRITE_TO_FILE)
		{			
			String[] phrases=lyrics.split("\\r?\\n");
			
			for(String phrase : phrases)
			{
				phrase=phrase.trim();
				phrase=phrase.toLowerCase();
				
				String phrase_without_space=CommonUtil.removeAllWhiteSpaces(phrase);
				phrase_without_space=phrase_without_space.replaceAll("[^a-zA-Z]", "");

				//skip if single word
				if(phrase.split("\\s+").length==1)
				{
					continue;
				}
				//skip if info text
				if(phrase.contains(":"))
				{
					continue;
				}
				//skip if phrase too small
				if(phrase_without_space.equals("") || phrase_without_space.length()<10)
				{
					continue;
				}
				
				String[] words=phrase.split("\\s+");
				
				for(String word : words)
				{
					word=word.trim();
					word=word.replaceAll("[^a-zA-Z]", "");
					word=word.toLowerCase();

					if(word.equals("") || word.length()<=2)
					{
						continue;
					}
					if(word_to_phrase_json.has(word)==false)
					{
						word_to_phrase_json.put(word, new JSONArray());
					}
					if(word_to_phrase_json.get(word).toString().contains(phrase))
					{
						continue;
					}
					phrase=phrase.replaceAll("[^a-zA-Z_\\s]", "");
					word_to_phrase_json.getJSONArray(word).put(phrase);		
				}
			}
			
			FileUtil.writeStringToFile(ALL_PHRASES_SCRAPED_JSON_FILE_PATH, word_to_phrase_json.toString());
			logger.info("Writing to file success!!");
		}
		else
		{
			throw new RuntimeException("Tamil2LyricsScraper.IS_WRITE_TO_FILE is false, no use in running this scraper then");
		}
	}

	public static void writeWordsToFile(TreeSet<String> words) throws IOException
	{
		if(!IS_WRITE_TO_FILE)
		{
			return;
		}
		
		word_set.addAll(words);
		FileUtil.writeStringToFile(ALL_WORDS_SCRAPED_FILE_PATH, CommonUtil.toCommanSeperatedString(word_set));
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
