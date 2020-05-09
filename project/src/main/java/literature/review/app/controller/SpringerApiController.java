package literature.review.app.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Publications;
import literature.review.app.service.PublicationsService;
import literature.review.app.service.SpringerApiService;

@RestController
@RequestMapping("/springer/api")
public class SpringerApiController {

	@Autowired
	private SpringerApiService springer;
	@Autowired
	private PublicationsService publications;
	
	@PostMapping(value="/update_publications", produces = MediaType.APPLICATION_JSON_VALUE)
	public void updateSpringerPublicationsByDOI(
			@RequestBody(required = false) Map<String, String> values) 
	{
		try {
			int limite = values.containsKey("limit") ? Integer.parseInt(values.get("limit")):0;
			String document = values.get("document");
			String state = values.get("state");
			boolean considerAuthors =  values.containsKey("authors") ? Boolean.parseBoolean(values.get("authors")) : false;
			List<Publications> pub = null;
			
			if(considerAuthors) 
				pub = this.publications.findNullAuthorsPublications(document, "1.inserted", limite);
			else
				pub = this.publications.findByTypeState(document, state, limite);
			
			this.springer.updatePublicationsSpringer(pub);
			
		} catch (Exception e) {
			System.err.println("Error de actualizacion Springer : "+e.getMessage());
		}
	}
	
	@GetMapping(value = "/test")
	public void test() throws JSONException, Exception
	{
		this.springer.test("10.1007/s00034-015-0165-7");
		
		
		
	}
	
}
