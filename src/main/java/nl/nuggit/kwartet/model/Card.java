package nl.nuggit.kwartet.model;

public class Card implements Comparable<Card> {

    private String times;
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
            int thisTimes = Integer.parseInt(times);
            int otherTimes = Integer.parseInt(other.times);
            boolean sameTimesSet = (thisTimes <= 4 && otherTimes <= 4) || (thisTimes >= 5 && otherTimes >= 5);
            boolean sameTable = other.getTable().equals(table);
            return sameTable && sameTimesSet;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
