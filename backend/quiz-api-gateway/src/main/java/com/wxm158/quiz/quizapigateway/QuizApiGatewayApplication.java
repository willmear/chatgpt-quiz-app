package com.wxm158.quiz.quizapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
public class QuizApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizApiGatewayApplication.class, args);
	}

}
