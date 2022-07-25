package com.arkmusic.tamilrhymefinder.scraping.tamil2lyrics.pages;

import java.util.HashSet;
import java.util.List;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MoviesListPage 
{
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
}
