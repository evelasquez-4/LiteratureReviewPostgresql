package literature.review.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.model.Authors;
import literature.review.app.model.Publications;
import literature.review.app.service.PublicationsService;

@RestController
@RequestMapping("/publication")
public class PublicationsController {

	@Autowired
	private PublicationsService publications;
	
	@GetMapping(value="/findByType")
	public List<Publications> listByType(@RequestParam String type,
			@RequestParam String limit)
	{
		int limite = limit.length() == 0 ? 10:Integer.parseInt( limit );
		
		return this.publications.findPublicationsByType(type, limite);
	}
	
	@GetMapping(value="/findByKey") 
	public Optional<Publications> findByKey(@RequestParam String key)
	{
		return this.publications.findPublicationByKey(key);
	}
	
	public List<Authors> findAuthorsbyPublications(Publications pub)
	{
		List<Authors> res  = new ArrayList<Authors>();
		
		
		
		return res;
	}
}
