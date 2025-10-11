package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Pet.*;
import org.newdawn.spaceinvaders.entity.boss.BossEntity;
import org.newdawn.spaceinvaders.entity.weapon.DefaultGun;
import org.newdawn.spaceinvaders.entity.weapon.Shotgun;
import org.newdawn.spaceinvaders.entity.weapon.Laser;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.gamestates.*;
import org.newdawn.spaceinvaders.gamestates.PetMenuState;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.view.*;
import org.newdawn.spaceinvaders.wave.Formation;
import org.newdawn.spaceinvaders.wave.FormationManager;


import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임의 전반적인 로직과 상태를 관리하는 핵심 클래스
 * 게임의 모든 주요 구성 요소를 관리
 */
public class GameManager implements GameContext {

    // 핵심 게임 구성 요소

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final GameStateManager gsm;
    /** 사용 가능한 무기 목록 */
    private final Map<String, Weapon> weapons;

    // UI and Data Managers
    public final DatabaseManager databaseManager;
    public final GameWindow gameWindow;
    public final MainMenu mainMenu;
    public final PauseMenu pauseMenu;
    public final GameOverMenu gameOverMenu;
    public final ConfirmDialog confirmDialog;
    public final ShopManager shopManager;
    // 적 배치 관리
    public final FormationManager formationManager;
    // 배경 스크롤 관리
    public final Background background;
    /// 고정된 배경
    public final Sprite staticBackgroundSprite;
    public ShopMenu shopMenu;
    private final SoundManager soundManager;

    // User and Game Data
    // 현재 사용자
    public final AuthenticatedUser user;
    public PlayerData currentPlayer;
    // 현재 플레이어의 능력치
    public PlayerStats playerStats;
    // 다음게임 상태
    public GameState.Type nextState = null;
    // 화면에 표시될 메세지
    public String message = "";
    // 메시지가 사라지는 시간
    private long messageEndTime = 0;
    // 게임 시작 시간
    private long gameStartTime = 0;
    public int score = 0;
    public int wave = 0;

    // Wave Management
    // 웨이브당 배치 수
    public int formationsPerWave;
    // 웨이브에서 생성된 배치 수
    public int formationsSpawnedInWave;
    // 회복 지역 스폰 여부
    public boolean healingAreaSpawnedForWave = false;
    // 다음 배치 생성 시간
    public long nextFormationSpawnTime = 0;

    // Game State Flags
    // 이번 루프에서 로직 업데이트가 필요한지 여부
    public boolean logicRequiredThisLoop = false;
    public boolean showHitboxes = false;
    // 수집한 아이템 수
    public int collectedItems = 0;
    // 플레이어 공격 비활성화 종료
    private long playerAttackDisabledUntil = 0;

    // Game Constants
    // 플레이어 이동 속도
    public final double moveSpeed = 300;
    // 적 처치 시 획득 점수
    public static final int ALIEN_SCORE = 10;


    // 게임 실행 여부
    private boolean gameRunning = true;

    // GameManager 객체를 생성하고 모든 구성 요소를 초기화

    public GameManager(AuthenticatedUser user, Firestore db) {
        this.user = user;
        this.inputHandler = new InputHandler();
        this.databaseManager = new DatabaseManager(db);
        this.entityManager = new EntityManager(this);
        this.gameWindow = new GameWindow(inputHandler);
        this.mainMenu = new MainMenu();
        this.pauseMenu = new PauseMenu();
        this.gameOverMenu = new GameOverMenu();
        this.shopManager = new ShopManager();
        this.formationManager = new FormationManager();
        this.soundManager = new SoundManager();

        this.background = new Background("sprites/gamebackground.png");
        this.staticBackgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");
        this.playerStats = new PlayerStats();
        this.confirmDialog = new ConfirmDialog("Are you sure you want to exit?");

        this.weapons = new HashMap<>();
        weapons.put("DefaultGun", new DefaultGun());
        weapons.put("Shotgun", new Shotgun());
        weapons.put("Laser", new Laser());

        this.gsm = new GameStateManager();
        this.setCurrentState(GameState.Type.MAIN_MENU);
    }

    //  사용 가능한 모든 무기 목록
    public Map<String, Weapon> getWeapons() {
        return weapons;
    }

