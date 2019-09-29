package app.literature.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import app.literature.review.repository.DblpDocumentRepository;


@SpringBootApplication
//@ComponentScan(basePackages="app.literature.review.controller,app.literature.review.service,app.literature.review.model")
////@ComponentScan({"app.literature.review.controller",
////	"app.literature.review.model",
////	"app.literature.review.service"})
@EnableMongoRepositories(basePackageClasses = DblpDocumentRepository.class)
@EnableAutoConfiguration
public class LiteratureReviewApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiteratureReviewApplication.class, args);
	}

}