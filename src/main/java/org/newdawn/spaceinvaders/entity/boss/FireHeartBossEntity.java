package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Enemy.TentacleAttackEntity;
import org.newdawn.spaceinvaders.core.Game;

public class FireHeartBossEntity extends BossEntity {

    private final boolean isMiniBoss;
    private boolean hasSplit = false;

    public FireHeartBossEntity(GameContext context, int x, int y, int health, boolean isMiniBoss) {
        super(context, "sprites/bosses/fireheart.png", x, y, health);
        this.isMiniBoss = isMiniBoss;
        if (isMiniBoss) {
            setScale(1.5);
        } else {
            setScale(2.5);
        }
    }

    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireTentacleAttackPattern);
    }

    @Override
    protected void fireTentacleAttackPattern() {
        int numberOfAttacks = isMiniBoss ? 12 : 6;
        for (int i = 0; i < numberOfAttacks; i++) {
            int randomX = (int) (Math.random() * (Game.GAME_WIDTH - 100)) + 50;
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100;
            context.addEntity(new TentacleAttackEntity(context, randomX, randomY));
        }
    }

    @Override
    public void collidedWith(Entity other) {
        super.collidedWith(other);

        // Splitting logic, only if the boss is still alive after the collision
        if (health.isAlive() && !hasSplit && !isMiniBoss && health.getCurrentHealth() <= health.getHp().getMAX_HP() / 2) {
            hasSplit = true;
            splitIntoMiniBosses();
            context.removeEntity(this); // Remove the main boss
        }
    }

    private void splitIntoMiniBosses() {
        int miniBossHealth = (int) (health.getHp().getMAX_HP() / 2);

        // Left mini-boss
        FireHeartBossEntity miniBoss1 = new FireHeartBossEntity(context, getX() - 50, getY(), miniBossHealth, true);
        context.addEntity(miniBoss1);

        // Right mini-boss
        FireHeartBossEntity miniBoss2 = new FireHeartBossEntity(context, getX() + 50, getY(), miniBossHealth, true);
        context.addEntity(miniBoss2);
    }
}
