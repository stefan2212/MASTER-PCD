package handler;

public class Handler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("Unhandler exception caught");
    }
}
