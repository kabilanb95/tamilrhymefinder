package com.arkmusic.tamilrhymefinder.scraping.hindilyrics123.pages;

import org.jsoup.nodes.Document;

import com.arkmusic.tamilrhymefinder.scraping.JSoupUtil;

public class SongPage
{
	public static String getLyricsWithFormatting(Document document)
	{
		String html=document.select("p").get(2).html();
		String text=JSoupUtil.getFormattedTextFromHTML(html);
		return text.trim();	
	}
}
