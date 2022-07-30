package com.arkmusic.tamilrhymefinder.server.phrases;

public class PhraseNotFoundException extends RuntimeException
{	
	public PhraseNotFoundException(String word)
	{
		super("Phrases not found for word '"+word+"'");
	}
}
