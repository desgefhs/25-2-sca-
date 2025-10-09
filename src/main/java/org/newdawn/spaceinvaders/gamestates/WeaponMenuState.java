package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeaponMenuState implements GameState {

    private final GameManager gameManager;
    private WeaponMenu weaponMenu;
    private Map<String, Sprite> weaponSprites = new HashMap<>();

    public WeaponMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {
        ArrayList<String> weaponNames = new ArrayList<>();
        // Show all weapons, indicating their status
        weaponNames.add("DefaultGun");
        weaponNames.add("Shotgun");
        weaponNames.add("Laser");
        this.weaponMenu = new WeaponMenu(weaponNames);

        weaponSprites.put("DefaultGun", SpriteStore.get().getSprite("sprites/weapon/gun.png"));
        weaponSprites.put("Shotgun", SpriteStore.get().getSprite("sprites/weapon/shotgun.png"));
        weaponSprites.put("Laser", SpriteStore.get().getSprite("sprites/weapon/lasergun.png"));
    }

    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) {
            weaponMenu.moveUp();
        }
        if (input.isDownPressedAndConsume()) {
            weaponMenu.moveDown();
        }
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.MAIN_MENU);
        }

        if (input.isFirePressedAndConsume()) {
            String selectedWeapon = weaponMenu.getSelectedItem();
            PlayerData playerData = gameManager.currentPlayer;

            // DefaultGun is always available
            if (selectedWeapon.equals("DefaultGun")) {
                playerData.setEquippedWeapon(selectedWeapon);
                gameManager.savePlayerData();
                gameManager.message = selectedWeapon + " 장착됨";
                return;
            }

            int level = playerData.getWeaponLevels().getOrDefault(selectedWeapon, 0);
            if (level > 0) {
                playerData.setEquippedWeapon(selectedWeapon);
                gameManager.savePlayerData();
                gameManager.message = selectedWeapon + " 장착됨";
            } else {
                gameManager.message = "상점에서 먼저 무기를 잠금 해제해야 합니다.";
            }
        }

        if (input.isUPressedAndConsume()) { // Assuming 'U' key for upgrade
            String selectedWeapon = weaponMenu.getSelectedItem();
            if (selectedWeapon.equals("Shotgun")) {
                PlayerData playerData = gameManager.currentPlayer;
                int currentLevel = playerData.getWeaponLevels().getOrDefault("Shotgun", 0);
                if (currentLevel > 0 && currentLevel < 5) { // Max level 5 for shotgun
                    int cost = getShotgunUpgradeCost(currentLevel + 1);
                    if (playerData.getCredit() >= cost) {
                        playerData.setCredit(playerData.getCredit() - cost);
                        playerData.getWeaponLevels().put("Shotgun", currentLevel + 1);
                        gameManager.savePlayerData();
                        gameManager.message = "Shotgun upgraded to Level " + (currentLevel + 1) + "!";
                    } else {
                        gameManager.message = "Not enough credits!";
                    }
                } else if (currentLevel >= 5) {
                    gameManager.message = "Shotgun is already at max level.";
                }
            } else if (selectedWeapon.equals("Laser")) {
                PlayerData playerData = gameManager.currentPlayer;
                int currentLevel = playerData.getWeaponLevels().getOrDefault("Laser", 0);
                if (currentLevel > 0 && currentLevel < 5) { // Max level 5 for laser
                    int cost = getLaserUpgradeCost(currentLevel + 1);
                    if (playerData.getCredit() >= cost) {
                        playerData.setCredit(playerData.getCredit() - cost);
                        playerData.getWeaponLevels().put("Laser", currentLevel + 1);
                        gameManager.savePlayerData();
                        gameManager.message = "Laser upgraded to Level " + (currentLevel + 1) + "!";
                    } else {
                        gameManager.message = "Not enough credits!";
                    }
                } else if (currentLevel >= 5) {
                    gameManager.message = "Laser is already at max level.";
                }
            }
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        g.drawString("Weapon Locker", 50, 50);

        // Draw weapon list
        g.setFont(new Font("Dialog", Font.PLAIN, 18));
        int yPos = 100;
        for (int i = 0; i < weaponMenu.getItems().size(); i++) {
            String weaponName = weaponMenu.getItems().get(i);
            int level = gameManager.currentPlayer.getWeaponLevels().getOrDefault(weaponName, 0);
            String equippedWeapon = gameManager.currentPlayer.getEquippedWeapon();

            if (i == weaponMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }

            String status;
            if (weaponName.equals("DefaultGun")) {
                status = "기본 무기";
            } else {
                status = level > 0 ? "Level " + level : "[LOCKED]";
            }

            String displayText = weaponName + " - " + status;
            if (weaponName.equals(equippedWeapon)) {
                g.setColor(Color.CYAN);
                displayText += " [EQUIPPED]";
            }

            g.drawString(displayText, 100, yPos);
            yPos += 40;
        }

        // Draw selected weapon info
        String selectedWeapon = weaponMenu.getSelectedItem();
        if (selectedWeapon != null) {
            // Draw weapon image
            Sprite sprite = weaponSprites.get(selectedWeapon);
            if (sprite != null) {
                int boxX = 550;
                int boxY = 100;
                int boxWidth = 150;
                int boxHeight = 150;

                g.setColor(Color.DARK_GRAY);
                g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);

                sprite.draw(g, boxX, boxY, boxWidth, boxHeight);
            }

            // Draw upgrade button
            int level = gameManager.currentPlayer.getWeaponLevels().getOrDefault(selectedWeapon, 0);
            boolean isUpgradeableWeapon = selectedWeapon.equals("Shotgun") || selectedWeapon.equals("Laser");

            if (isUpgradeableWeapon && level > 0) { // Show button if unlocked
                int buttonX = 550;
                int buttonY = 270;
                int buttonWidth = 150;
                int buttonHeight = 50;

                if (level < 5) { // Upgradeable
                    int cost = selectedWeapon.equals("Shotgun") ? getShotgunUpgradeCost(level + 1) : getLaserUpgradeCost(level + 1);
                    
                    g.setColor(Color.YELLOW); // Active color
                    g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);

                    g.setFont(new Font("Dialog", Font.BOLD, 16));
                    String upgradeText = "Upgrade (U)";
                    int textWidth = g.getFontMetrics().stringWidth(upgradeText);
                    g.drawString(upgradeText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 20);

                    g.setFont(new Font("Dialog", Font.PLAIN, 14));
                    String costText = "Cost: " + cost;
                    textWidth = g.getFontMetrics().stringWidth(costText);
                    g.drawString(costText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 40);

                } else { // Max level
                    g.setColor(Color.GRAY); // Disabled color
                    g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);

                    g.setFont(new Font("Dialog", Font.BOLD, 16));
                    String maxLevelText = "Max Level";
                    int textWidth = g.getFontMetrics().stringWidth(maxLevelText);
                    g.drawString(maxLevelText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 30);
                }
            }
        }


        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString(gameManager.message, 50, 500);
        }

        g.setColor(Color.GRAY);
        g.drawString("Go to the Shop to unlock new weapons.", 50, 450);
    }

    public int getShotgunUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }

    public int getLaserUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }

    @Override
    public void onEnter() {
        init(); // Re-initialize to get the latest weapon stats
        gameManager.message = "";
    }

    @Override
    public void onExit() {
        gameManager.message = "";
    }
}
