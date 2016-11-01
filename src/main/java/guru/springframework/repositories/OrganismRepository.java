package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import guru.springframework.domain.Organism;


public interface OrganismRepository extends CrudRepository<Organism, Integer>{
	Organism findByOrganismName(String organismName);
	List<Organism> findAll();	
}
