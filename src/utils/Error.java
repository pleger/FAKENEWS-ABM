package utils;

public class Error {

    public static void trigger(Object msg) {
        Console.error(msg);
        stopExecution();
    }

    public static void trigger(Object msg, Throwable ex) {
        Console.error(msg, ex);
        stopExecution();
    }

    private static void stopExecution() {
        Console.error("This execution has to stop. This is the current execution trace:",
                new IllegalStateException("Execution trace"));
        System.exit(1);
    }

    public static void setAssert(boolean test, Object msg) {
        if (!test) trigger(msg);
    }
}
