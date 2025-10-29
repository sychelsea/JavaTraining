package com.practice2;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Bean;

@Configuration
@ComponentScan("com.practice2")
@EnableAspectJAutoProxy
class AppConfig {
    // Wrapper method of Singleton getter
    @Bean
    public Singleton singletonBean() {
        return Singleton.getInstance();
    }
}