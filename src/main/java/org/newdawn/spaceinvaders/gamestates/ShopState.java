package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.shop.Upgrade;

import java.awt.*;

public class ShopState implements GameState {
    private final GameManager gameManager;

    public ShopState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) gameManager.shopMenu.moveUp();
        if (input.isDownPressedAndConsume()) gameManager.shopMenu.moveDown();
        if (input.isEscPressedAndConsume()) gameManager.setCurrentState(Type.MAIN_MENU);

        if (input.isFirePressedAndConsume()) {
            Upgrade selectedUpgrade = gameManager.shopMenu.getSelectedItem();
            if (selectedUpgrade == null) return;

            int currentLevel = gameManager.currentPlayer.getUpgradeLevel(selectedUpgrade.getId());
            if (currentLevel >= selectedUpgrade.getMaxLevel()) {
                gameManager.message = "이미 최고 레벨입니다.";
                return;
            }

            int cost = selectedUpgrade.getCost(currentLevel + 1);
            if (gameManager.currentPlayer.getCredit() >= cost) {
                gameManager.currentPlayer.setCredit(gameManager.currentPlayer.getCredit() - cost);
                gameManager.currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);
                gameManager.saveGameResults(); // Save after purchase
                gameManager.message = "업그레이드 성공!";
            } else {
                gameManager.message = "크레딧이 부족합니다!";
            }
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 타이틀
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.drawString("업그레이드 상점", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("업그레이드 상점")) / 2, 80);

        // 보유크레딧
        g.setFont(new Font("Dialog", Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameManager.currentPlayer.getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 120);

        // 설명
        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        g.drawString("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기")) / 2, 550);

        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 520);
        }

        //
        int itemHeight = 60;
        int startY = 160;
        java.util.List<Upgrade> items = gameManager.shopMenu.getItems();

        for (int i = 0; i < items.size(); i++) {
            Upgrade upgrade = items.get(i);
            int currentLevel = gameManager.currentPlayer.getUpgradeLevel(upgrade.getId());
            int maxLevel = upgrade.getMaxLevel();

            if (i == gameManager.shopMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }

            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(upgrade.getName(), 100, startY + (i * itemHeight));

            g.setFont(new Font("Dialog", Font.PLAIN, 16));
            g.drawString("레벨: " + currentLevel + " / " + maxLevel, 350, startY + (i * itemHeight));

            String costString;
            if (currentLevel >= maxLevel) {
                costString = "최고 레벨";
            } else {
                costString = "비용: " + upgrade.getCost(currentLevel + 1);
            }
            g.drawString(costString, 550, startY + (i * itemHeight));
        }
    }

    @Override
    public void onEnter() {
        gameManager.message = "";
    }

    @Override
    public void onExit() {}
}
