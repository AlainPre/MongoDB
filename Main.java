package principal;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

// Le nom de la classe n'est pas critique
// Par contre la méthode doit etre nommée main()

public class Main {

	public static void main(String[] args) {
		
		// Syntaxe complète : pour accéder à un serveur distant avec login/pwd	
		// MongoClient client1 = new MongoClient(new MongoClientURI("mongodb://user:pwd@adresseIP:27017"));
		
		// Syntaxe intermédiaire pour accéder à un serveur distant, sans mot de passe
		// MongoClient client3 = new MongoClient("adresseIP", 27017);
		
		try {
			MongoClient client = new MongoClient();							// Connection
			
			MongoDatabase db = client.getDatabase("demo");					// Choisir la base de données
			MongoCollection coll = db.getCollection("departements");		// Choisir la collection							
			
			
			// Extraire sur un critère unique (syntaxe raccourcie):
			
			Bson filtre1 = new BasicDBObject("region","Occitanie");			// Créer une paire clé/valeur (qui sera le filtre pour find() )
			
			MongoCursor<Document> cursor = coll.find(filtre1).cursor();		// On utilise un cursor typé pour les documents MongoDB (json)
			while(cursor.hasNext()) {
				System.out.println(cursor.next().get("nom"));
			}
			
			System.out.println("Critère double :");
			

			// Extraire d'après un critère multiple:
			
			BasicDBObject filtre2 = new BasicDBObject("region", "Occitanie");
			filtre2.put("no_dept", "81");
			
			MongoCursor<Document> cursor2 = coll.find(filtre2).cursor();		// On utilise un cursor typé pour les documents MongoDB (json)
			while(cursor2.hasNext()) {
				System.out.println(cursor2.next().get("nom"));
			}

			// Ajouter un document :
			
			System.out.println("Ajout d'un département");
			
			LocalDate ld = LocalDate.now();
			Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
			
			Document obj = new Document()
					.append("_id", "101")
					.append("no_dept", "101")
					.append("nom", "Nouveau département")
					.append("surface", 2521)
					.append("date_creation", date);
			
			try {
				db.getCollection("departements").insertOne(obj);
			}
			catch(Exception ex) {
				System.out.println("Département déjà existant.");
			}
			
			
			// Suppression de document(s) :
/*			
			System.out.println("Suppression de départements");
			
			BasicDBObject filtre3 = new BasicDBObject("no_dept", "101");
			db.getCollection("departements").deleteMany(filtre3);
*/			
			
			
			// Mise à jour d'un document :
			
			System.out.println("Mise à jour d'un document :");
			
			BasicDBObject filtre4 = new BasicDBObject("no_dept", "101");
			Document doc = new Document(
						"$set", new Document()
							.append("nom", "nom modifié")
							.append("surface", 1000)
					);
			
			/*  Equivalent au code JSON suivant :
			 		{
			 		$set: {
			 				nom:"nom modifié",
			 				surface: 1000
			 				}
			 		}
			 */
			
			db.getCollection("departements").updateOne(filtre4, doc);
			
			
			// Importation d'un fichier CSV :
			
			System.out.println("Importation du fichier");
			
			Runtime rt = Runtime.getRuntime();
			
			String exe = "\"C:/Program Files/MongoDB/tools/100/bin/mongoimport.exe\"";
			String csv =  " --file \"C:/Users/ALP/Documents 2021/Apsoft/Supports/M2I-MongoDb/mongodb/data/departements.csv\"";
			String dbas = " --db demo";
			String col =  " --collection departements2";
			String opts = " --type csv --headerline";
			String cmd = exe + dbas + col + opts + csv;
		
			try {
				rt.exec(cmd);
			}
			catch (Exception ex){
				System.out.println("Erreur : " + ex.toString());
			}
			
			System.out.println("Tout est OK");
			
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
}









/* Filtre par date
 * 
 	System.out.println("\nRecherche par date :");
			LocalDate ld = LocalDate.of(1950, 1, 1);
			Date dateMin = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
			BasicDBObject critere = new BasicDBObject();
			critere.put("date_creation", new BasicDBObject("$gte", dateMin));
			MongoCursor<Document> it = db.getCollection("departements").find(critere).cursor();
			while(it.hasNext()) {
				System.out.println(it.next().get("nom"));
			}
 */

/* Ajouter un document
 * 
  			LocalDate ld = LocalDate.now();
			Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
			Document doc = new Document()
				.append("no_dept", "101")
				.append("nom", "Nouveau département")
				.append("surface", 2521)
				.append("date_creation", date);
			
			
			db.getCollection("departements").insertOne(doc);

 */

/* Supprimer un document
 
 			BasicDBObject filtre3 = new BasicDBObject("no_dept","101");
			coll.deleteOne(Filters.eq("no_dept", "101"));
			

 */

/* Importataion :
 * 
 			System.out.println("\nImportation :");
 			Runtime rt = Runtime.getRuntime();
			String exe =  "\"C:/Program Files/MongoDB/Tools/100/bin/mongoimport.exe\"";
			String csv =  " --file \"C:/Users/ALP/Documents 2021/Apsoft/Supports/M2I-MongoDb/mongodb/data/departements.csv\"";
			String dbas = " --db demo";
			String col =  " --collection departements2";
			String opts = " --type csv --headerline";
			String cmd = exe + dbas + col + csv + opts;

			try {
				rt.exec(cmd);
			}
			catch (Exception ex){
				ex.printStackTrace();
			}	

 */





	/*
	
	try {
	// Connection, plusieurs syntaxes suivant la version:
	
	MongoClient client1 = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
	MongoClient client2 = new MongoClient();
	MongoClient client3 = new MongoClient("localhost", 27017);
	

	// Choisir une BDD (peu importe si elle existe ou pas) :
	
	DB db = client3.getDB("demo");
	
	
	// Remarque mongodb ne connait que le type Date, qui est déprécié en Java
	// Nous devons donc créer un objet Localdate (java) et le convertir ensuite en type date
	
	LocalDate ld1 = LocalDate.now();
	Date date1 = Date.from(ld1.atStartOfDay(ZoneId.systemDefault()).toInstant());

	LocalDate ld2 = LocalDate.of(2001, 2, 10);
	Date date2 = Date.from(ld2.atStartOfDay(ZoneId.systemDefault()).toInstant());

	LocalDate ld3 = LocalDate.of(2000, 1, 1);
	Date date3 = Date.from(ld3.atStartOfDay(ZoneId.systemDefault()).toInstant());

	LocalDate ld4 = LocalDate.of(2000, 12, 31);
	Date date4 = Date.from(ld4.atStartOfDay(ZoneId.systemDefault()).toInstant());

	// Ajouter un document :

	DBObject obj = new BasicDBObject()
			.append("nom", "Martin")
			.append("prénom", "Paul")
			.append("nb_enfants", 2)
			.append("date_naissance",  date1);
	
	db.getCollection("demo").insert(obj);
	
	// Ajouter un document aavec un document incorporé :
	
	DBObject obj2 = new BasicDBObject()
			.append("nom", "Dupond")
			.append("adresse", new BasicDBObject()
					.append("rue", "Rue de la République")
					.append("cp", "69002")
					.append("ville", "Lyon")
					)
			.append("date_naissance", date2);
	
	db.getCollection("demo").insert(obj2);
	
	
	// Retrouver un ou des documents à partir du nom :
	
	System.out.println("Recherches par nom :");
	
	DBObject query1 = new BasicDBObject("nom", "Martin");
	DBCursor cursor1 = db.getCollection("demo").find(query1);
	
	while(cursor1.hasNext()) {
		System.out.println( cursor1.next());
	}
	
	
	// Retrouver un ou des documents avec des conditions multiples :
	// Exemple : les personnes nées en 2001
	
	System.out.println("Recherches par dates :");
	
	DBObject query2 = new BasicDBObject();
	query2.put("date_naissance", new BasicDBObject("$gte", date3));
	query2.put("date_naissance", new BasicDBObject("$lte", date4));
	
	DBCursor cursor2 = db.getCollection("demo").find(query2);
	
	while(cursor2.hasNext()) {
		System.out.println( cursor2.next().get("nom"));
	}

	
	

} 
catch (UnknownHostException e) {
	e.printStackTrace();
}

System.out.println("\nTraitement terminé");
}
	
	*/