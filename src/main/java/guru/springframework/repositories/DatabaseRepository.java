package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import guru.springframework.domain.Lab;
import guru.springframework.domain.LabDatabase;


public interface DatabaseRepository extends PagingAndSortingRepository<LabDatabase, Long>{
	List<LabDatabase> findAll();
	List<LabDatabase> findByLab(Lab lab);
}
