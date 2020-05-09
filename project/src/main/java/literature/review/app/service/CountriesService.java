package literature.review.app.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import literature.review.app.model.Countries;
import literature.review.app.repository.CountriesRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CountriesService {

	@Autowired
	private CountriesRepository country;
	
	public void loadCountriesFromAPI(String continent) throws Exception
	{
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		Request request = new Request.Builder()
				.url("https://restcountries.eu/rest/v2/region/"+continent)
				.get()
				.build();
		
		JSONArray json = new JSONArray();
		
		try(Response response = client.newCall(request).execute())
		{
			if (!response.isSuccessful()) 
				throw new IOException("Unexpected code  -> " + response.code()+" "+response);
			
			json = new JSONArray(response.body().string());
			json.iterator().forEachRemaining(country->{
				
				Countries c = new Countries();
				String name = ((JSONObject) country).getString("name");
				String code = ((JSONObject) country).getString("alpha2Code");
				
				c.setCountryName( new String(name.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8")));
				c.setCode(new String(code.getBytes(Charset.forName("ISO-8859-1")),Charset.forName("UTF-8")));
				c.setCreatedAt(new Date());
				
				this.country.saveAndFlush(c);
				
			});
			
			response.close();
			
		} catch (JSONException e) 
		{
			System.out.println("Error json -> "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	public Optional<Countries> findCountryByName(String country)
	{
		return this.country.findCountryByName(country);
	}
	
	public Optional<Countries> findCountryByCode(String code)
	{
		return this.country.findCountryByCode(code);
	}
}
