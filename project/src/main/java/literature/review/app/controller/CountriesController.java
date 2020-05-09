package literature.review.app.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.service.CountriesService;

@RestController
@RequestMapping("/countries")
public class CountriesController {

	@Autowired
	private CountriesService country;
	
	// Search by region: africa, americas, asia, europe, oceania
	//https://restcountries.eu/#api-endpoints-region
	@PostMapping(value = "/load_countries", produces = MediaType.APPLICATION_JSON_VALUE)
	public void loadCountries(
			@RequestBody(required = true) Map<String, String> values) throws IOException, Exception
	{
		String region = values.containsKey("continent")?values.get("continent"):"";
		this.country.loadCountriesFromAPI(region);
	}
}
