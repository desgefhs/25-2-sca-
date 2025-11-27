package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ItemDrawInputHandler;
import org.newdawn.spaceinvaders.view.ItemDrawView;

import java.awt.*;

/**
 * 아이템 뽑기(가챠) 화면을 담당하는 게임 상태.
 * 플레이어에게 아이템 뽑기 옵션을 제공하고, 결과 메시지를 표시하며, 관련 입력을 처리합니다.
 */
public class ItemDrawState implements GameState {

    private static final String FONT_NAME = "Dialog";
    private final GameContext gameContext;
    private final ItemDrawView itemView;
    private final ItemDrawInputHandler inputHandler;
    private final Rectangle[] menuBounds; // 메뉴 항목의 시각적 경계를 저장

    /**
     * ItemDrawState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ItemDrawState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.itemView = new ItemDrawView();
        this.inputHandler = new ItemDrawInputHandler(gameContext, this.itemView);
        this.menuBounds = new Rectangle[itemView.getItemCount()];
        for (int i = 0; i < itemView.getItemCount(); i++) {
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
     * 아이템 뽑기 화면에 대한 사용자 입력을 처리합니다.
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
     * 아이템 뽑기 화면을 렌더링합니다.
     * 배경, 제목, 보유 크레딧, 메시지, 그리고 선택 가능한 메뉴 항목들을 그립니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font(FONT_NAME, Font.BOLD, 32));
        g.setColor(Color.white);
        String title = "아이템 뽑기";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.SCREEN_WIDTH - titleWidth) / 2, 150);

        g.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 200);

        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 450);
        }

        g.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        int itemHeight = 60;
        int startY = 300;

        for (int i = 0; i < itemView.getItemCount(); i++) {
            String menuItemText = itemView.getItem(i);
            if (i == 0) { // "아이템 뽑기" 버튼
                String costText = " (비용: " + gameContext.getGameContainer().getShopManager().getItemDrawCost() + ")";
                menuItemText += costText;
            }

            int itemWidth = g.getFontMetrics().stringWidth(menuItemText);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == itemView.getSelectedIndex()) {
                g.setColor(Color.GREEN);
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItemText, x, y);
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 메시지를 초기화하고 메뉴 선택 인덱스를 0으로 설정합니다.
     */
    @Override
    public void onEnter() {
        gameContext.setMessage("");
        itemView.setSelectedIndex(0);
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}