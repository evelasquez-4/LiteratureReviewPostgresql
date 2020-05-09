package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import literature.review.app.model.Keywords;
import literature.review.app.model.Publications;
import literature.review.app.model.SpringerApi;
import literature.review.app.repository.SpringerApiRepository;

@Service
public class SpringerApiService implements SpringerApiRepository{

	private OkHttpClient client = new OkHttpClient().newBuilder().build();
	private String api_key = "&api_key=de56a248cf6ecfa4f5dbce1342c57e81";
	private String urlBase;
	
	@Autowired
	private PublicationsService publications;
	@Autowired
	private KeywordsService keyword;
	@Autowired
	private PublicationKeywordsService publication_keyword;
	
	public void test(String doi)
	{
		try {
		
			SpringerApi api = new SpringerApi(findSpringerPublicationByDOI(doi));
			
			
			System.out.println(api.getAbstract_());
			api.getKeywords().iterator().forEachRemaining((a)->{
				System.out.println(a);
			});
			
		} catch (Exception e) {
			System.out.println("Error springer :"+e.getMessage());
		}
	}
	
	public void updatePublicationsSpringer(List<Publications> publications) throws Exception
	{
		try {
			
			for (Publications pub : publications) {
				String doi = pub.extractDOI();
				SpringerApi api = null;
				JSONObject obj = null;
				
				if(!doi.isEmpty())
				{
					obj = findSpringerPublicationByDOI(doi);
					
					if(obj.length() > 0)//si encuentra match por DOI
					{
						api = new SpringerApi(obj);
						pub.setAbstract_(api.getAbstract_());
						
						if(api.getKeywords().size() > 0)
							this.publication_keyword.registerPublicationsKeywords(api.getKeywords(), pub);
							//registerPublicationSpringerKeywords(api.getKeywords(), pub);
					}
				}
				
				pub.setUpdatedState("2.mendeley_updated");
				this.publications.save(pub);
			}
			
		} catch (Exception e) {
			System.out.println("Error actualizacion Springer:"+e.getMessage());
		}
	}
	
	public JSONObject findSpringerPublicationByDOI(String doi) throws Exception
	{
		setUrlBase("http://api.springernature.com/meta/v2/json?q=doi:"+doi+"&p=1"+api_key);
		
		Request request = new Request.Builder()
				.url(this.urlBase)
				.method("GET", null)
				.build();
		
		JSONObject res = new JSONObject();
		try(Response response = client.newCall(request).execute())
		{
			if (!response.isSuccessful()) 
				throw new IOException("Unexpected code  -> " + response.code()+" "+response);
		
			res = new JSONObject(response.body().string());
			response.close();
		}catch (Exception e) {
			System.out.println("Error json -> "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}
	
	public void registerPublicationSpringerKeywords(List<String> keys,Publications pub)
	{
		keys.forEach( (key)->{
			Keywords k = this.keyword.registerKeyword(key);
			this.publication_keyword.savePublicationKeyword(pub, k);
		});
	}
	
	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}
	
	public String getKey() {
		return api_key;
	}
	public void setKey(String key) {
		this.api_key = key;
	}
	
}
