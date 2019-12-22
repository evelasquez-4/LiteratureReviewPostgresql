package literature.review.app.staxparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.springframework.stereotype.Component;



@Component
public class Parser {

	HashMap<String, Object> services = new HashMap<>();
	
	
	public Parser(HashMap<String, Object> services)
	{
		this.services = services;
	}
	
	//first time insert
	public Parser() {
		
	}
	
	public boolean readXml() throws XMLStreamException, IOException
	{
		
		String path = "/Users/raven/Documents/project/RSL/RevisionNoLiteraria/data/dblp.xml";
		
		InputStream is = new FileInputStream(path);
		ParseDocument p = new ParseDocument(this.services,false); 
		boolean ended = p.readFromXML(is);
		if(ended)
		{
			System.err.println("success XML parse !!!! :D !!! ");
//			parse.mostrar();
//			Long end = System.currentTimeMillis();
//			System.out.println("Used: " + (end - start) / 1000 + " seconds");
		}else
			System.err.println("parse error :(  ");
		
		
		return ended;
	}

	public boolean readFirstTimeXml() throws XMLStreamException, IOException
	{
		String path = "/Users/raven/Documents/project/RSL/RevisionNoLiteraria/data/dblp.xml";
		InputStream is = new FileInputStream(path);
		ParseDocument p = new ParseDocument(this.services,true);//1->services; 2->boolean first time insert
		
		Long start = System.currentTimeMillis();
		
		boolean ended = p.readFromXML(is);
		if(ended)
		{
			System.err.println("success XML parse !!!! :D !!! ");
//			parse.mostrar();
			Long end = System.currentTimeMillis();
			System.out.println("Used: " + (end - start) / 1000 + " seconds");
		}else
			System.err.println("parse error :(  ");
		
		
		return ended;
	}
	
	
	public HashMap<String, Object> getServices() {
		return services;
	}

	public void setServices(HashMap<String, Object> services) {
		this.services = services;
	}
	
	
	public boolean precargaDblpData()throws XMLStreamException, IOException
	{
		String path = "/Users/raven/Documents/project/RSL/RevisionNoLiteraria/data/dblp.xml";
		InputStream is = new FileInputStream(path);
		ParseDocument p = new ParseDocument();
		
		boolean ended = p.readFromXML(is);
		
		if(ended)
		{
			System.err.println("success XML parse !!!! :D !!! ");
//			parse.mostrar();
//			Long end = System.currentTimeMillis();
//			System.out.println("Used: " + (end - start) / 1000 + " seconds");
		}else
			System.err.println("parse error :(  ");
		
		
		return ended;
	}
	
	/*
	public static void main(String[] args) throws IOException, XMLStreamException
	{
		//path->args[0]
		String path = "/Users/raven/Documents/project/RSL/RevisionNoLiteraria/data/dblp.xml";
		
//		try(InputStream is = Parser.class.getResourceAsStream(path))
//		{
//			//Long start = System.currentTimeMillis();
//			
//			if (is == null)
//                throw new IOException("Failed to open resource");
//			
			InputStream is = new FileInputStream(path);
			//ParseHandler parse = new ParseHandler();
			ParseDocument p = new ParseDocument();
			boolean ended = p.readFromXML(is);
			
			if(ended)
			{
				System.err.println("success XML parse !!!! :D !!! ");
//				parse.mostrar();
//				Long end = System.currentTimeMillis();
//				System.out.println("Used: " + (end - start) / 1000 + " seconds");
			}else
				System.err.println("parse error :(  ");
//		}
		
		
		
	}
	
	*/

}
