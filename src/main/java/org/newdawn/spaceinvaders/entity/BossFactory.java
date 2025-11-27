package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.boss.*;

public class BossFactory {

    public static BossEntity createBoss(GameContext context, int waveNumber, int x, int y, int health) {
        return switch (waveNumber) {
            case 5 -> new KrakenBossEntity(context, x, y, health);
            case 10 -> new HydraBossEntity(context, x, y, health);
            case 15 -> new GriffinBossEntity(context, x, y, health);
            case 20 -> new FireHeartBossEntity(context, x, y, health, false); // The main boss is not a mini-boss initially
            case 25 -> new EndBossEntity(context, x, y, health);
            default -> null; // Default or unknown boss wave
        };
    }
}
