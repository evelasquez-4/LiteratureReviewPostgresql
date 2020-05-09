package literature.review.app.repository;

import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Publications;



@RepositoryRestResource
public interface PublicationsRepository extends JpaRepository<Publications, Long> 
{
	@Query("FROM Publications WHERE dblp_key = ?1")
	public Optional<Publications> findPublicationByKey(String key);
	
	//@Query("FROM publications WHERE doc_type ?1 AND updated_state ?2")
	
	@Query(value = "SELECT * FROM slr.Publications WHERE doc_type = :type LIMIT :limit", nativeQuery = true)
	public List<Publications> listPublicationByType(@Param("type") String type,@Param("limit") int limit);
	 
	@Query(value = "SELECT * FROM slr.Publications pub WHERE pub.updated_state = :state AND pub.doc_type = :type LIMIT :limit", nativeQuery = true)
	public List<Publications> findByTypeState(	@Param("state") String state,
													@Param("type") String type,
													@Param("limit") int limit);
	
	
	@Query(value = "SELECT * FROM slr.Publications pub "
			+ "WHERE pub.updated_state = :state AND pub.doc_type = :type "
			+ "AND pub.authors::text= '{}' LIMIT :limit", nativeQuery = true)
	public List<Publications> findNullAuthorsPublications(
			@Param("state") String state,
			@Param("type") String type,
			@Param("limit") int limit
			);

//	@Query("FROM Publications WHERE updated_state = ?1 AND doc_type = ?2  LIMIT ?3")
//	public Optional<Publications> listPublicationByTypeState(String state,String type, int limit);
}
