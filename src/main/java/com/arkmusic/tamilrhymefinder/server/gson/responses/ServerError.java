package com.arkmusic.tamilrhymefinder.server.gson.responses;

public class ServerError
{
	public int error_code;
	public String error_message;

	public ServerError(int error_code, String error_message)
	{
		this.error_code = error_code;
		this.error_message = error_message;
	}
}
