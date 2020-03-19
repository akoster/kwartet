package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;

import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.exception.NotEnoughPlayersException;
import nl.nuggit.kwartet.exception.PlayerNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class Game {

    private StringUtil stringUtil;
    private List<Player> players = new ArrayList<>();
    private State state = State.JOINING;

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

    private boolean isNameTaken(Player player) {
        return players.stream().anyMatch(p -> p.getName().equals(player.getName()));
    }

    public Player randomPlayer() {
        int playerCount = players.size();
        if (playerCount == 0) {
            throw new NotEnoughPlayersException();
        }
        int playerIndex = (int) (Math.random() * playerCount);
        return players.get(playerIndex);
    }

    public Player playerById(String id) {
        return players.stream()
                .filter(player -> player.getId().equals(id))
                .findAny()
                .orElseThrow(PlayerNotFoundException::new);
    }

    enum State {
        JOINING,
        STARTED,
        FINISHED
    }
}
