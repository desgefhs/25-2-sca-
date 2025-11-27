package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어가 사용할 수 있는 산탄총(Shotgun) 무기를 구현한 클래스.
 * {@link Weapon} 인터페이스를 구현하며, 여러 발의 발사체를 부채꼴 모양으로 발사합니다.
 * 레벨에 따라 발사체 수, 확산 각도, 데미지, 발사 간격이 변화합니다.
 */
public class Shotgun implements Weapon {

    /** 마지막 발사 이후 시간 (밀리초). */
    private long lastFire = 0;
    /** 무기의 현재 레벨. */
    private int level = 1;

    /** 발사 간격 (밀리초). */
    private long firingInterval = 1000;
    /** 발사체 하나당 데미지. */
    private int bulletDamage = 2;

    /** 레벨별 발사체 수. */
    private final int[] numProjectiles = {3, 4, 5, 6, 7};
    /** 레벨별 발사체 확산 각도 (도). */
    private final double[] spreadAngle = {15, 20, 25, 30, 35};

    /**
     * 산탄총으로 발사합니다.
     * 현재 레벨에 따라 발사체 수와 확산 각도를 결정하여 여러 발의 발사체를 생성하고 발사합니다.
     *
     * @param context 게임 컨텍스트
     * @param owner 무기를 소유한 함선 엔티티
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
            // 확산 각도 계산
            double angle = Math.toRadians(i * (currentSpreadAngle / (currentNumProjectiles - 1)) - currentSpreadAngle / 2);
            double dx = Math.sin(angle);
            double dy = -Math.cos(angle);
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, owner.getX() + 10, owner.getY() - 30, dx * moveSpeed, dy * moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
    }

    /**
     * 이 무기는 자체 업그레이드 시스템을 가지므로, {@link org.newdawn.spaceinvaders.player.PlayerStats}를
     * 통한 일반적인 업그레이드 로직은 적용되지 않습니다.
     * @param stats 플레이어 스탯 (사용되지 않음)
     */
    @Override
    public void upgrade(org.newdawn.spaceinvaders.player.PlayerStats stats) {
        // 이 무기는 PlayerStats와 독립적인 자체 업그레이드 시스템을 가집니다.
    }

    /**
     * 무기의 레벨을 설정하고, 이에 따라 데미지와 발사 간격을 업데이트합니다.
     *
     * @param level 설정할 레벨 (1부터 {@link #numProjectiles} 배열의 길이까지)
     */
    @Override
    public void setLevel(int level) {
        if (level > 0 && level <= numProjectiles.length) {
            this.level = level;
            this.bulletDamage = 2 + (level - 1); // 레벨 1당 데미지 1 증가
            this.firingInterval = 1000 - ((level - 1) * 50L); // 레벨 1당 발사 간격 0.05초 감소
        }
    }

    /**
     * 이 무기를 발사할 때 재생될 사운드 이름을 반환합니다.
     * @return 사운드 이름 ("shotgun")
     */
    @Override
    public String getSoundName() {
        return "shotgun";
    }
}