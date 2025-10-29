package com.practice2;

import org.springframework.stereotype.Component;

@Component
public class SingletonFacade {

    private final Singleton singleton;

    public SingletonFacade(Singleton singleton) {
        this.singleton = singleton;
    }

    // call the functional logics in singleton
    public void work() {
        // no try/catch, handle the exception via AOP
        singleton.throwException();
    }
}
