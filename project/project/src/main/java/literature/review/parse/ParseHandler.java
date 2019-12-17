package literature.review.parse;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
import org.codehaus.jettison.json.JSONObject;

//import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Base64;



public class ParseHandler extends DefaultHandler {
		
	Integer docsNumber = 0;
	Ancestor ancestor = new Ancestor() ;
	String curElement = "";
	DblpDocument document = new DblpDocument();
	
	
	List<DblpDocument> docs = new ArrayList<DblpDocument>();
	
	Map<String, String> elemento =new HashMap<String, String>();
	
	DBConnect conn = null;
	Integer contador_autores = 0;
	List<String> autores = new ArrayList<String>();
	
	
	public void startElement(String namespaceURI, String localName, String rawName, Attributes atts)throws SAXException {
	
		if(Element.contains(rawName))
		{ 
			document.setDoc_type(rawName);
			if (atts.getLength() > 0) {
				document.setKey_dblp(atts.getValue("key"));
				document.setMdate(atts.getValue("mdate"));
				
				Map<String, String> attributes = new HashMap<String, String>();
				for(int i=0; i< atts.getLength();i++) {
					attributes.put(atts.getLocalName(i), atts.getValue(i));
				}
				document.setUnknow_atts(attributes);
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
			
			String str = new String(ch,start,length);
			//String str = Base64.getEncoder().encodeToString(str1.getBytes(StandardCharsets.UTF_8)).replace("\n", "");
		
			try {
				if (curElement.equals("author") || curElement.equals("editor")) {
					//document.getAuthors().put(String.valueOf(contador_autores), str);
					//Map<String, String> aut = new HashMap<String, String>();
					//str = str.replace("&yacute;", "y");
					document.getAuthors().put(contador_autores, str);
					contador_autores++;

				}
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
			System.out.println( document.getAuthors().toString() );
			System.out.println("===========================");
			contador_autores = 0;
			// seteo de valores de documento
			setDocumento(elemento);

//				System.out.println("raw->"+rawName);
//				System.out.println("acestor->"+ancestor.getName());
			
			docsNumber++;

			docs.add(document);
			document = new DblpDocument();
			ancestor = new Ancestor();

			
			//docs.size() % 10 == 0
			//docs.size() == 10000
			
			if(docs.size() == 20) {
				
				for (int i = 0; i < docs.size(); i++) {
					DblpDocument d = docs.remove(i);
					try {
						
						PreparedStatement st = conn.getInstance().getConnection().
								prepareStatement("INSERT INTO slr.dblp_document (KEY_DBLP,AUTHORS,DOC_TYPE,EDITOR,BOOKTITLE,"
										+ "MDATE,PAGES,YEAR,TITLE,ADDRESS,JOURNAL,VOLUME,NUMBER,MONTH,URL,EE,CDROM,CITE,PUBLISHER,"
										+ "NOTE,CROSSREF,ISBN,SERIES,SCHOOL,CHAPTER,PUBLNR,UNKNOW_FIELDS,UNKNOW_ATTS) "
										+ "VALUES (?, to_json(?::json), ?, ?, ?, "
										+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
										+ "?, ?, ?, ?, ?, ?, ?, to_json(?::json), to_json(?::json))");
						
						st.setString(1, d.getKey_dblp());//key
						st.setString(2, new JSONObject(d.getAuthors()).toString() );//authors
						st.setString(3, d.getDoc_type());//doc_type
						st.setString(4, d.getEditor());//editor
						st.setString(5, d.getBooktitle());//booktitle
						
						st.setString(6, d.getMdate());//mdate
						st.setString(7, d.getPages());//pages
						st.setInt(8, d.getYear());//year
						st.setString(9, d.getTitle());//title
						st.setString(10, d.getAddress());//address
						st.setString(11, d.getJournal());//journal
						st.setString(12, d.getVolume());//volume
						st.setString(13, d.getNumber());//number
						st.setString(14, d.getMonth());//month
						st.setString(15, d.getUrl());//url
						st.setString(16, d.getEe());//ee
						st.setString(17, d.getCdrom());//cdrom
						st.setString(18, d.getCite());//cite
						st.setString(19, d.getPublisher());//publisher
						
						st.setString(20, d.getNote());//note
						st.setString(21, d.getCrossref());//crossref
						st.setString(22, d.getIsbn());//isbn
						st.setString(23, d.getSeries());//series
						st.setString(24, d.getSchool());//school
						st.setString(25, d.getChapter());//chapter
						st.setString(26, d.getPblnr());//publnr
						st.setString(27, new JSONObject(d.getUnknow_fields()).toString());
						st.setString(28, new JSONObject(d.getUnknow_atts()).toString());
						
						st.executeUpdate();
						
						
					}catch (SQLException e) {
						// TODO: handle exception
						System.out.println(e.getMessage());
					}
					
				}
				
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


	public int insertarRestante(List<DblpDocument> resto)
	{
		int res = 0;
		for (int i = 0; i < resto.size(); i++) {
			
			DblpDocument d = docs.remove(i);
			try {
				
				PreparedStatement st = conn.getInstance().getConnection().
						prepareStatement("INSERT INTO slr.dblp_document (KEY_DBLP,AUTHORS,DOC_TYPE,EDITOR,BOOKTITLE,"
								+ "MDATE,PAGES,YEAR,TITLE,ADDRESS,JOURNAL,VOLUME,NUMBER,MONTH,URL,EE,CDROM,CITE,PUBLISHER,"
								+ "NOTE,CROSSREF,ISBN,SERIES,SCHOOL,CHAPTER,PUBLNR,UNKNOW_FIELDS,UNKNOW_ATTS) "
								+ "VALUES (?, to_json(?::json), ?, ?, ?, "
								+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
								+ "?, ?, ?, ?, ?, ?, ?, to_json(?::json), to_json(?::json))");
				
				st.setString(1, d.getKey_dblp());//key
				st.setString(2, new JSONObject(d.getAuthors()).toString() );//authors
				st.setString(3, d.getDoc_type());//doc_type
				st.setString(4, d.getEditor());//editor
				st.setString(5, d.getBooktitle());//booktitle
				
				st.setString(6, d.getMdate());//mdate
				st.setString(7, d.getPages());//pages
				st.setInt(8, d.getYear());//year
				st.setString(9, d.getTitle());//title
				st.setString(10, d.getAddress());//address
				st.setString(11, d.getJournal());//journal
				st.setString(12, d.getVolume());//volume
				st.setString(13, d.getNumber());//number
				st.setString(14, d.getMonth());//month
				st.setString(15, d.getUrl());//url
				st.setString(16, d.getEe());//ee
				st.setString(17, d.getCdrom());//cdrom
				st.setString(18, d.getCite());//cite
				st.setString(19, d.getPublisher());//publisher
				
				st.setString(20, d.getNote());//note
				st.setString(21, d.getCrossref());//crossref
				st.setString(22, d.getIsbn());//isbn
				st.setString(23, d.getSeries());//series
				st.setString(24, d.getSchool());//school
				st.setString(25, d.getChapter());//chapter
				st.setString(26, d.getPblnr());//publnr
				st.setString(27, new JSONObject(d.getUnknow_fields()).toString());
				st.setString(28, new JSONObject(d.getUnknow_atts()).toString());
				
				st.executeUpdate();
				
				
			}catch (SQLException e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
			}
			res++;
		}
		return res;
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
			    case "editor": document.setEditor(value); break;
			    case "pages": document.setPages(value); break;
			    case "year": document.setYear(Integer.parseInt(value) ); break;
			    case "address": document.setAddress(value); break;
				case "journal": document.setJournal(value); break;
				case "volume": document.setVolume(value); break;		
				case "number": document.setNumber(value);break;
				case "month": document.setMonth(value);break;
				case "url":document.setUrl(value);break;
				case "ee": document.setEe(value); break;
				case "cdrom":document.setCdrom(value);break;
				case "cite": document.setCite(value);break;
				case "publisher" : document.setPublisher(value); break;
				case "note" : document.setNote(value); break;
				case "crossref": document.setCrossref(value);break;
				case "isbn" : document.setIsbn(value); break;
				case "series":document.setSeries(value);break;
				case "school": document.setSchool(value); break;
				case "chapter": document.setChapter(value);break;
				case "pblnr": document.setPblnr(value);break;
	
				default:
					break;
				}
//		    }

		}
		elemento.clear();
	}
}
