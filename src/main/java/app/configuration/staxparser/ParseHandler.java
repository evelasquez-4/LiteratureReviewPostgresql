package app.configuration.staxparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.events.Attribute;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import app.documents.Document;


public class ParseHandler 
{
	int totalElements = 0;
	private List<Document> publications = new ArrayList<Document>(); 
	Document document = new Document();
	
	
	public List<Document> readFromXML(InputStream is) throws XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = null;
		
		try {
			xmlStreamReader = inputFactory.createXMLStreamReader(is);
			return readDocument(xmlStreamReader);
        } 
		finally
		{
            if (xmlStreamReader != null) {
            	xmlStreamReader.close();
            }
        }
		
	}
	
	private List<Document> readDocument(XMLStreamReader reader) throws XMLStreamException {
		while(reader.hasNext()) {
			int eventType = reader.next();
			
			switch (eventType)
			{
				case XMLStreamReader.START_ELEMENT:
					String elementName = reader.getLocalName();
					if (elementName.equals("dblp")) {
						return readDocuments(reader);
					}
					break;
				case XMLStreamReader.END_ELEMENT:
                    break;
				
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private List<Document> readDocuments(XMLStreamReader reader) throws XMLStreamException
	{
		List<Document> res = new ArrayList<Document>();
		while(reader.hasNext())
		{
			int eventType = reader.next();
			System.out.println("ingresa2");
			
			switch (eventType) {
			case XMLStreamReader.START_ELEMENT:
				String elementName = reader.getLocalName();
				System.out.println(elementName);
				
				if( //"article".equalsIgnoreCase(elementName) || 
					"mastersthesis".equalsIgnoreCase(elementName)	
				   )
				{
					
					if(reader.getAttributeCount() > 0) 
					{
						String key = reader.getAttributeValue(null, "key");
						String mdate = reader.getAttributeValue(null, "mdate");
						System.out.println("key->"+key);
						this.document.setKey(key);
						this.document.setMdate(mdate);
						
						res.add( readPublication(reader) );
					}
				}
				break;

			case XMLStreamReader.END_ELEMENT:
                return res;
			default:
				break;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	private Document readPublication(XMLStreamReader reader) throws XMLStreamException
	{
		Document doc = new Document();
		 while (reader.hasNext()) {
			 int eventType = reader.next();
			 System.out.println("ingresa3");
			 switch (eventType) 
			 {
				case XMLStreamReader.START_ELEMENT:
					String elementName = reader.getLocalName();
					
					if("author".equalsIgnoreCase(elementName))
						doc.setAuthors( readAuthors(reader) );
					
					break;

			}
		 }
		
		 return doc;
		
	}
	 
	private ArrayList<String> readAuthors(XMLStreamReader reader) throws XMLStreamException
	{
		ArrayList<String> authors = new ArrayList<String>();
		
		while (reader.hasNext()) 
		{
			 int eventType = reader.next();
			 System.out.println("ingresa4");
			 System.out.println("tipo evento 4->"+eventType);
			 
			 switch (eventType)
			 {
				case XMLStreamReader.START_ELEMENT:
					String elementName = reader.getLocalName();
					
					if("author".equalsIgnoreCase(elementName))
					{
						authors.add(reader.getText());
					}
					break;
				case XMLStreamReader.CHARACTERS:
					authors.add(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
                    return authors;
			}
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	
	private String readCharacters(XMLStreamReader reader) throws XMLStreamException
	{
		StringBuilder result = new StringBuilder();
		while (reader.hasNext()) 
		{
            int eventType = reader.next();
            System.out.println("ingresa5");
            switch (eventType) 
            {
            	case XMLStreamReader.CDATA:
            		result.append(reader.getText());
            		break;
            	case XMLStreamReader.END_ELEMENT:
            		return result.toString();
            }
		}
		throw new XMLStreamException("Premature end of file");
	}
	
	public void mostrar() 
	{
		System.out.println(publications.size());
		for (int i = 0; i < publications.size(); i++) {
			System.out.println(publications.get(i).getAuthors().toString());
			
		}
	}
	
	
	
	public static void proccessXMLFile(File xmlFile) throws FileNotFoundException, XMLStreamException {
		//StringBuffer rawXml = new StringBuffer();
		InputStream inputStream = new FileInputStream(xmlFile);
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
		
		//XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(xmlFile));
		
		while(xmlStreamReader.hasNext())
		{
			int eventType = xmlStreamReader.next();
			int cant = 0;
			
			switch (eventType) 
			{
				case XMLStreamReader.START_ELEMENT:
					
					if("mastersthesis".equalsIgnoreCase(xmlStreamReader.getLocalName()))
					{
						if(xmlStreamReader.getAttributeCount() > 0) {
							String key = xmlStreamReader.getAttributeValue(null, "key");
							String mdate = xmlStreamReader.getAttributeValue(null, "mdate");
							
							System.out.println(key);
							System.out.println(mdate);
						}
						
						
					}
					
					break;
				
				case XMLStreamReader.END_ELEMENT:
					break;
	
				default:
//					System.out.println(eventType);
					break;
			}
		}
		
		
		
//		while(xmlEventReader.hasNext()){
//			XMLEvent xmlEvent = xmlEventReader.nextEvent();
//			
//			if (xmlEvent.isStartElement())
//			{
//				
//				StartElement startElement = xmlEvent.asStartElement();
//				
//				if("mastersthesis".equalsIgnoreCase( startElement.getName().getLocalPart()) )
//				{
//					
//	                Iterator<Attribute> iterator = startElement.getAttributes();
//					
//					while(iterator.hasNext()) 
//					{
//						Attribute attribute = iterator.next();
//						QName name = attribute.getName();
//						
//						if("key".equalsIgnoreCase(name.getLocalPart()))
//							System.out.println(attribute.getValue());
//							
//						else if("mdate".equalsIgnoreCase(name.getLocalPart()))
//							System.out.println(attribute.getValue());
//					}
//					
//				}
//				//fin mastersthesis
//			
//			}
//			else if(xmlEvent.isEndElement()) {
//				EndElement endElement = xmlEvent.asEndElement();
//				if(endElement.getName().getLocalPart().equals("mastersthesis") )
//				{
//					System.out.println( "fin del elemento" );
//				}
//			}
//			else if(xmlEvent.isCharacters()) {
//				
//			}
//		}

	}

}
