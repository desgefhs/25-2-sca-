package org.newdawn.spaceinvaders.entity.weapon;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어가 사용할 수 있는 레이저 무기를 구현한 클래스.
 * {@link Weapon} 인터페이스를 구현하며, 긴 빔 형태의 발사체를 발사하고 레벨에 따라
 * 피해량과 재사용 대기시간이 변화합니다.
 */
public class Laser implements Weapon {

    /** 마지막 발사 이후 시간 (밀리초). */
    private long lastFire = 0;
    /** 발사 간격 (재사용 대기시간, 밀리초). */
    private long firingInterval = 1500; // 1.5초 재사용 대기시간
    /** 무기의 현재 레벨. */
    private int level = 1;
    /** 무기의 기본 데미지. */
    private int damage = 3;

    /**
     * 레이저 무기로 발사합니다.
     * @param context 게임 컨텍스트
     * @param owner 무기를 소유한 함선 엔티티
     */
    @Override
    public void fire(GameContext context, ShipEntity owner) {
        if (level == 0) return; // 잠금 해제되지 않은 무기는 발사 불가

        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        int duration = 500; // 500ms 지속 시간
        LaserBeamEntity laserBeam = new LaserBeamEntity(context, owner, duration, damage);
        context.addEntity(laserBeam);
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
     * @param level 설정할 레벨
     */
    @Override
    public void setLevel(int level) {
        this.level = level;
        this.damage = 3 + (level - 1); // 레벨 1당 데미지 1 증가
        this.firingInterval = 1500 - ((level - 1) * 250L); // 레벨 1당 발사 간격 0.25초 감소
    }

    /**
     * 이 무기를 발사할 때 재생될 사운드 이름을 반환합니다.
     * @return 사운드 이름 ("lasershot")
     */
    @Override
    public String getSoundName() {
        return "lasershot";
    }
}
