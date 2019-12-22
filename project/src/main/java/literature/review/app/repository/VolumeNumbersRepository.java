package literature.review.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import literature.review.app.model.VolumeNumbers;

@RepositoryRestResource
public interface VolumeNumbersRepository extends JpaRepository<VolumeNumbers, Long> {

	@Query("FROM VolumeNumbers WHERE volume =?1 AND number = ?2")
	public Optional<VolumeNumbers> findByVolumeNumber(Integer volume,Integer number);
	
	@Query("FROM VolumeNumbers WHERE volume =?1 AND number IS NULL")
	public Optional<VolumeNumbers> findByVolume(Integer volume);
	
	@Query("FROM VolumeNumbers WHERE number =?1 AND volume IS NULL")
	public Optional<VolumeNumbers> findByNumber(Integer number);
}
