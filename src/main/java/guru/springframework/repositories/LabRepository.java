package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import guru.springframework.domain.Lab;

public interface LabRepository extends CrudRepository<Lab, Long> {
	
	List<Lab> findAll() ;
	
	List<Lab> findByApproved(boolean approved);

}
