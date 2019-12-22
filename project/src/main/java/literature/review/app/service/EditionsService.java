package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Editions;
import literature.review.app.repository.EditionsRepository;

@Service
public class EditionsService {
	
	@Autowired
	private EditionsRepository edition;
	
	public Optional<Editions> findById(Long id)
	{
		return edition.findById(id);
	}
	
	public Editions save(Editions e)
	{
		return edition.save(e);
	}
	
	public Optional<Editions> findByDescription(String description)
	{
		return edition.findByDescription(description.toLowerCase());
	}
}
