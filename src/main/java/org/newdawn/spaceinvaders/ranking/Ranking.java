package org.newdawn.spaceinvaders.ranking;

public class Ranking {
    private final String name;
    private final int score;

    public Ranking(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
