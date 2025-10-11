package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.shop.Upgrade;

import java.awt.*;
import java.util.List;

public class ShopState implements GameState {
    private final GameManager gameManager;
    private int selectedIndex = 0;
    private List<Upgrade> upgrades;

    public ShopState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.upgrades = gameManager.shopMenu.getItems();
    }

    @Override
    public void init() {
    }

    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = upgrades.size() - 1;
            }
        }
        if (input.isDownPressedAndConsume()) {
            selectedIndex++;
            if (selectedIndex >= upgrades.size()) {
                selectedIndex = 0;
            }
        }
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.SHOP_MAIN_MENU);
        }

        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            // Handle Upgrade Purchase
            if (selectedIndex < upgrades.size()) {
                Upgrade selectedUpgrade = upgrades.get(selectedIndex);
                int currentLevel = gameManager.currentPlayer.getUpgradeLevel(selectedUpgrade.getId());
                if (currentLevel >= selectedUpgrade.getMaxLevel()) {
                    gameManager.message = "이미 최고 레벨입니다.";
                    return;
                }

                int cost = selectedUpgrade.getCost(currentLevel + 1);
                if (gameManager.currentPlayer.getCredit() >= cost) {
                    gameManager.currentPlayer.setCredit(gameManager.currentPlayer.getCredit() - cost);
                    gameManager.currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);
                    gameManager.savePlayerData(); // Save after purchase
                    gameManager.message = "업그레이드 성공!";
                } else {
                    gameManager.message = "크레딧이 부족합니다!";
                }
            }
        }
    }
    @Override
    public void update(long delta) {
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.drawString("캐릭터 강화", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("캐릭터 강화")) / 2, 80);

        g.setFont(new Font("Dialog", Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameManager.currentPlayer.getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 120);

        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        g.drawString("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기")) / 2, 550);

        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 520);
        }

        int itemHeight = 60;
        int startY = 160;

        // Draw Upgrades
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            int currentLevel = gameManager.currentPlayer.getUpgradeLevel(upgrade.getId());
            int maxLevel = upgrade.getMaxLevel();

            Rectangle itemBounds = new Rectangle(100, startY + (i * itemHeight) - 40, Game.SCREEN_WIDTH - 200, itemHeight);

            if (i == selectedIndex) {
                g.setColor(Color.GREEN);
                g.fillRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
            }

            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(upgrade.getName(), 120, startY + (i * itemHeight));

            g.setFont(new Font("Dialog", Font.PLAIN, 16));
            g.drawString("레벨: " + currentLevel + " / " + maxLevel, 370, startY + (i * itemHeight));

            String costString = (currentLevel >= maxLevel) ? "최고 레벨" : "비용: " + upgrade.getCost(currentLevel + 1);
            g.drawString(costString, 570, startY + (i * itemHeight));
        }
    }

    @Override
    public void onEnter() {
        gameManager.message = "";
        this.selectedIndex = 0; // Reset selection when entering shop
    }

    @Override
    public void onExit() {}
}
