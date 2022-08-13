package com.arkmusic.tamilrhymefinder.server.mapdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.arkmusic.tamilrhymefinder.server.Configuration;
import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;

public class MapDBUtil
{
	private static Logger logger = Logger.getLogger(MapDBUtil.class.getName());

	private static HashMap<Language, DBManager> db_managers_by_language;

	public static void init()
	{
		db_managers_by_language = new HashMap<Language, DBManager>();

		for(Language language : Language.values())
		{
			DBManager db_manager;
			if(Configuration.IS_USE_MULTIPLE_TINY_DBS)
			{
				db_manager = new MultiFileDBManager(language);
			}
			else
			{
				db_manager = new SingleFileDBManager(language);
			}

			db_managers_by_language.put(language, db_manager);
		}
	}

	public static TreeSet<String> getAllWords(Language language)
	{
		return db_managers_by_language.get(language).getAllWords();
	}

	public static void addWord(Language language, String word)
	{
		db_managers_by_language.get(language).addWord(word);
	}

	public static void addPhrasesToWord(Language language, String word, TreeSet<String> phrases)
	{
		db_managers_by_language.get(language).addPhrasesToWord(word, phrases);
	}

	public static TreeSet<String> getPhrases(Language language, String word)
	{
		return db_managers_by_language.get(language).getPhrases(word);
	}

	/*
	 * If we only have data in mapDB form, it makes the server depend on the library
	 * heavily To avoid library change issues in future, we scrape and store the
	 * words & phrases in .txt & .json formats And we migrate them to map db using
	 * this class. this is to avoid having too much dependency to an external
	 * library
	 */
	public static class JSONToDBMigrator
	{
		public static void migratePhrasesJSONToDB(Language language, String phrase_json_file_path) throws IOException
		{
			db_managers_by_language.get(language).startWriteMode();

			int words_added_to_db = 0;

			TreeMap<String, TreeSet<String>> phrases_hashmap = GsonUtil.getHugeJSONAsHashMap(phrase_json_file_path);

			for(String word : phrases_hashmap.keySet())
			{
				if(isInvalidWord(word))
				{
					continue;
				}

				addPhrasesToWord(language, word, new TreeSet<String>(phrases_hashmap.get(word)));
				addWord(language, word);

				words_added_to_db++;

				if(words_added_to_db % 1000 == 0)
				{
					logger.info("Words added to DB :" + words_added_to_db);
				}
			}

			db_managers_by_language.get(language).endWriteMode();
		}

		private static boolean isInvalidWord(String word)
		{
			// to ignore nonsensical words like PAYANAMAAAAAA, AAAGAM, sakkkara
			return word.matches(".+(.)\\1{2,}.+") || word.matches("(.)\\1{2,}.+") || word.matches(".+(.)\\1{2,}");
		}
	}

	/*
	 * 
	 * Migration Script Sample Code
	 * 
	 * public static void main(String args[]) throws IOException { Long start =
	 * System.currentTimeMillis();
	 * 
	 * init();
	 * 
	 * String phrase_json_file_path=
	 * "/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/phrases_tamil.json";
	 * JSONToDBMigrator.migratePhrasesJSONToDB(Language.TAMIL,
	 * phrase_json_file_path);
	 * 
	 * Long end = System.currentTimeMillis(); logger.info("Total Time:" + (end -
	 * start)); }
	 * 
	 */
}
