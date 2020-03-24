package nl.nuggit.kwartet.model;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Player {

    private final String id;
    private String name;
    private Set<Card> cards = new TreeSet<>();

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

    @Override
    public String toString() {
        return name;
    }

}