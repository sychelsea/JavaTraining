package com.practice2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccessAndExceptionAspect {

    @Around("execution(public * com.practice2.SingletonFacade.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Thread t = Thread.currentThread();
        System.out.println("[AOP] Thread[" + t.getName() + "-" + t.getId() + "] -> " + pjp.getSignature().toShortString());
        try {
            return pjp.proceed();
        } catch (Throwable ex) {
            System.out.println("[AOP] Handled " + ex.getClass().getSimpleName() +
                    " in " + pjp.getSignature().toShortString() + " -> " + ex.getMessage());
            return null;
        }
    }

}
