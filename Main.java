package principal;

import org.json.JSONArray;
import org.json.JSONObject;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 	Syntaxe de l'URL pour accéder au WebService:
 	https://api.openweathermap.org/data/2.5/onecall?appid=298d444f015f0512eac4f88f98587d01&lat=45.764043&lon=4.835659&units=metric&lang=fr&exclude=minutely
 	
*/
public class Main {
	
	static final String URL = "https://api.openweathermap.org/data/2.5/onecall?appid=298d444f015f0512eac4f88f98587d01&exclude=minutely";
	static final Double LAT = 45.764043;
	static final Double LON = 4.835659;
	static final String LANG = "fr";

	public static void main(String[] args) throws InterruptedException {
	
		// Connection au web service et récupération de la réponse :
		
		String url = URL + "&lat=" + LAT + "&lon=" + LON + "&lng=" + LANG;
		HttpClient client = new HttpClient(url);
		client.start();
		client.join();
		String reponse = client.getResponse();
		
		// Connection à MongoDB :
		
		MongoClient clientMongo = new MongoClient();
		MongoDatabase db = clientMongo.getDatabase("meteo");	
		
		
		// Analyse de la réponse JSON :
		
		JSONObject json = new JSONObject(reponse);
		JSONArray jsonHourly = json.getJSONArray("hourly");
				
		for(int i=0; i<jsonHourly.length(); i++) {
			JSONObject obj = jsonHourly.getJSONObject(i);
			
			String description = obj.getJSONArray("weather").getJSONObject(0).getString("description");
			
			Document sdoc = new Document();
			sdoc.append("id",  obj.getJSONArray("weather").getJSONObject(0).getLong("id"));
			sdoc.append("main",  obj.getJSONArray("weather").getJSONObject(0).getString("main"));
			sdoc.append("description",  obj.getJSONArray("weather").getJSONObject(0).getString("description"));
			sdoc.append("icon",  obj.getJSONArray("weather").getJSONObject(0).getString("icon"));
			
					
			Document doc = new Document();
			doc.append("_id", obj.getLong("dt"));
			doc.append("dt", obj.getLong("dt"));
			doc.append("temp", obj.getDouble("temp"));
			doc.append("pressure", obj.getDouble("pressure"));
			doc.append("humidity", obj.getDouble("humidity"));
			doc.append("wind_speed", obj.getDouble("wind_speed"));
			doc.append("wind_deg", obj.getDouble("wind_deg"));
			doc.append("pressure", obj.getDouble("pressure"));
			//doc.append("weather", description);
			doc.append("weather",  sdoc);
			
			try {
				db.getCollection("lyon").insertOne(doc);
			}
			catch(MongoWriteException ex) {}
			catch(Exception ex) {
				System.out.println("Erreur pendant l'insertion");
			}
		}	
		
	}

}
