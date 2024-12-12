package com.arkmusic.tamilrhymefinder.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.models.Word;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.arkmusic.tamilrhymefinder.server.gson.responses.ServerError;
import com.arkmusic.tamilrhymefinder.service.WordService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhrasesController extends HttpServlet
{
	private final WordService wordService;
	private final Gson gson;

	public PhrasesController()
	{
		this.wordService = WordService.getInstance();
		this.gson = GsonUtil.getGsonInstance();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String wordParam = req.getParameter("word").toLowerCase();
		String languageParam = req.getParameter("language");

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		if(wordParam == null || languageParam == null)
		{
			ServerError error = new ServerError(HttpServletResponse.SC_BAD_REQUEST, "Both 'word' and 'language' parameters are required.");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			gson.toJson(error, resp.getWriter());
			return;
		}

		try
		{
			Language language = Language.valueOf(languageParam.toUpperCase());
			Word word = wordService.findByWordAndLanguage(wordParam, language);

			if(word != null)
			{
				String jsonResponse = gson.toJson(word);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(jsonResponse);
			}
			else
			{
				ServerError error = new ServerError(HttpServletResponse.SC_NOT_FOUND, "Word not found.");
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				gson.toJson(error, resp.getWriter());
			}
		}
		catch(IllegalArgumentException e)
		{
			log.error("Invalid arguments: {}", e);
			ServerError error = new ServerError(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			gson.toJson(error, resp.getWriter());
		}
		catch(Exception e)
		{
			log.error("Error processing the request", e);
			ServerError error = new ServerError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			gson.toJson(error, resp.getWriter());
		}
	}
}
