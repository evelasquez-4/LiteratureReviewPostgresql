package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.JournalPapers;
import literature.review.app.repository.JournalPapersRepository;



@Service
public class JournalPapersService {

	@Autowired
	private JournalPapersRepository journal_paper;
	
	public JournalPapers save(JournalPapers journalPaper)
	{
		return this.journal_paper.save(journalPaper);
	}
	
}