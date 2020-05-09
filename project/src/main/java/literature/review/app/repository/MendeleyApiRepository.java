package literature.review.app.repository;

import java.util.List;

import org.json.JSONArray;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Publications;

@RepositoryRestResource
public interface MendeleyApiRepository {
	
	public void updatePublications(List<Publications> publications,String mendeleyKey) throws Exception;
	
	public JSONArray findMendeleyPublicationByTitle(String title) throws Exception;
	
	public JSONArray findMendeleyPublicationByDOI(String doi) throws Exception;
 
}
