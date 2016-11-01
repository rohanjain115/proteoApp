package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import guru.springframework.domain.Instrument;

public interface InstrumentRepository extends CrudRepository<Instrument, Integer> {
	Instrument findByInstrumentName(String instrumentName);
	List<Instrument> findAll();
}
