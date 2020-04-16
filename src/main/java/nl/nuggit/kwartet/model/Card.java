package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;

public class Card implements Comparable<Card> {

    private static final int SET_SIZE = 4;

    private String times;
    private List<String> others = new ArrayList<>();
    private String table;
    private String outcome;

    public Card(int table, int times) {
        this.table = String.valueOf(table);
        this.times = String.valueOf(times);
        this.outcome = (String.valueOf(table * times));
    }

    public Card() {
        // default
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public List<String> getOthers() {
        return others;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @Override
    public int compareTo(Card other) {
        int result = table.compareTo(other.table);
        if (result == 0) {
            result = times.compareTo(other.times);
        }
        return result;
    }

    public String getDescription() {
        return String.format("%s x %s = %s", times, table, outcome);
    }

    public static Card fromDescription(String description) {
        String[] parts = description.trim().split("\\s+");
        Card card = new Card();
        if (parts.length >= 4) {
            card.times = parts[0];
            card.table = parts[2];
            card.outcome = parts[4];
        }
        return card;
    }

    public boolean inSetWith(Card other) {
        try {
            return other.getTable().equals(table);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getSetSize() {
        return SET_SIZE;
    }

    @Override
    public String toString() {
        return String.format("%s x %s = %s", times, table, outcome);
    }
}
