package com.wxm158.quiz.quizserviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class QuizServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizServiceRegistryApplication.class, args);
	}

}
