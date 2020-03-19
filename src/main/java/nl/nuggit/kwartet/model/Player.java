package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private String id;
    private List<Card> cards = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return name;
    }
}