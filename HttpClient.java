
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/*	Cette classe accède à une adresse par HTTP ddans un Thread séparé
 * 	Elle peut être utilisée de façon événementielle ou non
 *
 *	Exemple d'utilisation (non événementielle) :
 *  
 *		HttpClient client = new HttpClient("http://.....");
 *		client.start();
 *		client.join();
 *		String result = client.getResponse();
 *
 *
 *	Exemple d'utilisation exploitant les événements :
 *	
 *		class ... implements OnResponseListener {
 *			void start() {
 *				HttpClient client = new HttpClient("http://.....");
 *				client.setOnResponseListener(this);
 *				client.start();
 *			}
 *			@Override
 *			public void onResponse(int id, String response) {
 *				...		
 *			}
 *			@Override
 *			public void onError(int id, String msg) {
 *				...
 *			}
 *		}
 */

public class HttpClient extends Thread {

	private int id;
    private String adr = "";
    private String method = "GET";
    private String body = "";
    private String response = "";
    
    private OnResponseListener onResponseListener;
   
	// Constructeurs :
	
    public HttpClient() {this(1, "", "GET", "");}    
    public HttpClient(int id) {this(id, "", "GET", "");}
    public HttpClient(String adr) {this(1, adr, "GET", "");}
    public HttpClient(int id, String adr) {this(id, adr, "GET", "");}
    public HttpClient(int id, String adr, String method) {this(id, adr, method, "");}

    public HttpClient(int id, String adr, String method, String body) {
        this.id = id;
    	this.adr = adr;
        this.method = method;
        this.body = body;
    }

    // Propriétés :

    public String getAdr() {return adr;}
    public void setAdr(String value) {adr = value;}

    public String getMethod() {return method;}
    public void setMethod(String value) {method = value;}

    public String getBody() {return body;}
    public void setBody(String value) {body = value;}

    public String getResponse() {return response;}

    // Méthode run() :
	//		Effectue la connexion, 
	//		Écrit les données et lit la réponse

    @Override
    public void run() {
        URL url;
        HttpURLConnection cnt = null;
        try {
            url = new URL(adr);

            // Établir la connexion :

            cnt = (HttpURLConnection) url.openConnection();
            cnt.setRequestMethod(method);
            cnt.setDoInput(true);

            // Envoyer les données si méthode POST :

            if(method.equals("POST")) {
                cnt.setDoOutput(true);
                OutputStream out = cnt.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                out.close();
            }

            // Lire la réponse :

            InputStream in = cnt.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            response = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                 response += line;
            }
            
            if(onResponseListener != null) {
            	onResponseListener.onResponse(this.id, response);
            }
            
            reader.close();
            in.close();

        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        	if(onResponseListener != null) {
        		onResponseListener.onError(this.id, ex.getMessage());
        	}
        } finally {
            cnt.disconnect();
        }
    }
    
    public void setOnResponseListener(OnResponseListener listener) {
    	this.onResponseListener = listener;
    }
    
    public interface OnResponseListener {
    	public void onResponse(int id, String response);
    	public void onError(int id, String msg);
    }
}