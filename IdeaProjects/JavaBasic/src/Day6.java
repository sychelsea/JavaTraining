/*
 * Thread (creation, lifecyle, threadpool)
 *  - JVM: stack, heap, program counter, method area, native method stack
 *  - 6 stauts: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED
 *  - Deamon threads
 *  - thread, runnable, callable, threadpool
 *  - ThreadPool
 *       ThreadPoolSerive <- ExecutorService <- Executor <- Runnable
 *                                              Executors
 *  - 7 parameters:
 *      - corePoolSize
 *      - maxPoolSize
 *      - keepAliveTime
 *      - unit
 *      - workQueue
 *      - threadFactory
 *      - handler
 *
 * Thread safety (2 types of lock)
 *  - read & write prblem
 *  - pessimistic lock
 *      - sycronized keyword  vs. volatile
 *          - method, block statement
 *          - critical section
 *      - Lock
 *          - try/catch/finally
 *  - optimistic lock
 *     - no lock + versioning
 *     - CAS
 *          - ABA problem
 *          - AtomicStampedReference
 * Singleton
 */


public class Day6 {
    // lazy loading
    class Singleton {
        private static volatile Singleton instance;
        private Singleton() {}
        public static Singleton getInstace() {
            if (instance == null) {
                synchronized (Singleton.class) {
                    if (instance == null) {
                        instance = new Singleton();
                    }
                }
            }
            return instance;
        }
    }

    // eager loading
    class Singleton {
        private static final Singleton instance = new Singleton;
        private Singleton() {}
        public static Singleton getInstace() {
            return instance;
        }
    }
}

