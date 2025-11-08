package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.data.DatabaseManager;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.gamestates.*;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.shop.ShopManager;
import org.newdawn.spaceinvaders.sound.SoundManager;
import org.newdawn.spaceinvaders.view.*;
import org.newdawn.spaceinvaders.wave.FormationManager;
import org.newdawn.spaceinvaders.wave.WaveManager;

import java.awt.Graphics2D;
import java.util.Map;

public class GameManager implements GameContext {

    private InputHandler inputHandler;
    private EntityManager entityManager;
    private GameStateManager gsm;
    private Map<String, Weapon> weapons;

    private DatabaseManager databaseManager;
    private GameWindow gameWindow;
    private MainMenu mainMenu;
    private PauseMenu pauseMenu;
    private GameOverMenu gameOverMenu;
    private ConfirmDialog confirmDialog;
    private ShopManager shopManager;
    private FormationManager formationManager;
    private WaveManager waveManager;
    private PlayerManager playerManager;
    private Background background;
    private Sprite staticBackgroundSprite;
    public ShopMenu shopMenu;
    private SoundManager soundManager;
    private GameStateFactory gameStateFactory;

    // 사용자, 게임 데이터
    public GameState.Type nextState = null;
    private String message = "";
    private long messageEndTime = 0;

    private boolean logicRequiredThisLoop = false;
    private boolean showHitboxes = false;
    public int collectedItems = 0;
    private long playerAttackDisabledUntil = 0;

    // 설정
    public final double moveSpeed = 300;
    public static final int ALIEN_SCORE = 10;

    private boolean gameRunning = true;

    public GameManager() {
        // Dependencies will be injected via setters
    }

    @Override
    public boolean getShowHitboxes() {
        return showHitboxes;
    }

    @Override
    public void setShowHitboxes(boolean show) {
        this.showHitboxes = show;
    }

    @Override
    public void setLogicRequiredThisLoop(boolean required) {
        this.logicRequiredThisLoop = required;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public long getMessageEndTime() {
        return messageEndTime;
    }

    @Override
    public void setMessageEndTime(long time) {
        this.messageEndTime = time;
    }

    @Override
    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setGameStateFactory(GameStateFactory gameStateFactory) {
        this.gameStateFactory = gameStateFactory;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setGsm(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public void setWeapons(Map<String, Weapon> weapons) {
        this.weapons = weapons;
    }

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void setPauseMenu(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
    }

    public void setGameOverMenu(GameOverMenu gameOverMenu) {
        this.gameOverMenu = gameOverMenu;
    }

    public void setConfirmDialog(ConfirmDialog confirmDialog) {
        this.confirmDialog = confirmDialog;
    }

    public void setShopManager(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    public void setFormationManager(FormationManager formationManager) {
        this.formationManager = formationManager;
    }

    public void setWaveManager(WaveManager waveManager) {
        this.waveManager = waveManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public void setStaticBackgroundSprite(Sprite staticBackgroundSprite) {
        this.staticBackgroundSprite = staticBackgroundSprite;
    }

    public void setShopMenu(ShopMenu shopMenu) {
        this.shopMenu = shopMenu;
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public MainMenu getMainMenu() {
        return mainMenu;
    }

    @Override
    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }

    @Override
    public GameOverMenu getGameOverMenu() {
        return gameOverMenu;
    }

    @Override
    public ConfirmDialog getConfirmDialog() {
        return confirmDialog;
    }

    @Override
    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    @Override
    public Sprite getStaticBackgroundSprite() {
        return staticBackgroundSprite;
    }

    @Override
    public ShopManager getShopManager() {
        return shopManager;
    }

    public Map<String, Weapon> getWeapons() {
        return weapons;
    }

    //
    public void initializePlayer() {
        playerManager.initializePlayer();
        this.shopMenu = new ShopMenu(shopManager.getAllUpgrades());
    }

    //게임 시작, 메인루프 호출
    public void startGame() {
        gameWindow.setVisible(true);
        mainLoop();
    }

    //현재 상태 설정
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = gameStateFactory.create(stateType, this);
        gsm.setState(newState);
    }

    public void onWaveCleared() {
        waveManager.startNextWave();
    }

    @Override
    public void updatePlayingLogic(long delta) {
        this.getBackground().update(delta);
        this.getWaveManager().update(delta);
        this.getEntityManager().moveAll(delta);
        new CollisionDetector().checkCollisions(this.getEntityManager().getEntities());
        this.getEntityManager().cleanup();

        if (this.getEntityManager().getAlienCount() == 0 && this.getWaveManager().getFormationsSpawnedInWave() >= this.getWaveManager().getFormationsPerWave()) {
            this.onWaveCleared();
        }

        this.setLogicRequiredThisLoop(true);
    }

    // 메인 루프
    private void mainLoop() {
        long lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            gsm.handleInput(inputHandler);

            gsm.update(delta);

            // Check for timed message expiry
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

    // playingstate 정보
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

    //처치한 적 처리( 점수 처리 )
    @Override
    public void notifyAlienKilled() {
        playerManager.increaseScore(ALIEN_SCORE);
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

    }

    //화면 밖으로 나간 적 처리
    @Override
    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();

    }

    @Override
    public void notifyMeteorDestroyed(int scoreValue) {
        playerManager.increaseScore(scoreValue);
    }

    @Override
    public java.util.List<Entity> getEntities() { return entityManager.getEntities(); }

    @Override
    public ShipEntity getShip() { return entityManager.getShip(); }

    public void startGameplay() {
        soundManager.stopSound("menubackground");
        playerManager.setGameStartTime(System.currentTimeMillis());
        playerManager.calculatePlayerStats();
        playerManager.resetScore();
        waveManager.startFirstWave();
    }

    // 게임 결과 저장( 크레딧, 스코어 )
    public void saveGameResults() {
        playerManager.saveGameResults();
    }

    /**
     * Saves the current state of the player data to the database.
     */
    public void savePlayerData() {
        playerManager.savePlayerData();
    }

    public InputHandler getInputHandler() { return inputHandler; }
    public EntityManager getEntityManager() { return entityManager; }
    public GameWindow getGameWindow() { return gameWindow; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerData getCurrentPlayer() { return playerManager.getCurrentPlayer(); }
    public PlayerStats getPlayerStats() { return playerManager.getPlayerStats(); }
    public GameStateManager getGsm() { return gsm; }
    public WaveManager getWaveManager() { return waveManager; }
    public PlayerManager getPlayerManager() { return playerManager; }

    public long getGameStartTime() {
        return playerManager.getGameStartTime();
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


    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public Background getBackground() {
        return background;
    }
}
