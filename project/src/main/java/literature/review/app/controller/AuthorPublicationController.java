package literature.review.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.service.AuthorPublicationsService;

@RestController
@RequestMapping("/author_publication")
public class AuthorPublicationController {
	
	@Autowired
	private AuthorPublicationsService authorPublicationsService;
	/*
	 * @params
	 * {
	 * 	"document":"[article|book|..]"
	 * 	"limit" : "[docs number]"
	 * }
	 * curl --location --request POST 'http://localhost:8081/author_publication/insert' --header 'Content-Type: application/json' --data-raw '{	"document":"article","limit":"1"}'
	 */
	@PostMapping(value="/insert", produces = MediaType.APPLICATION_JSON_VALUE)	
	public boolean processAuthors(@RequestBody(required = false) Map<String, String> values)
	{
		int limite = values.containsKey("limit") ? Integer.parseInt( values.get("limit") ) : 0  ;
		
		String document = values.get("document");
		List<String> doc_types = Arrays.asList("article","inproceedings","proceedings","book","incollection");
		
		if(doc_types.contains(document))
			return this.authorPublicationsService.authorPublicationsInsert(document, limite);
		else
		{
			System.out.println("error, tipo de documento invalido.");
				return false;
		}
	}

}
