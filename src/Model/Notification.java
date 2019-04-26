package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

import Commands.Command;

public class Notification {
	private static final String COLLECTION_NAME = "notifications";
	private static int DbPoolCount = 4;
	private static MongoCollection<Document> collection = null;

	public static HashMap<String, Object> create(HashMap<String, Object> attributes, String target_id) throws ParseException {

		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				"mongodb://localhost",options);
		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
		Document newNotification = new Document();

		for (String key : attributes.keySet()) {
			newNotification.append(key, attributes.get(key));
		}

		newNotification.append("target_username", target_id);
		
		collection.insertOne(newNotification);

		JSONParser parser = new JSONParser();

		HashMap<String, Object> returnValue = Command.jsonToMap((JSONObject) parser.parse(newNotification.toJson()));


		mongoClient.close();
		
		return returnValue;
		
	}
	public static int getDbPoolCount() {
		return DbPoolCount;
	}
	public static void setDbPoolCount(int dbPoolCount) {
		DbPoolCount = dbPoolCount;
	}
	public static ArrayList<HashMap<String, Object>> get(String messageId) {
		MongoClientOptions.Builder options = MongoClientOptions.builder()
	            .connectionsPerHost(DbPoolCount);
		MongoClientURI uri = new MongoClientURI(
				"mongodb://localhost",options);

		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("El-Menus");

		// Retrieving a collection
		MongoCollection<Document> collection = database.getCollection("notifications");
		System.out.println("Inside Get");
		BasicDBObject query = new BasicDBObject();
		System.out.println(messageId);
		query.put("target_username", messageId);

		System.out.println(query.toString());
		HashMap<String, Object> message = null;
		FindIterable<Document> docs = collection.find(query);
		JSONParser parser = new JSONParser(); 
		ArrayList<HashMap<String, Object>> notifications = new ArrayList<HashMap<String, Object>>();

		for (Document document : docs) {
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(document.toJson());
				HashMap<String, Object> notification = Command.jsonToMap(json);	
				notifications.add(notification);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mongoClient.close();
        return notifications;
	}
	
}

