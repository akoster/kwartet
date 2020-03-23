package nl.nuggit.kwartet.model;

import java.util.Set;
import java.util.TreeSet;

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

    public Set<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return name;
    }

}