package org.newdawn.spaceinvaders.player;

import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.view.ShopMenu;
import org.newdawn.spaceinvaders.wave.WaveManager;

/**
 * 플레이어 관련 데이터와 상태를 총괄하는 클래스.
 * 플레이어 데이터 로딩, 스탯 계산, 게임 결과 저장 등의 역할을 수행합니다.
 */
public class PlayerManager {

    /** 현재 인증된 사용자 정보. */
    private final AuthenticatedUser user;
    /** 데이터베이스와 상호작용하는 매니저. */
    private final DatabaseManager databaseManager;
    /** 상점 관련 로직을 처리하는 매니저. */
    private final ShopManager shopManager;
    /** 사운드 재생을 관리하는 매니저. */
    private final SoundManager soundManager;
    /** 웨이브 진행을 관리하는 매니저. */
    private final WaveManager waveManager;

    /** 현재 플레이어의 영구 데이터. */
    private PlayerData currentPlayer;
    /** 현재 게임 세션에서 사용될 플레이어의 계산된 스탯. */
    private PlayerStats playerStats;
    /** 현재 게임의 점수. */
    private int score = 0;
    /** 게임 플레이 시작 시간. */
    private long gameStartTime = 0;
    /** 상점 메뉴 UI. */
    private ShopMenu shopMenu;

    /**
     * PlayerManager 생성자.
     *
     * @param user 인증된 사용자 객체
     * @param databaseManager 데이터베이스 매니저
     * @param shopManager 상점 매니저
     * @param soundManager 사운드 매니저
     * @param waveManager 웨이브 매니저
     */
    public PlayerManager(AuthenticatedUser user, DatabaseManager databaseManager, ShopManager shopManager, SoundManager soundManager, WaveManager waveManager) {
        this.user = user;
        this.databaseManager = databaseManager;
        this.shopManager = shopManager;
        this.soundManager = soundManager;
        this.waveManager = waveManager;
        this.playerStats = new PlayerStats();
    }

    /**
     * 데이터베이스에서 플레이어 데이터를 로드하고, 이를 바탕으로 스탯을 계산하여 플레이어를 초기화합니다.
     */
    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
        calculatePlayerStats();
    }

    /**
     * 플레이어의 영구 데이터(업그레이드 레벨 등)를 기반으로 현재 게임 세션에 적용될 스탯을 계산합니다.
     */
    public void calculatePlayerStats() {
        playerStats = new PlayerStats();

        // 먼저, 무기 레벨의 기본값을 설정합니다.
        playerStats.getWeaponLevels().put("DefaultGun", 1);
        playerStats.getWeaponLevels().put("Shotgun", 0);
        playerStats.getWeaponLevels().put("Laser", 0);

        // 그 다음, 저장된 데이터가 있으면 덮어씁니다.
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

    /**
     * 게임이 끝났을 때 호출되어, 현재 점수를 크레딧에 더하고 최고 기록을 갱신한 후 데이터베이스에 저장합니다.
     */
    public void saveGameResults() {
        if (user == null || currentPlayer == null) return;
        currentPlayer.setCredit(currentPlayer.getCredit() + score);
        currentPlayer.setHighScore(Math.max(currentPlayer.getHighScore(), score));
        savePlayerData();
    }

    /**
     * 현재 플레이어 데이터를 데이터베이스에 저장합니다.
     */
    public void savePlayerData() {
        if (user == null || currentPlayer == null) return;
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    /**
     * 현재 점수를 증가시킵니다.
     *
     * @param amount 증가시킬 점수
     */
    public void increaseScore(int amount) {
        score += amount;
    }

    /**
     * 현재 점수를 0으로 리셋합니다.
     */
    public void resetScore() {
        score = 0;
    }

    /**
     * 현재 플레이어의 영구 데이터를 반환합니다.
     *
     * @return 현재 플레이어 데이터
     */
    public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * 현재 게임 세션의 플레이어 스탯을 반환합니다.
     *
     * @return 플레이어 스탯
     */
    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    /**
     * 현재 점수를 반환합니다.
     *
     * @return 현재 점수
     */
    public int getScore() {
        return score;
    }

    /**
     * 게임 플레이 시작 시간을 반환합니다.
     *
     * @return 게임 시작 시간 (타임스탬프)
     */
    public long getGameStartTime() {
        return gameStartTime;
    }

    /**
     * 게임 플레이 시작 시간을 설정합니다.
     *
     * @param gameStartTime 게임 시작 시간 (타임스탬프)
     */
    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    /**
     * 실제 게임 플레이를 시작하기 위한 초기 작업을 수행합니다.
     * 배경 음악을 변경하고, 게임 시작 시간을 기록하며, 스탯을 계산하고, 점수를 리셋하며 첫 웨이브를 시작합니다.
     */
    public void startGameplay() {
        soundManager.stopSound("menubackground");
        setGameStartTime(System.currentTimeMillis());
        calculatePlayerStats();
        resetScore();
        waveManager.startFirstWave();
    }

    /**
     * 상점 메뉴 UI 객체를 설정합니다.
     *
     * @param shopMenu 상점 메뉴
     */
    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }
}
