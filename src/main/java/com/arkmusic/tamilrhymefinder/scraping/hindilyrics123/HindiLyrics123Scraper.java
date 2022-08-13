package com.arkmusic.tamilrhymefinder.scraping.hindilyrics123;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.scraping.FileUtil;
import com.arkmusic.tamilrhymefinder.scraping.hindilyrics123.pages.MoviesListPage;

public class HindiLyrics123Scraper
{
	private static Logger logger = Logger.getLogger(HindiLyrics123Scraper.class.getName());

	public static int WAIT_TIME_BETWEEN_EACH_PAGE_IN_SECONDS = 0;

	public static final String RESOURCE_FOLDER_PATH = "/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/";
	public static final String ALL_PHRASES_SCRAPED_JSON_FILE_PATH = RESOURCE_FOLDER_PATH + "phrases_hindi.json";

	private static JSONObject word_to_phrase_json = null;

	// config variables
	public static boolean IS_WRITE_TO_FILE = true;// set as false if you are not scraping and storing to file
	public static boolean IS_CONTINUE_SCRAPING = true;// set as true if scraping stopped in the middle and we are continuing it for
														// the remaining pages alone

	public static int songs_iterated = 0;

	public static void main(String[] args) throws IOException
	{
		Long start = System.currentTimeMillis();
		scrapeAndStoreAllPhrases();
		Long end = System.currentTimeMillis();
		logger.info("Total Time:" + (end - start));
	}

	// Scraper
	public static void scrapeAndStoreAllPhrases() throws IOException
	{
		if(IS_CONTINUE_SCRAPING)
		{
			String word_to_phrase_json_str = FileUtil.getFileAsString(ALL_PHRASES_SCRAPED_JSON_FILE_PATH);
			word_to_phrase_json = new JSONObject(word_to_phrase_json_str);
			logger.info("Phrases JSON was loaded from file with " + word_to_phrase_json.keySet().size() + " words");
		}
		else
		{
			word_to_phrase_json = new JSONObject();
		}

		Document document = null;

		for(String movie_list_page_url : MoviesListPage.getAllMoviesListPageURLS())
		{
			try
			{
				document = Jsoup.connect(movie_list_page_url).get();
				logger.info("Current movie list page URL : " + movie_list_page_url);
				MoviesListPage.scrapeLyricsOfAllMoviesInCurrentMovieListPage(document);
				logger.info("MOVIE SCRAPING COMPLETED FOR LIST PAGE INDEX : " + movie_list_page_url);
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Could not scrape this page fully : " + document.location(), e);
			}
		}
	}

	public static void writeLyricsToFile(String lyrics) throws IOException
	{
		if(HindiLyrics123Scraper.IS_WRITE_TO_FILE)
		{
			String[] phrases = lyrics.split("\\r?\\n");

			for(String phrase : phrases)
			{
				phrase = phrase.trim();
				phrase = phrase.toLowerCase();

				String phrase_without_space = CommonUtil.removeAllWhiteSpaces(phrase);
				phrase_without_space = phrase_without_space.replaceAll("[^a-zA-Z]", "");

				// skip if single word
				if(phrase.split("\\s+").length == 1)
				{
					continue;
				}
				// skip if info text
				if(phrase.contains(":"))
				{
					continue;
				}
				// skip if phrase too small
				if(phrase_without_space.equals("") || phrase_without_space.length() < 10)
				{
					continue;
				}

				String[] words = phrase.split("\\s+");

				for(String word : words)
				{
					word = word.trim();
					word = word.replaceAll("[^a-zA-Z]", "");
					word = word.toLowerCase();

					if(word.equals("") || word.length() <= 2)
					{
						continue;
					}
					if(word_to_phrase_json.has(word) == false)
					{
						word_to_phrase_json.put(word, new JSONArray());
					}
					if(word_to_phrase_json.get(word).toString().contains(phrase))
					{
						continue;
					}
					phrase = phrase.replaceAll("[^a-zA-Z_\\s]", "");
					word_to_phrase_json.getJSONArray(word).put(phrase);
				}
			}

			songs_iterated++;

			// write to file every 50 songs
			if(songs_iterated % 50 == 0)
			{
				FileUtil.writeStringToFile(ALL_PHRASES_SCRAPED_JSON_FILE_PATH, word_to_phrase_json.toString());
				logger.info("Writing to phrases file success!!");
			}
		}
		else
		{
			throw new RuntimeException("HindiLyrics123Scraper.IS_WRITE_TO_FILE is false, no use in running this scraper then");
		}
	}
}
