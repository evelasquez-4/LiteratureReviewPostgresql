package literature.review.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Publications;
import literature.review.app.repository.PublicationsRepository;

@Service
public class PublicationsService {

	@Autowired
	private PublicationsRepository publication;
	
	public List<Publications> saveAll(List<Publications> publications) {
		return publication.saveAll(publications);
	}
	
	public Publications save(Publications p)
	{
		return publication.save(p);
	}
	
	public Optional<Publications> findPublicationById(int id)
	{
		return publication.findById((long) id);
	}
	
	public Optional<Publications> findPublicationByKey(String key)
	{
		return publication.findPublicationByKey(key);
	}
	
	public List<Publications> findPublicationsByType(String doc_type, int limit)
	{
		return this.publication.listPublicationByType(doc_type, limit);
	}
	
	public List<Publications> findByTypeState(String type, String state, int limit)
	{
		return this.publication.findByTypeState(state, type, limit);
	}
	
	public List<Publications> findNullAuthorsPublications(String type, String state, int limit)
	{
		return this.publication.findNullAuthorsPublications(state, type, limit);
		
	}
}
