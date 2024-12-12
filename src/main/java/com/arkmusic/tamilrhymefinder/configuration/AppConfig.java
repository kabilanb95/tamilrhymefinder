package com.arkmusic.tamilrhymefinder.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig
{
	private static Properties properties;

	static
	{
		init();
	}

	// Initialize the properties file manually
	public static void init()
	{
		if(properties != null)
		{
			return;
		}

		properties = new Properties();

		try(InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties"))
		{
			if(input == null)
			{
				throw new IOException("Unable to find application.properties");
			}
			properties.load(input);

			// Check if the environment variable ATLAS_CONNECTION_STRING is set
			String connectionStringEnv = System.getenv("ATLAS_CONNECTION_STRING");
			if(connectionStringEnv != null && !connectionStringEnv.isEmpty())
			{
				// Override the MongoDB connection string in the properties with the environment
				// variable
				properties.setProperty("mongodb.connectionString", connectionStringEnv);
			}
		}
		catch(IOException ex)
		{
			throw new RuntimeException("Failed to load application.properties", ex);
		}
	}

	// Get a property by key
	public static String getProperty(String key)
	{
		return properties.getProperty(key);
	}
}
