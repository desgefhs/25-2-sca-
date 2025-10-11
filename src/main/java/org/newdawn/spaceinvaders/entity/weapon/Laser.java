package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 지속적인 레이저 빔을 발사하는 레이저 무기 클래스
 * 이 무기는 자체적인 레벨 시스템을 가지며, 레벨에 따라 데미지와 발사 간격이 결정
 */
public class Laser implements Weapon {

    /** 마지막 발사 시간 */
    private long lastFire = 0;
    /** 발사 간격 (쿨다운) */
    private long firingInterval = 1500; // 기본 1.5초
    /** 무기 레벨 */
    private int level = 1;
    /** 데미지 */
    private int damage = 1;

    /**
     * 레이저 빔을 발사
     *
     * @param context 발사된 레이저 빔을 게임에 추가하기 위한 게임 컨텍스트
     * @param owner   무기를 발사하는 우주선 엔티티
     */
    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (level == 0) return; // 잠금 상태의 무기

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        int duration = 500; // 0.5초 지속
        LaserBeamEntity laserBeam = new LaserBeamEntity(context, owner, duration, damage);
        context.addEntity(laserBeam);
    }

    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        // 이 무기는 PlayerStats와 독립적인 자체 업그레이드 시스템을 가짐
    }

    /**
     * 무기의 레벨을 설정하고, 레벨에 따라 데미지와 발사 간격을 조정
     *
     * @param level 설정할 레벨
     */
    @Override
    public void setLevel(int level) {
        this.level = level;
        // 레벨 당 데미지 1 증가
        this.damage = 1 + (level - 1);
        // 레벨 당 발사 간격 0.25초 감소
        this.firingInterval = 1500 - ((level - 1) * 250);
    }

    @Override
    public String getSoundName() {
        return "lasershot";
    }
}
