package com.arkmusic.tamilrhymefinder.server.gson;

import java.lang.reflect.Type;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;

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
}
