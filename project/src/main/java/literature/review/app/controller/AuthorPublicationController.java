package literature.review.app.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.service.AuthorPublicationsService;

@RestController
@RequestMapping("/author_publication")
public class AuthorPublicationController {
	
	@Autowired
	private AuthorPublicationsService authorPublicationsService;
	
	@RequestMapping(value="/insert", method = RequestMethod.POST)
	public boolean processAuthors(@RequestParam String document,
								@RequestParam String limit)
	{
		int limite = limit.length() == 0 ? -1:Integer.parseInt( limit );
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
