package com.networx.networx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetworxApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworxApplication.class, args);
	}

}