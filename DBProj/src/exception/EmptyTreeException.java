package exception;

public class EmptyTreeException extends Exception {
    public EmptyTreeException(String msg) {
        super(msg);
    }

    public EmptyTreeException() {
        super("tree is currently empty");
    }
}
