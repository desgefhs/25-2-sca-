package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.*;
import java.util.ArrayList;

public class WeaponMenuState implements GameState {

    private final GameManager gameManager;
    private WeaponMenu weaponMenu;

    public WeaponMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {
        ArrayList<String> weaponNames = new ArrayList<>();
        // Show all weapons, indicating their status
        weaponNames.add("DefaultGun");
        weaponNames.add("Flamethrower");
        weaponNames.add("Laser");
        this.weaponMenu = new WeaponMenu(weaponNames);
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

        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString(gameManager.message, 50, 500);
        }

        g.setColor(Color.GRAY);
        g.drawString("Go to the Shop to unlock new weapons.", 50, 450);
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
