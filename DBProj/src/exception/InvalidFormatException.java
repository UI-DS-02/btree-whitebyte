package exception;

public class InvalidFormatException extends Exception {
    public InvalidFormatException(String msg) {
        super(msg);
    }

    public InvalidFormatException() {
        super("input format not valid.");
    }
}
