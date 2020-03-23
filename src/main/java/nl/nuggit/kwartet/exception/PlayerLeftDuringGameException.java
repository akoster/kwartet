package nl.nuggit.kwartet.exception;

public class PlayerLeftDuringGameException extends RuntimeException {
    public PlayerLeftDuringGameException() {
        super("Player left during game");
    }
}
