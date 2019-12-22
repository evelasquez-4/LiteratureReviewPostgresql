package literature.review.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Publishers;
import literature.review.app.repository.PublishersRepository;

@Service
public class PublishersService
{
	@Autowired
	private PublishersRepository publisherRepository;

	public List<Publishers> findAll() {
		// TODO Auto-generated method stub
		return publisherRepository.findAll();
	}
	
	
	public Optional<Publishers> findById(Long id) {
		return publisherRepository.findById(id);
	}
	
	public Publishers save(Publishers publisher) {
		// TODO Auto-generated method stub
		return publisherRepository.save(publisher);
	}
	
	public Optional<Publishers> getPublisherByDescription(String description)
	{
		return publisherRepository.getPublisherByDescription(description.toLowerCase());
	}

	
}
