package nl.nuggit.kwartet.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.nuggit.kwartet.exception.CannotJoinGameException;
import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.model.Game;
import nl.nuggit.kwartet.model.Message;
import nl.nuggit.kwartet.model.Player;
import nl.nuggit.kwartet.model.PlayerNames;
import nl.nuggit.kwartet.model.Turn;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    private Messenger messenger;
    private Game game;

    public WebsocketController(Messenger messenger, Game game) {
        this.messenger = messenger;
        this.game = game;
    }

    @MessageMapping("/join")
    public void join(String name, Principal principal) {
        Player player;
        Optional<Player> foundPlayer = game.findPlayerById(principal.getName());
        if (foundPlayer.isPresent()) {
            player = foundPlayer.get();
            player.setName(name);
        } else {
            player = addNewPlayer(name, principal);
        }
        messenger.sendToUser(player.getId(), player);
        updatePlayerNames();
    }

    private Player addNewPlayer(String name, Principal principal) {
        Player player = new Player(principal.getName());
        player.setName(name);
        try {
            game.join(player);
        } catch (NameTakenException e) {
            messenger.sendToUser(player.getId(), new Message(String.format("De naam '%s' is al bezet", name)));
        } catch (CannotJoinGameException e) {
            messenger.sendToUser(player.getId(), new Message("Je kunt op dit ogenblik niet meedoen"));
        }
        return player;
    }

    @MessageMapping("/leave")
    public void leave(Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(player -> {
            if (game.getState() == Game.State.JOINING) {
                game.leave(player);
            } else {
                messenger.send(new Message(Message.Type.MESSAGE,
                        String.format("Het spel kan niet verder gaan zonder %s", player.getName())));
                game.init();
            }
            updatePlayerNames();
        });
    }

    private void updatePlayerNames() {
        List<Player> players = game.getPlayers();
        PlayerNames playerNames = new PlayerNames(players.stream().map(Player::getName).collect(Collectors.toList()));
        for (Player p : players) {
            messenger.sendToUser(p.getId(), playerNames);
        }
    }

    @MessageMapping("/start")
    public void start(Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(this::start);
    }

    private void start(Player player) {
        game.start();
        messenger.send(new Message(Message.Type.START, String.format("Spel gestart door %s", player.getName())));
        updateEachPlayer();
        messenger.sendToUser(game.getCurrentPlayer().getId(), new Message(Message.Type.YOUR_TURN, "Jij mag beginnen!"));
    }

    private void updateEachPlayer() {
        for (Player p : game.getPlayers()) {
            messenger.sendToUser(p.getId(), p);
        }
    }

    @MessageMapping("/ask")
    public void ask(Turn turn, Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(player -> ask(turn, player));
    }

    private void ask(Turn turn, Player player) {
        messenger.send(new Message(
                String.format("%s vraagt aan %s om %s", player.getName(), turn.getOpponent(), turn.getCard())));
    }
}
