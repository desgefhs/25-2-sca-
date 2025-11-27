package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.userinput.ShopInputHandler;
import org.newdawn.spaceinvaders.view.ShopView;

import java.awt.*;
import java.util.List;

/**
 * 캐릭터 강화를 위한 상점 화면을 담당하는 게임 상태.
 * 플레이어가 업그레이드 항목을 확인하고 구매하는 상호작용을 처리합니다.
 */
public class ShopState implements GameState {
    private static final String FONT_NAME = "Dialog";
    private final GameContext gameContext;
    private final ShopView shopView; // 상점 메뉴 뷰
    private final ShopInputHandler inputHandler; // 상점 입력 핸들러

    /**
     * ShopState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ShopState(GameContext gameContext) {
        this.gameContext = gameContext;
        // 상점 메뉴에서 업그레이드 항목 목록을 가져와 ShopView를 초기화
        this.shopView = new ShopView(gameContext.getGameContainer().getUiManager().getShopMenu().getItems());
        this.inputHandler = new ShopInputHandler(gameContext, this.shopView);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 상점 화면에 대한 사용자 입력을 처리합니다.
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
     * 상점 화면을 렌더링합니다.
     * 제목, 보유 크레딧, 각 업그레이드 항목의 이름, 현재 레벨, 최대 레벨, 비용 등을 표시합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font(FONT_NAME, Font.BOLD, 32));
        g.drawString("캐릭터 강화", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("캐릭터 강화")) / 2, 80);

        g.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 120);

        g.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
        g.drawString("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기")) / 2, 550);

        // 게임 컨텍스트에 설정된 메시지 표시 (예: "크레딧 부족!")
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 520);
        }

        int itemHeight = 60;
        int startY = 160;

        List<Upgrade> upgrades = shopView.getUpgrades();
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            int currentLevel = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getUpgradeLevel(upgrade.getId());
            int maxLevel = upgrade.getMaxLevel();

            Rectangle itemBounds = new Rectangle(100, startY + (i * itemHeight) - 40, Game.SCREEN_WIDTH - 200, itemHeight);

            // 선택된 항목 강조 표시
            if (i == shopView.getSelectedIndex()) {
                g.setColor(Color.GREEN);
                g.fillRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
            }

            g.setFont(new Font(FONT_NAME, Font.BOLD, 20));
            g.drawString(upgrade.getName(), 120, startY + (i * itemHeight));

            g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
            g.drawString("레벨: " + currentLevel + " / " + maxLevel, 370, startY + (i * itemHeight));

            String costString = (currentLevel >= maxLevel) ? "최고 레벨" : "비용: " + upgrade.getCost(currentLevel + 1);
            g.drawString(costString, 570, startY + (i * itemHeight));
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 메시지를 초기화합니다.
     */
    @Override
    public void onEnter() {
        gameContext.setMessage("");
        // 선택된 인덱스는 ShopView 자체에서 관리되며 기본값으로 0에서 시작합니다.
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}