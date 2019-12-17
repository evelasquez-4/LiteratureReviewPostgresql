package app.references;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import app.documents.Document;
import app.literature.review.model.DblpDocument;


//import busquedaArticulos.Articulo;
//import lucene.ArticuloDblp;
//import lucene.ImportarMongo;

public class Mendeley {

	public static String access_token="MSwxNTY3NDAzMjYwODU4LDU0NzQ0MzY1MSwxMDI4LGFsbCwsLDk1OGZlZWI2MTQ3NTEyNDVkNjI5NzIyNDgyNmNjNDc4YmVjN2d4cnFiLGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNiw1WUVBcDBxM0FobnJ3THBWZVQzTXZURVQtWU0";
	
	public Referencia mendeleySearchByISBN(String isbn)
	{
		
		RestTemplate restTemplate = new RestTemplate();
	    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
	    
	    
	    String url = "https://api.mendeley.com/catalog?isbn="+isbn.trim()+"&limit=1"; 
	    String encoding3 = Base64.getEncoder().encodeToString(Mendeley.access_token.getBytes(StandardCharsets.UTF_8));
	    encoding3 = encoding3.replace("\n", "");
	    httpHeaders.add("Authorization","Bearer " +  Mendeley.access_token);
		
	    HttpEntity<String> requestEntity = new HttpEntity<>("Headers", httpHeaders);
	    String ref = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
	    Charset utf8charset = Charset.forName("UTF-8"); 
	    ref= new String ( ref.getBytes(), utf8charset);
	    
	    JSONArray objectArray = new JSONArray(ref);
	    Referencia  referencia = new Referencia();
	    
	    
	    if(objectArray.length()>0) {
	    	String refMedeley = objectArray.get(0).toString();
	    	JSONObject object = new JSONObject(refMedeley);
	    	
	    	System.out.println(object.length());
	    	System.out.println(object.getString("abstract"));
	    	
	    	if(object.has("doi"))
	    		referencia.setDoi(object.getString("doi"));
	    	
	    	if(object.has("year")) 
	    		referencia.setYear(object.get("year").toString());
	    	
	    	if(object.has("abstract"))
	    		referencia.setAbstract(object.getString("abstract"));
	    	
	    	if(object.has("keywords")) {
	    		ArrayList<String> keywords = new ArrayList<>();
	    		JSONArray jsonArray = object.getJSONArray("keywords");
			
	    		if (jsonArray != null) { 
	    			int len = jsonArray.length();
				
	    			for (int i=0;i<len;i++){ 
	    				keywords.add(jsonArray.get(i).toString());
	    			}
	    		}
			
	    		referencia.setKeywords(keywords);
    	
	    	}
	    	
	    }
	    
		return referencia;
	}
	
	public Referencia mendeleyConsulta(String DOI) throws IOException, JSONException{
		RestTemplate restTemplate = new RestTemplate();
	    org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();

	    String url = "https://api.mendeley.com/catalog?doi="+DOI;        
        String userpass = Mendeley.access_token;
        String encoding3 = Base64.getEncoder().encodeToString(userpass.getBytes(StandardCharsets.UTF_8));
        encoding3 = encoding3.replace("\n", "");
        httpHeaders.add("Authorization","Bearer " +  userpass);
        
	    HttpEntity<String> requestEntity = new HttpEntity<>("Headers", httpHeaders);
	    String ref = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
	    Charset utf8charset = Charset.forName("UTF-8"); 
	    ref= new String ( ref.getBytes(), utf8charset);
	   
	    
	    JSONArray objectArray = new JSONArray(ref);
	    Referencia  referencia = new Referencia();

	    if(objectArray.length()>0) {
	    	String refMedeley = objectArray.get(0).toString();
	    	JSONObject object = new JSONObject(refMedeley);
	    	
	    	
	    	if(object.has("doi")) 
	    		referencia.setDoi(object.getString("doi"));
	    	
	    	if(object.has("abstract")) 
	    		referencia.setAbstract(object.getString("abstract"));
	    	 
	    	if(object.has("year")) 
	    		referencia.setYear(object.get("year").toString());
	    	
	    	
	    	if(object.has("keywords")) {
	    		ArrayList<String> keywords = new ArrayList<>();
	    		JSONArray jsonArray = object.getJSONArray("keywords");
			
	    		if (jsonArray != null) { 
	    			//int len = jsonArray.length();
				
	    			for (int i=0;i<jsonArray.length();i++){ 
	    				keywords.add(jsonArray.get(i).toString());
	    			}
	    		}
			
	    		referencia.setKeywords(keywords);
    	
	    	}
	    }	
	   return referencia;

	}
	
