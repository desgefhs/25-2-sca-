package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class Shotgun implements Weapon {

    private long lastFire = 0;
    private int level = 1;

    private long firingInterval = 1000; // ms
    private int bulletDamage = 2;

    private final int[] numProjectiles = {3, 4, 5, 6, 7};
    private final double[] spreadAngle = {15, 20, 25, 30, 35}; // Degrees

    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;
        double moveSpeed = type.moveSpeed;

        int currentNumProjectiles = numProjectiles[level - 1];
        double currentSpreadAngle = spreadAngle[level - 1];

        for (int i = 0; i < currentNumProjectiles; i++) {
            double angle = Math.toRadians(i * (currentSpreadAngle / (currentNumProjectiles - 1)) - currentSpreadAngle / 2);
            double dx = Math.sin(angle);
            double dy = -Math.cos(angle);
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, owner.getX() + 10, owner.getY() - 30, dx * moveSpeed, dy * moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
    }

    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        // This weapon will have its own upgrade system, independent of PlayerStats
    }

    @Override
    public void setLevel(int level) {
        if (level > 0 && level <= numProjectiles.length) {
            this.level = level;
            this.bulletDamage = 2 + (level - 1);
            this.firingInterval = 1000 - ((level - 1) * 50L);
        }
    }

    @Override
    public String getSoundName() {
        return "shotgun";
    }
}