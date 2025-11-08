package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.util.function.Supplier;

public class EquipWeaponCommand implements Command {

    private final GameContext gameContext;
    private final Supplier<WeaponMenu> weaponMenuSupplier;

    public EquipWeaponCommand(GameContext gameContext, Supplier<WeaponMenu> weaponMenuSupplier) {
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

        gameContext.getSoundManager().playSound("buttonselect");
        PlayerData playerData = gameContext.getPlayerManager().getCurrentPlayer();

        // DefaultGun is always available
        if (selectedWeapon.equals("DefaultGun")) {
            playerData.setEquippedWeapon(selectedWeapon);
            gameContext.savePlayerData();
            gameContext.setMessage(selectedWeapon + " 장착됨");
            return;
        }

        int level = playerData.getWeaponLevels().getOrDefault(selectedWeapon, 0);
        if (level > 0) {
            playerData.setEquippedWeapon(selectedWeapon);
            gameContext.savePlayerData();
            gameContext.setMessage(selectedWeapon + " 장착됨");
        } else {
            gameContext.setMessage("상점에서 먼저 무기를 잠금 해제해야 합니다.");
        }
    }
}