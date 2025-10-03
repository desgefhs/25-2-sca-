package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class WeaponMenuState implements GameState {

    private final GameManager gameManager;
    private WeaponMenu weaponMenu;

    public WeaponMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {
        PlayerStats stats = gameManager.playerStats;
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
        // Fire/upgrade logic is removed
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
            int level = gameManager.playerStats.getWeaponLevel(weaponName);

            if (i == weaponMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            
            String status = level > 0 ? "Level " + level : "[LOCKED]";
            g.drawString(weaponName + " - " + status, 100, yPos);
            yPos += 40;
        }

        g.setColor(Color.YELLOW);
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
