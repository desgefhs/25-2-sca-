package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ShopMainMenuInputHandler;
import org.newdawn.spaceinvaders.view.ShopMainMenuView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * 게임의 주 상점 메뉴 화면을 담당하는 게임 상태.
 * "캐릭터 강화", "아이템 뽑기", "뒤로가기"와 같은 메뉴 항목을 표시하고,
 * 사용자 입력을 통해 메뉴를 탐색하며, 관련 UI를 렌더링합니다.
 */
public class ShopMainMenuState implements GameState {

    private final GameContext gameContext;
    private final ShopMainMenuView shopMainMenuView; // 주 상점 메뉴 뷰
    private final ShopMainMenuInputHandler inputHandler; // 주 상점 메뉴 입력 핸들러

    private final Rectangle[] menuBounds; // 메뉴 항목의 시각적 경계를 저장

    /**
     * ShopMainMenuState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ShopMainMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.shopMainMenuView = new ShopMainMenuView();
        this.inputHandler = new ShopMainMenuInputHandler(gameContext, this.shopMainMenuView);
        this.menuBounds = new Rectangle[shopMainMenuView.getItemCount()];
        for (int i = 0; i < shopMainMenuView.getItemCount(); i++) {
            menuBounds[i] = new Rectangle();
        }
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 주 상점 메뉴에 대한 사용자 입력을 처리합니다.
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    /**
     * 이 상태에서는 특별한 업데이트 로직이 필요하지 않습니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 주 상점 메뉴 화면을 렌더링합니다.
     * 배경, 제목, 그리고 선택 가능한 메뉴 항목들을 그립니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
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

        for (int i = 0; i < shopMainMenuView.getItemCount(); i++) {
            String menuItem = shopMainMenuView.getItem(i);
            int itemWidth = g.getFontMetrics().stringWidth(menuItem);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == shopMainMenuView.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로 표시
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE); // 선택되지 않은 항목은 흰색으로 표시
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItem, x, y);
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 메뉴 선택 인덱스를 초기화하는 등의 작업을 수행할 수 있습니다.
     */
    @Override
    public void onEnter() {
        // 선택된 인덱스는 ShopMainMenuView 자체에서 관리되지만,
        // 상태에 진입할 때 필요하면 초기화할 수 있습니다.
        // 현재는 뷰 자체의 기본값이 충분합니다.
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
