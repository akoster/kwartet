package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    public void addCard(Card card) {
        cards.add(card);
    }

    @JsonIgnore
    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public int getScore() {
        return score;
    }

    @JsonIgnore
    public void increaseScore() {
        score++;
    }

    @JsonIgnore
    public List<List<Card>> getCompleteSets() {
        List<List<Card>> sets = getSets();
        removeIncompleteSets(sets);
        return sets;
    }

    private void removeIncompleteSets(List<List<Card>> sets) {
        sets.removeIf(set -> set.size() < set.get(0).getSetSize());
    }

    //    @JsonProperty("groupedCards")
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
        List<Card> set = new ArrayList<>();
        set.add(firstCard);
        return set;
    }

    @Override
    public String toString() {
        return name;
    }

}