package com.example.collab_desk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CollabDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollabDeskApplication.class, args);
	}

}