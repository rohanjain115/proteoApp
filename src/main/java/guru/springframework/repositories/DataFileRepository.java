package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import guru.springframework.domain.DataFile;
import guru.springframework.domain.Experiment;

public interface DataFileRepository extends PagingAndSortingRepository<DataFile, Long>{
	List<DataFile> findByExperiment(Experiment experiment);
}
