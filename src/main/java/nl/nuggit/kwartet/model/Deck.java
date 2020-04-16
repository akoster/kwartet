package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Deck {

    private static final int TABLE_FROM = 2;
    private static final int TABLE_TO = 9;

    private static final int TIMES_FROM = 2;
    private static final int TIMES_TO = 9;

    private static final int SET_SIZE = 4;

    private List<Card> cards = new ArrayList<>();

    public Deck() {
        init();
    }

    private void init() {
        for (int table = TABLE_FROM; table < TABLE_TO; table++) {
            cards.addAll(initCardSet(table));
        }
    }

    private List<Card> initCardSet(int table) {
        List<Card> cardSet = new ArrayList<>();
        List<Integer> timesRange = initTimesRange();
        for (int i = 0; i < SET_SIZE; i++) {
            Card card = new Card(table, takeRandomValueFrom(timesRange));
            cardSet.add(card);
        }
        addOthers(cardSet);
        return cardSet;
    }

    private void addOthers(List<Card> cardSet) {
        for (Card card : cardSet) {
            for (Card other : cardSet) {
                if (other != card) {
                    card.getOthers().add(other.getTimes());
                }
            }
        }
    }

    private List<Integer> initTimesRange() {
        return IntStream.rangeClosed(TIMES_FROM, TIMES_TO)
                .boxed().collect(Collectors.toList());
    }

    private int takeRandomValueFrom(List<Integer> values) {
        double range = values.size();
        int randomIndex = (int) (Math.random() * range);
        return values.remove(randomIndex);
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
