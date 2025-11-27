package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.view.ShopMenu;
import org.newdawn.spaceinvaders.wave.WaveManager;

public class PlayerManager {

    private final AuthenticatedUser user;
    private final DatabaseManager databaseManager;
    private final ShopManager shopManager;
    private final SoundManager soundManager;
    private final WaveManager waveManager;

    private PlayerData currentPlayer;
    private PlayerStats playerStats;
    private int score = 0;
    private long gameStartTime = 0;
    private ShopMenu shopMenu;

    public PlayerManager(AuthenticatedUser user, DatabaseManager databaseManager, ShopManager shopManager, SoundManager soundManager, WaveManager waveManager) {
        this.user = user;
        this.databaseManager = databaseManager;
        this.shopManager = shopManager;
        this.soundManager = soundManager;
        this.waveManager = waveManager;
        this.playerStats = new PlayerStats();
    }

    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
        calculatePlayerStats();
    }

    public void calculatePlayerStats() {
        playerStats = new PlayerStats();

        // First, set the defaults for weapon levels
        playerStats.getWeaponLevels().put("DefaultGun", 1);
        playerStats.getWeaponLevels().put("Shotgun", 0);
        playerStats.getWeaponLevels().put("Laser", 0);

        // Then, overwrite with saved data if it exists
        if (currentPlayer.getWeaponLevels() != null && !currentPlayer.getWeaponLevels().isEmpty()) {
            playerStats.getWeaponLevels().putAll(currentPlayer.getWeaponLevels());
        }

        for (Upgrade upgrade : shopManager.getAllUpgrades()) {
            int level = currentPlayer.getUpgradeLevel(upgrade.getId());
            if (level > 0) {
                switch (upgrade.getId()) {
                    case "DAMAGE": playerStats.setBulletDamage((int) upgrade.getEffect(level)); break;
                    case "HEALTH": playerStats.setMaxHealth((int) upgrade.getEffect(level)); break;
                    case "ATK_SPEED": playerStats.setFiringInterval((long) upgrade.getEffect(level)); break;
                    case "PROJECTILE": playerStats.setProjectileCount((int) upgrade.getEffect(level)); break;
                    default:
                        throw new IllegalArgumentException("Unknown upgrade ID: " + upgrade.getId());
                }
            }
        }
    }

    public void saveGameResults() {
        if (user == null || currentPlayer == null) return;
        currentPlayer.setCredit(currentPlayer.getCredit() + score);
        currentPlayer.setHighScore(Math.max(currentPlayer.getHighScore(), score));
        savePlayerData();
    }

    public void savePlayerData() {
        if (user == null || currentPlayer == null) return;
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    public void increaseScore(int amount) {
        score += amount;
    }

    public void resetScore() {
        score = 0;
    }

    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public int getScore() {
        return score;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public void startGameplay() {
        soundManager.stopSound("menubackground");
        setGameStartTime(System.currentTimeMillis());
        calculatePlayerStats();
        resetScore();
        waveManager.startFirstWave();
    }
    
    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }
}
