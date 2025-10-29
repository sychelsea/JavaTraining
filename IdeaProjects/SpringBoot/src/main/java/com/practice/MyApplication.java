package com.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Swagger/OpenAPI enabled
 * <a href="http://localhost:9091/swagger-ui/index.html">Swagger doc</a>
 * <a href="http://localhost:9091/v3/api-docs">OpenAPI link</a>
 */
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}