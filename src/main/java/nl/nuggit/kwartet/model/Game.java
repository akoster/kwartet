package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public Optional<Player> findPlayerByName(String name) {
        return players.stream().filter(player -> player.getName().equals(name)).findAny();
    }

    public Player getCurrentPlayer() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Game not started yet");
        }
        return players.get(currentPlayerIndex);
    }

    public void setCurrentPlayer(Player player) {
        if (state != State.STARTED) {
            throw new IllegalStateException("Game not started yet");
        }
        currentPlayerIndex = players.indexOf(player);
    }

    public Stream<Player> getPlayers() {
        return players.stream();
    }

    private void deal(int numberOfCards) {
        for (Player player : players) {
            for (int i = 0; i < numberOfCards; i++) {
                player.addCard(deck.drawRandomCard().orElseThrow(() -> new IllegalStateException("Not enough cards")));
            }
        }
    }

    private boolean isNameTaken(Player player) {
        return players.stream().anyMatch(p -> p.getName().equals(player.getName()));
    }

    public boolean askCardFrom(Player player, String cardDescription, Player opponent) {
        Optional<Card> opponentCard = findCard(cardDescription, opponent);
        boolean success = opponentCard.isPresent();
        if (success) {
            opponentGivesCard(player, opponent, opponentCard.get());
        } else {
            opponentDoesNotHaveCard(player, opponent);
        }
        return success;
    }

    private Optional<Card> findCard(String cardDescription, Player opponent) {
        return opponent.getCards().filter(card -> card.getDescription().equals(cardDescription)).findAny();
    }

    private void opponentGivesCard(Player player, Player opponent, Card card) {
        opponent.removeCard(card);
        player.addCard(card);
    }

    private void opponentDoesNotHaveCard(Player player, Player opponent) {
        deck.drawRandomCard().ifPresent(player::addCard);
        currentPlayerIndex = players.indexOf(opponent);
    }

    public enum State {
        JOINING,
        STARTED,
        FINISHED
    }
}
