package org.newdawn.spaceinvaders.ranking;

/**
 * 게임 랭킹 항목의 데이터를 나타내는 클래스.
 * 플레이어의 이름과 점수를 저장합니다.
 */
public class Ranking {
    /** 랭킹을 기록한 플레이어의 이름. */
    private final String name;
    /** 플레이어가 기록한 점수. */
    private final int score;

    /**
     * Ranking 클래스의 생성자.
     *
     * @param name 랭킹을 기록한 플레이어의 이름
     * @param score 플레이어가 기록한 점수
     */
    public Ranking(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * 플레이어의 이름을 반환합니다.
     *
     * @return 플레이어의 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 플레이어의 점수를 반환합니다.
     *
     * @return 플레이어의 점수
     */
    public int getScore() {
        return score;
    }
}
