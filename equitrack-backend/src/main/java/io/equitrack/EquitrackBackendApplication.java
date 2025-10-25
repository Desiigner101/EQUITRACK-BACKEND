package io.equitrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EquitrackBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EquitrackBackendApplication.class, args);
	}

}
