package app.configuration;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;

public class Parser {
	
	
	
	public Parser(String uri)  {
		try {
			System.out.println("Parsing...");
			
			
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			//ConfigParseHandler handler = new ConfigParseHandler();
			ParseHandler handler = new ParseHandler();
			parser.getXMLReader().setFeature("http://xml.org/sax/features/validation", true);
			parser.parse(new File(uri), handler);
			
			try {
				System.out.println("Processed ");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			System.out.println("Error reading URI: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("Error in parsing: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("Error in XML parser configuration: "
					+ e.getMessage());
		}
	}
	
	//exec cmd line: java -DentityExpansionLimit=2000000 Parser.java
	public static void main(String[] args) throws ClassNotFoundException {
		Long start = System.currentTimeMillis();
//		Parser p = new Parser(args[0]);
		Parser p = new Parser("/Users/raven/Documents/project/code/backend/data/data.xml");
	
		Long end = System.currentTimeMillis();
		System.out.println("Used: " + (end - start) / 1000 + " seconds");

		
	}

}
