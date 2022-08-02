package com.arkmusic.tamilrhymefinder.server;

public enum Language
{
	TAMIL("tamil","db/tamil/tamil.db");

	public String unique_name, db_file_path;

	private Language(String unique_name,String db_file_path)
	{
		this.unique_name = unique_name;
		this.db_file_path = db_file_path;

	}

	public static boolean isValidLanguage(String language_unique_name)
	{
		for(Language language : Language.values())
		{
			if(language_unique_name.equals(language.unique_name))
			{
				return true;
			}
		}

		return false;
	}

	public static Language getLanguageByUniqueName(String language_unique_name)
	{
		for(Language language : Language.values())
		{
			if(language_unique_name.equals(language.unique_name))
			{
				return language;
			}
		}

		throw new RuntimeException("Unconfigured language : " + language_unique_name);
	}
}