package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;

/**
 * 게임의 최종 보스 엔티티를 구현한 클래스.
 * {@link BossEntity}를 상속받아 다양한 공격 패턴을 사용하며, 체력에 따른 페이즈(phase) 변화 로직을 가집니다.
 */
public class EndBossEntity extends BossEntity {

    /** 보스의 현재 전투 페이즈. */
    private int phase = 1;

    /**
     * EndBossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     */
    public EndBossEntity(GameContext context, int x, int y, int health) {
        super(context, "sprites/bosses/endboss.png", x, y, health);
    }

    /**
     * 최종 보스가 사용할 수 있는 모든 공격 패턴을 설정합니다.
     */
    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireCirclePattern);
        availablePatterns.add(this::fireThreeWayPattern);
        availablePatterns.add(this::fireGlobalLaserPattern);
        availablePatterns.add(this::fireFollowingShotPattern);
        availablePatterns.add(this::fireCurtainPattern);
        availablePatterns.add(this::fireFeatherPattern);
        availablePatterns.add(this::fireFeatherStreamPattern);
        availablePatterns.add(this::fireTentacleAttackPattern);
    }

    /**
     * 다른 엔티티와의 충돌을 처리하고, 체력 감소에 따른 보스 페이즈 전환 로직을 포함합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        super.collidedWith(other);

        if (health.isAlive()) {
            double maxHealth = health.getHp().getMAX_HP();
            int currentHealth = health.getCurrentHealth();
            if (phase == 1 && currentHealth <= maxHealth * 0.66) {
                phase = 2;
                // 선택 사항: 페이즈 전환 효과 추가
            } else if (phase == 2 && currentHealth <= maxHealth * 0.33) {
                phase = 3;
                // 선택 사항: 페이즈 전환 효과 추가
            }
        }
    }
}
