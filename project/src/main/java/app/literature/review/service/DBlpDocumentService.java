package app.literature.review.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import app.configuration.AppConfig;

import app.documents.MongoQueries;
import app.literature.review.model.DblpDocument;
import app.literature.review.repository.DblpDocumentRepository;
import app.references.Mendeley;

@Service
public class DBlpDocumentService {

	@Autowired
	private DblpDocumentRepository dblpDocumentRepository;

	public List<DblpDocument> getAll() {
		return dblpDocumentRepository.findAll();
	}

	public List<DblpDocument> findByMendeleyState(String mendeleyState, int limit) throws JSONException, IOException, Exception 
	{
		List<DblpDocument> res = new ArrayList<DblpDocument>();
		
		if(mendeleyState.isEmpty() || mendeleyState.length() == 0)
		{
			MongoQueries mongo = new MongoQueries();
			Mendeley mendeley = new Mendeley();
			
			List<DblpDocument> aux = mendeley.updateDblpDocuments( mongo.findByMendeleyState(mendeleyState, limit) );
			
			AppConfig db = new AppConfig();
			MongoTemplate mongoTemplate = db.mongoTemplate(); 
		
			for(DblpDocument doc : aux) 
			{
				Query query = new Query();
				query.addCriteria(Criteria.where("_id").is(doc.get_id()));
				
				Update update = new Update();
				update.set("abstract", doc.getAbstractDocument());
				update.set("keywords", doc.getKeywords());
				
				 //searched by isbn -> set doi
				 if(doc.getDoi().isEmpty())
				 {
					 update.set("doi", doc.getDoi());
				 }
				 update.set("lastDateSearched", String.valueOf(LocalDate.now()));
				 update.set("stateMendeley","updated");
				 update.set("mendeleySearched", true);
				 
				 
				 mongoTemplate.updateFirst(query, update, DblpDocument.class);
				 
				 res.add(doc);
			}
			
			return aux;
		}
		
		return res;
	}

	public List<DblpDocument> test(String mendeley,int limit) throws Exception
	{
		
		MongoQueries mongo = new MongoQueries();
		
		return mongo.findByMendeleyState(mendeley, limit);
	}
}
