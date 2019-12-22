package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.AuthorPublications;
import literature.review.app.repository.AuthorPublicationsRepository;

@Service
public class AuthorPublicationsService {

	@Autowired
	private AuthorPublicationsRepository author_publications;
	
	public AuthorPublications save(AuthorPublications data)
	{
		return author_publications.save(data);
	}
	
}
