package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Publications;



@RepositoryRestResource
public interface PublicationsRepository extends JpaRepository<Publications, Long> 
{
	@Query("FROM Publications WHERE LOWER(dblp_key) = ?1")
	public Optional<Publications> findPublicationByKey(String key);
}
