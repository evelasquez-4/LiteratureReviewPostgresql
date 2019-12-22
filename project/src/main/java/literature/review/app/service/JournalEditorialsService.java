package literature.review.app.service;

import org.springframework.stereotype.Service;

import literature.review.app.model.JournalEditorials;
import literature.review.app.repository.JournalEditorialsRepository;


@Service
public class JournalEditorialsService {

	private JournalEditorialsRepository journal_editorial;
	
	public JournalEditorials save(JournalEditorials journalEditorial)
	{
		return this.journal_editorial.save(journalEditorial);
	}
}
