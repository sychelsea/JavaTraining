package com.practice;

import com.practice.model.User;
import com.practice.service.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple concurrency demo: two threads update the same User concurrently
 * to demonstrate optimistic and pessimistic locking behavior.
 */

// uncomment it to run demo
//@SpringBootApplication(scanBasePackages = "com.practice")
public class ConcurrencyDemo implements CommandLineRunner {

    private final UserServiceImpl userService;

    public ConcurrencyDemo(UserServiceImpl userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyDemo.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Prepare a user in DB first
        User user = new User();
        user.setName("Chelsea");
        user.setEmail("Init");
        userService.createUser(user);

        System.out.println("Created user id=" + user.getId());

        // Run the two-thread demo
        runOptimisticLockDemo(user.getId());
        runPessimisticLockDemo(user.getId());
    }

    private void runOptimisticLockDemo(long userId) throws Exception {
        System.out.println("\n=== Optimistic Lock Demo ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        Runnable t1 = () -> {
            try {
                latch.await();
                User info = new User();
                info.setName("User-A");
                info.setEmail("P1");
                userService.updateUserWithOptimisticLock(userId, info);
                System.out.println("Thread-1: success");
            } catch (Exception e) {
                System.out.println("Thread-1: " + e.getClass().getSimpleName());
            }
        };

        Runnable t2 = () -> {
            try {
                latch.await();
                User info = new User();
                info.setName("User-B");
                info.setEmail("P2");
                userService.updateUserWithOptimisticLock(userId, info);
                System.out.println("Thread-2: success");
            } catch (Exception e) {
                System.out.println("Thread-2: " + e.getClass().getSimpleName());
            }
        };

        pool.submit(t1);
        pool.submit(t2);
        latch.countDown();  // Start both threads simultaneously
        pool.shutdown();

        Thread.sleep(3000); // Wait a bit to see results
    }

    private void runPessimisticLockDemo(long userId) throws Exception {
        System.out.println("\n=== Pessimistic Lock Demo ===");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        Runnable t1 = () -> {
            try {
                latch.await();
                User info = new User();
                info.setName("User-C");
                info.setEmail("PX");
                // Hold the lock for 5 seconds
                userService.updateUserWithPessimisticLock(userId, info, 5000);
                System.out.println("Thread-1: finished after 5s");
            } catch (Exception e) {
                System.out.println("Thread-1: " + e.getClass().getSimpleName());
            }
        };

        Runnable t2 = () -> {
            try {
                latch.await();
                long start = System.currentTimeMillis();
                User info = new User();
                info.setName("User-D");
                info.setEmail("PY");
                userService.updateUserWithPessimisticLock(userId, info, 0);
                long elapsed = System.currentTimeMillis() - start;
                System.out.println("Thread-2: finished, elapsed=" + elapsed + "ms");
            } catch (Exception e) {
                System.out.println("Thread-2: " + e.getClass().getSimpleName());
            }
        };

        pool.submit(t1);
        pool.submit(t2);
        latch.countDown();
        pool.shutdown();

        Thread.sleep(7000); // Wait enough time for both threads to finish
        System.out.println("=== Demo Finished ===");
    }
}
