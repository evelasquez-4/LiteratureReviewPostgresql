package app.literature.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.literature.review.model.DblpDocument;
import app.literature.review.service.DBlpDocumentService;


@RestController
@RequestMapping("/dblpdocs")
public class DblpDocumentController {
	
	@Autowired
	private DBlpDocumentService dBlpDocumentService;
	
	@RequestMapping(value = "/lista", method = RequestMethod.GET)
	public List<DblpDocument> getAll()
	{
		return dBlpDocumentService.getAll();
	}
	
	
	@RequestMapping(value = "/updatedocs", method = RequestMethod.GET)
	public List<DblpDocument> updateDblpDocumentsFromMendeley(
			@RequestParam String mendeley,@RequestParam int limit) throws Exception
	{		
		return dBlpDocumentService.findByMendeleyState(mendeley, limit);
	}
	
	@RequestMapping(value = "/test")
	public List<DblpDocument> test(
			@RequestParam String mendeley,
			@RequestParam int limit) throws Exception{
		return dBlpDocumentService.test(mendeley, limit);
	}
	
	
	@RequestMapping("/hola")
	public String hola() {
		return "Hola Mundo!!";
	}

//	@Autowired(required = true)
//	DblpDocumentRepository dblpDocumentRepository;
//
//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	public List<DblpDocument> getAllDblpDocuments(){
//		return dblpDocumentRepository.findAll();
//		
//	}
//	
//	@RequestMapping(method = RequestMethod.GET, value = "/updatedocs/{limit}" )
//	public String updateDblpDocumentsFromMendeley(@PathVariable("limit") int limit) throws Exception
//	{
//		List<DblpDocument> res = new ArrayList<>();
//		MongoQueries queries = new MongoQueries();
//		Mendeley mendeley = new Mendeley();
//		
////		//lista docs dblp no modificados
////		List<DblpDocument> docsNoModificados = queries.findByMendeleyState("", limit) ;
////		
////		//lista docs dblp modificados
////		List<DblpDocument> docsModificados = mendeley.updateDblpDocuments(docsNoModificados);
////		
////		//update collection mongodb
////		for(DblpDocument d : docsModificados)
////		{
////			
////		}
//		System.out.println("=============");
//		return String.valueOf(limit);
//	}
}
