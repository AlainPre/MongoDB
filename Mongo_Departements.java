package principal;

import java.io.IOException;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.jsoup.Jsoup;


/*
 * 	Ce code ajoute une description à la collection des départements
 * 	Le texte de la description est récupéré sur Wiokipedia
 * 
 */
public class Mongo_Departements {

	public static void main(String[] args) throws IOException {

		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("demo");	

		MongoCursor<Document> cursor = db.getCollection("departements").find().cursor();
		
		while(cursor.hasNext()) {
			Document dept = cursor.next();
			String url = dept.getString("url_wiki");
			url = url.replace("http","https");					// Vérifier si nécessaire
			
			String description = getDescription(url);
			
			ObjectId id = (ObjectId) dept.get("_id");
			BasicDBObject filtre = new BasicDBObject("_id", id);	
			Document doc = new Document(
					"$set",
					new Document(
						"description", description
					)
				);
			db.getCollection("departements").updateOne(filtre, doc);
		}
		System.out.println("C'est fini");
	}

	private static String getDescription(String url) throws IOException {
		org.jsoup.nodes.Document html = Jsoup.connect(url).get();

		return html.getElementsByTag("main").text();
		
	}
}
