package com.kinetiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KinetiqBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KinetiqBackendApplication.class, args);
    }

}