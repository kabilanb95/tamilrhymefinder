package com.arkmusic.tamilrhymefinder.server;

import java.io.IOException;

import com.arkmusic.tamilrhymefinder.scraping.FileUtil;

public class ResourceUtil 
{
	private static String getFilePath(String relative_file_path_from_resources_folder)
	{
//		return "/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/"+relative_file_path_from_resources_folder;
		if(Configuration.IS_JSON_TO_DB_MIGRATION_MODE)
		{
			throw new RuntimeException("Please comment this exception and make me return the resources folder path when running in db migration mode");
		}
		return ResourceUtil.class.getClassLoader().getResource(relative_file_path_from_resources_folder).getFile();
	}
	
	public static String getDBPath(Language language,String db_file_name)
	{
		return getFilePath("")+"db/"+language.unique_name+"/"+db_file_name;
	}
	
	public static String getFileAsStringFromResourcesFolder(String relative_file_path_from_resources_folder) throws IOException
	{
		String file_str=FileUtil.getFileAsString(getFilePath(relative_file_path_from_resources_folder));
		return file_str;
	}
}
