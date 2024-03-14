package utils;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

public class ConnectMongoDB {
	private static final String CONNECTION_STRING = "mongodb://localhost:27017/";
	private static final String DB_NAME = "BikeStores"; // "ordersDB";

	private static MongoClient client;
	private static MongoDatabase db;
	
	public static void getConnection() {
		ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
		client = MongoClients.create(connectionString);
		db = client.getDatabase(DB_NAME);
	}
	public static void getConnection(String dbName) {
		
		ConnectionString connectionString = new ConnectionString(CONNECTION_STRING);
		client = MongoClients.create(connectionString);
		db = client.getDatabase(dbName);
	}
	public static  MongoClient getClient() {
		return client;
	}

	public static  MongoDatabase getDatabase() {
		return db;
	}
	public static void closeClient() {
		client.close();
	}
}
