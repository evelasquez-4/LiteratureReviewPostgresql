package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Journals;
import literature.review.app.repository.JournalsRepository;

@Service
public class JournalsService {

	@Autowired
	private JournalsRepository journal;
	
	public Journals save(Journals journal)
	{
		return this.journal.save(journal);
	}
	
	public Optional<Journals> findById(Long id)
	{
		return this.journal.findById(id);
	}
	
	public Optional<Journals> findByDescription(String description)
	{
		return this.journal.findByDescription(description.toLowerCase());
	}
	
}
