package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.domain.Experiment;
import guru.springframework.domain.Project;


public interface ExperimentRepository extends PagingAndSortingRepository<Experiment, Long>{
	List<Experiment> findAll();
	List<Experiment> findByProjectAndIsArchive(Project p, boolean isArchive);
	
	@Modifying
	@Transactional	
	@Query("update Experiment e set e.isArchive = ?1 where e.experiment_id = ?2")
	int setIsArchiveFor(boolean isArchive, long experiment_id);
	
	@Modifying
	@Transactional	
	@Query("update Experiment e set e.isArchive = ?1 where e.project = ?2")
	int setIsArchiveForProject(boolean isArchive, Project project);	
}
