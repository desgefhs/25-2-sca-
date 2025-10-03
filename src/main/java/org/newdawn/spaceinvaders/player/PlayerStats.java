package org.newdawn.spaceinvaders.player;

import java.util.HashMap;
import java.util.Map;

// 상점 업그레이드를 통한 플레이어 상태
public class PlayerStats {

    private int maxHealth;
    private int bulletDamage;
    private long firingInterval;
    private int projectileCount; // 다중발사
    private Map<String, Integer> weaponLevels;

    //기본값( 생성자)
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

    // Getters
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

    // Setters
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
