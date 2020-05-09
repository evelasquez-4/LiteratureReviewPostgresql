package literature.review.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Institutions;

@RepositoryRestResource
public interface InstitutionsRepository  extends JpaRepository<Institutions, Long>{

	@Query("FROM Institutions WHERE country_id = ?1")
	public List<Institutions> findByCountryId(Integer countryId);
	
	@Query("FROM Institutions WHERE description = ?1")
	public List<Institutions> findInstitutionByDescription(String description);
	
	@Query("FROM Institutions WHERE updated = false AND link is not null")
	public List<Institutions> parseMembersInstitution(Integer country_id, Integer limit);
	
	@Query(value = "SELECT ins.*,cou.code,cou.country_name " + 
			"FROM slr.institutions  ins " + 
			"INNER JOIN slr.countries cou ON cou.id = ins.country_id " + 
			"WHERE cou.code = :code  AND ins.link IS NOT null AND ins.updated = :updated " + 
			"LIMIT :limit",nativeQuery = true)
	public List<Institutions> findResearchGateInstitutionsByCountryCode(@Param("code")String code,
			@Param("limit") Integer limit,@Param("updated") boolean updated );
}
