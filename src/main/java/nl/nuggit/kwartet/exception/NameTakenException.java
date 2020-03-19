package nl.nuggit.kwartet.exception;

public class NameTakenException extends RuntimeException {
    public NameTakenException() {
        super("Name is already taken");
    }
}
