package literature.review.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.service.ResearchGateService;

@RestController
@RequestMapping("/researchgate")
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ResearchGateController {

	@Autowired
	private ResearchGateService researchgate;
	
	/*
	 * http://localhost:8081/researchgate/register_depatments
	 * curl --location --request POST 'http://localhost:8081/researchgate/register_depatments' --header 'Content-Type: application/json' --data-raw '{"country_code":"AR","limit":3}'
	 */
	@PostMapping(value = "/register_depatments",  produces = MediaType.APPLICATION_JSON_VALUE)
	public void registerInstitutionDepartmentsReseachGate(
			@RequestBody(required = true) Map<String, String> values){
		String countryCode = values.containsKey("country_code")?values.get("country_code"):"DEF";
		Integer limit = values.containsKey("limit") ? Integer.parseInt( values.get("limit") ):0;
		
		researchgate.registerInstitutionDepartmentsReseachGate(countryCode, limit);
	}
	
	/*
	 * http://localhost:8081/researchgate/profile_contributions
	 * curl --location --request POST 'http://localhost:8081/researchgate/profile_contributions' --header 'Content-Type: application/json' --data-raw '{"country_code":"AR","limit":2}'
	 */
	@PostMapping(value = "/profile_contributions",  produces = MediaType.APPLICATION_JSON_VALUE)
	public void registerProfileContributionsResearchGate(
			@RequestBody(required = true) Map<String, String> values) {
		String countryCode = values.containsKey("country_code")?values.get("country_code"):"DEF";
		Integer limit = values.containsKey("limit") ? Integer.parseInt( values.get("limit") ):0;
		
		this.researchgate.registerDepartmentMembersResearchGate(countryCode, limit);
	}
	
	@PostMapping(value = "/test")
	public void test(
			@RequestBody(required = true) Map<String, String> values
			) throws InterruptedException
	{
		
		this.researchgate.testGoogleSearch(values.get("url"));
	}
}
