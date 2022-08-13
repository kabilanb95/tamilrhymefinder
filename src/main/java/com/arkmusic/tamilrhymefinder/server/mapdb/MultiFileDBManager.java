package com.arkmusic.tamilrhymefinder.server.mapdb;

import java.util.NavigableSet;
import java.util.TreeMap;
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

public class MultiFileDBManager implements DBManager
{
	private static Logger logger = Logger.getLogger(MultiFileDBManager.class.getName());

	private static final String[] ALPHABETS = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };

	private static final String PHRASE_MAP_DB_FILE_NAME = "phrasemap_<alphabet>.db";
	private static final String WORD_SET_DB_FILE_NAME = "wordset.db";
	private static final String WORD_SET = "wordset";

	Language language;

	private TreeMap<String, Volume> readable_phrase_volumes_by_alphabet;
	private TreeMap<String, Volume> writable_phrase_volumes_by_alphabet;
	private TreeMap<String, SortedTableMap<String, String>> readable_phrase_maps_by_alphabet;
	private TreeMap<String, SortedTableMap.Sink<String, String>> writable_phrase_maps_by_alphabet;

	private DB wordset_db;
	private NavigableSet<String> wordset;

	private boolean is_write_mode = false;

	public MultiFileDBManager(Language language)
	{
		this.language = language;
		initDBs();
	}

	private void initDBs()
	{
		readable_phrase_volumes_by_alphabet = new TreeMap<String, Volume>();
		readable_phrase_maps_by_alphabet = new TreeMap<String, SortedTableMap<String, String>>();

		writable_phrase_volumes_by_alphabet = new TreeMap<String, Volume>();
		writable_phrase_maps_by_alphabet = new TreeMap<String, SortedTableMap.Sink<String, String>>();

		if(!Configuration.IS_JSON_TO_DB_MIGRATION_MODE)
		{
			initPhraseMapsByAlphabet(true);
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

		wordset = wordset_db.treeSet(WORD_SET + language.unique_name).serializer(Serializer.STRING).createOrOpen();
	}

	private void initPhraseMapsByAlphabet(boolean is_readable_volume)
	{
		TreeMap<String, Volume> phrase_volumes_by_alphabet = (is_readable_volume ? this.readable_phrase_volumes_by_alphabet : this.writable_phrase_volumes_by_alphabet);

		for(String alphabet : ALPHABETS)
		{
			String file_path = ResourceUtil.getDBPath(this.language, PHRASE_MAP_DB_FILE_NAME.replace("<alphabet>", alphabet));
			Volume volume = MappedFileVol.FACTORY.makeVolume(file_path, is_readable_volume);
			phrase_volumes_by_alphabet.put(alphabet, volume);
			if(is_readable_volume)
			{
				SortedTableMap<String, String> phrase_map = SortedTableMap.open(volume, Serializer.STRING, Serializer.STRING);
				this.readable_phrase_maps_by_alphabet.put(alphabet, phrase_map);
			}
			else
			{
				SortedTableMap.Sink<String, String> phrase_map = SortedTableMap.create(volume, Serializer.STRING, Serializer.STRING).createFromSink();
				this.writable_phrase_maps_by_alphabet.put(alphabet, phrase_map);
			}
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

		String alphabet = "" + word.charAt(0);
		word = word.toLowerCase();

		SortedTableMap.Sink<String, String> writable_phrase_map = writable_phrase_maps_by_alphabet.get(alphabet);

		try
		{
			writable_phrase_map.put(word, CommonUtil.toCommanSeperatedString(phrases));
		}
		catch(java.lang.AssertionError e)
		{
			logger.info("Could not add to DB. word '" + word + "', phrases :" + CommonUtil.toCommanSeperatedString(phrases));
			throw e;
		}

		writable_phrase_maps_by_alphabet.put(alphabet, writable_phrase_map);
	}

	public TreeSet<String> getPhrases(String word)
	{
		String alphabet = "" + word.charAt(0);

		SortedTableMap<String, String> readable_phrase_map = readable_phrase_maps_by_alphabet.get(alphabet);

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

		initPhraseMapsByAlphabet(false);

		this.is_write_mode = true;
	}

	public void endWriteMode()
	{
		if(!is_write_mode)
		{
			throw new RuntimeException("Write mode already ended!!");
		}

		for(String alphabet : writable_phrase_maps_by_alphabet.keySet())
		{
			writable_phrase_maps_by_alphabet.get(alphabet).create();
		}

		wordset_db.commit();

		this.is_write_mode = false;
	}

	public boolean isWriteMode()
	{
		return this.is_write_mode;
	}
}
