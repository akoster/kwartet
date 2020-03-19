package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private static final int TABLE_FROM = 2;
    private static final int TABLE_TO = 9;

    private static final int TIMES_FROM = 2;
    private static final int TIMES_TO = 9;

    private List<Card> cards = new ArrayList<>();

    public Deck() {
        for (int table = TABLE_FROM; table < TABLE_TO; table++) {
            for (int times = TIMES_FROM; times < TIMES_TO; times++) {
                Card card = new Card();
                card.setTable(String.valueOf(table));
                card.setTimes(String.valueOf(times));
                card.setOutcome(String.valueOf(table * times));
                cards.add(card);
            }
        }
    }

    public Card getRandomCard() {
        if (cards.size() < 1) {
            throw new IllegalStateException("No more cards");
        }
        return cards.get((int) (Math.random() * cards.size()));
    }
}
