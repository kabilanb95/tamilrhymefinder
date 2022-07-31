package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.Tamil2LyricsScraper;

public class MoviesListPage 
{
	private static Logger logger=Logger.getLogger(MoviesListPage.class.getName());

	public static final String
	URL="https://www.tamil2lyrics.com/movie/";
	
	public static HashSet<String> getAllMoviesURLSInCurrentPage(Document document)
	{
		HashSet<String> movie_urls=new HashSet<String>();
		
		for(Element movie_url_element : document.select("a[href*='/movies/']"))
		{
			movie_urls.add(movie_url_element.attr("href"));
		}
		
		return movie_urls;
	}
	
	public static int getCurrentPageIndex(Document document)
	{
		String index_str=document.select(".custom-pagination").select(".active").select("a").text().trim();
		return Integer.parseInt(index_str);
	}
	
	public static int getLastPageIndex(Document document)
	{
		Elements a_tags=document.select(".custom-pagination").select("a");
		String index_str=a_tags.get(a_tags.size()-2).text().trim();//the button before 'next' button
		return Integer.parseInt(index_str);
	}
	
	public static String getListURLByIndex(int index)
	{
		return "https://www.tamil2lyrics.com/movie/page/"+index+"/";
	}
	
	public static HashSet<String> getWordsByScrapingAllMoviesInCurrentMovieListPage(Document document) throws IOException, InterruptedException
	{
		HashSet<String> words=new HashSet<String>();
		
		HashSet<String> movie_urls=MoviesListPage.getAllMoviesURLSInCurrentPage(document);
		logger.info("All movie URLS in this page : "+movie_urls.toString());
		
		for(String movie_url : movie_urls)
		{
			try
			{
				document=Jsoup.connect(movie_url).get();
				logger.info("Current movie url :"+movie_url);
				words.addAll(MoviePage.getWordsByScrapingAllSongsInCurrentMoviePage(document));
				logger.info("Scraping song lyrics completed for movie url :"+movie_url);				
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE,"Could not scrape all the movies in this page",e);
			}
		}
		
		return words;
	}
	
	public static void scrapeLyricsOfAllMoviesInCurrentMovieListPage(Document document) throws IOException, InterruptedException
	{		
		HashSet<String> movie_urls=MoviesListPage.getAllMoviesURLSInCurrentPage(document);
		logger.info("All movie URLS in this page : "+movie_urls.toString());
		
		for(String movie_url : movie_urls)
		{
			try
			{
				document=Jsoup.connect(movie_url).get();
				logger.info("Current movie url :"+movie_url);
				MoviePage.scrapeLyricsOfAllSongsInCurrentMoviePage(document);
				logger.info("Scraping song lyrics completed for movie url :"+movie_url);			
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE,"Could not scrape all the movies in this page",e);
			}
		}
	}
}
