package com.arkmusic.tamilrhymefinder.helpers;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.arkmusic.tamilrhymefinder.configuration.AppConfig;
import com.arkmusic.tamilrhymefinder.mongodb.MongoDBConnection;

@WebListener
public class ServerListener implements ServletContextListener
{

	private static Logger logger = Logger.getLogger(ServerListener.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		logger.info("---------- Server Initialized process started successfully ----------");
		AppConfig.init();
		logger.info("---------- Server Initialized process ended successfully ----------");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		logger.info("Server Shutting Down");
		logger.info("Destroying resources");
		MongoDBConnection.getInstance().closeConnection();
		logger.info("Destroyed!!");
	}
}