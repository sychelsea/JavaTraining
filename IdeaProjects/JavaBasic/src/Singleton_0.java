/*
 * access modifier: private, public, default, protected
 * static: field, method, class, block
 * class template vs. instance
 * JVM: stack, heap, PC register, method area, native method stack (c/c++ system call API)
 */

// lazy loading
public class Singleton {
    private static Singleton instance;
    private Singleton() {}
    public static Singleton getinstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}

// eager loading
public class Singleton {
    private static final Singleton instance = new Singleton();
    private Singleton() {}
    public static Singleton getinstance() {
        return instance;
    }
}
