package com.arkmusic.tamilrhymefinder.server.words;

public enum Language 
{
    TAMIL("tamil","tamil.txt")
    ;

    public String unique_name,wordset_filepath;
    
    private Language(String unique_name,String wordset_filepath)
    {
        this.unique_name = unique_name;
        this.wordset_filepath = wordset_filepath;
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