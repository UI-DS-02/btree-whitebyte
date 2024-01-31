package exception;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException(String msg) {
        super(msg);
    }

    public KeyNotFoundException() {
        super("key not found.");
    }
}