	public void nuevoToken() throws IOException, JSONException, InterruptedException{
	
		String[] command = new String[]{"curl", "-u", "7258:wfPNFp3iBJtowj2b",
		         "-XPOST","-H", "Content-Type: application/x-www-form-urlencoded;charset=UTF-8", 
		         "-H", "Accept: application/json","-d","grant_type=client_credentials&scope=all",
		         "https://api.mendeley.com/oauth/token"};
				
		
	   // StringBuffer output = new StringBuffer();
	    Process p;	    
	    p = Runtime.getRuntime().exec(command);
	    p.waitFor();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	    p.getInputStream()));
	    
	    String result = reader.readLine(); // value is NULL
	    
	    
	   JSONObject object =  new JSONObject( result);
	    if(object.has("access_token")) {
	    	Mendeley.access_token=object.getString("access_token");
    	}
	}
	
	public void obtainToken() throws OAuthSystemException, OAuthProblemException, ClientProtocolException, IOException {
		OAuthClientRequest request = OAuthClientRequest
				.authorizationLocation("https://api.mendeley.com/oauth/authorize")
				.setClientId("7258")
				.setRedirectURI("http://localhost:8080/")
				.setResponseType("token")
				.setScope("all")
				.buildQueryMessage();
				
		
		System.out.println(request.getLocationUri());
		
	}

	public List<Document> referenciasDocumentosDOI(List<Document> documents) throws ParserConfigurationException, SAXException, IOException, JSONException{
		
		for(Document doc : documents){
			if(doc.getDoi().trim().length() > 0 || doc.getDoi() != null) 
			{
				if(doc.getAbstractDocument() == null || doc.getKeywords().size() == 0  ) 
				{
					Referencia referencia = mendeleyConsulta((doc.getDoi()));
					
					if(referencia != null) {
							
						if(doc.getAbstractDocument() == null && referencia.getAbstract() !=null) {
							doc.setAbstractDocument(referencia.getAbstract());
						}
					
						if(doc.getKeywords().size() == 0 && referencia.getKeywords() !=null) {
							doc.setKeywords(referencia.getKeywords());
						}
						
						if(doc.getAnio() == null && referencia.getYear() !=null) {
							doc.setAnio(referencia.getYear());
						}
					
					}
					
				} 
				
				
		   }
		}
		
		return documents;
	}
	
	public List<DblpDocument> updateDblpDocuments(List<DblpDocument> dblp) throws JSONException, IOException
	{
		List<DblpDocument> res = new ArrayList<DblpDocument>();
		
		for(DblpDocument d : dblp) 
		{
			//d->DOI true
			if( d.getDoi().trim().length() > 0 || !d.getDoi().isEmpty())
			{
				Referencia referencia = mendeleyConsulta(d.getDoi());
				if(d.getAbstractDocument().isEmpty() || d.getKeywords().size() == 0)
				{
					if(referencia != null)
					{
						if(d.getAbstractDocument().isEmpty() && referencia.getAbstract()!=null) 
							d.setAbstractDocument(referencia.getAbstract());
						
						if(d.getKeywords().size() == 0 && !referencia.getKeywords().isEmpty())
							d.setKeywords(referencia.getKeywords());
						
						if(d.getYear() == 0 && !referencia.getYear().isEmpty())
							d.setYear(Integer.parseInt( referencia.getYear()) );
						
					}
				} 
				
				res.add(d);
				
			}
			//ISBN true
			else if(!d.getIsbn().isEmpty() && d.getIsbn().trim().length() > 0)
			{
				Referencia referencia = mendeleySearchByISBN(d.getIsbn().trim());
			
				if(referencia != null) {
					
					if(d.getAbstractDocument().isEmpty() && referencia.getAbstract()!=null)
						d.setAbstractDocument(referencia.getAbstract());
					
					if(d.getKeywords().size() == 0 && !referencia.getKeywords().isEmpty())
						d.setKeywords(referencia.getKeywords());
					
					if(d.getYear() == 0 && !referencia.getYear().isEmpty())
						d.setYear(Integer.parseInt( referencia.getYear()) );
					
					if(d.getDoi().isEmpty() && !referencia.getDoi().isEmpty())
					{
						String aux = "";
						String[] split = referencia.getDoi().split("/");
						if(split[0].contains("https")  && split[2].contains("doi."))
						{
							for (int i = 3; i < split.length; i++) {
								aux += split[i] + "/";
							}
							aux = aux.substring(0, aux.length() - 1);
							
							d.setDoi(aux);
						}
					}
				}
				res.add(d);	
			}
			
			
		}
		
		return res;
	}

/*
public ArrayList<ArticuloDblp>  referenciasArticulosDblp(ArrayList<ArticuloDblp> Articulos) throws Exception{
	
	ImportarMongo im = new ImportarMongo();
		for(ArticuloDblp articulo : Articulos){
			if(articulo.getEe()!=null) {
				if(articulo.getEe().length()>16) {
					if(articulo.getEe().substring(0, 16).equals("https://doi.org/")){
						articulo.setDoi(articulo.getEe().substring(16));
					}
				
					if(articulo.getEe().substring(0, 1).equals("[")){					
						String[] dois = articulo.getEe().split("\" ,");
						if(dois[0].substring(0, 19).equals("[ \"https://doi.org/")){
							articulo.setDoi(dois[0].substring(19));
						}					
					}
				}				
				if(articulo.getDoi() != null) {
					if(articulo.getResumen() == null || articulo.getKeywords().isEmpty() ) {
						Referencia referencia;				
						referencia = mendeleyConsulta((articulo.getDoi()));
						if(referencia != null) {								
							if(articulo.getResumen() == null && referencia.getAbstract() !=null) {
								articulo.setResumen(referencia.getAbstract());
							}
							
							if(articulo.getKeywords() == null && referencia.getKeywords()!=null ) {							
								articulo.setKeywords(referencia.getKeywords());
							}
							
							if(articulo.getYear() == null && referencia.getYear() !=null) {
								articulo.setYear(referencia.getYear());
							}						
						}
						im.ActualizarExtras(articulo);	
					} 
			   }				
			}			
		}		
	    return  Articulos;
	}
*/
}
