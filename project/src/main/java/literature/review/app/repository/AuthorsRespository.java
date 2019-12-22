package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Authors;

@RepositoryRestResource
public interface AuthorsRespository extends JpaRepository<Authors, Long> 
{
	@Query("FROM Authors WHERE id = ?1")
	public Optional<Authors> findById(Long id);
	
	@Query("FROM Authors WHERE LOWER(names) = ?1")
	public Optional<Authors> findByNames(String names);
}
