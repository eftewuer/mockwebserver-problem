package com.eft.mockwebserverproblem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MockwebserverProblemApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockwebserverProblemApplication.class, args);
	}

	@Bean
	WebClient webClient() {
		String baseUrl = "http://127.0.0.1:59000";
		return WebClient.create(baseUrl);
	}
}
