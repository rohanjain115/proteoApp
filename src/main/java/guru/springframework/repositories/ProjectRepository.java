package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.domain.Project;
import guru.springframework.domain.User;


public interface ProjectRepository extends PagingAndSortingRepository<Project, Long>{
	List<Project> findAll();
	List<Project> findByUser(User user);
	Project findOne(Long id);
	
	@Modifying
	@Transactional	
	@Query("update Project p set p.archiveStatus = ?1 where p.project_id = ?2")
	int setIsArchiveFor(String archiveStatus, long project_id);	
	
	@Query("SELECT distinct p FROM Project p join p.users u WHERE u.username=:userName and p.archiveStatus=:archiveStatus")
	List<Project> findAllProjectsByUserAndArchiveStatus(@Param("userName")String userName, @Param("archiveStatus")String archiveStatus);
}
