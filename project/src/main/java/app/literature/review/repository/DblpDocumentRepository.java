package app.literature.review.repository;




import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import app.literature.review.model.DblpDocument;

@Repository
public interface DblpDocumentRepository extends MongoRepository<DblpDocument, String>
{
} 
