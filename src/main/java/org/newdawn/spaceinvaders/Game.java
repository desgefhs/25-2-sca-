package org.newdawn.spaceinvaders;
/**
 * 게임의 주 진입점(Entry Point) 역할을 하는 클래스.
 * GameManager를 생성하고 게임을 시작시키는 역할만 합니다.
 */
public class  Game {

    /**
     * 게임 시작점.
     */
    public static void main(String[] argv) {
        GameManager gameManager = new GameManager();
        gameManager.startGame();
    }
}
