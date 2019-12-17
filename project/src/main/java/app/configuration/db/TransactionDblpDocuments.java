package app.configuration.db;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import app.configuration.db.tables.Author;
import app.configuration.db.tables.AuthorPublications;
import app.configuration.db.tables.Publication;
import app.configuration.staxparser.DblpDocument;

public class TransactionDblpDocuments 
{
	static String DEFAULT = null;
	
	
	public boolean isPublisherExist(String publisheName, Session session)
	{
		return session.createQuery("select 1 from slr.publisher where description = ? ")
			.setParameter(0, publisheName.toLowerCase())
			.uniqueResult() != null;
	}
	
	public boolean insertAuthorPublication(DblpDocument dblp,Session session) 
	{
		boolean res;
		Publication publication = insertPublication(dblp, session);
		List<String> authors = dblp.getAuthors();
		Author author = new Author();
		
		if(dblp.getAuthors().size() > 0)
		{
				
			for (int i = 0; i < authors.size(); i++) {
				author = insertAuthor(authors.get(i), session);
				
				AuthorPublications ap = new AuthorPublications();
				ap.setHerarchy(i+1);
				ap.setCreateAt(obtainDate());
				ap.setAuthor(author);
				ap.setPublication(publication);
			
				session.save(ap);
			}
			res = true;
		}
		else{
			res = false;
		}
		
		return res;
		
	}
	
	public Publication insertPublication(DblpDocument dblp, Session session)
	{
		Publication publication = new Publication(
				dblp.getTitle(),
				dblp.getDoc_type(),
				DEFAULT, 
				getDOI(dblp.getEe()),
				dblp.getYear() > 0 ? dblp.getYear(): 0 ,
				obtainDate(), 
				dblp.getKey_dblp() 
				);
			
		
		session.save(publication);
		session.flush();
		
		return publication;
		
	}
	
	public Author insertAuthor(String name,Session session)
	{
		Author author = new Author(name,new Date(),"home page");
		session.save(author);
		session.flush();
		
		return author;
		
	}
	
	public boolean hasYear(int year)
	{
		return year > 0;
	}
	
	public Timestamp obtainTimeStamp()
	{
		return new Timestamp(System.currentTimeMillis());
	}
	
	public Date obtainDate()
	{
		return new Date();
	}
	
	public String getDOI(String ee) 
	{
		if(ee.isEmpty()) 
			return DEFAULT;
		else {
			String res = "";
			String[] split = ee.split("/");
			if( ee.toLowerCase().contains("doi.org"))
			{
				try {
					res = res.concat("/").concat(split[3]).concat("/"+split[split.length-1]);
				}catch (IndexOutOfBoundsException e) 
				{
					System.out.println(e.getMessage());
					res = DEFAULT;
				}
			}else {
				res = DEFAULT;
			}
			return res;
		}
	}
}
