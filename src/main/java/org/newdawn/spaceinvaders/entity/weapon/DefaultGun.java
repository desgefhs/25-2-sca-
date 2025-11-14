package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

public class DefaultGun implements Weapon {

    private long lastFire = 0;

    @Override
    public void fire(GameContext context, ShipEntity owner) {
        GameManager gm = (GameManager) context;
        PlayerStats stats = gm.getPlayerStats();

        long firingInterval = stats.getFiringInterval();
        int bulletDamage = stats.getBulletDamage();
        int projectileCount = stats.getProjectileCount();

        if (owner.getBuffManager().hasBuff(org.newdawn.spaceinvaders.player.BuffType.DAMAGE_BOOST)) {
            double buffMultiplier = 1.2;
            firingInterval /= buffMultiplier; // Faster fire rate
            bulletDamage *= buffMultiplier;   // More damage
        }

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;
        double moveSpeed = type.moveSpeed;

        for (int i = 0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, owner.getX() + 10 + xOffset, owner.getY() - 30, 0, -moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
    }

    @Override
    public void upgrade(PlayerStats stats) {
        stats.upgradeWeapon("DefaultGun");
        // DefaultGun's upgrades are handled by general PlayerStats (damage, fire rate, etc.)
    }

    @Override
    public void setLevel(int level) {
        // This weapon's stats are derived from PlayerStats, so no internal level is needed.
    }

    @Override
    public String getSoundName() {
        return "gunshot";
    }
}
