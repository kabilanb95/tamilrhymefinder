package com.arkmusic.tamilrhymefinder.mongodb;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.arkmusic.tamilrhymefinder.configuration.AppConfig;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDBConnection
{

	private static MongoDBConnection instance;
	private MongoClient mongoClient;
	private static final String MONGODB_CONNECTION_STRING = AppConfig.getProperty("mongodb.connectionString");
	private static final String DATABASE_NAME = AppConfig.getProperty("mongodb.databaseName");

	// Private constructor to prevent external instantiation
	private MongoDBConnection()
	{
		ConnectionString connectionString = new ConnectionString(MONGODB_CONNECTION_STRING);
		CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
		MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).codecRegistry(codecRegistry).build();
		mongoClient = MongoClients.create(clientSettings);
		log.info("MongoDB connection established successfully.");
	}

	// Get the singleton instance of MongoDBConnection
	public static MongoDBConnection getInstance()
	{
		if(instance == null)
		{
			synchronized(MongoDBConnection.class)
			{
				if(instance == null)
				{
					instance = new MongoDBConnection();
				}
			}
		}
		return instance;
	}

	// Get the MongoDatabase instance
	public MongoDatabase getDatabase()
	{
		return mongoClient.getDatabase(DATABASE_NAME);
	}

	// Close connection on application shutdown
	public void closeConnection()
	{
		if(mongoClient != null)
		{
			mongoClient.close();
			mongoClient = null;
		}
	}
}