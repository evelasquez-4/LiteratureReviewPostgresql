package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.AuthorPublications;
import literature.review.app.model.Authors;

@RepositoryRestResource
public interface AuthorPublicationsRepository extends JpaRepository<AuthorPublications, Long> {

	@Query(
	value = " SELECT au.* FROM slr.author_publications "
			+" INNER JOIN slr.authors au  ON au.id = ap.author_id "+
			" WHERE ap.herarchy = 1 AND ap.publication_id = :publication_id"
			,nativeQuery = true)
	public Optional<Authors> findAuthorByPublication(@Param("publication_id") int id);
}
