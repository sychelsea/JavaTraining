package com.practice;

import org.springframework.stereotype.Component;

@Component
public class Singleton {
    private Singleton instance;

    public Singleton getInstance() {
        if (instance == null) {
            // purposely throw to let AOP handle
            throw new NullPointerException("instance is null");
            // instance = new Singleton(); // commented as in the skeleton
        }
        return instance;
    }

    public void throwException() {
        new RuntimeException();
    }

}