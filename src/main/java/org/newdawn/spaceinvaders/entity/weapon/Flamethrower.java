package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.FlameParticleEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

import java.util.Random;

public class Flamethrower implements Weapon {

    private long lastFire = 0;
    private long firingInterval = 50; // Fire every 50ms
    private int level = 1;

    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (level == 0) return; // Locked weapon

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        Random random = new Random();
        int particleCount = 3 + level; // More particles at higher levels

        for (int i = 0; i < particleCount; i++) {
            // Add some random spread to the particles
            double spread = (random.nextDouble() - 0.5) * 200; // pixels per second
            double speed = -300 - (level * 20);

            FlameParticleEntity particle = new FlameParticleEntity(context, owner.getX() + 20, owner.getY() - 30, spread, speed);
            context.addEntity(particle);
        }
    }

    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        stats.upgradeWeapon("Flamethrower");
        this.level = stats.getWeaponLevel("Flamethrower");
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}
