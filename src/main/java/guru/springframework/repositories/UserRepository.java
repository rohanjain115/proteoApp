package guru.springframework.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import guru.springframework.domain.User;


public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	List<User> findByLastName(@Param("name") String name);
	User findByUsernameOrEmail(@Param("username") String username,@Param("email") String email);
	User findByUsername(@Param("username") String username);
	List<User> findAll();
	
	
}