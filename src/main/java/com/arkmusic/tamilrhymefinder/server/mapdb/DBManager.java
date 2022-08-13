package com.arkmusic.tamilrhymefinder.server.mapdb;

import java.util.TreeSet;

public interface DBManager
{
	public void addWord(String word);

	public TreeSet<String> getAllWords();

	public TreeSet<String> getPhrases(String word);

	public void addPhrasesToWord(String word, TreeSet<String> phrases);

	public void startWriteMode();

	public void endWriteMode();

	public boolean isWriteMode();
}
