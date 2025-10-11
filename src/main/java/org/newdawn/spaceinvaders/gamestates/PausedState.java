package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

/**
 * 게임이 일시정지되었을 때의 상태를 처리하는 클래스
 * 플레이 화면 위에 반투명 오버레이와 일시정지 메뉴를 렌더링
 */
public class PausedState implements GameState {
    private final GameManager gameManager;

    public PausedState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    /**
     * 사용자 입력을 처리하여 메뉴를 탐색하고,
     * 선택된 항목(재개, 메인 메뉴, 종료)에 따라 동작을 수행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) gameManager.pauseMenu.moveUp();
        if (input.isDownPressedAndConsume()) gameManager.pauseMenu.moveDown();
        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            String selected = gameManager.pauseMenu.getSelectedItem();
            switch (selected) {
                case "재개하기":
                    gameManager.setCurrentState(Type.PLAYING);
                    break;
                case "메인메뉴로 나가기":
                    gameManager.saveGameResults();
                    gameManager.setCurrentState(Type.MAIN_MENU);
                    break;
                case "종료하기":
                    System.exit(0);
                    break;
            }
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 배경으로 플레이 상태를 렌더링하고, 그 위에 반투명 오버레이와 일시정지 메뉴를 그림
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 배경으로 플레이 상태 렌더링
        PlayingState playingState = gameManager.getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        // 반투명 오버레이 그리기
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 일시정지 메뉴 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 40;
        int startY = (Game.SCREEN_HEIGHT - (gameManager.pauseMenu.getItemCount() * itemHeight)) / 2;

        for (int i = 0; i < gameManager.pauseMenu.getItemCount(); i++) {
            if (i == gameManager.pauseMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로
            } else {
                g.setColor(Color.WHITE);
            }
            String itemText = gameManager.pauseMenu.getItem(i);
            int textWidth = g.getFontMetrics().stringWidth(itemText);
            g.drawString(itemText, (Game.SCREEN_WIDTH - textWidth) / 2, startY + (i * itemHeight));
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}