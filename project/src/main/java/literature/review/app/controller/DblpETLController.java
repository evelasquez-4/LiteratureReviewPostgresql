package literature.review.app.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import literature.review.app.service.DblpETLService;



@RestController
@RequestMapping("/dblp")
public class DblpETLController {

	@Autowired
	DblpETLService dblpETLService;
	
	/*
	 * insert directo en tablas authors,publications y author_publications
	 */
	@RequestMapping(value = "/first_insert", method = RequestMethod.POST)
	public boolean firstTimeInsert() throws XMLStreamException, IOException
	{
		return dblpETLService.firstTimeInsert();
	}
	//función que carga la data en public.dblp_publication
	@RequestMapping(value = "/precarga_data", method = RequestMethod.POST)
	public boolean precargaDblpData() throws XMLStreamException, IOException
	{
		return dblpETLService.precargaDblpData();
	}
	/*
	 * funcion que toma la data de public.dblp_publication
	 * e inserta los datos correspondientes en slr.author,slr.publication
	 * y slr.author_publications
	 * @params: accion -> AUTHPUB_INS
	 * 			publication_type -> [book,article,proceeding,etc]
	 */
	@RequestMapping(value = "/proceso_data", method = RequestMethod.POST)
	public boolean authorPublicationsInsert( 
				@RequestParam String doc,
				@RequestParam String limit
			)
	{
		int limite = limit.length() == 0 ? -1:Integer.parseInt( limit );
		List<String> doc_types = Arrays.asList("article","inproceedings","proceedings","book","incollection");

		
		if(doc_types.contains(doc))
			return this.dblpETLService.authorPublicationsInsert(doc , limite);
		else
		{
			System.out.println("error, tipo de documento invalido.");
				return false;
		}
	}
}
