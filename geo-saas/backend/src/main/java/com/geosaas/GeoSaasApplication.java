package com.geosaas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GeoSaasApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeoSaasApplication.class, args);
    }
}
