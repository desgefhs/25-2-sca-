package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

public class GriffinBossEntity extends BossEntity {

    public GriffinBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/Grifin.png", x, y, health);
    }

    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireFeatherPattern);
        availablePatterns.add(this::fireFeatherStreamPattern);
    }
}
