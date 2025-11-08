package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;

public class SwitchWeaponCommand implements Command {

    private final GameContext gameContext;
    private final String weaponName;

    public SwitchWeaponCommand(GameContext gameContext, String weaponName) {
        this.gameContext = gameContext;
        this.weaponName = weaponName;
    }

    @Override
    public void execute() {
        // The default gun is always available. For others, check if the player has upgraded it.
        if (!"DefaultGun".equals(weaponName) && gameContext.getPlayerManager().getPlayerStats().getWeaponLevel(weaponName) <= 0) {
            return; // Player doesn't own this weapon yet.
        }

        Weapon weapon = gameContext.getWeapons().get(weaponName);
        if (weapon == null) {
            return; // Should not happen if weaponName is correct.
        }
        
        ShipEntity ship = gameContext.getShip();
        if (ship == null) {
            return;
        }

        // Set the weapon's level from player stats before equipping
        weapon.setLevel(gameContext.getPlayerManager().getPlayerStats().getWeaponLevel(weaponName));
        ship.setWeapon(weapon);
    }
}
