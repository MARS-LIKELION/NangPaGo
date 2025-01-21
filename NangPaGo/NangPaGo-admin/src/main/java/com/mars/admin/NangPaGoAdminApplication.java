package com.mars.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mars.admin", "com.mars.common"})
@EntityScan(basePackages = {"com.mars.admin", "com.mars.common"})
@EnableJpaRepositories(basePackages = {"com.mars.common", "com.mars.admin"})
public class NangPaGoAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NangPaGoAdminApplication.class, args);
    }

}
