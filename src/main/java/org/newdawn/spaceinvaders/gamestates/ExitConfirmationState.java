package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

/**
 * 게임 종료 여부를 사용자에게 확인하는 상태
 * 메인 메뉴 위에 반투명한 오버레이와 확인 대화 상자를 렌더링
 */
public class ExitConfirmationState implements GameState {
    private final GameManager gameManager;

    public ExitConfirmationState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    /**
     * 사용자 입력을 처리하여 대화 상자의 선택지를 탐색하고,
     * Enter 키 입력 시 선택된 동작(종료 또는 취소)을 수행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isLeftPressedAndConsume()) gameManager.confirmDialog.moveLeft();
        if (input.isRightPressedAndConsume()) gameManager.confirmDialog.moveRight();

        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            String selected = gameManager.confirmDialog.getSelectedItem();
            if ("Confirm".equals(selected)) {
                System.exit(0); // 게임 종료
            } else if ("Cancel".equals(selected)) {
                gameManager.setCurrentState(Type.MAIN_MENU); // 메인 메뉴로 복귀
            }
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 배경으로 메인 메뉴를 렌더링하고, 그 위에 반투명 오버레이와 종료 확인 대화 상자를 그림
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 배경으로 메인 메뉴 렌더링
        MainMenuState mainMenuState = gameManager.getGsm().getMainMenuState();
        if (mainMenuState != null) {
            mainMenuState.render(g);
        }

        // 반투명 오버레이 그리기
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 대화 상자 배경 그리기
        g.setColor(Color.BLACK);
        g.fillRect(200, 200, 400, 200);
        g.setColor(Color.WHITE);
        g.drawRect(200, 200, 400, 200);

        // 메시지 텍스트 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 20));
        g.drawString(gameManager.confirmDialog.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.confirmDialog.getMessage())) / 2, 260);

        // 버튼(선택지) 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 80;

        for (int i = 0; i < gameManager.confirmDialog.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameManager.confirmDialog.getItem(i));
        }
        totalWidth += (gameManager.confirmDialog.getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameManager.confirmDialog.getItemCount(); i++) {
            if (i == gameManager.confirmDialog.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로 표시
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameManager.confirmDialog.getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameManager.confirmDialog.getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}