package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Conferences;
import literature.review.app.repository.ConferencesRepository;


@Service
public class ConferencesService {
	
	@Autowired
	private ConferencesRepository conference;
	
	public Conferences save(Conferences conference)
	{
		return this.conference.save(conference);
	}
	
	public Optional<Conferences> findById(Long id)
	{
		return this.conference.findById(id);
	}
	
	public Optional<Conferences> findByDescription(String description)
	{
		return this.conference.findByDescription(description.toLowerCase());
	}

}
