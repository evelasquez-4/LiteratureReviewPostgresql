package literature.review.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Authors;
import literature.review.app.service.AuthorsService;

@RestController
@RequestMapping("/author")
public class AuthorsController {
	
	@Autowired
	private AuthorsService authorsService;
	
	@GetMapping(value = "/search/{id}")
	public Optional<Authors> findById(@PathVariable Long id)
	{
		return authorsService.findById(id);
	}
	
	@GetMapping(value = "/default")
	public Authors getDefaultAuthor()
	{
		return authorsService.getDefaultAuthor();
	}
	
	@GetMapping(value = "/search") 
	public Optional<Authors> findbyNames(@RequestParam String names)
	{
		return authorsService.getAuthorByNames(names);
	}
	
	/*
	* @params
	* {
	* 	"document":"[article|book|..]"
	* 	"limit" : "[docs number]"
	* }
	* curl --location --request POST 'http://localhost:8081/author/process_authors' --header 'Content-Type: application/json' --data-raw '{"document":"article","limit":"2"}'
	*/
	@PostMapping(value="/process_authors", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean processAuthors(@RequestBody(required = false) Map<String, String> values)
	{
		int limite = values.containsKey("limit") ? Integer.parseInt( values.get("limit") ) : 0  ;
		
		String document = values.get("document");
		List<String> doc_types = Arrays.asList("article","inproceedings","proceedings","book","incollection");
		
		if(doc_types.contains(document))
			return this.authorsService.processAuthors(document, limite);
		else
		{
			System.out.println("error, tipo de documento invalido.");
				return false;
		}
	}
}
