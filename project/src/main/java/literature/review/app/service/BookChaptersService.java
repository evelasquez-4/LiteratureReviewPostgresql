package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.BookChapters;
import literature.review.app.repository.BookChaptersRepository;

@Service
public class BookChaptersService {

	@Autowired
	private BookChaptersRepository book_chapter;
	
	public BookChapters save(BookChapters chapter)
	{
		return book_chapter.save(chapter);
	}
}
