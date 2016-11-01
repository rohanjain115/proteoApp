package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import guru.springframework.domain.Source;


public interface SourceRepository extends CrudRepository<Source, Integer>{
	Source findBySourceName(String sourceName);
	List<Source> findAll();
}
	