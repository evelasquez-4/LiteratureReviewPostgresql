package literature.review.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import literature.review.app.model.BookChapters;

@RepositoryRestResource
public interface BookChaptersRepository extends JpaRepository<BookChapters, Long> {

}
