package com.arkmusic.tamilrhymefinder.server.gson;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.gson.Gson;

public class GsonUtil
{
	private static final Logger logger = Logger.getLogger(GsonUtil.class.getName());

	// Private static field to hold the Singleton instance of Gson
	private static Gson gson;

	// Private constructor to prevent instantiation
	private GsonUtil()
	{
	}

	// Public method to get the Singleton instance of Gson
	public static Gson getGsonInstance()
	{
		if(gson == null)
		{
			// Synchronized block to make it thread-safe (optional)
			synchronized(GsonUtil.class)
			{
				if(gson == null)
				{
					gson = new Gson();
				}
			}
		}
		return gson;
	}

	// Serialize object to JSON
	public static JSONObject serialize(Object object)
	{
		return new JSONObject(getGsonInstance().toJson(object));
	}

	// Deserialize JSON string to object
	public static <T> T deserialize(String json, Class<T> classOfT)
	{
		return getGsonInstance().fromJson(json, classOfT);
	}
}