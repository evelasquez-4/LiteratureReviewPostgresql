package literature.review.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Publications;
import literature.review.app.service.MendeleyApiService;
import literature.review.app.service.PublicationsService;


@RestController
@RequestMapping("/mendeley/api")
public class MendeleyApiController {

	@Autowired
	private MendeleyApiService mendeley;
	@Autowired
	PublicationsService publications;
	
	@GetMapping(value = "/test")
	public void updatePublications() throws Exception
	{
		List<Publications> pubs = this.publications.findPublicationsByType("book", 2);	
		
		try{
			mendeley.updatePublications(pubs);
		} catch (Exception e) {
			System.err.println("Error de actualizacion : "+e.getMessage());
		}
		
	}
	
	/*
	 * actualizacion desde la api de mendeley
	 * @params : 	tipos de publicacion ["article","inproceedings","proceedings","book","incollection"]
	 * 				publicaciones con estado = 2.authors_processed
	 */
//	@GetMapping(value = "/update_publications")
//	public List<Publications> updatePublications(
//			@RequestParam String type,
//			@RequestParam String limit )
//	{
//		int limite = limit.length() == 0 ? 10:Integer.parseInt(limit);
//		List<Publications> listaPublicaciones = this.publications
//				.findByTypeState(type, "2.authors_processed", limite);
//		
//		for (Publications pub : listaPublicaciones) {
//			
//		}
//		
//		return null;
//	}
	

//	
//	public void updatePublicationsByDOI(Publications pub) throws Exception
//	{
//		String responderUrl = pub.extractDOI();
//		
//		HttpGet request = new HttpGet(this.urlBase+responderUrl);
//		request.addHeader("Authorization", "Bearer "+this.key);
//		request.addHeader("Accept", "application/vnd.mendeley-document.1+json");
//		
//		CloseableHttpResponse response = httpClient.execute(request);
//		JsonReader reader = null;
//		
//		try {
//			if(response.getStatusLine().getStatusCode() != 200)
//				throw new RuntimeException("Failed : HTTP error code : "+response.getStatusLine().getStatusCode());
//			
//			if(response.getEntity() != null)
//			{
//				//String result = EntityUtils.toString(response.getEntity());
//				reader = Json.createReader(new InputStreamReader((response.getEntity().getContent())));
//				JsonArray array = reader.readArray();
//				
//				for (int i = 0; i < array.size(); i++) {
//					JsonObject jo = array.getJsonObject(i);
//					System.out.println(jo.toString());
//					if( jo.getString("abstract") != null )
//						pub.setAbstract_(jo.getString("abstract"));				
//					
//				}
//			}
//		}
//		catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//		finally {
//			response.close();
//			reader.close();
//		}
//		
//	}
	/*
	public String completeByDoi(Publications pub)
	{
		this.ResponderUrl = "/catalog?doi=10.1016/j.molcel.2009.09.013";
		
		try 
		{
			HttpURLConnection httpcon = (HttpURLConnection)((new URL(urlBase+ResponderUrl).openConnection()));
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Authorization", "Bearer "+this.key);
			httpcon.setRequestProperty("Accept", "application/vnd.mendeley-document.1+json");
			httpcon.setRequestMethod("GET");
			httpcon.connect();
			
			BufferedReader inreader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
	        String decodedString;
	        
	        while ((decodedString = inreader.readLine()) != null) {
	                System.out.println(decodedString);
	                System.out.println("--------------------");
	                System.out.println(httpcon.getResponseCode());

	        }

	         
	        inreader.close();
	        httpcon.disconnect();
			
		} catch (Exception e) 
		{
			System.out.println(e.getMessage());
			System.out.println("Verifique la caducidad de la clave.");
		}
		return "";
	}
	*/
	
//	public String updateConecction() throws Exception
//	{
//		String url = "https://mendeley-show-me-access-tokens.herokuapp.com/refresh_token?refresh_token=MSw1NDc0NDM2NTEsMTAyOCxhbGwsNzM3ZjVhY2U2NDEwZDk0MGU4ODlhMjk5NTJiNTFkMzhkZjA3Z3hycWIsNzNmNzUwOTRhMTY1MDcxMWY1ZWU2YzEtMWZkYTA4YjU1NjE0ZjQzYywxNTgyMDAyNzI2OTE0LGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNiwsLCwseWJJVlVfQnoxMzl4TzZrOGJhaF9nVVREeEw4";
//		String res = "";
//		HttpGet request = new HttpGet(url);
//		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
//		request.addHeader("Authorization","Bearer "+this.key);
//		
//		CloseableHttpResponse response = httpClient.execute(request);
//		JsonReader reader = null;
//		try {
//			if(response.getStatusLine().getStatusCode() != 200)
//				throw new RuntimeException("Failed : HTTP error code : "+response.getStatusLine().getStatusCode());
//			
//			if(response.getEntity() != null)
//			{
//				//String result = EntityUtils.toString(response.getEntity());
//				reader = Json.createReader(new InputStreamReader((response.getEntity().getContent())));
//				JsonObject json = reader.readObject();
//				res = json.getString("access_token");
//			}
//		
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//		finally {
//			response.close();	
//			reader.close();
//		}
//		return res;
//	}
}
