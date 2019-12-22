package literature.review.app.staxparser;

//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Date;
import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//
//import javax.transaction.Transactional;
//import javax.transaction.TransactionalException;
//
import org.springframework.stereotype.Component;
//
//import app.literature.review.model.Author;
//import app.literature.review.model.AuthorPublications;
//import app.literature.review.model.Book;
//import app.literature.review.model.BookChapter;
//import app.literature.review.model.Conference;
//import app.literature.review.model.ConferenceEditorial;
//import app.literature.review.model.ConferencePaper;
//import app.literature.review.model.Department;
//import app.literature.review.model.Edition;
//import app.literature.review.model.Journal;
//import app.literature.review.model.JournalPaper;
//import app.literature.review.model.Publication;
//import app.literature.review.model.Publisher;
//import app.literature.review.model.VolumeNumber;
//import app.literature.review.service.AuthorPublicationsService;
//import app.literature.review.service.AuthorService;
//import app.literature.review.service.BookChapterService;
//import app.literature.review.service.BookService;
//import app.literature.review.service.ConferenceEditorialService;
//import app.literature.review.service.ConferencePaperService;
//import app.literature.review.service.ConferenceService;
//import app.literature.review.service.DepartmentService;
//import app.literature.review.service.EditionService;
//import app.literature.review.service.JournalEditorialService;
//import app.literature.review.service.JournalPaperService;
//import app.literature.review.service.JournalService;
//import app.literature.review.service.PublicationService;
//import app.literature.review.service.PublisherService;
//import app.literature.review.service.VolumeNumberService;
//
@Component
public class DblpPersistence{ 
//
//	private HashMap<String, Object> services;
//	
//	private AuthorService author_service;
//	private DepartmentService depart_service; 
//	private PublicationService publication_service;
//	private AuthorPublicationsService author_publication;
//	
//	private BookChapterService bookChapter_service;
//	private BookService book_service;
//	private PublisherService publisher_service;
//	
//	private EditionService edition_service;
//	private ConferencePaperService conference_paper_service;
//	private ConferenceEditorialService conference_editorial_service;
//	private ConferenceService conference_service;
//	private JournalPaperService journal_paper_service;
//	private JournalEditorialService journal_editorial_service;
//	private VolumeNumberService volume_number_service;
//	private JournalService journal_service;
//	
//	
//	private Author defaultAuthor ; 
//	private Department defaultDepartment;
//	
//	
//	public DblpPersistence() {}
//	
	public DblpPersistence(HashMap<String, Object> services)
	{
//		this.services = services;
//		
//		this.author_service = (AuthorService) this.services.get("author");
//		this.publication_service = (PublicationService) this.services.get("publication");
//		this.depart_service = (DepartmentService) this.services.get("department");
//		this.author_publication = (AuthorPublicationsService) this.services.get("author_publication");
//		this.bookChapter_service = (BookChapterService) this.services.get("book_chapter");
//		this.book_service = (BookService) this.services.get("book");
//		this.publisher_service = (PublisherService) this.services.get("publisher");
//		
//		this.edition_service = (EditionService) this.services.get("edition");
//		this.conference_paper_service = (ConferencePaperService) this.services.get("conference_paper");
//		this.conference_editorial_service = (ConferenceEditorialService) this.services.get("conference_editorial");
//		this.conference_service = (ConferenceService) this.services.get("conference");
//		this.journal_paper_service = (JournalPaperService) this.services.get("journal_paper");
//		this.journal_editorial_service = (JournalEditorialService) this.services.get("journal_editorial");
//		this.volume_number_service = (VolumeNumberService) this.services.get("volume_number");
//		this.journal_service = (JournalService) this.services.get("journal");
//		
	}
//	
//	
//	public HashMap<String, Object> getServices() {
//		return services;
//	}
//
//	public void setServices(HashMap<String, Object> services) {
//		this.services = services;
//	}
//
//	public Publication verificaPublicacion(DblpDocument dblp) throws Exception
//	{
//		Publication publication = null;
//		Optional<Publication> optional = publication_service.findPublicationByKey(dblp.getKey_dblp());
//		
//		if(optional.isPresent())
//			publication = optional.get();
//		else
//			publication = publication_service.save(publicationConstructor(dblp));
//		
//		return publication;
//	}
//	
//	public Author verificaAuthor(String names)
//	{
//		Author autor = null;
//		Optional<Author> optional = author_service.getAuthorByNames(names);
//		
//		if(optional.isPresent())
//			autor = optional.get();
//		else
//		{
//			autor = author_service.save(new Author(
//						0,
//						this.defaultDepartment,//depart_service.getDefaultDepartment(),
//						names,
//						new Date(),
//						"DEFAULT HOME PAGE",
//						null
//					));
//		}
//		
//		return autor;
//	}
//	//funcion que hace la primera insercion de publicaciones y autores
//	@Transactional(rollbackOn = Exception.class )
//	public void firstTimeInsertPublicationAuthor(List<DblpDocument> dblpList) throws Exception
//	{
//		
//		this.defaultAuthor = author_service.getDefaultAuthor();
//		this.defaultDepartment = depart_service.getDefaultDepartment();
//		
//		Author author = new Author();
//		
//		for (DblpDocument dblp : dblpList) 
//		{
//			//insert publicacion
//			Publication publicacion = publication_service.save(publicationConstructor(dblp));
//			
//			List<String> authors = dblp.getAuthors().size() > 0 ? dblp.getAuthors(): new ArrayList<>();
//			
//			if(authors.size() == 0) {
//				author_publication.save(
//						new AuthorPublications(0, 
//								this.defaultAuthor, 
//								publicacion, 
//								null, 
//								new Date()) );
//			}else {
//				for (int i = 0; i < authors.size(); i++) {
//					author = verificaAuthor(authors.get(i));
//					author_publication.save(new AuthorPublications(0,
//							author, 
//							publicacion,
//							i + 1,
//							new Date()) );
//				}
//			}
//		}
//		
//	}
//	
//	
//	@Transactional(rollbackOn = Exception.class )
//	public void insertAuthorPublications(DblpDocument dblp) throws Exception
//	{
//		try 
//		{
//			//Optional<Publication> opt_publication = publication_service.findPublicationByKey(dblp.getKey_dblp());
//			Publication publicacion = verificaPublicacion(dblp);//opt_publication.isPresent()?opt_publication.get():null;
//
//			List<String> authors = dblp.getAuthors().size() > 0 ? dblp.getAuthors(): new ArrayList<>();
//			
//			if(authors.size() > 0)
//			{
//				for(int i=0;i< authors.size();i++) 
//				{
//					Author author = verificaAuthor(authors.get(i));
//					
//					author_publication.save(new AuthorPublications(
//								0,
//								author,
//								publicacion,
//								i+1,
//								new Date()
//							));
////					Optional<Author> option = author_service.getAuthorByNames( authors.get(i) );
////					//Author author_aux = opt_author.isPresent()?opt_author.get():null;
////					
////					if(option.isPresent())
////					{
////						author_publication.save(
////								new AuthorPublications(0, 
////										option.get(),
////										publicacion,
////										i+1,
////										new Date())
////								);
////					}else 
////					{
////						Author author_aux = author_service.save(
////											new Author(0,
////											depart_service.getDefaultDepartment(),
////											authors.get(i),
////											new Date(),
////											"HOME PAGE",
////											null ) );
////												
////						author_publication.save(
////								new AuthorPublications(0, 
////										author_aux,
////										publicacion,
////										i+1,
////										new Date())
////								);
////					}	
//				}
//			}
//			else {
//				author_publication.save(
//							new AuthorPublications(0, 
//									author_service.getDefaultAuthor(), 
//									publicacion, 
//									null, 
//									new Date())
//						);
//			}
//			
//			//begin insert rest documents
//			/*
//		switch (dblp.getDoc_type())
//			{
//			
//				case "incollection":
//				{
//					bookChapter_service.save( getBookChapter(publicacion.getId(), dblp) ); 
//					break;
//				}
//				case "book":
//				{
//					Publisher publisher_res = verificaPublisher(dblp.getPublisher());
//					book_service.save( getBook(publisher_res,publicacion.getId(),dblp) );
//					break;
//				}
//				case "inproceedings":
//				{
//					Edition edition = verificaEdition(dblp);
//					conference_paper_service.save(getConferencePaper(publicacion.getId(), edition, dblp));
//					break;
//				}
//				case "proceedings":
//				{
//					Edition edition = verificaEdition(dblp);
//					conference_editorial_service.save(getConferenceEditorial(publicacion.getId(), edition, dblp));
//					break;
//				}
//				case "article":
//				{
//					VolumeNumber volumeNumber = verificaVolumeNumber(dblp);
//					journal_paper_service.save(getJournalPaper(publicacion.getId(), volumeNumber, dblp));
//					break;
//				}
//			}
//			*/
//		} 
//		catch (TransactionalException e) {
//			System.out.println("Error al guardar :\n"+dblp.toString());
//			System.err.println(e.getMessage());
//			
//		}
//	}
//	public JournalPaper getJournalPaper(long publication_id,VolumeNumber volumeNumber,DblpDocument dblp) throws Exception
//	{
//		return new JournalPaper(
//				0,
//				volumeNumber,
//				publication_id,
//				dblp.getTitle(),
//				dblp.getDoc_type(),
//				"",//abstract
//				dblp.getEe(),
//				dblp.getYear(),
//				new Date(),
//				dblp.getKey_dblp(),
//				dblp.getUrl(),
//				dblp.getEe(),
//				dblp.getMonth(),
//				dblp.getNote(),
//				dblp.getSeries(),
//				dblp.getVolume(),
//				dblp.getCrossref(),
//				getFormatDate(dblp.getMdate()),
//				"1.inserted"
//				);
//	}
//	
//	public ConferenceEditorial getConferenceEditorial(long publication_id,Edition edition,DblpDocument dblp) throws Exception
//	{
//		return new ConferenceEditorial(
//					0,
//					edition,
//					publication_id,
//					dblp.getBooktitle().isEmpty()?dblp.getTitle():dblp.getBooktitle(),
//					dblp.getDoc_type(),
//					"",//abstract
//					dblp.getEe(),//DOI
//					dblp.getYear(),
//					new Date(),
//					dblp.getKey_dblp(),
//					dblp.getUrl(),
//					dblp.getEe(),
//					dblp.getMonth(),
//					dblp.getNote(),
//					dblp.getSeries(),
//					dblp.getVolume(),
//					dblp.getCrossref(),
//					getFormatDate( dblp.getMdate() ),
//					"1.inserted"
//				);
//	}
//	
//	public ConferencePaper getConferencePaper(long publication_id,Edition edition,DblpDocument dblp) throws Exception
//	{
//		return new ConferencePaper(
//					0,
//					edition,
//					publication_id,
//					dblp.getTitle()+"_"+dblp.getBooktitle(),
//					dblp.getDoc_type(),
//					"",//abstract
//					dblp.getEe(),//DOI
//					dblp.getYear(),
//					new Date(),
//					dblp.getKey_dblp(),
//					dblp.getUrl(),
//					dblp.getEe(),
//					dblp.getMonth(),
//					dblp.getNote(),
//					dblp.getSeries(),
//					dblp.getVolume(),
//					dblp.getCrossref(),
//					getFormatDate( dblp.getMdate() ),
//					"1.inserted"
//				);
//	}
//	
//	public Book getBook(Publisher publisher,long publication_id,DblpDocument dblp) throws Exception
//	{ 
//		return new Book(0,
//				publisher,
//				publication_id,
//				dblp.getTitle(),
//				dblp.getDoc_type(),
//				"",//abstract 
//				dblp.getEe(),//DOI
//				dblp.getYear(),
//				new Date(),
//				dblp.getKey_dblp(),
//				dblp.getIsbn(),
//				dblp.getUrl(), 
//				dblp.getEe(),
//				dblp.getMonth(),
//				dblp.getNote(),
//				dblp.getSeries(),
//				dblp.getVolume(),
//				dblp.getCrossref(),
//				getFormatDate( dblp.getMdate() ),
//				"1.inserted"
//				);
//	}
//	
//	public Publisher verificaPublisher(String desc)
//	{
//		Publisher publisher = publisher_service.findById((long) 0).get();
//		
//		if(!desc.isEmpty())
//		{
//			Optional<Publisher> publisher_optional = publisher_service.getPublisherByDescription(desc);
//			if(publisher_optional.isPresent())
//				publisher =  publisher_optional.get();
//			else 
//				publisher = publisher_service.save(new Publisher( 0,desc,"activo",new Date(),null,null,null,null));
//			
//		}
//		return publisher;
//	}
//	
//	public BookChapter getBookChapter(long publication_id, DblpDocument dblp) throws Exception
//	{
//		return new BookChapter(0, 
//				null,//Publisher
//				(long)publication_id,//id
//				dblp.getTitle(),
//				dblp.getDoc_type(),
//				"",//abstract 
//				dblp.getEe(),//DOI
//				dblp.getYear(),
//				new Date(),
//				dblp.getKey_dblp(),
//				dblp.getPages(),//pages
//				dblp.getUrl(), 
//				dblp.getEe(),
//				dblp.getIsbn(),
//				dblp.getMonth(),
//				dblp.getNote(),
//				dblp.getSeries(),
//				dblp.getVolume(),
//				dblp.getCrossref(),
//				getFormatDate(dblp.getMdate()),//mdate
//				"1.inserted"
//				);
//	}
//	
//	public Publication publicationConstructor(DblpDocument dblp) throws Exception
//	{
//		return new Publication(
//						0, 
//						dblp.getTitle(), 
//						dblp.getDoc_type(), 
//						"",//abstract 
//						dblp.getEe(),//DOI
//						dblp.getYear(),
//						new Date(),
//						dblp.getKey_dblp(),
//						dblp.getUrl(), 
//						dblp.getEe(),
//						dblp.getMonth(),
//						dblp.getNote(),
//						dblp.getSeries(),
//						dblp.getVolume(), 
//						dblp.getCrossref(),
//						getFormatDate(dblp.getMdate()) ,//modified date
//						"1.inserted",//update state
//						null,
//						null 
//				);	
//	}
//	
//	public Date getFormatDate(String fecha) throws Exception
//	{
//		LocalDate local = LocalDate.parse(fecha);
//		return  java.sql.Date.valueOf(local);
//	}
//	
//	public Edition verificaEdition(DblpDocument dblp)
//	{
//		Edition edition = edition_service.findById((long) 0).get();
//		
//		//edition para proceeding
//		if(dblp.getDoc_type() == "proceedings")
//		{
//			if(!dblp.getSeries().isEmpty())
//			{
//				Optional<Edition> optional = edition_service.findByDescription(dblp.getSeries());
//				
//				if(optional.isPresent())
//					edition = optional.get();
//				else
//				{
//					Publisher publisher = verificaPublisher(dblp.getPublisher());
//					Conference  conference = verificaConference(
//							dblp.getBooktitle().isEmpty()?dblp.getTitle():dblp.getBooktitle() );
//					
//					edition = edition_service.save( new Edition(0,conference,publisher,dblp.getSeries(),null,null) );
//				}		
//			}
//		}
//		
//		return edition;
//	}
//	
//	public Conference verificaConference(String nombre_conferencia)
//	{
//		Conference conference = conference_service.findById((long)0).get();
//		
//		if(!nombre_conferencia.isEmpty())
//		{
//			Optional<Conference> optional = conference_service.findByDescription(nombre_conferencia);
//			
//			if(optional.isPresent())
//				conference = optional.get();
//			else
//				conference = conference_service.save( new Conference(0,nombre_conferencia,null));
//		}
//		return conference;
//	}
//
//	public Journal verificaJournal(String journal_name)
//	{
//		Journal journal = journal_service.findById((long)0).get();
//		
//		if(!journal_name.isEmpty())
//		{
//			Optional<Journal> option = journal_service.findByDescription(journal_name);
//			
//			if(option.isPresent())
//				journal = option.get();
//			else
//				journal = journal_service.save(new Journal(0,journal_name,null) );
//		}
//		
//		return journal;
//	}
//
//	public VolumeNumber verificaVolumeNumber(DblpDocument dblp)
//	{
//		VolumeNumber volumeNumber = volume_number_service.findById((long) 0).get();
//
//		if(!dblp.getVolume().isEmpty() && !dblp.getNumber().isEmpty())
//		{
//			Optional<VolumeNumber> option = volume_number_service.findByVolumeNumber(
//					Integer.valueOf(dblp.getVolume()),
//					Integer.valueOf(dblp.getNumber())  );
//			
//			if(option.isPresent())
//				volumeNumber = option.get();
//			else
//			{
//				Publisher publisher = verificaPublisher(dblp.getPublisher());
//				
//				Journal journal = verificaJournal(dblp.getJournal());
//	
//				volumeNumber = volume_number_service.save(new VolumeNumber(
//						0,
//						journal,
//						publisher,
//						Integer.valueOf(dblp.getVolume()),
//						Integer.valueOf(dblp.getNumber()),
//						null,null
//						) );
//			}
//		
//		}
//		return volumeNumber;
//	}
}
