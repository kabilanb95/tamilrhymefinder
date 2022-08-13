package com.arkmusic.tamilrhymefinder.server;

import java.util.Arrays;
import java.util.List;

public class Configuration
{
	// set true if case the db file size gets too large because of lot of words in
	// the language
	public static final boolean IS_USE_MULTIPLE_TINY_DBS = false,
			// set as true only when adding data to DB through migration
			IS_USE_UNSTABLE_BUT_FAST_DB = false, IS_JSON_TO_DB_MIGRATION_MODE = false;

	public static List<Integer> getSupportedLastNChars()
	{
		// entries to the array should be in descending order ONLY
		Integer[] supported_last_n_chars = new Integer[] { 5, 4, 3 };
		return Arrays.asList(supported_last_n_chars);
	}
}
