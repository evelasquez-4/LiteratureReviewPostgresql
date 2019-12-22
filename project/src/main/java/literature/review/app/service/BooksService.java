package literature.review.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Books;
import literature.review.app.repository.BooksRepository;

@Service
public class BooksService {
	
	@Autowired
	private BooksRepository book;
	
	public Books save(Books b)
	{
		return book.save(b);
	}
}
