package literature.review.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Books;



@RepositoryRestResource
public interface BooksRepository extends JpaRepository<Books, Long>{

}
