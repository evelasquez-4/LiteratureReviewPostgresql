package app.configuration.staxparser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;




public class Parser {

	
	public static void main(String[] args) throws IOException, XMLStreamException
	{
		//path->args[0]
		String path = "/Users/raven/Documents/workspace-spring-tool-suite-4-4.3.2.RELEASE/LiteratureReview/src/main/resources/data/testData.xml";
		
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

}
