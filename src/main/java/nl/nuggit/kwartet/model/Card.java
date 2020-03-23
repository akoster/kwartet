package nl.nuggit.kwartet.model;

public class Card implements Comparable<Card>{

    private String times;
    private String table;
    private String outcome;

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
}
