/*
 * Thread safety -> read & write problem
 * Two type of Lock?  pessimistic lock vs. optimistic lock
 * Synchronized
 *  - method, block (critical section)
 *
 * Lock
 *  - lock.lock() + try/catch/finally
 *  - Lock + Condition => thread execution order
 *    - thread priority vs. thread execution order
 *
 * Optimistic Lock
 *  - not locking before the modification + versioning
 *  - CAS: compare & swap
 *    - ABA problem
 *    - Time stamp: AtomicInterger/AtomicReference -> AtomicTimeStampedReference
 *
 * pessimistic lock - r&w, w&w
 * optimistic lock - r&r
 *   - monitoring, common pattern
 *
 * Singleton

 */