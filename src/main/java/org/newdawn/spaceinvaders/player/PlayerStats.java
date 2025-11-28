package org.newdawn.spaceinvaders.player;

import java.util.HashMap;
import java.util.Map;

/**
 * 현재 게임 세션 동안의 플레이어 스탯을 저장하는 데이터 클래스.
 * 상점 업그레이드나 버프 효과가 모두 계산된 최종 스탯을 가집니다.
 */
public class PlayerStats {

    /** 플레이어의 최대 체력. */
    private int maxHealth;
    /** 플레이어의 기본 총알 데미지. */
    private int bulletDamage;
    /** 발사 간격 (밀리초). 낮을수록 연사 속도가 빠릅니다. */
    private long firingInterval;
    /** 한 번에 발사되는 발사체 수. */
    private int projectileCount;
    /** 소유한 무기들의 레벨. */
    private final Map<String, Integer> weaponLevels;

    /**
     * PlayerStats 생성자.
     * 모든 스탯을 기본값으로 초기화합니다.
     */
    public PlayerStats() {
        this.maxHealth = 3;
        this.bulletDamage = 1;
        this.firingInterval = 500;
        this.projectileCount = 1;
        this.weaponLevels = new HashMap<>();
    }

    /**
     * 특정 무기의 현재 레벨을 가져옵니다.
     *
     * @param weaponType 레벨을 조회할 무기 타입
     * @return 해당 무기의 레벨. 소유하지 않은 경우 0.
     */
    public int getWeaponLevel(String weaponType) {
        return weaponLevels.getOrDefault(weaponType, 0);
    }

    /**
     * 특정 무기의 레벨을 1 올립니다.
     *
     * @param weaponType 업그레이드할 무기 타입
     */
    public void upgradeWeapon(String weaponType) {
        int currentLevel = getWeaponLevel(weaponType);
        weaponLevels.put(weaponType, currentLevel + 1);
    }

    /**
     * 모든 무기의 레벨 정보를 담은 맵을 반환합니다.
     *
     * @return 무기 타입과 레벨을 매핑한 맵
     */
    public Map<String, Integer> getWeaponLevels() {
        return weaponLevels;
    }

    // Getter 메소드
    public int getMaxHealth() {
        return maxHealth;
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public long getFiringInterval() {
        return firingInterval;
    }

    public int getProjectileCount() {
        return projectileCount;
    }

    // Setter 메소드
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setBulletDamage(int bulletDamage) {
        this.bulletDamage = bulletDamage;
    }

    public void setFiringInterval(long firingInterval) {
        this.firingInterval = firingInterval;
    }

    public void setProjectileCount(int projectileCount) {
        this.projectileCount = projectileCount;
    }
}
