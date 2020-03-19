package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;

import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.exception.PlayerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class Game {

    private static final int NUMBER_OF_CARDS_DEALT_AT_START = 5;

    private StringUtil stringUtil;
    private List<Player> players = new ArrayList<>();
    private State state = State.JOINING;
    private int currentPlayerIndex;
    private Deck deck;

    public Game(StringUtil stringUtil) {
        this.stringUtil = stringUtil;
    }

    public void join(Player player) {
        if (state == State.JOINING) {
            if (isNameTaken(player)) {
                throw new NameTakenException();
            }
            players.add(player);
        }
    }

    public void start() {
        if (state != State.JOINING) {
            throw new IllegalStateException("Can only start game from state JOINING");
        }
        if (players.size() < 2) {
            throw new IllegalStateException("Not enough players");
        }
        state = State.STARTED;
        deck = new Deck();
        deal(NUMBER_OF_CARDS_DEALT_AT_START);
        currentPlayerIndex = (int) (Math.random() * players.size());
    }

    public Player findPlayerById(String id) {
        return players.stream()
                .filter(player -> player.getId().equals(id))
                .findAny()
                .orElseThrow(PlayerNotFoundException::new);
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

    enum State {
        JOINING,
        STARTED,
        FINISHED
    }
}
