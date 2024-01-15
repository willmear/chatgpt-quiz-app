package com.wxm158.quiz.quizuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class QuizUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizUserServiceApplication.class, args);
	}

}
