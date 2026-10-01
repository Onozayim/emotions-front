package com.emotions.emotions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class EmotionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmotionsApplication.class, args);
	}

}
