package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

public class HydraBossEntity extends BossEntity {

    public HydraBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/Hydra.png", x, y, health);
        this.dy = 50; // Enable vertical movement
    }

    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireFollowingShotPattern);
        availablePatterns.add(this::fireCurtainPattern);
    }

    @Override
    public void move(long delta) {
        // Vertical bouncing
        if ((dy < 0) && (y < 0)) {
            dy = -dy;
        }
        if ((dy > 0) && (y > 250)) {
            dy = -dy;
        }
        super.move(delta);
    }
}
