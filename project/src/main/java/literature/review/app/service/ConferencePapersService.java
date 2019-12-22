package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import literature.review.app.model.ConferencePapers;
import literature.review.app.repository.ConferencePapersRepository;

@Service
public class ConferencePapersService {
	@Autowired
	private ConferencePapersRepository conference_paper;
	
	public ConferencePapers save(ConferencePapers conferencePaper)
	{
		return conference_paper.save(conferencePaper);
	}
}
