package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Journals;

@RepositoryRestResource
public interface JournalsRepository extends JpaRepository<Journals, Long> {

	@Query("FROM Journals WHERE LOWER(name) = ?1")
	public Optional<Journals> findByDescription(String description);
}
