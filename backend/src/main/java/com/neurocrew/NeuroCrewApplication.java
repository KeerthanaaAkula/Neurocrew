package com.neurocrew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NeuroCrewApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeuroCrewApplication.class, args);
    }
}
