package app.configuration.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import app.configuration.staxparser.DblpDocument;

public class DocumentCreator 
{

	static String DEFAULT = null;
	
//	public Integer createAuthor(ArrayList<String> authors)
//	{
//		Integer res = 0;
//		for (int i = 0; i < authors.size(); i++) 
//		{
//			//insert author
//			Author author = new Author(authors.get(i), DEFAULT);
//			
//		}
//		return res;
//	}
//	
//	public void createBookDocument(DblpDocument dblp)
//	{
//
//		//ResultSet sql = Statement.RETURN_GENERATED_KEYS("");
//		
//		for (int i = 0; i < dblp.getAuthors().size(); i++) 
//		{
//			//insert author
//			Author author = new Author(dblp.getAuthors().get(i), DEFAULT);
//			
//		}
//		
//	}
	public Integer createPublication(DblpDocument dblp)
	{
		Publication publication = new Publication(dblp.getTitle(), dblp.getDoc_type(),getDOI(dblp.getEe()),dblp.getYear() );
		PreparedStatement sql ;
		return null;
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
				res = res.concat("/").concat(split[3]).concat("/"+split[split.length-1]);
			}else {
				res = DEFAULT;
			}
			return res;
		}
	}
}
