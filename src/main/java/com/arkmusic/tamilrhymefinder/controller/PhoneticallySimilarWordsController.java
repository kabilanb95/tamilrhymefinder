package com.arkmusic.tamilrhymefinder.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arkmusic.tamilrhymefinder.controller.dto.PhoneticallySimilarWordsResponse;
import com.arkmusic.tamilrhymefinder.models.Language;
import com.arkmusic.tamilrhymefinder.repository.WordRepository.PhoneticPropertyCriteria;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.arkmusic.tamilrhymefinder.server.gson.responses.ServerError;
import com.arkmusic.tamilrhymefinder.service.WordService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PhoneticallySimilarWordsController extends HttpServlet
{
	private final WordService wordService;
	private final Gson gson;

	public PhoneticallySimilarWordsController()
	{
		this.wordService = WordService.getInstance();
		this.gson = GsonUtil.getGsonInstance();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String wordParam = req.getParameter("word").toLowerCase();
		String languageParam = req.getParameter("language");
		String consonantVowelPattern = req.getParameter("isMatchingConsonantVowelPattern");
		String soundexCode = req.getParameter("isMatchingSoundexCode");
		String metaphoneCode = req.getParameter("isMatchingMetaphoneCode");
		String syllableCount = req.getParameter("isMatchingSyllableCount");
		String stressPattern = req.getParameter("isMatchingStressPattern");

		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");

		// Validate the presence of 'word' and 'language' parameters
		if(wordParam == null || languageParam == null)
		{
			ServerError error = new ServerError(HttpServletResponse.SC_BAD_REQUEST, "Both 'word' and 'language' parameters are required.");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			gson.toJson(error, resp.getWriter());
			return;
		}

		try
		{
			// Convert language parameter to Language enum
			Language language = Language.valueOf(languageParam.toUpperCase());

			// Create PhoneticFeatureCriteria and set flags accordingly
			PhoneticPropertyCriteria criteria = new PhoneticPropertyCriteria();
			criteria.setMatchingConsonantVowelPattern(consonantVowelPattern != null && Boolean.parseBoolean(consonantVowelPattern));
			criteria.setMatchingSoundexCode(soundexCode != null && Boolean.parseBoolean(soundexCode));
			criteria.setMatchingMetaphoneCode(metaphoneCode != null && Boolean.parseBoolean(metaphoneCode));
			criteria.setMatchingSyllableCount(syllableCount != null && Boolean.parseBoolean(syllableCount));
			criteria.setMatchingStressPattern(stressPattern != null && Boolean.parseBoolean(stressPattern));

			// Ensure that at least one feature is specified
			if(!criteria.hasAnyMatchingFeature())
			{
				ServerError error = new ServerError(HttpServletResponse.SC_BAD_REQUEST, "At least one phonetic feature must be specified.");
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				gson.toJson(error, resp.getWriter());
				return;
			}

			// Fetch matching words based on phonetic features (using word and language
			// parameters)
			List<String> matchingWords = wordService.findByPhoneticFeatures(wordParam, language, criteria);

			if(!matchingWords.isEmpty())
			{
				PhoneticallySimilarWordsResponse response = new PhoneticallySimilarWordsResponse(wordParam, language, criteria, matchingWords);
				String jsonResponse = gson.toJson(response);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(jsonResponse);
			}
			else
			{
				ServerError error = new ServerError(HttpServletResponse.SC_NOT_FOUND, "No matching words found.");
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				gson.toJson(error, resp.getWriter());
			}
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
