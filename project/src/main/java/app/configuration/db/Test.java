package app.configuration.db;



import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import app.configuration.db.tables.Author;
import app.configuration.db.tables.Publisher;
import app.configuration.staxparser.DblpDocument;

public class Test {

	public static void main(String[] args) 
	{
		DblpDocument dblp =  getDblp();

		try(Session session = HibernateUtil.getSessionFactory().openSession()) 
		{
			session.beginTransaction(); 
			TransactionDblpDocuments transaction = new TransactionDblpDocuments();
			
			
			System.out.println(transaction.insertAuthorPublication(dblp, session));
			
			
	        
			session.getTransaction().commit();
			session.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			HibernateUtil.getSessionFactory().close();
		}
		
	}
	
	public static DblpDocument getDblp()
	{
		DblpDocument res = new DblpDocument();
		ArrayList<String> authors = new ArrayList<String>();
		authors.add("a 1");
		authors.add("a 2");
		authors.add("a 3");
		res.setAuthors(authors);
		
		res.setDoc_type("book");
		res.setTitle("publication 1");
		res.setEe("org/DOI1");
		res.setKey_dblp("book/conf/2019");
		
		return res;
	}
	

}
