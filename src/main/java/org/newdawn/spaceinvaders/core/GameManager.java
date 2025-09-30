package org.newdawn.spaceinvaders.core;

import com.google.cloud.firestore.Firestore;
import org.newdawn.spaceinvaders.auth.AuthenticatedUser;
import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.gamestates.*;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.view.*;


import java.awt.Graphics2D;

public class GameManager implements GameContext {

    private final InputHandler inputHandler;
    private final EntityManager entityManager;
    private final GameStateManager gsm;

    public final DatabaseManager databaseManager;
    public final GameWindow gameWindow;
    public final MainMenu mainMenu;
    public final PauseMenu pauseMenu;
    public final GameOverMenu gameOverMenu;
    public final ConfirmDialog confirmDialog;
    public final ShopManager shopManager;
    public final Background background;
    public final Sprite staticBackgroundSprite;
    public ShopMenu shopMenu;

    // 사용자, 게임 데이터
    public final AuthenticatedUser user;
    public PlayerData currentPlayer;
    public PlayerStats playerStats;
    public String message = "";
    public int score = 0;
    public int wave = 1;

    public boolean logicRequiredThisLoop = false;
    public boolean showHitboxes = false;
    public long lastFire = 0;
    public int lineCount = 0;
    public long lastLineSpawnTime = 0;
    public long lastMeteorSpawnTime = 0;
    public long lastBombSpawnTime = 0;

    // 설정
    public final double moveSpeed = 300;
    public static final int ALIEN_SCORE = 10;
    public final long lineSpawnInterval = 3000;
    public final int LINES_PER_WAVE = 10;
    public final long meteorSpawnInterval = 3000;
    public final long bombSpawnInterval = 5000;

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

        this.background = new Background("sprites/gamebackground.png");
        this.staticBackgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");
        this.playerStats = new PlayerStats();
        this.confirmDialog = new ConfirmDialog("Are you sure you want to exit?");

        this.gsm = new GameStateManager();
        this.setCurrentState(GameState.Type.MAIN_MENU);
    }

    //
    public void initializePlayer() {
        this.currentPlayer = databaseManager.loadPlayerData(user.getLocalId(), user.getUsername());
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
    public void notifyDeath() { setCurrentState(GameState.Type.GAME_OVER); }
    @Override
    public void notifyWin() { setCurrentState(GameState.Type.GAME_WON); }

    //처치한 적 처리( 점수 처리 )
    @Override
    public void notifyAlienKilled() {
        increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0) {
            int effectiveWave = ((wave - 1) % 5) + 1;
            boolean isBossWave = (effectiveWave == 5);
            if (isBossWave || lineCount >= LINES_PER_WAVE) {
                setCurrentState(GameState.Type.WAVE_CLEARED);
            }
        }
    }

    //화면 밖으로 나간 적 처리
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();
        if (entityManager.getAlienCount() == 0 && lineCount >= LINES_PER_WAVE) {
            setCurrentState(GameState.Type.WAVE_CLEARED);
        }
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
        wave = 1;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        entityManager.initShip(playerStats);
        getShip().reset();
        message = "";
        setCurrentState(GameState.Type.PLAYING);

        // 엔티티 테스트용
        int shooterY = -50;
        entityManager.addEntity(new ThreeWayShooter(this, Game.GAME_WIDTH / 4, shooterY));
        entityManager.addEntity(new ThreeWayShooter(this, Game.GAME_WIDTH / 2, shooterY));
        entityManager.addEntity(new ThreeWayShooter(this, (Game.GAME_WIDTH * 3) / 4, shooterY));
    }

    // 다음 웨이브로 전환
     public void startNextWave() {
        wave++;
        lineCount = 0;
        lastLineSpawnTime = System.currentTimeMillis();
        message = "Wave " + wave;
        entityManager.initShip(playerStats);
        setCurrentState(GameState.Type.PLAYING);
    }

    // 업그레이드 정보 바탕으로 능력치 설정
    public void calculatePlayerStats() {
        playerStats = new PlayerStats();
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
        databaseManager.updatePlayerData(user.getLocalId(), currentPlayer);
    }

    public InputHandler getInputHandler() { return inputHandler; }
    public EntityManager getEntityManager() { return entityManager; }
    public GameWindow getGameWindow() { return gameWindow; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerData getCurrentPlayer() { return currentPlayer; }
    public GameStateManager getGsm() { return gsm; }
}