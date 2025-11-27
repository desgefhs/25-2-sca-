package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

public class KrakenBossEntity extends BossEntity {

    public KrakenBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/kraken_anim.gif", x, y, health);
        context.resetItemCollection();
    }

    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireCirclePattern);
        availablePatterns.add(this::fireThreeWayPattern);
        availablePatterns.add(this::fireGlobalLaserPattern);
    }
}
