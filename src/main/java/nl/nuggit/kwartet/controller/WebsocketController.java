package nl.nuggit.kwartet.controller;

import java.security.Principal;

import nl.nuggit.kwartet.exception.NameTakenException;
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
            this.template.convertAndSend("/topic", new Message(player.getName() + " joined"));
        } catch (NameTakenException e) {
            this.template.convertAndSendToUser(player.getId(), "/topic",
                    new Message(String.format("De naam '%s' is al bezet", name)));
        }
    }

    @MessageMapping("/startGame")
    public void startGame(String body, Principal principal) {
        Player player = game.playerById(principal.getName());
        this.template.convertAndSend("/topic", new Message("Spel gestart door " + player.getName()));
        Player startPlayer = game.randomPlayer();
        this.template.convertAndSendToUser(startPlayer.getId(), "/topic", new Message("Jij mag beginnen!"));
    }

}
