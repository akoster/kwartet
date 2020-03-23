package nl.nuggit.kwartet.exception;

public class CannotJoinGameException extends RuntimeException {
    public CannotJoinGameException() {
        super("The game can currently not be joined");
    }
}
