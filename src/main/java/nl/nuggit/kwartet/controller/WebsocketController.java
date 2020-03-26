package nl.nuggit.kwartet.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import nl.nuggit.kwartet.exception.CannotJoinGameException;
import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.model.Ask;
import nl.nuggit.kwartet.model.Card;
import nl.nuggit.kwartet.model.Game;
import nl.nuggit.kwartet.model.Player;
import nl.nuggit.kwartet.model.ScoreBoard;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    private GameMessenger gameMessenger;
    private Game game;

    public WebsocketController(GameMessenger gameMessenger, Game game) {
        this.gameMessenger = gameMessenger;
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
        gameMessenger.sendTo(player, player.getId());
        updateScoreBoard();
    }

    private Player addNewPlayer(String name, Principal principal) {
        Player player = new Player(principal.getName());
        player.setName(name);
        try {
            game.join(player);
        } catch (NameTakenException e) {
            gameMessenger.nameAlreadyTaken(name, player);
        } catch (CannotJoinGameException e) {
            gameMessenger.cannotJoinGame(player);
        }
        return player;
    }

    @MessageMapping("/leave")
    public void leave(Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(player -> {
            if (game.getState() == Game.State.JOINING) {
                game.leave(player);
            } else {
                gameMessenger.gameHaltedBecausePlayerLeft(player);
                game.init();
            }
            updateScoreBoard();
        });
    }

    @MessageMapping("/start")
    public void start(Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(this::start);
    }

    private void start(Player player) {
        game.start();
        updatePlayers();
        gameMessenger.gameStarted(player, game.getCurrentPlayer());
    }

    @MessageMapping("/ask")
    public void ask(Ask ask, Principal principal) {
        game.findPlayerById(principal.getName()).ifPresent(player -> ask(ask, player));
    }

    private void ask(Ask ask, Player player) {
        game.findPlayerByName(ask.getOpponent()).ifPresent(opponent -> ask(ask, player, opponent));
    }

    private void ask(Ask ask, Player player, Player opponent) {
        String[] spectatorIds = getAllPlayerIdsExcept(player, opponent);
        gameMessenger.cardAsked(ask, player);
        Game.AskResult askResult = game.askCardFrom(player, ask.getCard(), opponent);
        if (askResult == Game.AskResult.OPPONENT_DOES_NOT_HAVE_CARD) {
            game.playerDrawsCard(player).ifPresent(card -> {
                gameMessenger.playerDrewCard(player, card);
            });
        }
        List<String> sets = game.removeSets(player);
        if (!sets.isEmpty()) {
            gameMessenger.playerScoredSets(player, sets);
        }
        updateScoreBoard();
        updatePlayers();
        handleResult(ask, player, opponent, spectatorIds, askResult);
    }


    private void handleResult(Ask ask, Player player, Player opponent, String[] spectatorIds,
            Game.AskResult askResult) {
        switch (askResult) {
        case CARD_DOES_NOT_EXIST:
            gameMessenger.playerAskedInvalidCard(player, opponent, spectatorIds);
            break;
        case PLAYER_HAS_NO_CARD_IN_SAME_SET:
            gameMessenger.playerHasNoCardInAskedSet(player, opponent, spectatorIds);
            break;
        case OPPONENT_HAS_CARD:
            gameMessenger.opponentGivesCard(ask, player, opponent, spectatorIds);
            break;
        case OPPONENT_DOES_NOT_HAVE_CARD:
            gameMessenger.opponentDoesNotHaveCard(ask, player, opponent, spectatorIds);
            break;
        default:
            throw new IllegalStateException("Cannot handle AskResult " + askResult);
        }
    }

    private void updateScoreBoard() {
        game.getPlayers().forEach(p -> gameMessenger.sendTo(new ScoreBoard(game.getPlayers()), p.getId()));
    }

    private void updatePlayers() {
        game.getPlayers().forEach(p -> gameMessenger.sendTo(p, p.getId()));
    }

    private String[] getAllPlayerIdsExcept(Player player1, Player player2) {
        return game.getPlayers().filter(p -> p != player1 && p != player2).map(Player::getId).toArray(String[]::new);
    }

}
