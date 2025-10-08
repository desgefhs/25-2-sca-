package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Enemy.BurstShooterEntity;
import org.newdawn.spaceinvaders.entity.Pet.*;
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
import org.newdawn.spaceinvaders.view.*;
import org.newdawn.spaceinvaders.wave.Formation;
import org.newdawn.spaceinvaders.wave.FormationManager;


import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final GameStateManager gsm;
    private final Map<String, Weapon> weapons;
    private final Random random = new Random();

    public final DatabaseManager databaseManager;
    public final GameWindow gameWindow;
    public final MainMenu mainMenu;
    public final PauseMenu pauseMenu;
    public final GameOverMenu gameOverMenu;
    public final ConfirmDialog confirmDialog;
    public final ShopManager shopManager;
    public final FormationManager formationManager;
    public final Background background;
    public final Sprite staticBackgroundSprite;
    public ShopMenu shopMenu;

    // 사용자, 게임 데이터
    public final AuthenticatedUser user;
    public PlayerData currentPlayer;
    public PlayerStats playerStats;
    public GameState.Type nextState = null;
    public String message = "";
    public int score = 0;
    public int wave = 0;

    // Wave Management
    public int formationsPerWave;
    public int formationsSpawnedInWave;
    public long nextFormationSpawnTime;

    public boolean logicRequiredThisLoop = false;
    public boolean showHitboxes = false;
    public int collectedItems = 0;
    private long playerAttackDisabledUntil = 0;

    // 설정
    public final double moveSpeed = 300;
    public static final int ALIEN_SCORE = 10;


    private boolean gameRunning = true;

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

    public Map<String, Weapon> getWeapons() {
        return weapons;
    }

    //
    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
        this.currentPlayer.setCredit(100000); // Grant 100,000 credits for testing
        this.shopMenu = new ShopMenu(shopManager.getAllUpgrades());
        calculatePlayerStats();
    }

    //게임 시작, 메인루프 호출
    public void startGame() {
        gameWindow.setVisible(true);
        mainLoop();
    }

    //현재 상태 설정
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = switch (stateType) {
            case MAIN_MENU -> new MainMenuState(this);
            case PLAYING -> new PlayingState(this);
            case PAUSED -> new PausedState(this);
            case GAME_OVER -> new GameOverState(this, false);
            case GAME_WON -> new GameOverState(this, true);
            case RANKING -> new RankingState(this);
            case SHOP -> new ShopState(this);
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

    // 메인 루프
    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();


            gsm.handleInput(inputHandler);

            gsm.update(delta);

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

    // playingstate 정보
    @Override
    public void addEntity(Entity entity) { entityManager.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { entityManager.removeEntity(entity); }
    @Override
    public void notifyDeath() { this.nextState = GameState.Type.GAME_OVER; }
    @Override
    public void notifyWin() { setCurrentState(GameState.Type.GAME_WON); }

    //처치한 적 처리( 점수 처리 )
    @Override
    public void notifyAlienKilled() {
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();

        // Buff drop logic
        double roll = Math.random();
        if (roll < 0.05) { // 5% chance for invincibility
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.INVINCIBILITY);
        } else if (roll < 0.10) { // 5% chance for speed boost
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.SPEED_BOOST);
        } else if (roll < 0.15) { // 5% chance for heal
            getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.HEAL);
        }

        if (entityManager.getAlienCount() == 0) {
            if (formationsSpawnedInWave >= formationsPerWave) {
                setCurrentState(GameState.Type.WAVE_CLEARED);
            } else {
                // Screen is clear, but more formations are coming. Spawn next one immediately.
                spawnNextFormationInWave();
            }
        }
    }

    //화면 밖으로 나간 적 처리
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();

        if (entityManager.getAlienCount() == 0) {
            if (formationsSpawnedInWave >= formationsPerWave) {
                setCurrentState(GameState.Type.WAVE_CLEARED);
            } else {
                // Screen is clear, but more formations are coming. Spawn next one immediately.
                spawnNextFormationInWave();
            }
        }
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


    public void startGameplay() {
        calculatePlayerStats();
        resetScore();
        wave = 0; // Start at wave 0, so startNextWave() increments to 1
        startNextWave();

        // Spawn three upgraded ThreeWayShooters for an immediate challenge
        int shooterY = -50;

        // Spawn a new BurstShooter for testing
        BurstShooterEntity s1 = new BurstShooterEntity(this, Game.GAME_WIDTH / 2, shooterY + 50);
        s1.upgrade();
        addEntity(s1);
    }

    // 다음 웨이브로 전환
    public void startNextWave() {
        wave++;
        if (wave > 25) {
            notifyWin();
            return;
        }
        message = "Wave " + wave;

        // Create and set the player's equipped weapon
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

        // Spawn the equipped pet, if any
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
            formationsSpawnedInWave = 1; // The boss is the only formation
        } else {
            formationsPerWave = Math.min(1 + wave, 5);
            formationsSpawnedInWave = 0;
            spawnNextFormationInWave();
        }

        setCurrentState(GameState.Type.PLAYING);
    }

    public void spawnNextFormationInWave() {
        if (formationsSpawnedInWave >= formationsPerWave || wave % 5 == 0) {
            return; // Safeguard: Don't spawn more if wave is complete or it's a boss wave
        }

        Formation formation = formationManager.getRandomFormation();
        entityManager.spawnFormation(formation, wave);

        formationsSpawnedInWave++;

        // Set timer for the next spawn with a random delay
        long delay = (random.nextBoolean()) ? 100 : 2000 + random.nextInt(3000);
        nextFormationSpawnTime = System.currentTimeMillis() + delay;
    }

    // 업그레이드 정보 바탕으로 능력치 설정
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
                }
            }
        }
    }

    // 게임 결과 저장( 크레딧, 스코어 )
    public void saveGameResults() {
        if (user == null || currentPlayer == null) return;
        currentPlayer.setCredit(currentPlayer.getCredit() + score);
        currentPlayer.setHighScore(Math.max(currentPlayer.getHighScore(), score));
        savePlayerData(); // Use the new centralized save method
    }

    /**
     * Saves the current state of the player data to the database.
     */
    public void savePlayerData() {
        if (user == null || currentPlayer == null) return;
        currentPlayer.getWeaponLevels().putAll(playerStats.getWeaponLevels());
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    public InputHandler getInputHandler() { return inputHandler; }
    public EntityManager getEntityManager() { return entityManager; }
    public GameWindow getGameWindow() { return gameWindow; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerData getCurrentPlayer() { return currentPlayer; }
    public GameStateManager getGsm() { return gsm; }


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


    public void spawnBossNow() {
        // Clear all entities except the player
        getEntityManager().getEntities().removeIf(entity -> !(entity instanceof ShipEntity));

        // Spawn the boss
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.5, cycle);
        int bossHealth = (int) (50 * cycleMultiplier);
        Entity boss = new BossEntity(this, Game.GAME_WIDTH / 2, 50, bossHealth, cycle, wave, false);
        addEntity(boss);
        entityManager.setAlienCount(1);
    }
}