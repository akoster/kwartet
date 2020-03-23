package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import nl.nuggit.kwartet.exception.CannotJoinGameException;
import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.exception.NotEnoughPlayersException;
import nl.nuggit.kwartet.exception.PlayerLeftDuringGameException;
import org.springframework.stereotype.Component;

@Component
public class Game {

    private static final int NUMBER_OF_CARDS_DEALT_AT_START = 5;

    private List<Player> players = new ArrayList<>();
    private State state = State.JOINING;
    private int currentPlayerIndex;
    private Deck deck;

    public void init() {
        players.clear();
        state = State.JOINING;
        currentPlayerIndex = 0;
        deck = null;
    }

    public void join(Player player) {
        if (state == State.JOINING) {
            if (findPlayerById(player.getId()).isPresent()) {
                throw new IllegalStateException("Player cannot join twice");
            }
            if (isNameTaken(player)) {
                throw new NameTakenException();
            }
            players.add(player);
            sortPlayers();
        } else {
            throw new CannotJoinGameException();
        }
    }

    private void sortPlayers() {
        players.sort(Comparator.comparingInt(p -> p.getId().hashCode()));
    }

    public State getState() {
        return state;
    }

    public void leave(Player player) {
        if (state == State.JOINING) {
            if (findPlayerById(player.getId()).isEmpty()) {
                throw new IllegalStateException("Unknown player cannot leave");
            }
            players.remove(player);
            sortPlayers();
        } else if (state == State.STARTED) {
            throw new PlayerLeftDuringGameException();
        }
    }

    public void start() {
        if (state != State.JOINING) {
            throw new IllegalStateException("Can only start game from state JOINING");
        }
        if (players.size() < 2) {
            throw new NotEnoughPlayersException();
        }
        state = State.STARTED;
        deck = new Deck();
        deal(NUMBER_OF_CARDS_DEALT_AT_START);
        currentPlayerIndex = 0;
    }

    public Optional<Player> findPlayerById(String id) {
        return players.stream().filter(player -> player.getId().equals(id)).findAny();
    }

    public Player getCurrentPlayer() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Game not started yet");
        }
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    private void deal(int numberOfCards) {
        for (Player player : players) {
            for (int i = 0; i < numberOfCards; i++) {
                player.getCards().add(deck.getRandomCard());
            }
        }
    }

    private boolean isNameTaken(Player player) {
        return players.stream().anyMatch(p -> p.getName().equals(player.getName()));
    }

    public enum State {
        JOINING,
        STARTED,
        FINISHED
    }
}
