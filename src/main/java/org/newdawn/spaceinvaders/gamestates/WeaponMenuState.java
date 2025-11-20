package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.userinput.WeaponMenuInputHandler;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeaponMenuState implements GameState {

    private final GameContext gameContext;
    private WeaponMenu weaponMenu;
    private final WeaponMenuInputHandler inputHandler;
    private final Map<String, Sprite> weaponSprites = new HashMap<>();

    public WeaponMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
        // The weaponMenu is initialized in onEnter/init, so the handler is also created there.
        // This is slightly different from other states, but necessary because the menu depends on dynamic data.
        this.inputHandler = new WeaponMenuInputHandler(gameContext, () -> this.weaponMenu);
    }

    @Override
    public void init() {
        ArrayList<String> weaponNames = new ArrayList<>();
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
        inputHandler.handle(input);
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
            int level = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getWeaponLevels().getOrDefault(weaponName, 0);
            String equippedWeapon = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getEquippedWeapon();

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

            int level = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getWeaponLevels().getOrDefault(selectedWeapon, 0);
            boolean isUpgradeableWeapon = selectedWeapon.equals("Shotgun") || selectedWeapon.equals("Laser");

            if (isUpgradeableWeapon && level > 0) {
                int buttonX = 550;
                int buttonY = 270;
                int buttonWidth = 150;
                int buttonHeight = 50;

                if (level < 5) {
                    int cost = getUpgradeCost(selectedWeapon, level + 1);
                    g.setColor(Color.YELLOW);
                    g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
                    g.setFont(new Font("Dialog", Font.BOLD, 16));
                    String upgradeText = "Upgrade (U)";
                    int textWidth = g.getFontMetrics().stringWidth(upgradeText);
                    g.drawString(upgradeText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 20);
                    g.setFont(new Font("Dialog", Font.PLAIN, 14));
                    String costText = "Cost: " + cost;
                    textWidth = g.getFontMetrics().stringWidth(costText);
                    g.drawString(costText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 40);
                } else {
                    g.setColor(Color.GRAY);
                    g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
                    g.setFont(new Font("Dialog", Font.BOLD, 16));
                    String maxLevelText = "Max Level";
                    int textWidth = g.getFontMetrics().stringWidth(maxLevelText);
                    g.drawString(maxLevelText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 30);
                }

                g.setFont(new Font("Dialog", Font.PLAIN, 14));
                g.setColor(Color.LIGHT_GRAY);
                String description1 = "";
                String description2 = "";
                if (selectedWeapon.equals("Shotgun")) {
                    int[] numProjectiles = {3, 4, 5, 6, 7};
                    double[] spreadAngle = {15, 20, 25, 30, 35};
                    description1 = "탄환 " + numProjectiles[level-1] + "개, " + spreadAngle[level-1] + "도 각도로 발사";
                    if (level < 5) description2 = "다음 레벨: 탄환 " + numProjectiles[level] + "개";
                } else if (selectedWeapon.equals("Laser")) {
                    int damage = 3 + (level - 1);
                    long interval = 1500 - ((level - 1) * 250L);
                    description1 = "데미지 " + damage + ", 쿨타임 " + (interval/1000.0) + "초";
                    if (level < 5) description2 = "다음 레벨: 데미지 " + (damage + 1);
                }
                g.drawString(description1, buttonX, buttonY + buttonHeight + 25);
                if (level < 5) g.drawString(description2, buttonX, buttonY + buttonHeight + 45);
            }
        }

        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString(gameContext.getMessage(), 50, 500);
        }

        g.setColor(Color.GRAY);
        g.drawString("Go to the Shop to unlock new weapons.", 50, 450);
    }

    private int getUpgradeCost(String weapon, int level) {
        if (weapon.equals("Shotgun")) {
            switch (level) {
                case 2: return 1000;
                case 3: return 2000;
                case 4: return 4000;
                case 5: return 8000;
            }
        } else if (weapon.equals("Laser")) {
            switch (level) {
                case 2: return 1000;
                case 3: return 2000;
                case 4: return 4000;
                case 5: return 8000;
            }
        }
        return 999999;
    }

    @Override
    public void onEnter() {
        init();
        gameContext.setMessage("");
    }

    @Override
    public void onExit() {
        gameContext.setMessage("");
    }
}