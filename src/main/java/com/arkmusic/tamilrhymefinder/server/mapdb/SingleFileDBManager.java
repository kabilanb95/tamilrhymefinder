package com.arkmusic.tamilrhymefinder.server.mapdb;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.mapdb.SortedTableMap;
import org.mapdb.volume.MappedFileVol;
import org.mapdb.volume.Volume;

import com.arkmusic.tamilrhymefinder.CommonUtil;
import com.arkmusic.tamilrhymefinder.server.Configuration;
import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.ResourceUtil;
import com.arkmusic.tamilrhymefinder.server.phrases.PhraseNotFoundException;

public class SingleFileDBManager implements DBManager
{
	private static final String PHRASE_MAP_DB_FILE_NAME = "phrasemap.db";
	private static final String WORD_SET_DB_FILE_NAME = "wordset.db";
	private static final String WORD_SET = "wordset";

	Language language;

	private Volume readable_phrases_volume;
	private Volume writable_phrases_volume;
	private SortedTableMap<String, String> readable_phrase_map;
	private SortedTableMap.Sink<String, String> writable_phrase_map;

	private DB wordset_db;
	private NavigableSet<String> wordset;

	private boolean is_write_mode = false;

	private static Logger logger = Logger.getLogger(SingleFileDBManager.class.getName());

	public SingleFileDBManager(Language language)
	{
		this.language = language;
		initDBs();
	}

	private void initDBs()
	{
		if(!Configuration.IS_JSON_TO_DB_MIGRATION_MODE)
		{
			initPhraseMap(true);
		}

		String file_path = ResourceUtil.getDBPath(this.language, WORD_SET_DB_FILE_NAME);

		if(Configuration.IS_USE_UNSTABLE_BUT_FAST_DB)
		{
			wordset_db = DBMaker.fileDB(file_path).make();
		}
		else
		{
			wordset_db = DBMaker.fileDB(file_path).transactionEnable().make();
		}

		wordset = wordset_db.treeSet(WORD_SET).serializer(Serializer.STRING).createOrOpen();
	}

	private void initPhraseMap(boolean is_readable_volume)
	{
		Volume volume = (is_readable_volume ? this.readable_phrases_volume : this.writable_phrases_volume);

		String file_path = ResourceUtil.getDBPath(this.language, PHRASE_MAP_DB_FILE_NAME);
		Volume new_volume = MappedFileVol.FACTORY.makeVolume(file_path, is_readable_volume);
		volume = new_volume;
		if(is_readable_volume)
		{
			this.readable_phrase_map = SortedTableMap.open(volume, Serializer.STRING, Serializer.STRING);
		}
		else
		{
			this.writable_phrase_map = SortedTableMap.create(volume, Serializer.STRING, Serializer.STRING).createFromSink();
		}
	}

	public TreeSet<String> getAllWords()
	{
		return new TreeSet<String>(wordset);
	}

	public void addWord(String word)
	{
		wordset.add(word.toLowerCase());
	}

	public void addPhrasesToWord(String word, TreeSet<String> phrases)
	{
		if(!isWriteMode())
		{
			throw new RuntimeException("Please set to write mode before this operation");
		}

		word = word.toLowerCase();

		try
		{
			writable_phrase_map.put(word, CommonUtil.toCommanSeperatedString(phrases));
		}
		catch(java.lang.AssertionError e)
		{
			logger.info("Could not add to DB. word '" + word + "', phrases :" + CommonUtil.toCommanSeperatedString(phrases));
			throw e;
		}
	}

	public TreeSet<String> getPhrases(String word)
	{
		if(readable_phrase_map.containsKey(word) == false)
		{
			throw new PhraseNotFoundException(word);
		}

		return CommonUtil.commanSeperatedStringToTreeSet(readable_phrase_map.get(word));
	}

	public void startWriteMode()
	{
		if(is_write_mode)
		{
			throw new RuntimeException("Already in write mode!!");
		}

		initPhraseMap(false);

		this.is_write_mode = true;
	}

	public void endWriteMode()
	{
		if(!is_write_mode)
		{
			throw new RuntimeException("Write mode already ended!!");
		}

		writable_phrase_map.create();
		wordset_db.commit();

		this.is_write_mode = false;
	}

	public boolean isWriteMode()
	{
		return this.is_write_mode;
	}
}
