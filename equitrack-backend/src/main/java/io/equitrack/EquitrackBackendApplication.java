package io.equitrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MAIN APPLICATION ENTRY POINT - The Heart of EquiTrack Backend
 *
 * This class boots up the entire Spring Boot application and enables critical features
 * It's the starting point that brings all your controllers, services, and configurations to life
 */
@EnableScheduling  // Enables Spring's scheduled task execution - allows @Scheduled methods to run automatically
@SpringBootApplication  // Core Spring Boot annotation that enables autoconfiguration, component scanning, and more
@EnableAsync  // Enables asynchronous method execution - allows @Async methods to run in background threads
public class EquitrackBackendApplication {

	/**
	 * MAIN METHOD - Application Entry Point
	 * This is the first method that runs when your application starts
	 * It bootstraps the entire Spring Boot application and starts the embedded web server
	 *
	 * @param args Command line arguments (rarely used in Spring Boot)
	 */
	public static void main(String[] args) {
		// Boots up Spring Boot application with this class as primary configuration
		// Starts embedded Tomcat server, loads all beans, and makes your API available
		SpringApplication.run(EquitrackBackendApplication.class, args);
	}
}