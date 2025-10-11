package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

/**
 * 최고 점수 랭킹을 표시하는 게임 상태
 */
public class RankingState implements GameState {
    private final GameManager gameManager;
    /** 데이터베이스에서 불러온 최고 점수 목록 */
    private java.util.List<String> highScores;

    public RankingState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    /**
     * 사용자 입력을 처리
     * Enter 키를 누르면 메인 메뉴로
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            gameManager.setCurrentState(Type.MAIN_MENU);
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 랭킹 화면을 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        g.drawString("Ranking", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("Ranking")) / 2, 100);

        g.setFont(new Font("Dialog", Font.BOLD, 18));
        int y = 150;
        if (highScores != null) {
            for (String score : highScores) {
                g.drawString(score, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(score)) / 2, y);
                y += 30;
            }
        }

        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString("Press Enter to return to Main Menu", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press Enter to return to Main Menu")) / 2, 500);
    }

    /**
     * 상태 진입 시 데이터베이스에서 최고 점수 목록을 불러옴
     */
    @Override
    public void onEnter() {
        highScores = gameManager.getDatabaseManager().getHighScores();
    }

    @Override
    public void onExit() {}
}
