package com.practice2;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MyClass {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        // spring will scan and inject the singleton bean (in AppConfig) into SingleFacade's constructor.
        var facade = ctx.getBean(SingletonFacade.class);

        // no try/catch.
        facade.work();

        ctx.close();
    }
}
