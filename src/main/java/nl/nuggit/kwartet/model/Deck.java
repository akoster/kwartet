package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Deck {

    private static final int TABLE_FROM = 2;
    private static final int TABLE_TO = 9;

    private static final int TIMES_FROM = 2;
    private static final int TIMES_TO = 9;

    private List<Card> cards = new ArrayList<>();

    public Deck() {
        for (int table = TABLE_FROM; table < TABLE_TO; table++) {
            for (int times = TIMES_FROM; times < TIMES_TO; times++) {
                Card card = new Card(table, times);
                cards.add(card);
            }
        }
    }

    public Optional<Card> drawRandomCard() {
        if (cards.size() < 1) {
            return Optional.empty();
        }
        int index = (int) (Math.random() * cards.size());
        return Optional.of(cards.remove(index));
    }

    public static boolean isValid(Card card) {
        try {
            int times = Integer.parseInt(card.getTimes());
            int table = Integer.parseInt(card.getTable());
            int outcome = Integer.parseInt(card.getOutcome());
            return TIMES_FROM <= times && TIMES_TO >= times && TABLE_FROM <= table && TABLE_TO >= table && outcome == (
                    times * table);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
