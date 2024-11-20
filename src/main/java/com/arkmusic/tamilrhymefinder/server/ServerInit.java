package com.arkmusic.tamilrhymefinder.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.arkmusic.tamilrhymefinder.server.mapdb.MapDBUtil;
import com.arkmusic.tamilrhymefinder.server.phrases.PhraseCacherManager;
import com.arkmusic.tamilrhymefinder.server.words.RhymeCacherManager;
import com.arkmusic.tamilrhymefinder.server.words.WordsManager;

public class ServerInit extends HttpServlet
{
	private static Logger logger = Logger.getLogger(ServerInit.class.getName());

	public static boolean isInitAlready = false;

	public void init() throws ServletException
	{
		if(isInitAlready)
		{
			return;
		}

		isInitAlready = true;

		logger.info("---------- ServerInit Initialized process started successfully ----------");
		try
		{
			logger.info("Intialising MapDB");
			MapDBUtil.init();
			logger.info("MapDB init completed, Going to wait 30 seconds");
			Thread.sleep(30*1000);
			logger.info("Intialising WordsManager");
			WordsManager.init();
			logger.info("WordsManager init completed, Going to wait 30 seconds");
			Thread.sleep(30*1000);
			logger.info("Intialising RhymeCacherManager");
			RhymeCacherManager.init();
			logger.info("RhymeCacherManager init completed, Going to wait 30 seconds");
			Thread.sleep(30*1000);
			logger.info("Intialising PhraseCacherManager");
			PhraseCacherManager.init();
			logger.info("PhraseCacherManager init completed, Going to wait 30 seconds");
			Thread.sleep(30*1000);			
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unexpected IO Exception", e);
		}
		logger.info("---------- ServerInit Initialized process ended successfully ----------");
	}
}
