package nl.nuggit.kwartet.model;

public class Score {
    private String name;
    private int score;

    public Score(Player player) {
        name = player.getName();
        score = player.getScore();
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
