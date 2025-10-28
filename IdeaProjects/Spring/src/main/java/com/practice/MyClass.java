package com.practice;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// SOLID

InventoryManager implmenet UserService
{
    InventoryService
            CreditHistoryService
}

BuyerService implmenet UserService {
    PaymentService
            ProfileService

}
interface UserService {
    PaymentService
    ProfileService
    EmailService

            ....
    *??? Beans injected ??? -> 7

}

public class MyClass {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Singleton bean = ctx.getBean(Singleton.class);
        try {
            bean.getInstance(); // will throw NPE

        } catch (Exception ignored) {}
        ctx.close();
    }
}



class MyClass1 {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Singleton bean = ctx.getBean(Singleton.class);
        try {
            bean.getInstance(); // will throw NPE

        } catch (Exception ignored) {}
        ctx.close();
    }
}


class MyClass2 {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Singleton bean = ctx.getBean(Singleton.class);
        try {
            bean.getInstance(); // will throw NPE

        } catch (Exception ignored) {}
        ctx.close();
    }
}

class MyClass3 {
    public static void main(String[] args) {
        var ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        Singleton bean = ctx.getBean(Singleton.class);
        try {
            bean.getInstance(); // will throw NPE

        } catch (Exception ignored) {}
        ctx.close();
    }
}
