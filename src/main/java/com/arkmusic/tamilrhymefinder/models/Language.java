package com.arkmusic.tamilrhymefinder.models;

public enum Language
{
	TAMIL("tamil")
//	HINDI("hindi")
	;

	public String unique_name;

	private Language(String unique_name)
	{
		this.unique_name = unique_name;

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