package org.newdawn.spaceinvaders.player;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어의 현재 인게임 능력치를 저장하는 클래스
 * 이 능력치들은 PlayerData에 저장된 영구적인 업그레이드 정보를 바탕으로 계산되어 설정
 */
public class PlayerStats {

    /** 최대 체력 */
    private int maxHealth;
    /** 총알 데미지 */
    private int bulletDamage;
    /** 발사 간격 (ms) */
    private long firingInterval;
    /** 한 번에 발사되는 발사체 수 */
    private int projectileCount;
    /** 무기 레벨 맵 (무기 이름, 레벨) */
    private Map<String, Integer> weaponLevels;

    /**
     * PlayerStats 객체를 기본값으로 생성
     */
    public PlayerStats() {
        this.maxHealth = 3;
        this.bulletDamage = 1;
        this.firingInterval = 500;
        this.projectileCount = 1;
        this.weaponLevels = new HashMap<>();
    }

    public int getWeaponLevel(String weaponType) {
        return weaponLevels.getOrDefault(weaponType, 0);
    }

    public void upgradeWeapon(String weaponType) {
        int currentLevel = getWeaponLevel(weaponType);
        weaponLevels.put(weaponType, currentLevel + 1);
    }

    public Map<String, Integer> getWeaponLevels() {
        return weaponLevels;
    }

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
