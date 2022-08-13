package com.arkmusic.tamilrhymefinder.scraping.hindilyrics123.pages;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class MoviesListPage
{
	private static Logger logger = Logger.getLogger(MoviesListPage.class.getName());

	public static String[] getAllMoviesListPageURLS()
	{
//		final String[] ALPHABETS = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		final String[] ALPHABETS = { "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

		String[] movies_list_page_urls = new String[26];

		for(int i = 0; i < ALPHABETS.length; i++)
		{
			movies_list_page_urls[i] = getMoviesListPageURLByAlphabet(ALPHABETS[i]);
		}

		return movies_list_page_urls;
	}

	public static String getMoviesListPageURLByAlphabet(String alphabet)
	{
		return "https://hindilyrics123.com/list-" + alphabet + ".html";
	}

	public static LinkedHashSet<String> getAllMoviesURLSInCurrentPage(Document document)
	{
		LinkedHashSet<String> movie_urls = new LinkedHashSet<String>();

		for(Element movie_url_element : document.select("a[href$='-lyrics.html']"))
		{
			movie_urls.add(movie_url_element.absUrl("href"));
		}

		return movie_urls;
	}

	public static void scrapeLyricsOfAllMoviesInCurrentMovieListPage(Document document) throws IOException, InterruptedException
	{
		LinkedHashSet<String> movie_urls = getAllMoviesURLSInCurrentPage(document);
		logger.info("All movie URLS in this page : " + movie_urls.toString());

		for(String movie_url : movie_urls)
		{
			try
			{
				document = Jsoup.connect(movie_url).get();
				logger.info("Current movie url :" + movie_url);
				MoviePage.scrapeLyricsOfAllSongsInCurrentMoviePage(document);
				logger.info("Scraping song lyrics completed for movie url :" + movie_url);
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Could not scrape all the movies in this page", e);
			}
		}
	}
}
