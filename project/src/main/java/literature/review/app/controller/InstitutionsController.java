package literature.review.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Institutions;
import literature.review.app.service.InstitutionsService;

@RestController
@RequestMapping("/institutions")
public class InstitutionsController {

	@Autowired
	private InstitutionsService institution;
	
	@PostMapping(value = "/load_institutions", produces = MediaType.APPLICATION_JSON_VALUE)
	public void loadInstitutionsFromWorldUniversitiesJSON() throws Exception
	{
		this.institution.loadInstitutionsFromWorldUniversitiesJSON();
	}
	
	/*
	 * curl --location --request GET 'http://localhost:8081/institutions/list_bycountry_code' --header 'Content-Type: application/json' --data-raw '{"country_code":"AR","updated":true,"limit" :10}'
	 */
	@GetMapping(value = "/list_bycountry_code")
	public List<Institutions> listResearchGateInstitutionsByCountryCode(@RequestBody(required=true) Map<String,String> values)
	{
		int limit = values.containsKey("limit")? Integer.parseInt(values.get("limit")): 0;
		String code = values.containsKey("country_code")? values.get("country_code"):"DEF";
		boolean updated = Boolean.parseBoolean( values.get("updated") );
		
		return this.institution.findResearchGateInstitutionsByCountryCode(code, limit, updated);
	}
}
 