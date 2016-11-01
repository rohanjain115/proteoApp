package guru.springframework.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import guru.springframework.domain.DtaFileDetails;

public interface DtaFileDetailsRepository extends PagingAndSortingRepository<DtaFileDetails, Long>{

}
