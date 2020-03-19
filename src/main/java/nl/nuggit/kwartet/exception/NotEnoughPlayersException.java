package nl.nuggit.kwartet.exception;

public class NotEnoughPlayersException extends RuntimeException {
    public   NotEnoughPlayersException() {
        super("There are not enough players");
    }
}
