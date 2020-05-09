package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Keywords;

@RepositoryRestResource
public interface KeywordsRepository extends JpaRepository<Keywords, Long> {

	@Query("FROM Keywords WHERE decription = ?1")
	public Optional<Keywords> findByDescription(String desc);
	
}
