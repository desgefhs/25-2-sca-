package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

/**
 * 기본 무기(Default Gun)를 구현한 클래스
 * 발사 간격, 데미지, 발사체 수 등은 플레이어의 현재 능력치(PlayerStats)에 따라 결정
 */
public class DefaultGun implements Weapon {

    /** 마지막 발사 시간 */
    private long lastFire = 0;

    /**
     * 무기를 발사
     * 플레이어의 능력치와 버프 상태에 따라 발사 간격, 데미지, 발사체 수가 조절
     *
     * @param context 발사된 발사체를 게임에 추가하기 위한 게임 컨텍스트
     * @param owner   무기를 발사하는 우주선 엔티티
     */
    @Override
    public void fire(GameContext context, ShipEntity owner) {
        GameManager gm = (GameManager) context;
        PlayerStats stats = gm.playerStats;

        long firingInterval = stats.getFiringInterval();
        int bulletDamage = stats.getBulletDamage();
        int projectileCount = stats.getProjectileCount();

        // 데미지 부스트 버프가 있을 경우 능력치 강화
        if (owner.getBuffManager().hasBuff(org.newdawn.spaceinvaders.player.BuffType.DAMAGE_BOOST)) {
            double buffMultiplier = 1.20 + (owner.getBuffManager().getBuffLevel(org.newdawn.spaceinvaders.player.BuffType.DAMAGE_BOOST) * 0.01);
            firingInterval /= buffMultiplier; // 발사 속도 증가
            bulletDamage *= buffMultiplier;   // 데미지 증가
        }

        // 발사 간격 확인
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }

        lastFire = System.currentTimeMillis();
        ProjectileType type = ProjectileType.PLAYER_SHOT;

        // 발사체 수만큼 발사
        for (int i = 0; i < projectileCount; i++) {
            int xOffset = (i - projectileCount / 2) * 15;
            ProjectileEntity shot = new ProjectileEntity(context, type, bulletDamage, owner.getX() + 10 + xOffset, owner.getY() - 30, 0, -type.moveSpeed);
            shot.setScale(1);
            context.addEntity(shot);
        }
    }

    @Override
    public void upgrade(PlayerStats stats) {
        stats.upgradeWeapon("DefaultGun");
        // DefaultGun의 업그레이드는 PlayerStats(데미지, 발사 속도 등)를 통해 직접 처리
    }

    @Override
    public void setLevel(int level) {
        // 이 무기의 능력치는 PlayerStats에서 관리
    }

    @Override
    public String getSoundName() {
        return "gunshot";
    }
}
