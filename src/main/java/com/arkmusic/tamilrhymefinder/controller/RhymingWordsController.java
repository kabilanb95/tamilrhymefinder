package com.arkmusic.tamilrhymefinder.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arkmusic.tamilrhymefinder.controller.dto.RhymingWordsResponse;
import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.models.Word;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.arkmusic.tamilrhymefinder.server.gson.responses.ServerError;
import com.arkmusic.tamilrhymefinder.service.WordService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RhymingWordsController extends HttpServlet
{
	private final WordService wordService;
	private final Gson gson;

	public RhymingWordsController()
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

			// Fetch the raw list of rhyming words (Word objects) from the service
			List<Word> words = wordService.getRhymingWords(wordParam, language);

			// Create the response using the DTO which handles categorization internally
			RhymingWordsResponse response = new RhymingWordsResponse(wordParam, language.toString(), words);

			// Convert the response to JSON and send it
			String jsonResponse = gson.toJson(response);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(jsonResponse);
		}
		catch(IllegalArgumentException e)
		{
			log.error("Invalid language format: {}", languageParam, e);
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
