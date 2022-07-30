package com.arkmusic.tamilrhymefinder.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class JSoupUtil
{
	//https://stackoverflow.com/a/19602313
	public static String getFormattedTextFromHTML(String html) 
	{
	    if(html==null)
	        return html;
	    Document document = Jsoup.parse(html);
	    document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
	    document.select("br").append("\\n");
	    document.select("p").prepend("\\n\\n");
	    String s = document.html().replaceAll("\\\\n", "\n");
	    return Jsoup.clean(s, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
	}
}
