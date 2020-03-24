package nl.nuggit.kwartet.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.nuggit.kwartet.exception.CannotJoinGameException;
import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.model.Ask;
import nl.nuggit.kwartet.model.Card;
import nl.nuggit.kwartet.model.Deck;
import nl.nuggit.kwartet.model.Game;
import nl.nuggit.kwartet.model.Message;
import nl.nuggit.kwartet.model.Player;
import nl.nuggit.kwartet.model.PlayerNames;
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
        Optional<Player> existingPlayer = game.findPlayerById(principal.getName());
        if (existingPlayer.isPresent()) {
            player = existingPlayer.get();
            player.setName(name);
        } else {
            player = addNewPlayer(name, principal);
        }
        messenger.sendToUser(player.getId(), player);
        updatePlayerNames();
    }

    private void updatePlayerNames() {
        PlayerNames playerNames = new PlayerNames(game.getPlayers().map(Player::getName).collect(Collectors.toList()));
        game.getPlayers().forEach(p -> messenger.sendToUser(p.getId(), playerNames));
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
                messenger.sendToAll(new Message(Message.Type.MESSAGE,
                        String.format("Het spel kan niet verder gaan zonder %s", player.getName())));
                game.init();
            }
            updatePlayerNames();
        });
    }

    @MessageMapping("/start")
    public void start(Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(this::start);
    }

    private void start(Player player) {
        game.start();
        messenger.sendToAll(new Message(Message.Type.START, String.format("Spel gestart door %s", player.getName())));
        updatePlayers();
        messenger.sendToUser(game.getCurrentPlayer().getId(), new Message(Message.Type.YOUR_TURN, "Jij mag beginnen!"));
    }

    private void updatePlayers() {
        game.getPlayers().forEach(p -> messenger.sendToUser(p.getId(), p));
    }

    @MessageMapping("/ask")
    public void ask(Ask ask, Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(player -> ask(ask, player));
    }

    private void ask(Ask ask, Player player) {
        game.findPlayerByName(ask.getOpponent()).ifPresent(opponent -> ask(ask, player, opponent));
    }

    private void ask(Ask ask, Player player, Player opponent) {
        List<String> spectatorIds = getOtherPlayerIds(player, opponent);
        for (String spectatorId : spectatorIds) {
            messenger.sendToUser(spectatorId, new Message(
                    String.format("%s vraagt aan %s om %s", player.getName(), ask.getOpponent(), ask.getCard())));
        }
        Card askedCard = Card.fromDescription(ask.getCard());
        if (!Deck.isValid(askedCard)) {
            game.setCurrentPlayer(opponent);
            messenger.sendToAll(new Message(
                    String.format("%s vroeg een kaart die niet bestaat! Nu is %s aan de beurt", player.getName(),
                            opponent.getName())));
        }
        if (player.getCards().filter(card -> card.getTable().equals(askedCard.getTable())).findAny().isEmpty()) {
            game.setCurrentPlayer(opponent);
            messenger.sendToAll(new Message(
                    String.format("%s vroeg een kaart waar hij er geen van had! Nu is %s aan de beurt",
                            player.getName(), opponent.getName())));
        }
        boolean success = game.askCardFrom(player, ask.getCard(), opponent);
        updatePlayers();
        if (success) {
            cardAskedSuccessfully(ask, player, opponent, spectatorIds);
        } else {
            cardAskedUnsuccessfully(ask, player, opponent, spectatorIds);
        }
    }

    private void cardAskedSuccessfully(Ask ask, Player player, Player opponent, List<String> spectatorIds) {
        messenger.sendToUser(player.getId(), new Message(Message.Type.YOUR_TURN,
                String.format("Je hebt de %s van %s gekregen, je mag nog een keer", ask.getCard(),
                        opponent.getName())));
        messenger.sendToUser(opponent.getId(), new Message(
                String.format("%s heeft %s van jou gekregen en mag nog een keer", player.getName(), ask.getCard())));
        for (String spectatorId : spectatorIds) {
            messenger.sendToUser(spectatorId, new Message(
                    String.format("%s kreeg de %s van %s en mag nog een keer", player.getName(), ask.getCard(),
                            ask.getOpponent())));
        }
    }

    private void cardAskedUnsuccessfully(Ask ask, Player player, Player opponent, List<String> spectatorIds) {
        messenger.sendToUser(player.getId(), new Message(
                String.format("%s had de %s niet, je beurt is voorbij", opponent.getName(), ask.getCard())));
        messenger.sendToUser(opponent.getId(), new Message(Message.Type.YOUR_TURN,
                String.format("%s vroeg de %s maar die heb je niet, nu ben jij aan de beurt", player.getName(),
                        ask.getCard())));
        for (String spectatorId : spectatorIds) {
            messenger.sendToUser(spectatorId, new Message(
                    String.format("%1$s vroeg de %2$s aan %3$s maar die had hem niet. Nu is %3$s aan de beurt.",
                            player.getName(), ask.getCard(), ask.getOpponent())));
        }
    }

    private List<String> getOtherPlayerIds(Player player1, Player player2) {
        return game.getPlayers()
                .filter(p -> p != player1 && p != player2)
                .map(Player::getId)
                .collect(Collectors.toList());
    }

}
