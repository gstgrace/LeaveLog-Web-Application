package com.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the LeaveLog application.
 * This class initializes and configures the Spring Boot application,
 * including scheduling capabilities and entity scanning for JPA entities.
 */
@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.lms.models")
public class LeaveManagementSystemApplication {
	/**
	 * Main method which serves as the entry point for the Spring Boot application.
	 * This method launches the Spring Boot application by initializing the Spring context,
	 * setting up the application, and starting the embedded web server.
	 *
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(LeaveManagementSystemApplication.class, args);
	}

}
