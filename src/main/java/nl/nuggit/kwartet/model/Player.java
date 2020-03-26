package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Player {

    private final String id;
    private String name;
    private Set<Card> cards = new TreeSet<>();
    private int score;

    public Player(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public Stream<Card> getCards() {
        return cards.stream();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public int getScore() {
        return score;
    }

    public void increaseScore() {
        score++;
    }

    public List<List<Card>> getSets() {
        List<List<Card>> sets = new ArrayList<>();
        if (!cards.isEmpty()) {
            addSets(sets);
        }
        return sets;
    }

    private void addSets(List<List<Card>> sets) {
        for (Card card : cards) {
            if (sets.isEmpty()) {
                sets.add(createNewSetWith(cards.iterator().next()));
                continue;
            }
            addToSets(card, sets);
        }
        sets.removeIf(set -> set.size() < set.get(0).getSetSize());
    }

    private void addToSets(Card card, List<List<Card>> sets) {
        for (List<Card> set : sets) {
            Card setCard = set.get(0);
            if (card.inSetWith(setCard)) {
                set.add(card);
                return;
            }
        }
        sets.add(createNewSetWith(card));
    }

    private List<Card> createNewSetWith(Card firstCard) {
        List<Card> firstCompleted = new ArrayList<>();
        firstCompleted.add(firstCard);
        return firstCompleted;
    }

    @Override
    public String toString() {
        return name;
    }

}