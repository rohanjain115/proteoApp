package guru.springframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import guru.springframework.domain.User;
import guru.springframework.repositories.UserRepository;

@SpringBootApplication
@ComponentScan(basePackages = {"guru.springframework","it.ozimov.springboot"})
@EnableAsync
public class SpringBootWebApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebApplication.class);
	
    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
    /*
    @Bean
	public CommandLineRunner demo(UserRepository userRepository) {
		return (args) -> {
			
			userRepository.save(new User("Vishal1","Khatri1","vishalsarkar20501@gmail.com","vishal1","vishal1","ADMIN"));
			userRepository.save(new User("Vishal2","Khatri2","vishalsarkar20502@gmail.com","vishal2","vishal2","ADMIN"));
			userRepository.save(new User("Vishal3","Khatri3","vishalsarkar20503@gmail.com","vishal3","vishal3","ADMIN"));
			
			// fetch all customers
			log.info("Persons found with findAll():");
			log.info("-------------------------------");
			for (User person: userRepository.findAll()) {
				log.info(person.toString());
			}
            log.info("");

			// fetch an individual customer by ID
			User person = userRepository.findOne(1L);
			log.info("Person found with findOne(1L):");
			log.info("--------------------------------");
			log.info(person.toString());
            log.info("");

			// fetch customers by last name
			log.info("Person found with findByLastName('Bauer'):");
			log.info("--------------------------------------------");
			for (User khatri3 : userRepository.findByLastName("Khatri3")) {
				log.info(khatri3.toString());
			}
            log.info("");
		};
	}*/
}
