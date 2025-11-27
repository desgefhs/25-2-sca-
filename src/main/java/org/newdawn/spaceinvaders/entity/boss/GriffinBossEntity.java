package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

/**
 * '그리핀' 보스 엔티티를 구현한 클래스.
 * {@link BossEntity}를 상속받아 깃털(feather)을 이용한 공격 패턴을 주로 사용합니다.
 */
public class GriffinBossEntity extends BossEntity {

    /**
     * GriffinBossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     */
    public GriffinBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/Grifin.png", x, y, health);
    }

    /**
     * 그리핀 보스가 사용할 공격 패턴을 설정합니다.
     * 깃털 발사 패턴과 깃털 스트림 패턴을 포함합니다.
     */
    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireFeatherPattern);
        availablePatterns.add(this::fireFeatherStreamPattern);
    }
}
