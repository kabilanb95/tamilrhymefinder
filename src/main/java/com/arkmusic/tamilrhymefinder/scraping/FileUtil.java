package com.arkmusic.tamilrhymefinder.scraping;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileUtil 
{
    public static void writeStringToFile(String path,String file_content) throws IOException
    {
		FileUtils.writeStringToFile(new File(path),file_content,"UTF-8");
    }
    
    public static String getFileAsString(String path) throws IOException
    {
		return FileUtils.readFileToString(new File(path), "UTF-8");
    }
}
