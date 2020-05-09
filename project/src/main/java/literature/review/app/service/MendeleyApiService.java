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
import literature.review.app.model.Keywords;
import literature.review.app.model.MendeleyApi;
import literature.review.app.model.Publications;
import literature.review.app.repository.MendeleyApiRepository;


@Service
public class MendeleyApiService implements MendeleyApiRepository{
	
	@Autowired
	private PublicationsService publications;
	@Autowired
	private KeywordsService keyword;
	@Autowired
	private PublicationKeywordsService publication_keyword;
	
	private String urlBase = "https://api.mendeley.com/";
	private OkHttpClient client = new OkHttpClient().newBuilder().build();
	private String key = "";
		
	@Override
	public void updatePublications(List<Publications> publications, String mendeleyKey) throws Exception
	{		
		try {
			setKey(mendeleyKey);
			for (Publications pub : publications) 
			{	
				String doi = pub.extractDOI(); 
				MendeleyApi api = new MendeleyApi();
				JSONArray array = new JSONArray();
				
				if(doi.equals("") || doi.isEmpty())
				{
					//search by title
					array = findMendeleyPublicationByTitle(pub.getTitle());
					api = new MendeleyApi(array, pub);
				}
				else{
					//search by doi
					array = findMendeleyPublicationByDOI(doi);
					if(array.length() > 0)
						api = new MendeleyApi(array,pub);
					else
					{
						array = findMendeleyPublicationByTitle(pub.getTitle());
						api = new MendeleyApi(array, pub);
					} 
				}
				//condicion para ver si cualquier publicacion matcheo en el API mendeley
				if(api.getNumElements() > 0)
				{
					pub.setAbstract_(api.getAbstract_());
					//registro keywords
					if(api.getKeywords().size() > 0)
						registerPublicacionMendeleyKeywords(api.getKeywords(), pub);
				}	
				pub.setUpdatedState("2.mendeley_updated");
				this.publications.save(pub);
			}
			
		} catch (Exception e) {
			System.out.println("Error actualizacion publicaciones:"+e.getMessage());
		}
	}
	
	@Override
	public JSONArray findMendeleyPublicationByDOI(String doi) throws Exception
	{
		setUrlBase("https://api.mendeley.com/catalog?doi="+doi);
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
	
	@Override
	public JSONArray findMendeleyPublicationByTitle(String title) throws Exception
	{
		setUrlBase("https://api.mendeley.com/search/catalog?title="+title+"&limit=10");
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
	
	public JSONArray findMendeleyPublicationByISBN(String isbn) throws Exception
	{
		setUrlBase("https://api.mendeley.com/search/catalog?isbn="+isbn);
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
				.addHeader("Authorization", "Bearer MSwxNTgyOTE5ODkyODY5LDU0NzQ0MzY1MSwxMDI4LGFsbCwsLDY3MjUyM2IwMjUyNzQxNGY5NDg5Nzc2OThkODRhOGEwMjc1NWd4cnFiLGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNixkOTFzak1JelJGTWNiQU1Jd1JKcWxLSDUzV0k")
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

	public void registerPublicacionMendeleyKeywords(List<String> keys, Publications pub)
	{
		for (String desc : keys) {
			Keywords key = this.keyword.registerKeyword(desc);
			this.publication_keyword.savePublicationKeyword(pub, key);
		}
	}	
	
}
