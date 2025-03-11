package com.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.batch.util.Main;

@SpringBootApplication
//@ComponentScan("com.app.batch.controller")
public class SpringBootWebatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebatchApplication.class, args);
	}
 
@Override
public void run(String... args) throws Exception {
	 new Main().main(null);;
}
}
