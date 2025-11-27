package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.Enemy.TentacleAttackEntity;
import org.newdawn.spaceinvaders.core.Game;

/**
 * '파이어하트' 보스 엔티티를 구현한 클래스.
 * 특정 체력 이하로 내려가면 두 개의 미니 보스로 분열하는 특수 능력을 가집니다.
 */
public class FireHeartBossEntity extends BossEntity {

    /** 이 인스턴스가 메인 보스인지 미니 보스인지 나타냅니다. */
    private final boolean isMiniBoss;
    /** 메인 보스가 이미 분열했는지 여부. */
    private boolean hasSplit = false;

    /**
     * FireHeartBossEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param health 보스의 초기 체력
     * @param isMiniBoss 이 보스가 미니 보스인지 여부
     */
    public FireHeartBossEntity(GameContext context, int x, int y, int health, boolean isMiniBoss) {
        super(context, "sprites/bosses/fireheart.png", x, y, health);
        this.isMiniBoss = isMiniBoss;
        if (isMiniBoss) {
            setScale(1.5);
        } else {
            setScale(2.5);
        }
    }

    /**
     * 이 보스가 사용할 공격 패턴을 설정합니다.
     * 주로 촉수 공격 패턴을 사용합니다.
     */
    @Override
    protected void setupPatterns() {
        availablePatterns.add(this::fireTentacleAttackPattern);
    }

    /**
     * 보스의 촉수 공격 패턴을 실행합니다.
     * 미니 보스 여부에 따라 공격 횟수가 달라집니다.
     */
    @Override
    protected void fireTentacleAttackPattern() {
        int numberOfAttacks = isMiniBoss ? 12 : 6;
        for (int i = 0; i < numberOfAttacks; i++) {
            int randomX = (int) (Math.random() * (Game.GAME_WIDTH - 100)) + 50;
            int randomY = (int) (Math.random() * (Game.GAME_HEIGHT - 200)) + 100;
            context.addEntity(new TentacleAttackEntity(context, randomX, randomY));
        }
    }

    /**
     * 다른 엔티티와의 충돌을 처리하고, 메인 보스의 경우 체력에 따른 분열 로직을 포함합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        super.collidedWith(other);

        // 분열 로직: 보스가 살아있고, 아직 분열하지 않았으며, 미니 보스가 아니고, 체력이 절반 이하일 때
        if (health.isAlive() && !hasSplit && !isMiniBoss && health.getCurrentHealth() <= health.getHp().getMAX_HP() / 2) {
            hasSplit = true;
            splitIntoMiniBosses();
            context.removeEntity(this); // 메인 보스는 제거
        }
    }

    /**
     * 메인 보스가 두 개의 미니 보스로 분열하는 로직을 실행합니다.
     */
    private void splitIntoMiniBosses() {
        int miniBossHealth = (int) (health.getHp().getMAX_HP() / 2);

        // 왼쪽 미니 보스 생성
        FireHeartBossEntity miniBoss1 = new FireHeartBossEntity(context, getX() - 50, getY(), miniBossHealth, true);
        context.addEntity(miniBoss1);

        // 오른쪽 미니 보스 생성
        FireHeartBossEntity miniBoss2 = new FireHeartBossEntity(context, getX() + 50, getY(), miniBossHealth, true);
        context.addEntity(miniBoss2);
    }
}
