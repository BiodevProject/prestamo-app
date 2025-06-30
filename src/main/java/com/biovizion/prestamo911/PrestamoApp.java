package com.biovizion.prestamo911;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrestamoApp {
	public static void main(String[] args) {
		SpringApplication.run(PrestamoApp.class, args);
	}
}
