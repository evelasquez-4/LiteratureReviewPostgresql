package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.ConferenceEditorials;
import literature.review.app.repository.ConferenceEditorialsRepository;

@Service
public class ConferenceEditorialsService {

	@Autowired
	private ConferenceEditorialsRepository conference_editorial;
	
	public ConferenceEditorials save(ConferenceEditorials conferenceEditorial)
	{
		return conference_editorial.save(conferenceEditorial);
	}
	
}
