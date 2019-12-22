package literature.review.app.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

}
