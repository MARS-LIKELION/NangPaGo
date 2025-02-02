package com.mars.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mars.app", "com.mars.common", "com.mars.admin"})
@EntityScan(basePackages = {"com.mars.app", "com.mars.common"})
public class NangPaGoApplication {

	public static void main(String[] args) {

		SpringApplication.run(NangPaGoApplication.class, args);
	}

}	

