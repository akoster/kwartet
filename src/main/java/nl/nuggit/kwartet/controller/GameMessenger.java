package nl.nuggit.kwartet.controller;

import nl.nuggit.kwartet.model.Ask;
import nl.nuggit.kwartet.model.Message;
import nl.nuggit.kwartet.model.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GameMessenger extends Messenger {

    public GameMessenger(SimpMessagingTemplate template) {
        super(template);
    }

    public void gameStarted(Player playerWhoStartedGame, Player playerWithFirstTurn) {
        broadcast(
                new Message(Message.Type.START, String.format("Spel gestart door %s", playerWhoStartedGame.getName())));
        sendTo(new Message(Message.Type.YOUR_TURN, "Jij mag beginnen!"), playerWithFirstTurn.getId());
    }

    public void cannotJoinGame(Player player) {
        sendTo(new Message("Je kunt op dit ogenblik niet meedoen"), player.getId());
    }

    public void nameAlreadyTaken(String name, Player player) {
        sendTo(new Message(String.format("De naam '%s' is al bezet", name)), player.getId());
    }

    public void gameHaltedBecausePlayerLeft(Player player) {
        broadcast(new Message(Message.Type.RESTART,
                String.format("Het spel kan niet verder gaan zonder %s", player.getName())));
    }

    public void cardAsked(Ask ask, Player player) {
        broadcast(new Message(
                String.format("%s vraagt aan %s om %s", player.getName(), ask.getOpponent(), ask.getCard())));
    }

    public void playerHasNoCardInAskedSet(Player player, Player opponent, String[] spectatorIds) {
        sendTo(new Message(
                        String.format("Je vroeg een kaart waar je er geen van had! Nu is %s aan de beurt",
                                opponent.getName())),
                player.getId());
        sendTo(new Message(Message.Type.YOUR_TURN,
                String.format("%s vroeg jou een kaart waar hij er geen van had! Nu ben jij aan de beurt",
                        player.getName())), opponent.getId());
        sendTo(new Message(
                String.format("%s vroeg een kaart waar hij er geen van had! Nu is %s aan de beurt", player.getName(),
                        opponent.getName())), spectatorIds);
    }

    public void playerAskedInvalidCard(Player player, Player opponent, String[] spectatorIds) {
        sendTo(new Message(
                        String.format("Je vroeg een kaart die niet bestaat! Nu is %s aan de beurt",
                                opponent.getName())),
                player.getId());
        sendTo(new Message(Message.Type.YOUR_TURN,
                        String.format("%s vroeg jou een kaart die niet bestaat! Nu ben jij aan de beurt",
                                player.getName())),
                opponent.getId());
        sendTo(new Message(String.format("%s vroeg een kaart die niet bestaat! Nu is %s aan de beurt", player.getName(),
                opponent.getName())), spectatorIds);
    }

    public void opponentGivesCard(Ask ask, Player player, Player opponent, String[] spectatorIds) {
        sendTo(new Message(Message.Type.YOUR_TURN,
                        String.format("Je hebt de %s van %s gekregen, je mag nog een keer", ask.getCard(),
                                opponent.getName())),
                player.getId());
        sendTo(new Message(
                        String.format("%s heeft %s van jou gekregen en mag nog een keer", player.getName(),
                                ask.getCard())),
                opponent.getId());
        sendTo(new Message(String.format("%s kreeg de %s van %s en mag nog een keer", player.getName(), ask.getCard(),
                ask.getOpponent())), spectatorIds);
    }

    public void opponentDoesNotHaveCard(Ask ask, Player player, Player opponent, String[] spectatorIds) {
        sendTo(new Message(String.format("%s had de %s niet, je beurt is voorbij", opponent.getName(), ask.getCard())),
                player.getId());
        sendTo(new Message(Message.Type.YOUR_TURN,
                String.format("%s vroeg de %s maar die heb je niet, nu ben jij aan de beurt", player.getName(),
                        ask.getCard())), opponent.getId());
        sendTo(new Message(String.format("%1$s vroeg de %2$s aan %3$s maar die had hem niet. Nu is %3$s aan de beurt.",
                player.getName(), ask.getCard(), ask.getOpponent())), spectatorIds);
    }

}
