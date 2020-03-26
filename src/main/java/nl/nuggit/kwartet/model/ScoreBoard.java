package nl.nuggit.kwartet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScoreBoard {
    private List<Score> scores = new ArrayList<>();

    public ScoreBoard(Stream<Player> players) {
        scores.addAll(players.map(Score::new).collect(Collectors.toList()));
    }

    public List<Score> getScores() {
        return scores;
    }
}
