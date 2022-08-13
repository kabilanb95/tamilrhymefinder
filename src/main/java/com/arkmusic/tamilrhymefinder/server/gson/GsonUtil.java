package com.arkmusic.tamilrhymefinder.server.gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class GsonUtil
{
	private static Logger logger = Logger.getLogger(GsonUtil.class.getName());

	public static JSONObject serialize(Object object)
	{
		return new JSONObject(new Gson().toJson(object));
	}

	public static <T> T deserialize(String json, Class<T> classOfT)
	{
		return new Gson().fromJson(json, classOfT);
	}

	public static TreeMap<String, TreeSet<String>> getHugeJSONAsHashMap(String json_file_path) throws IOException
	{
		TreeMap<String, TreeSet<String>> map = new TreeMap<String, TreeSet<String>>();

		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(json_file_path), "UTF-8"));
		reader.beginObject();
		JsonToken token;

		String current_key = null;

		while(reader.hasNext())
		{
			token = reader.peek();

			if(token.equals(JsonToken.NAME))
			{
				current_key = reader.nextName();
				map.put(current_key, new TreeSet<String>());
			}
			if(token.equals(JsonToken.BEGIN_ARRAY))
			{
				reader.beginArray();

				while(true)
				{
					JsonToken token2 = reader.peek();
					if(token2.equals(JsonToken.END_ARRAY))
					{
						reader.endArray();
						break;
					}
					else if(token2.equals(JsonToken.STRING))
					{
						map.get(current_key).add(reader.nextString());
					}
				}
			}
		}

		return map;
	}
}
