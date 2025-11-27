package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

/**
 * '크라켄' 보스 엔티티를 구현한 클래스.
 * {@link BossEntity}를 상속받아 원형, 3방향, 글로벌 레이저 공격 패턴을 사용합니다.
 */
public class KrakenBossEntity extends BossEntity {

    /**
     * KrakenBossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     */
    public KrakenBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/kraken_anim.gif", x, y, health);
        context.resetItemCollection(); // 아이템 수집 상태 초기화 (크라켄 보스 등장 시)
    }

    /**
     * 크라켄 보스가 사용할 공격 패턴을 설정합니다.
     * 주로 원형, 3방향, 글로벌 레이저 패턴을 사용합니다.
     */
    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireCirclePattern);
        availablePatterns.add(this::fireThreeWayPattern);
        availablePatterns.add(this::fireGlobalLaserPattern);
    }
}
