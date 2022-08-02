package com.arkmusic.tamilrhymefinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import com.arkmusic.tamilrhymefinder.scraping.FileUtil;
import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.ResourceUtil;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.arkmusic.tamilrhymefinder.server.phrases.PhraseNotFoundException;

public class MapDBUtil
{
	private static Logger logger = Logger.getLogger(MapDBUtil.class.getName());

	private static HashMap<Language, DB> db_by_language;
	private static HashMap<Language, HTreeMap<String, TreeSet<String>>> phrase_map_by_language;
	private static HashMap<Language, NavigableSet<String>> word_set_by_language;

	private static final String PHRASE_MAP_PREFIX = "phrasemap_";
	private static final String WORD_SET_PREFIX = "wordset_";

	public static void init()
	{
		db_by_language = new HashMap<Language, DB>();
		phrase_map_by_language = new HashMap<Language, HTreeMap<String, TreeSet<String>>>();
		word_set_by_language = new HashMap<Language, NavigableSet<String>>();

		for(Language language : Language.values())
		{
			String file_path = ResourceUtil.getFilePath(language.db_file_path);
			DB db = DBMaker.fileDB(file_path).transactionEnable().make();
			db_by_language.put(language, db);
			HTreeMap<String, TreeSet<String>> phrase_htreemap = db.hashMap(PHRASE_MAP_PREFIX+language.unique_name).keySerializer(Serializer.STRING).valueSerializer(Serializer.JAVA).createOrOpen();
			phrase_map_by_language.put(language, phrase_htreemap);
			NavigableSet<String> words_navigable_set = db.treeSet(WORD_SET_PREFIX+language.unique_name).serializer(Serializer.STRING).createOrOpen();
			word_set_by_language.put(language, words_navigable_set);
		}
	}
	public static TreeSet<String> getAllWords(Language language)
	{
		return new TreeSet<String>(word_set_by_language.get(language));
	}

	public static void addWord(Language language, String word)
	{
		word_set_by_language.get(language).add(word.toLowerCase());
	}

	public static void addPhraseToWord(Language language, String word, String phrase)
	{
		word=word.toLowerCase();
		phrase=phrase.toLowerCase();
		
		TreeSet<String> phrases;
		
		if(phrase_map_by_language.get(language).containsKey(word))
		{
			phrases=phrase_map_by_language.get(language).get(word);
		}
		else
		{
			phrases=new TreeSet<String>();
		}
		
		phrases.add(phrase);
		
		phrase_map_by_language.get(language).put(word, phrases);
	}
	
	public static TreeSet<String> getPhrases(Language language,String word)
	{
		if(phrase_map_by_language.get(language).containsKey(word)==false)
		{
			throw new PhraseNotFoundException(word);
		}
		
		return phrase_map_by_language.get(language).get(word);
	}
	/*
	 * If we only have data in mapDB form, it makes the server depend on the library heavily
	 * To avoid library change issues in future, we scrape and store the words & phrases in .txt & .json formats
	 * And we migrate them to map db using this class. this is to avoid having too much dependency to an external library
	 */
	public static class JSONToDBMigrator
	{
		public static void migratePhrasesJSONToDB(Language language,String phrase_json_file_path) throws IOException
		{
			HashMap<String, HashSet<String>> phrases_hashmap=GsonUtil.getHugeJSONAsHashMap(phrase_json_file_path);
			
			for(String word : phrases_hashmap.keySet())
			{
				for(String phrase : phrases_hashmap.get(word))
				{
					addPhraseToWord(language, word, phrase);	
					addWord(language,word);
				}
			}
			
			db_by_language.get(language).commit();
		}
		
		public static void migrateWordSetTxtToDB(Language language,String wordset_file_path) throws IOException
		{
			TreeSet<String> words_treeset=new TreeSet<String>(CommonUtil.commanSeperatedStringToTreeSet(wordset_file_path));
			
			for(String word : words_treeset)
			{
				addWord(language, word);	
			}
			
			db_by_language.get(language).commit();
		}
	}

//	/*
	public static void main(String args[]) throws IOException
	{
		Long start = System.currentTimeMillis();

		init();

//		String phrase_json_file_path="/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/phrases_tamil.json";
//		JSONToDBMigrator.migratePhrasesJSONToDB(Language.TAMIL, phrase_json_file_path);		
//		String wordset_file_path="/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/words_tamil.txt";
//		JSONToDBMigrator.migrateWordSetTxtToDB(Language.TAMIL, wordset_file_path);
//		db_by_language.get(Language.TAMIL).commit();
				
		Long end = System.currentTimeMillis();
		logger.info("Total Time:" + (end - start));
	}
//	*/
	
	//DEMO CODE FOR MapDB
	/*
	public static void main(String args[])
	{
		Long start = System.currentTimeMillis();

		String file_path = "/Users/kabilan-5523/Documents/myherokuapps/test.db";

		DB db = DBMaker.fileDB(file_path).transactionEnable().closeOnJvmShutdown().make();

		HTreeMap<String, TreeSet<String>> hTreeMap = db.hashMap("myTreeMap").keySerializer(Serializer.STRING).valueSerializer(Serializer.JAVA).createOrOpen();

		for(int i=0;i<10;i++)
		{
			TreeSet<String> set=new TreeSet<String>();
			
			for(int j=0;j<2;j++)
			{
				set.add("value"+i+j);
			}
			
			hTreeMap.put("key"+i, set);
		}
		
		for(String key : hTreeMap.keySet())
		{
			System.out.println(key+"--->"+hTreeMap.get(key));
		}

		
		db.commit();


		Long end = System.currentTimeMillis();
		logger.info("Total Time:" + (end - start));
	}
	*/
}
