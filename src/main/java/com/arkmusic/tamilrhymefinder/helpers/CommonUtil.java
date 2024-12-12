package com.arkmusic.tamilrhymefinder.helpers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

public class CommonUtil
{
	public static String toCommanSeperatedString(TreeSet<String> set)
	{
		return set.toString().replace("[", "").replace("]", "").trim();
	}

	public static TreeSet<String> commanSeperatedStringToTreeSet(String comma_seperated_string)
	{
		return new TreeSet<String>(Arrays.asList(comma_seperated_string.split(",")));
	}

	public static String removeAllWhiteSpaces(String str)
	{
		return str.replaceAll("\\s+", "");
	}

	// removes all multiple whitespaces with single space
	public static String trim(String str)
	{
		return str.replaceAll("\\s+", " ");
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNReturnNull(String str, int n)
	{
		return str.length() >= n ? str.substring(str.length() - n) : null;
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNReturnInput(String str, int n)
	{
		return str.length() >= n ? str.substring(str.length() - n) : str;
	}

	public static String getLastNCharsIfInputStringIsSmallerThanNThrowException(String str, int n)
	{
		if(str.length() >= n)
		{
			return str.substring(str.length() - n);
		}
		else
		{
			throw new RuntimeException("This should not have happened, check the core logic of entire code. str:" + str + " n:" + n);
		}
	}

	public static TreeSet<String> getWordsFromLyrics(String lyrics)
	{
		TreeSet<String> words = new TreeSet<String>();

		lyrics = lyrics.replace("\n", " ").replace("\r", " ");

		for(String word : lyrics.split(" "))
		{
			word = word.toLowerCase();
			word = word.replaceAll("[^a-zA-Z]+", "");// replace all non alphabetics
			word = word.trim();
			if(!word.trim().equals(""))
			{
				words.add(word);
			}
		}

		return words;
	}

	public static boolean isValidJSON(String json)
	{
		try
		{
			new JSONObject(json);
			return true;
		}
		catch(JSONException e)
		{
			return false;
		}
	}
	
	public static void main(String args[])
	{
        // The date-time string to be converted
        String dateString = "Wed, 11 Dec 2024 00:00";

        // Define a formatter matching the date-time string pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm");

        // Parse the string to a ZonedDateTime object (assuming UTC for consistency)
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, formatter.withZone(ZoneId.of("UTC")));

        // Convert the ZonedDateTime to an Instant and get milliseconds since epoch
        long timestampMillis = zonedDateTime.toInstant().toEpochMilli();

        // Print the result
        System.out.println("Milliseconds since epoch: " + timestampMillis);
        
     // Convert the long to an Instant
        Instant instant = Instant.ofEpochMilli(timestampMillis);

        // Convert the Instant to LocalDateTime in UTC
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        // Format the LocalDateTime as a string
        String formattedDate = dateTime.format(formatter);

        // Print the result
        System.out.println("Formatted date string: " + formattedDate);
	}
}
