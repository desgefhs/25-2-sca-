package org.newdawn.spaceinvaders.model;

/**
 * Firebase에 저장될 플레이어의 데이터를 담는 클래스.
 * 최고 점수와 재화(크레딧) 정보를 포함합니다.
 */
public class PlayerData {

    private String username;
    private int highScore = 0;
    private int credit = 0;

    // Firestore가 데이터를 객체로 변환할 때 기본 생성자가 필요합니다.
    public PlayerData() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
