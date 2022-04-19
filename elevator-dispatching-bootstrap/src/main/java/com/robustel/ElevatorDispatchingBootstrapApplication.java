package com.robustel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.robustel")
public class ElevatorDispatchingBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElevatorDispatchingBootstrapApplication.class, args);
    }

}
