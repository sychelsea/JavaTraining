package com.practice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MyExceptionAspect {

    // pointcut: any method in com.practice.Singleton that throws @After @AfterReturning @Before
    @AfterThrowing(pointcut = "execution(* com.practice.Singleton.*(..))", throwing = "ex")
    public void handleException(JoinPoint jp, Throwable ex) {
        System.out.println("[AOP] Exception caught: " + ex.getMessage() +
                " in method " + jp.getSignature().toShortString());
    }
}

/**
 *
 *
 * ProxyClass extends Singleton {
 *
 *
 *     Wrapeer() {
 *
 *         // before
 *         LOGICS
 *         MYOWNWRPPER() {
 *              (STATIC)getInstance();
 *         }

 *
 *         // after
 *         LOGICS
 *     }
 *
 *
 *
 * }
 *
 *
 * aws S3 static Client.getObject()
 *
 * public WrapperClassS3{
 *
 *
 *     public void getObjectWrapper() {
 *
 *         Client.getObject()
 *
 *     }
 *
 * }
 *
 *
 *
 *
 *
 */