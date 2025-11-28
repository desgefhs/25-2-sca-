package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

/**
 * 플레이어의 기본 총을 구현한 클래스.
 * {@link Weapon} 인터페이스를 구현하며, 플레이어의 스탯과 버프에 따라 발사 속도, 데미지,
 * 발사체 수가 동적으로 결정됩니다.
 */
public class DefaultGun implements Weapon {

    /** 마지막 발사 이후 시간 (밀리초). */
    private long lastFire = 0;

    /**
     * 기본 총으로 발사합니다.
     * 플레이어의 현재 스탯과 활성화된 버프를 고려하여 발사체를 생성하고 발사합니다.
     *
     * @param context 게임 컨텍스트
     */
    @Override
    public void fire(GameContext context, ShipEntity owner) {
        GameManager gm = (GameManager) context;
        PlayerStats stats = gm.getPlayerStats();
        ShipEntity ship = owner;

        long firingInterval = stats.getFiringInterval();
        int bulletDamage = stats.getBulletDamage();
        int projectileCount = stats.getProjectileCount();

        // 버프 적용 (공격력 및 연사 속도 증가)
        if (ship.getBuffManager().hasBuff(org.newdawn.spaceinvaders.player.BuffType.DAMAGE_BOOST)) {
            double buffMultiplier = 1.2;
            firingInterval /= buffMultiplier; // 연사 속도 증가
            bulletDamage *= buffMultiplier;   // 데미지 증가
        }

        // 발사 간격이 지났는지 확인
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;
        double moveSpeed = type.moveSpeed;

        // 플레이어 스탯에 따른 발사체 수만큼 발사
        for (int i = 0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15; // 다중 발사 시 중앙 기준 분산
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, ship.getX() + 10 + xOffset, ship.getY() - 30, 0, -moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
    }

    /**
     * 이 무기는 PlayerStats를 통해 업그레이드되므로, 별도의 무기 자체 업그레이드 로직은 없습니다.
     *
     * @param stats 플레이어의 현재 스탯
     */
    @Override
    public void upgrade(PlayerStats stats) {
        // DefaultGun의 업그레이드는 일반 PlayerStats (데미지, 연사 속도 등)에 의해 처리됩니다.
    }

    /**
     * 이 무기는 PlayerStats를 통해 레벨이 결정되므로, 내부적으로 레벨을 설정할 필요가 없습니다.
     *
     * @param level 설정할 레벨 (사용되지 않음)
     */
    @Override
    public void setLevel(int level) {
        // 이 무기의 스탯은 PlayerStats에서 파생되므로, 내부 레벨은 필요 없습니다.
    }

    /**
     * 이 무기를 발사할 때 재생될 사운드 이름을 반환합니다.
     * @return 사운드 이름 ("gunshot")
     */
    @Override
    public String getSoundName() {
        return "gunshot";
    }
}
