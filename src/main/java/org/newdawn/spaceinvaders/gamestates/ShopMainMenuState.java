package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 상점의 메인 메뉴 역할을 하는 게임 상태
 * '캐릭터 강화'와 '아이템 뽑기' 메뉴로 이동하는 옵션을 제공
 */
public class ShopMainMenuState implements GameState {

    private final GameManager gameManager;
    private int selectedIndex = 0;
    private final String[] menuItems = {"캐릭터 강화", "아이템 뽑기", "뒤로가기"};

    private final Rectangle[] menuBounds = new Rectangle[menuItems.length];

    public ShopMainMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
        for (int i = 0; i < menuItems.length; i++) {
            menuBounds[i] = new Rectangle();
        }
    }

    @Override
    public void init() {
    }

    /**
     * 사용자 입력을 처리하여 메뉴를 탐색하고, 선택에 따라 다른 상점 상태나 메인 메뉴로 전환
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
        }
        if (input.isDownPressedAndConsume()) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
        }
        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            switch (selectedIndex) {
                case 0: // 캐릭터 강화
                    gameManager.setCurrentState(Type.SHOP);
                    break;
                case 1: // 아이템 뽑기
                    gameManager.setCurrentState(Type.ITEM_DRAW);
                    break;
                case 2: // 뒤로가기
                    gameManager.setCurrentState(Type.MAIN_MENU);
                    break;
            }
        }
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.MAIN_MENU);
        }
    }

    @Override
    public void update(long delta) {
    }

    /**
     * 상점 메인 메뉴 UI를 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.white);
        String title = "상점";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.SCREEN_WIDTH - titleWidth) / 2, 150);

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 60;
        int startY = 250;

        for (int i = 0; i < menuItems.length; i++) {
            String menuItem = menuItems[i];
            int itemWidth = g.getFontMetrics().stringWidth(menuItem);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == selectedIndex) {
                g.setColor(Color.GREEN);
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItem, x, y);
        }
    }

    /**
     * 상태 진입 시 선택 인덱스를 0으로 초기화
     */
    @Override
    public void onEnter() {
        selectedIndex = 0;
    }

    @Override
    public void onExit() {
    }
}
