package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.awt.*;

/**
 * 게임의 메인 메뉴 상태를 처리하는 클래스
 * 메뉴 항목 간의 탐색 및 선택에 따른 상태 전환을 담당
 */
public class MainMenuState implements GameState {

    private final GameManager gameManager;
    private final MainMenu mainMenu;

    public MainMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mainMenu = gameManager.mainMenu;
    }

    @Override
    public void init() {}

    /**
     * 사용자 입력을 처리하여 메뉴 항목을 탐색하고,
     * Enter 키 입력 시 선택된 항목에 해당하는 게임 상태로 전환
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isLeftPressedAndConsume()) mainMenu.moveLeft();
        if (input.isRightPressedAndConsume()) mainMenu.moveRight();
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.EXIT_CONFIRMATION);
        }

        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            String selected = mainMenu.getSelectedItem();
            switch (selected) {
                case "1. 게임시작":
                    gameManager.startGameplay();
                    break;
                case "2. 랭킹":
                    gameManager.setCurrentState(Type.RANKING);
                    break;
                case "3. 무기":
                    gameManager.setCurrentState(Type.WEAPON_MENU);
                    break;
                case "4. 펫":
                    gameManager.setCurrentState(Type.PET_MENU);
                    break;
                case "5. 상점":
                    gameManager.setCurrentState(Type.SHOP_MAIN_MENU);
                    break;
                case "6.종료":
                    System.exit(0);
                    break;
            }
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 배경 이미지와 메인 메뉴 항목들을 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.drawImage(gameManager.staticBackgroundSprite.getImage(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;

        // 모든 메뉴 항목의 총 너비 계산
        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(mainMenu.getItem(i));
        }
        totalWidth += (mainMenu.getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        // 각 메뉴 항목 그리기
        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            if (i == mainMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(mainMenu.getItem(i), currentX, 500);
            currentX += g.getFontMetrics().stringWidth(mainMenu.getItem(i)) + spacing;
        }
    }

    /**
     * 상태 진입 시 배경 음악을 재생
     */
    @Override
    public void onEnter() {
        gameManager.getSoundManager().loopSound("menubackground");
    }

    @Override
    public void onExit() {}
}
