package literature.review.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.Departments;


@RepositoryRestResource
public interface DepartmentsRepository extends JpaRepository<Departments, Long>{

	
	@Query(value = "SELECT dep.* "+ 
			"FROM slr.countries cou " + 
			"INNER JOIN slr.institutions ins ON ins.country_id = cou.id " + 
			"INNER JOIN slr.departments dep ON dep.institution_id = ins.id " + 
			"WHERE cou.code = :code AND dep.position=:state AND dep.validate = true  LIMIT :limit " 
			,nativeQuery = true)
	public List<Departments> listDepartmentsByInsitution(@Param("code") String code,
			@Param("limit") Integer limit, @Param("state") String state);
}
