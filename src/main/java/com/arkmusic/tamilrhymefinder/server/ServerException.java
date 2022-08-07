package com.arkmusic.tamilrhymefinder.server;

import com.arkmusic.tamilrhymefinder.server.gson.responses.ServerError;

public class ServerException extends RuntimeException
{
	ServerError error;
	
	public ServerException(int error_code,String error_message)
	{
		super("Error Code:"+error_code+", Error:"+error_message);
		error=new ServerError(error_code, error_message);
	}
}
