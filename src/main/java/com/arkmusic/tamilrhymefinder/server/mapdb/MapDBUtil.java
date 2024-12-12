package com.arkmusic.tamilrhymefinder.server.mapdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Soundex;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.arkmusic.tamilrhymefinder.server.Configuration;
import com.arkmusic.tamilrhymefinder.server.Language;
import com.arkmusic.tamilrhymefinder.server.gson.GsonUtil;
import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

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

	public static class JSONToMongoDBMigrator
	{

		private static final String CONNECTION_STRING = null; //enter value here
		private static final String DATABASE_NAME = "wordsDB";
		private static final String COLLECTION_NAME = "words";
		private static final String PROGRESS_FILE_PATH = "/Users/kabilan-5523/Desktop/progress.json"; // File to store progress
		private static final int PROGRESS_SAVE_INTERVAL = 10000; // Save progress every 10 seconds
		private static final Logger logger = Logger.getLogger(JSONToMongoDBMigrator.class.getName());

		public static void migratePhrasesJSONToDB(Language language, String phrase_json_file_path) throws IOException
		{
			// Fetch the public IP address using an external service
			String publicIP = getPublicIP();
			logger.info("MongoClient is using public IP address: " + publicIP);

			// Initialize MongoDB client with connection string
			MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(CONNECTION_STRING)).build();

			try(MongoClient mongoClient = MongoClients.create(settings))
			{
				logger.info("MongoClient Connected!!");

				MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
				MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

				db_managers_by_language.get(language).startWriteMode();

				// Fetch phrases from the JSON file
				TreeMap<String, TreeSet<String>> phrases_hashmap = GsonUtil.getHugeJSONAsHashMap(phrase_json_file_path);

				int totalWords = phrases_hashmap.size(); // Total words to be processed

				long startTime = System.currentTimeMillis(); // Track start time

				// Load the progress from the progress file if it exists
				int wordsProcessed = loadProgress(language);
				logger.info(wordsProcessed > 0 ? String.format("Resuming from word %d. Continuing migration...", wordsProcessed) : "No progress found. Starting from scratch.");

				// Skip the processed words using iterator
				Iterator<String> iterator = phrases_hashmap.keySet().iterator();
				for(int i = 0; i < wordsProcessed && iterator.hasNext(); i++)
				{
					iterator.next(); // Skip the processed words
				}

				// Process remaining words from the current position
				int duplicateEntries = 0; // Track how many words are identical in DB
				int newDBEntries = 0; // Track how many words added newly in DB
				int invalidWords = 0; // Track how many words added newly in DB

				while(iterator.hasNext())
				{
					String word = iterator.next();

					// Log progress every 'n' seconds
					long elapsedTime = System.currentTimeMillis() - startTime;
					
					if(elapsedTime >= PROGRESS_SAVE_INTERVAL)
					{
						saveProgress(language, wordsProcessed);
						startTime = System.currentTimeMillis(); // Reset the start time
						logger.info(String.format("Progress: Completed %d words, Remaining %d words, New %d words, Total %d words. %d duplicate words ignored, %d invalid words", wordsProcessed, totalWords - wordsProcessed, newDBEntries, totalWords, duplicateEntries, invalidWords));						
					}

					if(isInvalidWord(word))
					{
						invalidWords++;
						continue;
					}

					TreeSet<String> newPhrases = new TreeSet<>(phrases_hashmap.get(word));

					// Check if the word already exists in the database
					Bson filter = Filters.and(Filters.eq("language", language.toString()), Filters.eq("word", word));
					Document existingWordDoc = collection.find(filter).first();

					if(existingWordDoc != null)
					{
						// Skip if the word already exists
						duplicateEntries++;
						wordsProcessed++;
						continue; // Proceed to next word
					}

					// Insert a new document if the word does not exist
					Word wordObj = new Word(language, word, newPhrases);

					// Create the document for MongoDB
					Document wordDoc = new Document().append("language", wordObj.language.toString()).append("word", wordObj.word).append("phrases", wordObj.phrases).append("consonantVowelPattern", wordObj.consonantVowelPattern)
							.append("soundexCode", wordObj.soundexCode).append("metaphoneCode", wordObj.metaphoneCode).append("syllableCount", wordObj.syllableCount).append("stressPattern", wordObj.stressPattern);

					collection.insertOne(wordDoc);
					wordsProcessed++;
					newDBEntries++;
				}

				db_managers_by_language.get(language).endWriteMode();
			}
		}

		// Helper method to load the progress from the progress file
		private static int loadProgress(Language language) throws IOException
		{
			File progressFile = new File(PROGRESS_FILE_PATH);
			if(progressFile.exists())
			{
				String jsonContent = new String(Files.readAllBytes(progressFile.toPath()));
				Progress progress = new Gson().fromJson(jsonContent, Progress.class);
				if(progress.language.equals(language.toString()))
				{
					return progress.wordsProcessed;
				}
			}
			return 0; // Start from the beginning if the file doesn't exist or is empty
		}

		// Helper method to save the progress to the progress file
		private static void saveProgress(Language language, int wordsProcessed) throws IOException
		{
			Progress progress = new Progress(language.toString(), wordsProcessed, getCurrentTimestamp());

			File progressFile = new File(PROGRESS_FILE_PATH);
			try(BufferedWriter writer = new BufferedWriter(new FileWriter(progressFile)))
			{
				String json = new Gson().toJson(progress);
				writer.write(json);
			}
		}

		// Helper method to get the current timestamp
		private static String getCurrentTimestamp()
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			return sdf.format(new Date());
		}

		private static boolean isInvalidWord(String word)
		{
			// To ignore nonsensical words like PAYANAMAAAAAA, AAAGAM, sakkkara
			return word.matches(".+(.)\\1{2,}.+") || word.matches("(.)\\1{2,}.+") || word.matches(".+(.)\\1{2,}");
		}

		public static class Word
		{

			Language language;
			String word;
			TreeSet<String> phrases;

			// Phonetic features
			String consonantVowelPattern; // Consonant-Vowel pattern (CVCV)
			String soundexCode; // Soundex code
			String metaphoneCode; // Metaphone code
			int syllableCount; // Syllable count
			String stressPattern; // Stress pattern (101)

			// Constructor with arguments
			public Word(Language language, String word, TreeSet<String> phrases)
			{
				this.language = language;
				this.word = word;
				this.phrases = phrases;
				initializePhoneticData();
			}

			// Initialize phonetic features
			public void initializePhoneticData()
			{
				this.consonantVowelPattern = generateConsonantVowelPattern(this.word);
				this.soundexCode = generateSoundexCode(this.word);
				this.metaphoneCode = generateMetaphoneCode(this.word);
				this.syllableCount = countSyllables(this.word);
				this.stressPattern = generateStressPattern(this.word);
			}

			// Helper method to generate Consonant-Vowel pattern (CVCV)
			private String generateConsonantVowelPattern(String word)
			{
				StringBuilder pattern = new StringBuilder();
				for(char c : word.toCharArray())
				{
					if(isVowel(c))
					{
						pattern.append("V");
					}
					else
					{
						pattern.append("C");
					}
				}
				return pattern.toString();
			}

			// Helper method to check if a character is a vowel
			private boolean isVowel(char c)
			{
				return "AEIOUaeiou".indexOf(c) != -1;
			}

			// Helper method to generate Soundex code
			private String generateSoundexCode(String word)
			{
				Soundex soundex = new Soundex();
				return soundex.encode(word);
			}

			// Helper method to generate Metaphone code
			private String generateMetaphoneCode(String word)
			{
				Metaphone metaphone = new Metaphone();
				return metaphone.encode(word);
			}

			// Helper method to count syllables using simple logic
			private int countSyllables(String word)
			{
				int syllableCount = 0;
				boolean lastWasVowel = false;

				for(char c : word.toLowerCase().toCharArray())
				{
					if(isVowel(c))
					{
						if(!lastWasVowel)
						{ // Count a syllable when a vowel is followed after a consonant
							syllableCount++;
						}
						lastWasVowel = true;
					}
					else
					{
						lastWasVowel = false;
					}
				}

				// Handle edge cases for silent 'e' at the end
				if(word.endsWith("e"))
				{
					syllableCount--;
				}

				return syllableCount > 0 ? syllableCount : 1; // Ensure at least one syllable
			}

			// Helper method to generate Stress Pattern (e.g., "101" for stress patterns)
			private String generateStressPattern(String word)
			{
				StringBuilder stressPattern = new StringBuilder();
				for(int i = 0; i < word.length(); i++)
				{
					if(isVowel(word.charAt(i)))
					{
						stressPattern.append("1");
					}
					else
					{
						stressPattern.append("0");
					}
				}
				return stressPattern.toString();
			}
		}

		public static class Progress
		{
			String language;
			int wordsProcessed;
			String lastUpdated;

			public Progress(String language, int wordsProcessed, String lastUpdated)
			{
				this.language = language;
				this.wordsProcessed = wordsProcessed;
				this.lastUpdated = lastUpdated;
			}
		}

		/**
		 * Helper method to get the public IP address by calling an external service
		 */
		private static String getPublicIP()
		{
			try
			{
				// URL of the external service that returns the public IP address
				URL url = new URL("https://checkip.amazonaws.com");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				// Get the response and read the IP address from the input stream
				try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())))
				{
					String publicIP = in.readLine(); // Read the public IP address from the response
					return publicIP;
				}
			}
			catch(Exception e)
			{
				logger.log(Level.SEVERE, "Error fetching public IP address: ", e);
			}
			return "Unable to determine public IP address";
		}
	}

	/*
	 
	Sample migration code for JSONToMongoDBMigrator
	
	public static void main(String args[]) throws IOException
	{
		Long start = System.currentTimeMillis();

		init();

		String phrase_json_file_path = "/Users/kabilan-5523/code/personal/tamilrhymefinder/src/main/resources/phrases_tamil.json";
		JSONToMongoDBMigrator.migratePhrasesJSONToDB(Language.TAMIL, phrase_json_file_path);

		Long end = System.currentTimeMillis();
		logger.info("Total Time:" + (end - start));
	}
	
	*/
}
