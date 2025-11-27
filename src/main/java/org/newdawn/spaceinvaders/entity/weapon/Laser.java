package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class Laser implements Weapon {

    private long lastFire = 0;
    private long firingInterval = 1500; // 1.5 seconds cooldown
    private int level = 1;
    private int damage = 3;

    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (level == 0) return; // Locked weapon

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        int duration = 500; // 500ms duration
        LaserBeamEntity laserBeam = new LaserBeamEntity(context, owner, duration, damage);
        context.addEntity(laserBeam);
    }

    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        // This weapon will have its own upgrade system, independent of PlayerStats
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
        this.damage = 3 + (level - 1);
        this.firingInterval = 1500 - ((level - 1) * 250L);
    }

    @Override
    public String getSoundName() {
        return "lasershot";
    }
}
