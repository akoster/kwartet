package nl.nuggit.kwartet.controller;

import java.security.Principal;

import nl.nuggit.kwartet.exception.NameTakenException;
import nl.nuggit.kwartet.exception.PlayerNotFoundException;
import nl.nuggit.kwartet.model.Game;
import nl.nuggit.kwartet.model.Message;
import nl.nuggit.kwartet.model.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    private SimpMessagingTemplate template;
    private Game game;

    public WebsocketController(SimpMessagingTemplate template, Game game) {
        this.template = template;
        this.game = game;
    }

    @MessageMapping("/joinGame")
    public void joinGame(String name, Principal principal) {
        Player player = new Player();
        player.setName(name);
        player.setId(principal.getName());
        try {
            game.join(player);
            this.template.convertAndSend("/topic/messages", new Message(player.getName() + " doet mee"));
        } catch (NameTakenException e) {
            this.template.convertAndSendToUser(player.getId(), "/topic/messages",
                    new Message(String.format("De naam '%s' is al bezet", name)));
        }
    }

    @MessageMapping("/startGame")
    public void startGame(String body, Principal principal) {
        try {
            Player gameStarter = game.findPlayerById(principal.getName());
            game.start();
            this.template.convertAndSend("/topic/messages", new Message("Spel gestart door " + gameStarter.getName()));

            for (Player player : game.getPlayers()) {
                this.template.convertAndSendToUser(player.getId(), "/topic/player", player);
            }

            this.template.convertAndSendToUser(game.getCurrentPlayer().getId(), "/topic/messages",
                    new Message("Jij mag beginnen!"));
        } catch (PlayerNotFoundException e) {
            this.template.convertAndSendToUser(principal.getName(), "/topic", new Message("Kon geen speler vinden"));
        }
    }

}
