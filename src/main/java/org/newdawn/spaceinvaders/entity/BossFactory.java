package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.boss.*;

public class BossFactory {

    public static BossEntity createBoss(GameContext context, int waveNumber, int x, int y, int health) {
        return switch (waveNumber) {
            case 5 -> new KrakenBossEntity(context, x, y, health);
            case 10 -> new HydraBossEntity(context, x, y, health);
            case 15 -> new GriffinBossEntity(context, x, y, health);
            case 20 -> new FireHeartBossEntity(context, x, y, health, false); // 메인 보스는 처음에는 미니 보스가 아님
            case 25 -> new EndBossEntity(context, x, y, health);
            default -> null; // 기본 또는 알 수 없는 보스 웨이브
        };
    }
}