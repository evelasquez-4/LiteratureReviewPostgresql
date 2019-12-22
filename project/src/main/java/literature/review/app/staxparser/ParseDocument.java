package literature.review.app.staxparser;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.springframework.stereotype.Component;


@Component
public class ParseDocument 
{
	static Integer BATCHSIZE = 5000;
	int docsNumber = 0;
	List<DblpDocument> docs = new ArrayList<DblpDocument>();
	
	DblpDocument document = new DblpDocument();
	ArrayList<String> authors = new ArrayList<String>();
	
	HashMap<String, Object> services = new HashMap<>();
	boolean firstInsert = false;
	
	DblpPersistence dblp_inserts = null;
	
	public ParseDocument() {}
	
	
	public ParseDocument(boolean firstInsert)
	{
		this.firstInsert = firstInsert;
	}
	
	public ParseDocument(HashMap<String, Object> services,boolean firstInsert)
	{
		this.services = services;
		this.dblp_inserts = new  DblpPersistence(services);
		this.firstInsert = firstInsert;
	}
	
	public boolean readFromXML(InputStream is) throws XMLStreamException {
		
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = null;
		
		try { 
			//xmlStreamReader = inputFactory.createXMLStreamReader(is);
			xmlStreamReader = inputFactory.createXMLStreamReader(is, "ISO-8859-1" );
			return readXMLRoot(xmlStreamReader);
        } 
		finally
		{
            if (xmlStreamReader != null) {
            	xmlStreamReader.close();
            }
        }
		
	}
	
