package com.shotFormLetter.sFL;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SFlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SFlApplication.class, args);
	}

}
