package com.arkmusic.tamilrhymefinder.server.gson;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class GsonUtil 
{
	public static JSONObject serialize(Object object)
	{
		return new JSONObject(new Gson().toJson(object));
	}

	public static <T> T deserialize(String json, Class<T> classOfT)
	{
		return new Gson().fromJson(json, classOfT);
	}
	
	public static HashMap<String, HashSet<String>> getHugeJSONAsHashMap(String json_file_path) throws IOException
	{
		HashMap<String, HashSet<String>> map=new HashMap<String, HashSet<String>>();
		
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(json_file_path), "UTF-8"));
		reader.beginObject();
		JsonToken token;
		
		String current_key=null;
		
		while (reader.hasNext()) 
		{
			token=reader.peek();
			
	        if(token.equals(JsonToken.NAME)) 
	        { 
	        	current_key=reader.nextName();
	        	map.put(current_key,new HashSet<String>());
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
