package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Authors;
import literature.review.app.repository.AuthorsRespository;

@Service
public class AuthorsService {

	@Autowired
	private AuthorsRespository author;
	
	
	public Optional<Authors> findById(Long id)
	{
		return author.findById(id);
	}
	
	public Authors getDefaultAuthor()
	{
		return author.findById((long) 0).get();
	}
	
	public Authors save(Authors auth)
	{
		return author.save(auth);
	}
	
	public Optional<Authors> getAuthorByNames(String names)
	{
		return author.findByNames(names.toLowerCase());
	}

	
}
