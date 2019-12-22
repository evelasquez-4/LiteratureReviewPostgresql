package literature.review.app.service;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaxParseService {
	

	
	@Autowired
	AuthorsService author;
	@Autowired
	PublicationsService publication;
	@Autowired
	DepartmentsService department;
	@Autowired
	AuthorPublicationsService author_publication;
	@Autowired
	BooksService book;
	@Autowired
	BookChaptersService book_chapter;
	@Autowired
	PublishersService publisher;
	@Autowired
	EditionsService edition;
	@Autowired
	ConferencePapersService conference_paper;
	@Autowired
	ConferenceEditorialsService conference_editorial;
	@Autowired
	ConferencesService conference;
	@Autowired
	JournalPapersService journal_paper;
	@Autowired
	JournalEditorialsService journal_editorial;
	@Autowired
	VolumeNumbersService volume_number;
	@Autowired
	JournalsService journal;
	
	
	public boolean readDblpFile(String document,int limit) throws XMLStreamException, IOException
	{
		//Parser parser = new Parser(getservices());
		//boolean res = parser.readXml();

		return true;
	}
	
	public HashMap<String, Object> getservices()
	{
		HashMap<String, Object> services = new HashMap<>();
		services.put("author", this.author);
		services.put("publication", this.publication);
		services.put("department", this.department);
		services.put("author_publication", this.author_publication);
		services.put("book_chapter", this.book_chapter);
		services.put("book", this.book);
		services.put("publisher",this.publisher);
		services.put("edition", this.edition);
		services.put("conference_paper",this.conference_paper);
		services.put("conference_editorial",this.conference_editorial);
		services.put("conference",this.conference);
		services.put("journal_paper", this.journal_paper);
		services.put("journal_editorial", this.journal_editorial);
		services.put("volume_number", this.volume_number);
		services.put("journal", this.journal);
		
		return services;
	}
	
}
