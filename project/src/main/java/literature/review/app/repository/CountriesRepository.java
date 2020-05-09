package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


import literature.review.app.model.Countries;

@RepositoryRestResource
public interface CountriesRepository extends JpaRepository<Countries,Long> {
	
	@Query("FROM Countries WHERE country_name = ?1")
	public Optional<Countries> findCountryByName(String name);

	@Query("FROM Countries WHERE code = ?1")
	public Optional<Countries> findCountryByCode(String code);

}
 