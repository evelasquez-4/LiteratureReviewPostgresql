package literature.review.app.staxparser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import literature.review.app.model.AuthorPublications;
import literature.review.app.model.Authors;
import literature.review.app.model.Departments;
import literature.review.app.model.Publications;
import literature.review.app.service.AuthorPublicationsService;
import literature.review.app.service.AuthorsService;
import literature.review.app.service.DepartmentsService;
import literature.review.app.service.PublicationsService;

@Component
public class DblpETL {

	private HashMap<String, Object> services; 
 
	private AuthorsService author;
	private PublicationsService publication_service;
	private AuthorPublicationsService author_publication;
	private DepartmentsService department;
    
    public DblpETL(HashMap<String, Object> services)
    {
    	this.services = services;
    	this.author = (AuthorsService) this.services.get("author");
    	this.publication_service = (PublicationsService) this.services.get("publication");
    	this.author_publication =  (AuthorPublicationsService) this.services.get("author_publication");
    	this.department = (DepartmentsService) this.services.get("department");
    }
    
    
    /*
     * 
     */
    @Transactional(rollbackOn = Exception.class)
	public void firstTimeInsert(List<DblpDocument> dblpList) throws Exception
	{	
		Authors defaultAuthor = this.author.getDefaultAuthor();
		Departments defaultDepto = this.department.getDefaultDepartment();

		Authors auth = new Authors();
		for (DblpDocument dblp : dblpList) {

			Publications pub = this.publication_service.save(publicationConstructor(dblp));

			List<String> authors = dblp.getAuthors().size() > 0 ? dblp.getAuthors() : new ArrayList<>();

			if (authors.size() > 0) {
				for (int i = 0; i < authors.size(); i++) {
					auth = verificaAuthor(authors.get(i), defaultDepto);

					this.author_publication.save(new AuthorPublications(0, auth, pub, i + 1, new Date()));
				}

			} else {
				this.author_publication.save(new AuthorPublications(0, defaultAuthor, pub, null, new Date()));
			}
		}	
		
	}
	
	public Authors verificaAuthor(String names, Departments depto)
	{
		Authors autor = new Authors();
		Optional<Authors> option = this.author.getAuthorByNames(names);
		
		if (option.isPresent())
			autor = option.get();
		else {
			autor =	this.author.save(new Authors(0,
											depto,
											names,
											null,
											null,
											"DEFAULT HOME PAGE",
											new Date(),
											null));
		}
		return autor;
	}
		
	public Publications publicationConstructor(DblpDocument dblp) throws Exception
	{
		return new Publications(
						0, 
						null,//" ",//abstract 
						dblp.getTitle(),
						dblp.getPages(),
						dblp.getYear(),
						dblp.getAddress(),
						dblp.getJournal(),
						dblp.getVolume(), 
						dblp.getNumber(),
						dblp.getMonth(),
						dblp.getUrl(), 
						dblp.getEe(),//DOI
						dblp.getCite(),
						dblp.getPublisher(),
						dblp.getNote(),
						dblp.getCrossref(),
						dblp.getIsbn(),
						dblp.getSeries(),
						dblp.getChapter(),
						dblp.getPblnr(),
						"1.inserted",//update state
						getFormatDate(dblp.getMdate()) ,//modified date
						dblp.getKey_dblp(),
						dblp.getDoc_type(), 
						null,
						null 
				);	
	}
	
	public Date getFormatDate(String fecha) throws Exception
	{
		LocalDate local = LocalDate.parse(fecha);
		return  java.sql.Date.valueOf(local);
	}
	
	
		
}
