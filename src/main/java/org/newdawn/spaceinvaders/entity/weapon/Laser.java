package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class Laser implements Weapon {

    private long lastFire = 0;
    private long firingInterval = 1500; // 1.5 seconds cooldown
    private int level = 1;

    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (level == 0) return; // Locked weapon

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        int damage = 5 + (level * 2);
        ProjectileEntity laserShot = new ProjectileEntity(context, ProjectileType.LASER_SHOT, damage, owner.getX() + 22, owner.getY() - 30, 0, -ProjectileType.LASER_SHOT.moveSpeed);
        context.addEntity(laserShot);
    }

    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        stats.upgradeWeapon("Laser");
        this.level = stats.getWeaponLevel("Laser");
        this.firingInterval = 1500 - (level * 100);
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        this.firingInterval = 1500 - (level * 100);
    }
}
