package org.newdawn.spaceinvaders.player;

/**
 * Holds the calculated, final stats for the player for the current game session.
 * These stats are derived from the player's upgrade levels.
 */
public class PlayerStats {

    private int maxHealth;
    private int bulletDamage;
    private long firingInterval;
    private int projectileCount;
    private String weaponType; // Using String for flexibility, could be an Enum

    /**
     * Initializes with default, non-upgraded values.
     */
    public PlayerStats() {
        this.maxHealth = 3; // Default health
        this.bulletDamage = 1; // Default damage
        this.firingInterval = 500; // Default interval
        this.projectileCount = 1; // Default projectile count
        this.weaponType = "NORMAL"; // Default weapon type
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

    public String getWeaponType() {
        return weaponType;
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

    public void setWeaponType(String weaponType) {
        this.weaponType = weaponType;
    }
}
