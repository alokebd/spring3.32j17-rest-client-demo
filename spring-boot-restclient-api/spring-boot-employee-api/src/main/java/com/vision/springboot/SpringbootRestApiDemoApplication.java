package com.vision.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages="com.vision.springboot.controller, com.vision.springboot.service")
public class SpringbootRestApiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootRestApiDemoApplication.class, args);
	}

}
