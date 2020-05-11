package com.giraone.cdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The spring boot application class, that starts the app.
 */
@SpringBootApplication
public class CDCRelayApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Arguments passed to the app.
     */
    public static void main(String[] args) {
        SpringApplication.run(CDCRelayApplication.class, args);
    }
}
