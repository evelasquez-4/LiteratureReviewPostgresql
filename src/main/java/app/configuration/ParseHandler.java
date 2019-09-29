package app.configuration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;

//import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBObject;
import com.mongodb.MongoBulkWriteException;
import java.util.Base64;

import app.literature.review.model.DblpDocument;




public class ParseHandler extends DefaultHandler {
		
	Integer docsNumber = 0;
	Ancestor ancestor = new Ancestor() ;
	String curElement = "";
	DblpDocument document = new DblpDocument();
	
	
	//mongodb
	AppConfig db = new AppConfig();
	List<DBObject> docs = new ArrayList<DBObject>();
	
	
	Map<String, String> elemento =new HashMap<String, String>();
	
	
	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)throws SAXException {
	
		if(Element.contains(rawName))
		{ 
			document.setType(rawName);
			if (atts.getLength() > 0) {
				document.setKey(atts.getValue("key"));
				document.setMdate(atts.getValue("mdate"));
			}
			ancestor.setName(rawName);
			ancestor.setElements(document.getElementsAsString());
		}
		
		if(Element.contains(ancestor.getName()))
			//curElement = ancestor.contains(rawName)?rawName:"";
			curElement = rawName;
	}
	
	public void characters (char ch[], int start, int length){
		if(curElement.length() > 0)
		{
			String str1 = new String(ch,start,length);
			String str = Base64.getEncoder().encodeToString(str1.getBytes(StandardCharsets.UTF_8)).replace("\n", "");
			
			try {
				if (curElement.equals("author") || curElement.equals("editor"))
					document.getAutores().add(str);
				else if (curElement.equals("title") || curElement.equals("booktitle"))
					document.setTitle(str);
				else {
					elemento.put(curElement, str);
				}

			} catch (Exception e) {
				System.out.println("Verificar Elemento de :" + ancestor.getName() + "\nElemento:" + curElement);
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException 
	{

		if (Element.contains(rawName)) 
		{
			// seteo de valores de documento
			setDocumento(elemento);

//				System.out.println("raw->"+rawName);
//				System.out.println("acestor->"+ancestor.getName());
			
			docsNumber++;

			docs.add(new BasicDBObject(parseHashMap(document)));
			document = new DblpDocument();
			ancestor = new Ancestor();

			try {
				
			
				db.init();
				BulkWriteOperation builder = db.getCollection().initializeUnorderedBulkOperation();
				
				for(int i=0;i<docs.size();i++)
				{
					builder.insert(docs.remove(i));
//					if(cont % 1000 == 0)
//					{
//						bulk.execute();
//						bulk = db.getCollection().initializeOrderedBulkOperation();
//						
//						db.close();
//					}
				}
				builder.execute();
				db.close();
			 
			}catch (MongoBulkWriteException e) {
				e.getMessage();
				e.getWriteResult();
			}
		}

	}
	
	public void Message(String mode, SAXParseException exception) {
		System.out.println(mode + " Line: " + exception.getLineNumber() + " URI: " + exception.getSystemId() + "\n"
				+ " Message: " + exception.getMessage());
	}

	public void warning(SAXParseException exception) throws SAXException {
		Message("**Parsing Warning**\n", exception);
		throw new SAXException("Warning encountered");
	}

	public void error(SAXParseException exception) throws SAXException {
		Message("**Parsing Error**\n", exception);
		throw new SAXException("Error encountered");
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		Message("**Parsing Fatal Error**\n", exception);
		throw new SAXException("Fatal Error encountered");
	}

	public HashMap<String, Object> parseHashMap(DblpDocument d) {
		HashMap<String, Object> art = new HashMap<String, Object>();
		art.put("type", d.getType());
		art.put("key", d.getKey());
		art.put("mdate", d.getMdate());
		art.put("title", d.getTitle());
		art.put("authors", d.getAutores());
		art.put("journal", d.getJournal());
		art.put("pages", d.getPages());
		art.put("ee", d.getEe());
		art.put("volume", d.getVolume());
		art.put("year", d.getYear());
		art.put("publisher", d.getPublisher());
		art.put("url", d.getUrl());
		art.put("note", d.getNote());
		art.put("school", d.getSchool());
		art.put("isbn", d.getIsbn());
		
		
			String doi = d.getEe().length()>0 ? d.getEe():"";
		
		art.put("doi", getDOI(doi) );
		art.put("abstract", d.getAbstractDocument());
		art.put("keywords", d.getKeywords());
		
		
		art.put("mendeleySearched", d.isMendeleySearched());
		art.put("stateMendeley", d.getStateMendeley());
		art.put("lastDateSearched", d.getLastDateSearched());
		
		
		return art;
	}
	
	
	public String getDOI(String cad) {
		String res = "";
		String[] split = cad.split("/");
		  
		if (split[0].contains("http") && split[2].contains("doi.org")) 
		{
			for (int i = 3; i < split.length; i++) {
				res += split[i] + "/";
			}
			res = res.substring(0, res.length() - 1);
		}
		
		return res;
	}
	
	public void setDocumento(Map<String,String> val)
	{
		for (Map.Entry<String, String> entry : val.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    
//		    if(document.contains(key))
//		    {
			    switch (key) {
				case "journal": document.setJournal(value); break;
				case "pages": document.setPages(value); break;
				case "ee": document.setEe(value); break;
				case "volume": document.setVolume(Integer.parseInt(value) ); break;
				case "year": document.setYear(Integer.parseInt(value) ); break;
				case "publisher" : document.setPublisher(value); break;
				case "url": document.setUrl(value); break;
				case "school": document.setSchool(value); break;
				case "note" : document.setNote(value); break;
				case "isbn" : document.setIsbn(value); break;
	
				default:
					break;
				}
//		    }
		    
		}
		
		elemento.clear();
	}
}
