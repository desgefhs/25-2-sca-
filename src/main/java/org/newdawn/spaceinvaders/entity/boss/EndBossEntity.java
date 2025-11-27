package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;

public class EndBossEntity extends BossEntity {

    private int phase = 1;

    public EndBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/endboss.png", x, y, health);
    }

    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireCirclePattern);
        availablePatterns.add(this::fireThreeWayPattern);
        availablePatterns.add(this::fireGlobalLaserPattern);
        availablePatterns.add(this::fireFollowingShotPattern);
        availablePatterns.add(this::fireCurtainPattern);
        availablePatterns.add(this::fireFeatherPattern);
        availablePatterns.add(this::fireFeatherStreamPattern);
        availablePatterns.add(this::fireTentacleAttackPattern);
    }

    @Override
    public void collidedWith(Entity other) {
        super.collidedWith(other);

        if (health.isAlive()) {
            double maxHealth = health.getHp().getMAX_HP();
            int currentHealth = health.getCurrentHealth();
            if (phase == 1 && currentHealth <= maxHealth * 0.66) {
                phase = 2;
                // Optional: Add phase transition effect here
            } else if (phase == 2 && currentHealth <= maxHealth * 0.33) {
                phase = 3;
                // Optional: Add phase transition effect here
            }
        }
    }
}
