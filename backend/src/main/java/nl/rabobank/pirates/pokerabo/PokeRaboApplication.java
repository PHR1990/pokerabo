package nl.rabobank.pirates.pokerabo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("nl.rabobank")
@EnableJpaRepositories("nl.rabobank.pirates")
@EntityScan("nl.rabobank.pirates.domain")
public class PokeRaboApplication {

	public static void main(String[] args) {
		SpringApplication.run(PokeRaboApplication.class, args);
	}

}