	private boolean readXMLRoot(XMLStreamReader reader) throws XMLStreamException
	{
		
		while(reader.hasNext()) {
			int eventType = reader.next();
			
			switch (eventType)
			{
				case XMLStreamReader.START_ELEMENT:
					String elementName = reader.getLocalName();
					if (elementName.equals("dblp")) {
						return readDocument(reader);
					}
					
					break;
				case XMLStreamReader.END_ELEMENT:
					return true;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private boolean readDocument(XMLStreamReader reader) throws XMLStreamException 
	{
		
		while(reader.hasNext())
		{
			int eventType = reader.next();
			
			switch (eventType)
			{
				case XMLStreamReader.START_ELEMENT:
					String elementName = reader.getLocalName();
					
					if(
//							"mastersthesis".equalsIgnoreCase(elementName) 	||
//							"phdthesis".equalsIgnoreCase(elementName) ||
							"article".equalsIgnoreCase(elementName)||
							"book".equalsIgnoreCase(elementName)||
							"incollection".equalsIgnoreCase(elementName)||
							"inproceedings".equalsIgnoreCase(elementName)||
							"proceedings".equalsIgnoreCase(elementName)
						) 
					{		
						document.setDoc_type(elementName);
						
						if(reader.getAttributeCount() > 0) 
						{
							String key = reader.getAttributeValue(null, "key");
							String mdate = reader.getAttributeValue(null, "mdate");
														
							document.setKey_dblp(key);
							document.setMdate(mdate);
						}						
					}
					//title field
//					else if( "title".equalsIgnoreCase(elementName) ||
//           				 "booktitle".equalsIgnoreCase(elementName)
//           				 ) 
//					{	
//						String title = readStringTag(reader,"title");
//						document.setTitle(title);
//					}
					//author field
//					else if( "author".equalsIgnoreCase(elementName)  || 
//            				"editor".equalsIgnoreCase(elementName)
//            			)
//            		{
//						String author = readStringTag(reader,"author");
//						authors.add(author);
//            		}
					else
					{
						switch (elementName.toLowerCase()) 
						{
							//title
							case "title": 
								document.setTitle(readStringTag(reader, "title")); break;
							case "booktitle":
								document.setBooktitle(readStringTag(reader, "booktitle")); break;
							//author	
							case "author":
								authors.add(readStringTag(reader, "author"));break;
							case "editor":
								authors.add(readStringTag(reader, "editor"));break;
							//pages
							case "pages":
								document.setPages( readStringTag(reader, "pages") );break;
							//year field
							case "year":
								document.setYear( readIntegerTag(reader,"year") );break;
							//journal field
							case "journal":
								document.setJournal(readStringTag(reader, "journal") );break;
							//volume field
							case "volume":
								document.setVolume( readStringTag(reader, "volume") );break;
							//number field
							case "number":
								document.setNumber( readStringTag(reader, "number"));break;
							//month field
							case "month":
								document.setMonth( readStringTag(reader, "month") ); break;
							//url field
							case "url":
								document.setUrl( readStringTag(reader, "url") );break;
							case "ee":
								document.setEe( readStringTag(reader, "ee"));break;
							case "cite":
								document.setCite( readStringTag(reader, "cite") );break;
							case "publisher":
								document.setPublisher( readStringTag(reader, "publisher") );break;
							case "note":
								document.setNote( readStringTag(reader, "note") );break;
							case "crossref":
								document.setCrossref( readStringTag(reader, "crossref") );break;
							case "isbn":
								document.setIsbn(readStringTag(reader, "isbn") );break;
							case "series":
								document.setSeries( readStringTag(reader, "series") );break;
							case "school":
								document.setSchool( readStringTag(reader,"school") );break;
							case "chapter":
								document.setChapter( readStringTag(reader, "chapter") );break;
							case "publnr":
								document.setPblnr( readStringTag(reader, "publnr"));break;
						
						}
					}					
					break;
					
				case XMLStreamReader.END_ELEMENT:
					//System.out.println("fin->"+reader.getLocalName());
					String endElementName = reader.getLocalName();
					if(
//							"mastersthesis".equalsIgnoreCase(endElementName) ||
//							"phdthesis".equalsIgnoreCase(endElementName)	||
							"article".equalsIgnoreCase(endElementName)||
							"book".equalsIgnoreCase(endElementName)||
							"incollection".equalsIgnoreCase(endElementName)||
							"inproceedings".equalsIgnoreCase(endElementName)||
							"proceedings".equalsIgnoreCase(endElementName)

						)
					{
						
						document.setAuthors(authors);		
						docs.add(document);
						docsNumber++;
						
						//reset
						document = new DblpDocument();
						authors = new ArrayList<>();
						
						if(docs.size() == BATCHSIZE)
						{
							try 
							{
								
								if(this.firstInsert)//no services available
								{
									
									DblpETL dblpETL = new DblpETL(this.services);
									dblpETL.firstTimeInsert(docs);
									docs = new ArrayList<DblpDocument>();//reset docs
									
									
									
								}else 
								{
									//dblp_inserts.insertAuthorPublications(docs.remove(0));
									//dblp_inserts.firstTimeInsertPublicationAuthor(docs);
									//docs = new ArrayList<>();
									//System.out.println("=======INSERTED"+docsNumber+"=========");
								}
								
								
								//precarga de datos
								if(this.services.isEmpty() && !this.firstInsert)
								{
									DblpIUD etl = new DblpIUD();
									etl.precargaDataDBLP(docs);
									
									docs = new ArrayList<DblpDocument>();//reset docs
									
								}
									
								
							}catch (Exception e) {
								System.out.println(e.getMessage());
								System.err.println("insert error !!!");
							}
							
							
						}
					 
					}
					
					else if(endElementName.equalsIgnoreCase("dblp"))
					{

						try {
							
							if(this.firstInsert)//first
							{
								DblpETL dblpETL = new DblpETL(this.services);
								dblpETL.firstTimeInsert(docs);//@param Lis<DblpDocument> -> docs
								
							}else {
								//dblp_inserts.insertAuthorPublications(docs.remove(0));
								//dblp_inserts.firstTimeInsertPublicationAuthor(docs);
								//System.out.println("=======INSERTED"+docsNumber+"=========");
							}
							
							//insert into slr.dblp_publication
							if(this.services.isEmpty() && !this.firstInsert)
							{
								DblpIUD etl = new DblpIUD();
								etl.precargaDataDBLP(docs);
							}
							
						}catch (Exception e) {
							System.out.println(e.getMessage());
							System.err.println("insert error!!!");
						}
						
						return true;
					}
					else {
						authors = new ArrayList<>();
					}
					
				break;
			}
		}
	throw new XMLStreamException("Premature end of file");
	}
	
	private String readStringTag(XMLStreamReader reader,String character) throws XMLStreamException
	{
		StringBuilder result = new StringBuilder();
		while (reader.hasNext())
		{
			int eventType = reader.next();
			switch (eventType) {
			case XMLStreamReader.CHARACTERS:
				
				result.append( encoding(reader.getText()) );
				break;
			case XMLStreamReader.END_ELEMENT:
				return result.toString();
			
			}
		}
		throw new XMLStreamException("Premature end of file CHARACTER TAG ->"+character);
	}
	
	private int readIntegerTag(XMLStreamReader reader,String character) throws NumberFormatException, XMLStreamException 
	{
		Integer year = 0;
		 while (reader.hasNext()) 
		 {
			 int eventType = reader.next();
			 
			 switch (eventType) {
			 	case XMLStreamReader.CHARACTERS:
					year = Integer.parseInt( reader.getText() );
					
					break;
				case XMLStreamReader.END_ELEMENT:
					return year;
			 }
		 }
		
		throw new XMLStreamException("Premature end of file integer tag: "+character);
	}
	
	
	private String encoding(String text) 
	{
		String res = null;
		try {
			res = new String( text.getBytes("ISO-8859-1"),"UTF-8" )
					.replaceAll("\\s{2,}", " ")
					.trim();
			
		} 
		catch (UnsupportedEncodingException e) 
		{
			System.out.println(e.getMessage());
		}
		
		return res;
	}
}

	