package literature.review.app.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import literature.review.app.model.MendeleyApi;
import literature.review.app.model.Publications;
import literature.review.app.repository.MendeleyApiRepository;


@Service
public class MendeleyApiService implements MendeleyApiRepository{
	
	@Autowired
	private PublicationsService publications;
	
	private String urlBase = "https://api.mendeley.com/";
	private OkHttpClient client = new OkHttpClient().newBuilder().build();
	private String key = "MSwxNTgzMjUwMzU0ODg3LDU0NzQ0MzY1MSwxMDI4LGFsbCwsLDUwYjE5MmYzNTVkYTQzNGQ0YTM4Y2YyMDQyMWM3YzgyYTFiOGd4cnFiLGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNixBN1VRdmpFem5XalZFMWhmMFFKbmFxYjlWVGM";
	
	@Override
	public void updatePublications(List<Publications> publications) throws Exception
	{				
		for (Publications pub : publications) 
		{	
			MendeleyApi mendeley = new MendeleyApi
					( findMendeleyPublication(pub) , pub);
			
			if(mendeley.isUpdated())
			{
				pub.setAbstract_(mendeley.getAbstract_());
				pub.setTitle(mendeley.getTitle());
			}
			
			pub.setUpdatedState("1.mendeley_updated");
			//update
			this.publications.save(pub);
		}
		
	}
	
	@Override
	public JSONArray findMendeleyPublication(Publications pub) throws Exception
	{
		String doi = pub.extractDOI();
		String criteria = doi.isEmpty()? "title" : "doi";
		String cadenaBusqueda = doi.isEmpty()?pub.getTitle():doi;
		
		if( "title".equals(criteria) )
			setUrlBase("https://api.mendeley.com/search/catalog?title="+cadenaBusqueda);
		else
			setUrlBase("https://api.mendeley.com/catalog?doi="+cadenaBusqueda);
		
		Request request = new Request.Builder()
				.url(this.urlBase)
				.method("GET", null)
				.addHeader("Authorization", "Bearer "+getKey())
				.addHeader("Accept", "application/vnd.mendeley-document.1+json")
				.build();
		
		JSONArray res = new JSONArray();
		try(Response response = client.newCall(request).execute())
		{
			if (!response.isSuccessful()) 
				throw new IOException("Unexpected code  -> " + response.code()+" "+response);
		
			if(res.length() == 0)
			{
				switch (criteria) {
				case "doi":
					res  = findMendeleyPublicationByTitle(pub.getTitle());
					break;
				//registrar otros casos de busqueda
				default:
					res = new JSONArray(response.body().string());
					break;
				}
			}
			else
				res = new JSONArray(response.body().string());
			
			response.close();
			
		} catch (JSONException e) 
		{
			System.out.println("Error json -> "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public JSONArray findMendeleyPublicationByTitle(String title) throws Exception
	{
		setUrlBase("https://api.mendeley.com/search/catalog?title="+title);
		Request request = new Request.Builder()
				.url(this.urlBase)
				.method("GET", null)
				.addHeader("Authorization", "Bearer "+getKey())
				.addHeader("Accept", "application/vnd.mendeley-document.1+json")
				.build();
		
		JSONArray res = new JSONArray();
		try(Response response = client.newCall(request).execute())
		{
			if (!response.isSuccessful()) 
				throw new IOException("Unexpected code  -> " + response.code()+" "+response);
		
			res = new JSONArray(response.body().string());			
			response.close();
			
		} catch (JSONException e) 
		{
			System.out.println("Error json -> "+e.getMessage());
			e.printStackTrace();
		}
		
		
		return res;
	}
	
	public boolean verifToken()
	{
		boolean res = false;
		
		Request req = new Request.Builder()
				.url("https://api.mendeley.com/annotations")
				.method("GET", null)
				.addHeader("Content-Type", "application/x-www-form-urlencoded")
				.addHeader("Authorization","Bearer "+getKey())
				.build();
		
		
		try(Response response = client.newCall(req).execute()) {
			res = response.code() == 200;
			response.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return res;
		 
	}
	
	public void renewToken() 
	{
		try {
			String key = verifToken() == true ? this.key : obtainKey();
			if(!key.isEmpty())
				setKey(key);
			else
				System.out.println("verificar codigo autorizacion");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String obtainKey() throws IOException
	{
		String res = null;
		
		Request request = new Request.Builder()
				.url("https://mendeley-show-me-access-tokens.herokuapp.com/refresh_token?refresh_token=MSw1NDc0NDM2NTEsMTAyOCxhbGwsNzM3ZjVhY2U2NDEwZDk0MGU4ODlhMjk5NTJiNTFkMzhkZjA3Z3hycWIsNzNmNzUwOTRhMTY1MDcxMWY1ZWU2YzEtMWZkYTA4YjU1NjE0ZjQzYywxNTgyMDAyNzI2OTE0LGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNiwsLCwseWJJVlVfQnoxMzl4TzZrOGJhaF9nVVREeEw4")
				.method("GET", null)
				.addHeader("Content-Type", "application/x-www-form-urlencoded")
				.addHeader("Authorization", "Bearer MSwxNTY3NDAzMjYwODU4LDU0NzQ0MzY1MSwxMDI4LGFsbCwsLDk1OGZlZWI2MTQ3NTEyNDVkNjI5NzIyNDgyNmNjNDc4YmVjN2d4cnFiLGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNiw1WUVBcDBxM0FobnJ3THBWZVQzTXZURVQtWU0")
				.build();
		
		Response response = client.newCall(request).execute();
		 
		try
		{
			System.out.println("code->"+response.code());
			res = new JSONObject(response.body().string()).getString("access_token");
			
		}catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("code->"+response.code());
		}finally {
			response.close();
		}
		return res;
	}
	
	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	
}
