package com.arkmusic.tamilrhymefinder.server;

import java.io.File;
import java.io.IOException;

import com.arkmusic.tamilrhymefinder.scraping.FileUtil;

public class ResourceUtil 
{
	public static String getFilePath(String relative_file_path_from_resources_folder)
	{
		return "/Users/kabilan-5523/Documents/myherokuapps/tamilrhymefinder/src/main/resources/"+relative_file_path_from_resources_folder;
//		return ResourceUtil.class.getClassLoader().getResource(relative_file_path_from_resources_folder).getFile();
	}
	
	public static String getFileAsStringFromResourcesFolder(String relative_file_path_from_resources_folder) throws IOException
	{
		String file_str=FileUtil.getFileAsString(getFilePath(relative_file_path_from_resources_folder));
		return file_str;
	}
}
