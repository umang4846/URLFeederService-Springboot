package com.umang.springboot.urlfeederservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.umang.springboot.urlfeederservice.dao")
public class UrlFeederServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UrlFeederServiceApplication.class, args);
    }

}
