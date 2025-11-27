package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.boss.*;

/**
 * 웨이브 번호에 따라 다른 보스 엔티티를 생성하는 팩토리 클래스.
 * 보스 생성 로직을 캡슐화하고, {@link BossEntity}의 구체적인 구현체들을 반환합니다.
 */
public class BossFactory {

    /**
     * 지정된 웨이브 번호에 해당하는 보스 엔티티를 생성합니다.
     *
     * @param context 게임 컨텍스트
     * @param waveNumber 현재 웨이브 번호 (보스 타입 결정에 사용)
     * @param x 보스의 초기 x 좌표
     * @param y 보스의 초기 y 좌표
     * @param health 보스의 초기 체력
     * @return 생성된 {@link BossEntity} 객체. 해당하는 보스가 없으면 null.
     */
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