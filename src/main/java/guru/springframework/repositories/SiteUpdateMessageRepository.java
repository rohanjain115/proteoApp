package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import guru.springframework.domain.SiteUpdateMessage;

@RepositoryRestResource(exported=true,path="/message")
public interface SiteUpdateMessageRepository extends CrudRepository<SiteUpdateMessage, Long> {
	
	List<SiteUpdateMessage> findAll() ;

}
