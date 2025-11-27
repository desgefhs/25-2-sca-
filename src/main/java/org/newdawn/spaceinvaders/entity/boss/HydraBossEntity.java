package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;

/**
 * '히드라' 보스 엔티티를 구현한 클래스.
 * {@link BossEntity}를 상속받아 수직으로 움직이며, 특정 공격 패턴을 사용합니다.
 */
public class HydraBossEntity extends BossEntity {

    /**
     * HydraBossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     */
    public HydraBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/Hydra.png", x, y, health);
        this.dy = 50; // 수직 이동 활성화
    }

    /**
     * 히드라 보스가 사용할 공격 패턴을 설정합니다.
     * 주로 추적탄 패턴과 커튼 패턴을 사용합니다.
     */
    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireFollowingShotPattern);
        availablePatterns.add(this::fireCurtainPattern);
    }

    /**
     * 히드라 보스의 이동 로직을 처리합니다.
     * 수직 방향으로 튕기면서 이동합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    @Override
    public void move(long delta) {
        // 수직으로 튕김
        if ((dy < 0) && (y < 0)) {
            dy = -dy;
        }
        if ((dy > 0) && (y > 250)) {
            dy = -dy;
        }
        super.move(delta);
    }
}
