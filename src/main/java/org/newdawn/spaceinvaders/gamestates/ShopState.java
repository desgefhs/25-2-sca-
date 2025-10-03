package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.PetType;
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
    public void init() {}

        @Override
        public void handleInput(InputHandler input) {
            int totalItems = upgrades.size() + 1; // +1 for the draw
    
            if (input.isUpPressedAndConsume()) {
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = totalItems - 1;
                }
            }
            if (input.isDownPressedAndConsume()) {
                selectedIndex++;
                if (selectedIndex >= totalItems) {
                    selectedIndex = 0;
                }
            }
            if (input.isEscPressedAndConsume()) {
                gameManager.setCurrentState(Type.MAIN_MENU);
            }
    
            if (input.isFirePressedAndConsume()) {
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
                } else { // Handle Item Draw Purchase
                    String result = gameManager.shopManager.drawItem(gameManager.currentPlayer);
    
                    switch (result) {
                        case "INSUFFICIENT_FUNDS":
                            gameManager.message = "크레딧이 부족합니다!";
                            break;
                        case "CREDIT_250":
                            gameManager.message = "250 크레딧에 당첨되었습니다!";
                            break;
                        case "PET_ATTACK":
                            gameManager.message = "'공격형 펫'을 획득했습니다!";
                            break;
                        case "PET_DEFENSE":
                            gameManager.message = "'방어형 펫'을 획득했습니다!";
                            break;
                        case "PET_HEAL":
                            gameManager.message = "'치유형 펫'을 획득했습니다!";
                            break;
                        case "PET_BUFF":
                            gameManager.message = "'버프형 펫'을 획득했습니다!";
                            break;
                        case "WEAPON_FLAMETHROWER":
                            gameManager.playerStats.getWeaponLevels().put("Flamethrower", 1);
                            gameManager.message = "새로운 무기 '화염방사기'를 잠금 해제했습니다!";
                            break;
                        case "WEAPON_LASER":
                            gameManager.playerStats.getWeaponLevels().put("Laser", 1);
                            gameManager.message = "새로운 무기 '레이저'를 잠금 해제했습니다!";
                            break;
                        case "DUPLICATE_WEAPON":
                            gameManager.message = "이미 보유한 무기입니다! 300 크레딧을 돌려받습니다.";
                            break;
                    }
                    gameManager.savePlayerData(); // Save the result of the draw
                }
            }
        }
        @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.drawString("상점", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("상점")) / 2, 80);

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

            if (i == selectedIndex) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }

            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(upgrade.getName(), 100, startY + (i * itemHeight));

            g.setFont(new Font("Dialog", Font.PLAIN, 16));
            g.drawString("레벨: " + currentLevel + " / " + maxLevel, 350, startY + (i * itemHeight));

            String costString = (currentLevel >= maxLevel) ? "최고 레벨" : "비용: " + upgrade.getCost(currentLevel + 1);
            g.drawString(costString, 550, startY + (i * itemHeight));
        }

        // Draw Item Draw Item
        int itemDrawY = startY + (upgrades.size() * itemHeight);
        if (selectedIndex == upgrades.size()) {
            g.setColor(Color.CYAN);
        } else {
            g.setColor(Color.WHITE);
        }
        g.setFont(new Font("Dialog", Font.BOLD, 20));
        g.drawString("뽑기", 100, itemDrawY);

        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.drawString("비용: " + gameManager.shopManager.getItemDrawCost(), 550, itemDrawY);
    }

    @Override
    public void onEnter() {
        gameManager.message = "";
        this.selectedIndex = 0; // Reset selection when entering shop
    }

    @Override
    public void onExit() {}
}
