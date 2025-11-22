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

public class ShopState implements GameState {
    private static final String FONT_NAME = "Dialog";
    private final GameContext gameContext;
    private final ShopView shopView;
    private final ShopInputHandler inputHandler;

    public ShopState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.shopView = new ShopView(gameContext.getGameContainer().getUiManager().getShopMenu().getItems());
        this.inputHandler = new ShopInputHandler(gameContext, this.shopView);
    }

    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

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

    @Override
    public void onEnter() {
        gameContext.setMessage("");
        // The selected index is now managed by the ShopView, which starts at 0 by default.
    }

    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}