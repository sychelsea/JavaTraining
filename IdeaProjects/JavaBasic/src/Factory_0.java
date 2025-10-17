/*
 * credit card, paypal
 *
 * double vs. Double
 * primitive types: byte, short, int, long, char, boolean, float, double
 * Double, Integer
 * List<Integer>
 * Exceptions: compile-time vs. runtime
 */

interface Payment {
    void pay(double amount);
}

class CreditCard implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("Credit card has been paid for " + amount);
    }
}

class PaypalPayment implements Payment {
    @Override
    public void pay(double amount) {
        System.out.println("PayPal has been paid for " + amount);
    }
}

class PaymentFactory{
    public static Payment getPayment(String type) throws IllegalArgumentException {
        switch (type) {
            case "CreditCard": return new CreditCard();
            case "Paypal": return new PaypalPayment();
            default: throw new IllegalArgumentException("Invalid payment type");
        }
    }
}

public class PaymentService {
    public void pay(double amount, String type) {
        Payment payment = PaymentFactory.getPayment(type);
        payment.pay(amount);
    }
}