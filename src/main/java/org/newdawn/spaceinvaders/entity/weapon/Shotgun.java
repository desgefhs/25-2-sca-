package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 여러 발의 총알을 부채꼴 모양으로 발사하는 샷건 무기 클래스
 * 이 무기는 자체적인 레벨 시스템을 가지며, 레벨에 따라 발사되는 총알의 수와 퍼지는 각도가 달라짐
 */
public class Shotgun implements Weapon {

    /** 마지막 발사 시간 */
    private long lastFire = 0;
    /** 무기 레벨 */
    private int level = 1;

    /** 발사 간격 (쿨다운) */
    private long firingInterval = 1000; // ms
    /** 총알 데미지 */
    private int bulletDamage = 2;

    /** 레벨별 발사체 수 */
    private final int[] numProjectiles = {3, 4, 5, 6, 7};
    /** 레벨별 퍼지는 각도 (도) */
    private final double[] spreadAngle = {15, 20, 25, 30, 35};

    /**
     * 샷건을 발사
     * 현재 레벨에 맞는 수의 총알을 부채꼴 모양으로 발사
     *
     * @param context 발사된 발사체를 게임에 추가하기 위한 게임 컨텍스트
     * @param owner   무기를 발사하는 우주선 엔티티
     */
    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;
        double moveSpeed = type.moveSpeed;

        int currentNumProjectiles = numProjectiles[level - 1];
        double currentSpreadAngle = spreadAngle[level - 1];

        for (int i = 0; i < currentNumProjectiles; i++) {
            // 각 총알의 발사 각도 계산
            double angle = Math.toRadians(i * (currentSpreadAngle / (currentNumProjectiles - 1)) - currentSpreadAngle / 2);
            double dx = Math.sin(angle);
            double dy = -Math.cos(angle);
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, owner.getX() + 10, owner.getY() - 30, dx * moveSpeed, dy * moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
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
        if (level > 0 && level <= numProjectiles.length) {
            this.level = level;
            // 레벨 당 데미지 1 증가
            this.bulletDamage = 2 + (level - 1);
            // 레벨 당 발사 간격 100ms 감소
            this.firingInterval = 1000 - ((level - 1) * 100);
        }
    }

    @Override
    public String getSoundName() {
        return "shotgun";
    }
}