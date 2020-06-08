package com.magdenkov.cloudstatsengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudStatsEngineApplication {
// > mvn clean spring-boot:run
// >./mvnw.cmd clean compile spring-boot:run
	public static void main(String[] args) {
		SpringApplication.run(CloudStatsEngineApplication.class, args);
	}

}
