package com.arkmusic.tamilrhymefinder.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.arkmusic.tamilrhymefinder.server.phrases.PhraseCacherManager;
import com.arkmusic.tamilrhymefinder.server.words.RhymeCacherManager;
import com.arkmusic.tamilrhymefinder.server.words.WordsManager;

public class ServerInit extends HttpServlet
{
  private static Logger logger=Logger.getLogger(ServerInit.class.getName());
  
  public static boolean isInitAlready=false;

  public void init() throws ServletException
  {
      if(isInitAlready)
      {
          return;
      }
      
      isInitAlready=true;

      logger.info("---------- ServerInit Initialized process started successfully ----------");
      try 
      {
    	  WordsManager.init();
    	  RhymeCacherManager.init();
    	  PhraseCacherManager.init();
      }
      catch (IOException e) 
      {
    	  throw new RuntimeException("Unexpected IO Exception",e);
      }
      logger.info("---------- ServerInit Initialized process ended successfully ----------");
  }
}