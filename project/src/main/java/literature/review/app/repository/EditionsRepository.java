package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Editions;



@RepositoryRestResource
public interface EditionsRepository extends JpaRepository<Editions, Long> {

	
	@Query("FROM Editions WHERE LOWER(description) = ?1")
	public Optional<Editions> findByDescription(String description);
}
