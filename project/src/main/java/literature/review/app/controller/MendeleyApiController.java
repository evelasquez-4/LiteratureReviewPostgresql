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
import literature.review.app.service.MendeleyApiService;
import literature.review.app.service.PublicationsService;


@RestController
@RequestMapping("/mendeley/api")
public class MendeleyApiController {

	@Autowired
	private MendeleyApiService mendeley;
	@Autowired
	private PublicationsService publications;
	
	/*
	 * actualizacion desde la api de mendeley
	 * @params : 	tipos de publicacion ["article","inproceedings","proceedings","book","incollection"]
	 * 				publicaciones con estado = 2.authors_processed
	 * example:
	 * curl --location --request POST 'http://localhost:8081/mendeley/api/update_publications' --header 'Content-Type: application/json' --data-raw '{"document":"article","limit":"10","mendeley_key":"mendeley key"}'
	 */
	@PostMapping(value = "/update_publications", produces = MediaType.APPLICATION_JSON_VALUE)
	public void updatePublications(
			@RequestBody(required = false) Map<String, String> values) throws Exception
	{
		try
		{
			int limite = values.containsKey("limit") ? Integer.parseInt( values.get("limit") ) : 0  ;
			String document = values.get("document");
			String mendeleyKey = values.get("mendeley_key");
			
			List<Publications> listaPublicaciones = this.publications
					.findByTypeState(document, "1.inserted", limite);
			 
			mendeley.updatePublications(listaPublicaciones, mendeleyKey);
		}catch (Exception e) {
			System.err.println("Error de actualizacion Mendeley : "+e.getMessage());
		}
		
	}
	@GetMapping(value = "/test")
	public void test() throws JSONException, Exception
	{
//		Publications pub = this.publications.findPublicationById(29).get();
		
//		this.mendeley.test(pub , 
//				"MSwxNTg1ODc1OTU4NjExLDU0NzQ0MzY1MSwxMDI4LGFsbCwsLDA3MzIyYTJhMjMxNmI1NDg0MTQ5ZDI0MGNlNmIzM2QxZWMyMWd4cnFiLGEzMjM2ZDIwLTMyODktM2NlNC04NmZlLTEyNzJjZjljZmVhNixUT0FzQzJoRERHa3hsWS1xbFNScklnN1hiS28");
	}
	
}
