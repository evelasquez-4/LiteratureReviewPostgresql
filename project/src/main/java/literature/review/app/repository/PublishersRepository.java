package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Publishers;

@RepositoryRestResource
public interface PublishersRepository extends JpaRepository<Publishers, Long> 
{
	@Query("FROM Publishers WHERE id = ?1")
	Optional<Publishers> findById(Long id);
	
	@Query("FROM Publishers WHERE LOWER(description) = ?1")
	Optional<Publishers> getPublisherByDescription(String description);
	
}
