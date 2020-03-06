package literature.review.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
//	@PostMapping(value = "/saveauthor")
//	public Authors saveAuthor()
//	{
//		
//		return authorsService.save(
//				new Authors(0, 
//						de.getDefaultDepartment(),
//						"nombres", 
//						new Date(),
//						"hoempage", 
//						null) );
//	}

	@RequestMapping(value="/process_authors", method = RequestMethod.POST)
	public boolean processAuthors(@RequestParam String document,
								@RequestParam String limit)
	{
		int limite = limit.length() == 0 ? -1:Integer.parseInt( limit );
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
