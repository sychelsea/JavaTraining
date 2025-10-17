// lazy loading
class Singleton {
    private static Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// eager loading
// steps to create a immutable class
class Singleton {
    private static final Singleton instance = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() {
        return instance;
    }
}

//========================

interface Payment {
    void pay(double amount);
}

class CreditCardPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Credit card has been payed!");
    }
}

class PaypalPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Payment has been payed!");
    }
}

public class PaymentFactory {
    public static Payment getPayment(String type) throws IllegalArgumentException {
        switch (type) {
            case "CreditCard": return new CreditCardPayment();
            case "Paypal": return new PaypalPayment();
            default: throw new IllegalArgumentException("Invalid payment type!");
        }
    }
}

public class PaymentService {
    public void pay(String type, double amount) {
        Payment payment = PaymentFactory.getPayment(type);
        payment.pay(amount);
    }
}

//=============================
/*
 * Collection
 *   - List
 *      - ArrayList
 *      - LinkedList
 *      - Vetor, Stack
 *   - Queue
 *      - PriorityQueue
 *      - Deque
 *         - ArrayDeque
 *   - Set
 *      - HashSet
 *      - TreeSet
 *      - LinkedHashSet
 * Map
 *   - HashMap
 *   - TreeMap
 *   - LinkedHashMap
 *   - HashTale, CuncurrentMap
 */

//==========================
/*
 * OOP
 * - Encapsulation
 * - inheritance
 * -
 * -
 */

//================================
/*
 * SOLID
 * Single responsibility
 * Open / closed
 * Liskov Substitution
 * Interface segregation
 * Dependency inversion
 */