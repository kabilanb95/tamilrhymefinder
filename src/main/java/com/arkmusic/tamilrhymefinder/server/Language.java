package com.arkmusic.tamilrhymefinder.server;

public enum Language 
{
    TAMIL("tamil","words_tamil.txt","phrases_tamil.json")
    ;

    public String unique_name,wordset_filepath,phrasejson_filepath;
    
    private Language(String unique_name,String wordset_filepath,String phrasejson_filepath)
    {
        this.unique_name = unique_name;
        this.wordset_filepath = wordset_filepath;
        this.phrasejson_filepath = phrasejson_filepath;
    }
    
    public static boolean isValidLanguage(String language_unique_name)
    {
    	for (Language language : Language.values()) 
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
    	for (Language language : Language.values()) 
    	{ 
    	    if(language_unique_name.equals(language.unique_name))
    	    {
    	    	return language;
    	    }
    	}
    	
    	throw new RuntimeException("Unconfigured language : "+language_unique_name);
    }
}