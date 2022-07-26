package com.arkmusic.tamilrhymefinder.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.arkmusic.tamilrhymefinder.server.gson.responses.ServerError;
import com.arkmusic.tamilrhymefinder.server.words.Language;
import com.arkmusic.tamilrhymefinder.server.words.RhymingWords;

public class GetRhymingWords extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	private static final String
	URL_PARAM_LANGUAGE="language",
	URL_PARAM_WORD="word"
	;
       
    public GetRhymingWords() 
    {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

    	if(!request.getParameterMap().containsKey(URL_PARAM_LANGUAGE))
    	{
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		ServerError error=new ServerError(ErrorCodes.INVALID_PARAMETERS,"Missing parameter '"+URL_PARAM_LANGUAGE+"'");
    		out.print(GsonUtil.serialize(error));
    		out.flush();
    		return;
    	}
    	else if(!Language.isValidLanguage(request.getParameter(URL_PARAM_LANGUAGE)))
    	{
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		ServerError error=new ServerError(ErrorCodes.INVALID_PARAMETERS,"Invalid parameter '"+URL_PARAM_LANGUAGE+"'");
    		out.print(GsonUtil.serialize(error));
    		out.flush();
    		return;
    	}
    	else if(!request.getParameterMap().containsKey(URL_PARAM_WORD))
    	{
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		ServerError error=new ServerError(ErrorCodes.INVALID_PARAMETERS,"Missing parameter '"+URL_PARAM_WORD+"'");
    		out.print(GsonUtil.serialize(error));
    		out.flush();
    		return;
    	}

    	String word=request.getParameter(URL_PARAM_WORD);
    	String language_str=request.getParameter(URL_PARAM_LANGUAGE);
    	Language language=Language.getLanguageByUniqueName(language_str);
    	
    	JSONObject json=new JSONObject();
    	json.put(URL_PARAM_WORD,word);
    	json.put(URL_PARAM_LANGUAGE,language.unique_name);
    	json.put("rhyming_words",RhymingWords.getRhymingWords(word, language));
    	
		response.setStatus(HttpServletResponse.SC_OK);
		
		out.print(json.toString());
		out.flush();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}
}
