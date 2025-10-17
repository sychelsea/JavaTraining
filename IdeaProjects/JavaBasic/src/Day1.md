
# Mistakes and Missing Points

**Chelsea Shi**

**Topic:** Java Basics (Singleton, Factory Pattern, Exceptions, Collections)

**Date:** Oct 14, 2025

---

### (1) Mistakes

* JVM memory structure: forgot the name **native method stack**.
* Singleton (eager loading): forgot to remove the redundant `new` inside the getter method.
* `public class PaymentFactory throws IllegalArgumentException` — invalid syntax, `throws` must appear on a method, not a class.
* The explanation of **auto-boxing** of primitive types is not clear.
  * Auto-boxing: automatic conversion from a primitive type to its wrapper class (e.g., int → Integer).
  * Unboxing: automatic conversion from a wrapper class back to a primitive type (e.g., Integer → int).

The uploaded recording was my second one. The first one was interrupted and stopped recording. I made more mistakes in the first time:

* Stated that static block executes in heap; actually runs during **class loading** in the **method area**.
* Singleton constructor not declared **private**.
* Field declaration order should be consistent: `private static final`.
* Used `extends` instead of `implements` for interface implementation (`CreditCard`, `Paypal`).
* Missing parentheses in constructors: `new CreditCard()` / `new Paypal()`.
* Used `IOException`; better to use **IllegalArgumentException** in factory method.
* Primitive types incomplete — missed `long` and `float`.
* Did not clearly differentiate compile-time vs runtime exceptions:

    * Checked: must be handled at compile time (e.g., `IOException`).
    * Unchecked: subclasses of `RuntimeException` (e.g., `NullPointerException`, `IndexOutOfBoundsException`).


### (2) Missing Points

After reviewing my video, I realized I missed several important conceptual points discussed in class:

1. **Class Template Types** — In Java, a class template can be one of the following:

    * `class`, `interface`, `abstract class`, `enum`, or `annotation (@interface)`

2. **Interface vs. Abstract Class**

    * An interface defines contracts without implementation (all methods are implicitly `public abstract`).
    * An abstract class can have both abstract and concrete methods and can maintain state through fields.
    * A class can implement multiple interfaces but only extend one abstract class.

3. **Final, Finally, and Finalize**

    * `final`: prevents modification (on variables), inheritance (on classes), or overriding (on methods).
    * `finally`: executes after `try–catch` blocks.
    * `finalize()`: called by the garbage collector before object destruction (deprecated after Java 9).

4. **Final Methods**

    * A `final` method cannot be overridden by subclasses, which ensures consistent behavior across inheritance hierarchies.

    * **Overriding vs. Overloading**

      * *Overriding*: occurs between classes, requires the same signature, and is resolved at **runtime** (runtime polymorphism).
      * *Overloading*: occurs within the same class, uses different parameter lists, and is resolved at **compile time**.

6. **Final Class**

    * A class declared as `final` cannot be extended, which helps maintain immutability and prevents subclass modification (e.g., `String` class).

7. **Steps to Create an Immutable Class**

    * Declare the class as `final`.
    * Make all fields `private` and `final`.
    * Do not provide any setter methods.
    * Provide a parameterized constructor that initializes all fields.
    * If a field refers to a mutable object, perform a **deep copy** both in the constructor and in getter methods to prevent external modification.