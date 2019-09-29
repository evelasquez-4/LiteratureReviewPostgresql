package app.documents;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import app.configuration.AppConfig;
import app.literature.review.model.DblpDocument;;

public class MongoQueries {
	
	AppConfig db = new AppConfig();
	
	public List<DblpDocument> findByMendeleyState(String mendeley,int limite) throws Exception
	{
		List<DblpDocument> res =new ArrayList<DblpDocument>();
		MongoTemplate mongoTemplate = db.mongoTemplate(); 
		
		Query query = new Query();
		query.addCriteria(Criteria.where("stateMendeley").is(mendeley)).limit(limite);
		res = mongoTemplate.find(query, DblpDocument.class);
		
		return res;
	}
	
	public boolean updateDblpDocuments(List<DblpDocument> docs)
	{
		boolean res = false;
		try {
			MongoTemplate template = db.mongoTemplate();
			
			Query query = new Query();
			//template.updateMulti(query, update, entityClass)
			
			
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return res;
		}
		
	}

}
