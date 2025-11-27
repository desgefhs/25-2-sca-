package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.util.function.Function;
import java.util.function.Supplier;

public class UpgradeWeaponCommand implements Command {

    private final GameContext gameContext;
    private final Supplier<WeaponMenu> weaponMenuSupplier;

    public UpgradeWeaponCommand(GameContext gameContext, Supplier<WeaponMenu> weaponMenuSupplier) {
        this.gameContext = gameContext;
        this.weaponMenuSupplier = weaponMenuSupplier;
    }

    @Override
    public void execute() {
        WeaponMenu weaponMenu = weaponMenuSupplier.get();
        if (weaponMenu == null) {
            return;
        }
        
        String selectedWeapon = weaponMenu.getSelectedItem();
        if (selectedWeapon == null) {
            return;
        }

        if (selectedWeapon.equals("Shotgun")) {
            upgradeSpecificWeapon("Shotgun", 5, this::getShotgunUpgradeCost);
        } else if (selectedWeapon.equals("Laser")) {
            upgradeSpecificWeapon("Laser", 5, this::getLaserUpgradeCost);
        }
    }

    private void upgradeSpecificWeapon(String weaponName, int maxLevel, Function<Integer, Integer> costFunction) {
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        int currentLevel = playerData.getWeaponLevels().getOrDefault(weaponName, 0);

        if (currentLevel <= 0) {
            // Cannot upgrade a weapon that is not unlocked
            gameContext.setMessage("상점에서 먼저 무기를 잠금 해제해야 합니다.");
            return;
        }
        
        if (currentLevel >= maxLevel) {
            gameContext.setMessage(weaponName + " is already at max level.");
            return;
        }

        int cost = costFunction.apply(currentLevel + 1);
        if (playerData.getCredit() >= cost) {
            playerData.setCredit(playerData.getCredit() - cost);
            playerData.getWeaponLevels().put(weaponName, currentLevel + 1);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(weaponName + " upgraded to Level " + (currentLevel + 1) + "!");
        } else {
            gameContext.setMessage("Not enough credits!");
        }
    }

    private int getShotgunUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }

    private int getLaserUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }
}