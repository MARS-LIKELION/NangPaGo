package com.mars.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mars.app", "com.mars.common"})
@EntityScan(basePackages = {"com.mars.app", "com.mars.common"})
public class NangPaGoApplication {

	public static void main(String[] args) {

		SpringApplication.run(NangPaGoApplication.class, args);
	}

}	

