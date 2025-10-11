package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

/**
 * 게임 오버 또는 게임 승리 상태를 처리하는 클래스
 * 결과 메시지와 함께 '다시하기', '메인 메뉴로' 등의 옵션을 제공
 */
public class GameOverState implements GameState {
    private final GameManager gameManager;
    /** 게임 승리 여부 */
    private final boolean gameWon;

    /**
     * GameOverState 객체를 생성
     *
     * @param gameManager 게임 매니저
     * @param gameWon     게임 승리 여부 (true: 승리, false: 패배)
     */
    public GameOverState(GameManager gameManager, boolean gameWon) {
        this.gameManager = gameManager;
        this.gameWon = gameWon;
    }

    @Override
    public void init() {}

    /**
     * 사용자 입력을 처리하여 메뉴를 탐색하고 선택된 동작을 수행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isLeftPressedAndConsume()) gameManager.gameOverMenu.moveLeft();
        if (input.isRightPressedAndConsume()) gameManager.gameOverMenu.moveRight();

        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            String selected = gameManager.gameOverMenu.getSelectedItem();
            if ("다시하기".equals(selected)) {
                gameManager.startGameplay(); // 게임 재시작
            } else if ("메인 메뉴로".equals(selected)) {
                gameManager.setCurrentState(Type.MAIN_MENU); // 메인 메뉴로 이동
            }
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 배경으로 플레이 상태를 렌더링하고, 그 위에 결과 메시지와 메뉴를 그림
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

        // 결과 메시지 그리기
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 250);
        }

        // 게임 오버 메뉴 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;
        for (int i = 0; i < gameManager.gameOverMenu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameManager.gameOverMenu.getItem(i));
        }
        totalWidth += (gameManager.gameOverMenu.getItemCount() - 1) * spacing;
        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameManager.gameOverMenu.getItemCount(); i++) {
            if (i == gameManager.gameOverMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameManager.gameOverMenu.getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameManager.gameOverMenu.getItem(i)) + spacing;
        }
    }

    /**
     * 이 상태로 진입할 때 호출
     * 게임 승패에 따라 다른 메시지를 설정하고, 패배 시 게임 결과를 저장
     */
    @Override
    public void onEnter() {
        if (gameWon) {
            gameManager.message = "Well done! You Win!";
        } else {
            gameManager.saveGameResults();
            long finalCredit = gameManager.currentPlayer.getCredit();
            gameManager.message = String.format("이번 라운드 점수: %d / 최종 크레딧: %d", gameManager.score, finalCredit);
        }
    }

    /**
     * 이 상태에서 벗어날 때 호출
     * 메시지를 초기화
     */
    @Override
    public void onExit() {
        gameManager.message = "";
    }
}