    // 데이터베이스에서 플레이어 데이터를 가져오고 능력치를 계산

    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
        this.shopMenu = new ShopMenu(shopManager.getAllUpgrades());
        calculatePlayerStats();
    }

    // 게임 창을 표시하고 메인 루프를 시작
    public void startGame() {
        gameWindow.setVisible(true);
        mainLoop();
    }

    /**
     * 현재 게임 상태를 지정된 상태로 변경
     *
     * @param stateType 변경할 게임 상태의 타입
     */
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = switch (stateType) {
            case MAIN_MENU -> new MainMenuState(this);
            case PLAYING -> new PlayingState(this);
            case PAUSED -> new PausedState(this);
            case GAME_OVER -> new GameOverState(this, false);
            case GAME_WON -> new GameOverState(this, true);
            case RANKING -> new RankingState(this);
            case SHOP -> new ShopState(this);
            case SHOP_MAIN_MENU -> new ShopMainMenuState(this);
            case ITEM_DRAW -> new ItemDrawState(this);
            case PET_MENU -> new PetMenuState(this);
            case WEAPON_MENU -> new WeaponMenuState(this);
            case EXIT_CONFIRMATION -> new ExitConfirmationState(this);
            case WAVE_CLEARED -> {
                startNextWave();
                yield gsm.getCurrentState();
            }
        };
        gsm.setState(newState);
    }

    /**
     * 게임의 메인 루프입니다.
     * 입력 처리, 게임 상태 업데이트, 렌더링을 반복적으로 수행
     */
    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            gsm.handleInput(inputHandler);

            gsm.update(delta);

            // 시간 제한 매세지 확인
            if (messageEndTime > 0 && System.currentTimeMillis() > messageEndTime) {
                message = "";
                messageEndTime = 0;
            }

            if (nextState != null) {
                setCurrentState(nextState);
                nextState = null;
            }


            Graphics2D g = gameWindow.getGameCanvas().getGraphics2D();
            if (g != null) {
                gsm.render(g);
                g.dispose();
                gameWindow.getGameCanvas().showStrategy();
            }

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    @Override
    public void addEntity(Entity entity) { entityManager.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { entityManager.removeEntity(entity); }
    @Override
    public void notifyDeath() {
        this.nextState = GameState.Type.GAME_OVER;
        soundManager.stopAllSounds("ship-death-sound");
        soundManager.playSound("ship-death-sound");
     }
    @Override
    public void notifyWin() {
        soundManager.stopAllSounds();
        setCurrentState(GameState.Type.GAME_WON);
    }

    /**
     * 에일리언이 처치되었을 때 호출
     * 점수를 증가시키고, 에일리언 카운트를 감소시키며, 확률적으로 버프를 생성
     */
    @Override
    public void notifyAlienKilled() {
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();

        // 버프 생성 로직
        double roll = Math.random();
        if (roll < 0.05) { // 0.5퍼 확률 무적
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.INVINCIBILITY);
        } else if (roll < 0.10) { // 10퍼 확률 이동속도 증가
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.SPEED_BOOST);
        } else if (roll < 0.15) { // 15퍼 확률 힐
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.HEAL);
        }

    }

    /**
     * 에일리언이 화면 밖으로 탈출했을 때 호출
     * 해당 엔티티를 제거하고 에일리언 카운트를 감소
     */
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();

    }

    @Override
    public void notifyMeteorDestroyed(int scoreValue) {
        increaseScore(scoreValue);
    }

    @Override
    public java.util.List<Entity> getEntities() { return entityManager.getEntities(); }

    @Override
    public ShipEntity getShip() { return entityManager.getShip(); }

    public void increaseScore(int amount) { score += amount; }
    public void resetScore() { score = 0; }
    public void setWave(int newWave) { this.wave = newWave - 1; }


    /**
     * 게임 플레이를 시작
     * 배경 음악을 변경하고, 게임 시작 시간을 기록하며, 능력치와 점수를 초기화하고 첫 웨이브를 시작
     */
    public void startGameplay() {
        soundManager.stopSound("menubackground");
        gameStartTime = System.currentTimeMillis();
        calculatePlayerStats();
        resetScore();
        wave = 0;
        startNextWave();
    }

    /**
     * 다음 웨이브를 시작
     * 웨이브 번호를 증가시키고, 웨이브에 맞는 적과 펫을 생성
     */
    public void startNextWave() {
        wave++;
        healingAreaSpawnedForWave = false;
        if (wave > 25) {
            notifyWin();
            return;
        }
        message = "Wave " + wave;
        messageEndTime = System.currentTimeMillis() + 1000;

        // 웨이브에 따른 배경 음악 변경
        if (wave % 5 == 0) {
            soundManager.stopSound("gamebackground");
            soundManager.loopSound("boss1");
        } else if ((wave - 1) % 5 == 0 && wave > 1) {
            soundManager.stopSound("boss1");
            soundManager.loopSound("gamebackground");
        } else if (wave == 1) {
            soundManager.loopSound("gamebackground");
        }

        // 플레이어가 선택한 무기를 장착
        String equippedWeaponName = currentPlayer.getEquippedWeapon();
        Weapon selectedWeapon;
        if (equippedWeaponName != null) {
            switch (equippedWeaponName) {
                case "Shotgun":
                    selectedWeapon = new Shotgun();
                    break;
                case "Laser":
                    selectedWeapon = new Laser();
                    break;
                default:
                    selectedWeapon = new DefaultGun();
                    break;
            }
        } else {
            selectedWeapon = new DefaultGun();
        }

        entityManager.initShip(playerStats, selectedWeapon);

        // 펫 장착
        if (currentPlayer != null && currentPlayer.getEquippedPet() != null) {
            try {
                ShipEntity playerShip = getShip();
                PetType petType = PetType.valueOf(currentPlayer.getEquippedPet());
                switch (petType) {
                    case ATTACK:
                        addEntity(new AttackPetEntity(this, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                    case DEFENSE:
                        DefensePetEntity defensePet = new DefensePetEntity(this, playerShip, playerShip.getX(), playerShip.getY());
                        addEntity(defensePet);
                        playerShip.setShield(true, defensePet::resetAbilityCooldown); // Grant initial shield
                        defensePet.resetAbilityCooldown();      // Start cooldown timer
                        break;
                    case HEAL:
                        addEntity(new HealPetEntity(this, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                    case BUFF:
                        addEntity(new BuffPetEntity(this, playerShip, playerShip.getX(), playerShip.getY()));
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Attempted to spawn unknown pet type: " + currentPlayer.getEquippedPet());
            }
        }

        // 웨이브가 5의 배수이면 보스 생성, 아니면 일반 포메이션 생성
        if (wave % 5 == 0) {
            formationsPerWave = 1;
            formationsSpawnedInWave = 0;
            spawnBossNow();
            formationsSpawnedInWave = 1;
        } else {
            int stage = ((wave - 1) / 5) + 1;
            switch (stage) {
                case 1: formationsPerWave = 3; break;
                case 2: formationsPerWave = 4; break;
                case 3: formationsPerWave = 5; break;
                case 4: formationsPerWave = 6; break;
                case 5: formationsPerWave = 7; break;
                default: formationsPerWave = 3; break;
            }
            message = "Wave " + wave;
            formationsSpawnedInWave = 0;
            spawnNextFormationInWave();
        }

        setCurrentState(GameState.Type.PLAYING);
    }

    /**
     * 현재 웨이브에서 다음 적 포메이션을 생성
     */
    public void spawnNextFormationInWave() {
        if (formationsSpawnedInWave >= formationsPerWave || wave % 5 == 0) {
            return;
        }

        int stage = ((wave - 1) / 5) + 1;
        Formation formation = formationManager.getRandomFormationForStage(stage);

        boolean forceUpgrade = false;
        String formationName = formation.getName();

        if (stage == 4 && formationName.contains("Converging Shooters")) {
            forceUpgrade = true;
        }
        if (stage == 5) {
            if (formationName.contains("Burst Shooters") || formationName.contains("Converging Shooters")) {
                forceUpgrade = true;
            }
        }

        entityManager.spawnFormation(formation, wave, forceUpgrade);
        formationsSpawnedInWave++;

        // 생성할 배치가 더 있는 경우 타이머 3초 설정
        if (formationsSpawnedInWave < formationsPerWave) {
            nextFormationSpawnTime = System.currentTimeMillis() + 3000L;
        }
    }

    /**
     * 플레이어의 업그레이드 정보를 바탕으로 현재 능력치를 계산
     */
    public void calculatePlayerStats() {
        playerStats = new PlayerStats();

        // 초기화
        playerStats.getWeaponLevels().put("DefaultGun", 1);
        playerStats.getWeaponLevels().put("Shotgun", 0);
        playerStats.getWeaponLevels().put("Laser", 0);

        // 저장된 무기 업그레이드 정보가 있으면 가져고기
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
                }
            }
        }
    }

    /**
     * 게임 결과를 저장 (크레딧, 최고 점수).
     */
    public void saveGameResults() {
        if (user == null || currentPlayer == null) return;
        currentPlayer.setCredit(currentPlayer.getCredit() + score);
        currentPlayer.setHighScore(Math.max(currentPlayer.getHighScore(), score));
        savePlayerData();
    }

    /**
     * 플레이어 현재 상태를 데이터베이스에 저장
     */
    public void savePlayerData() {
        if (user == null || currentPlayer == null) return;
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    public InputHandler getInputHandler() { return inputHandler; }
    public EntityManager getEntityManager() { return entityManager; }
    public GameWindow getGameWindow() { return gameWindow; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerData getCurrentPlayer() { return currentPlayer; }
    public GameStateManager getGsm() { return gsm; }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public void notifyItemCollected() {
        collectedItems++;
    }

    public boolean hasCollectedAllItems() {
        return collectedItems >= 2;
    }

    public void resetItemCollection() {
        collectedItems = 0;
    }

    public void stunPlayer(long duration) {
        this.playerAttackDisabledUntil = System.currentTimeMillis() + duration;
    }

    @Override
    public boolean canPlayerAttack() {
        return System.currentTimeMillis() > playerAttackDisabledUntil;
    }


    /**
     * 현재 웨이브에 맞는 보스를 생성
     */
    public void spawnBossNow() {
        // 플레이어 제외 모든 엔티티 제거
        getEntityManager().getEntities().removeIf(entity -> !(entity instanceof ShipEntity));

        // 보스 생성
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.5, cycle);
        int bossHealth = (int) (50 * cycleMultiplier);
        Entity boss = new BossEntity(this, Game.GAME_WIDTH / 2, 50, bossHealth, cycle, wave, false);
        addEntity(boss);
        entityManager.setAlienCount(1);
    }

    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }
}