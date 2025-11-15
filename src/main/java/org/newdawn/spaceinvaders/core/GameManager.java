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
    private GameStateManager gsm;
    private Map<String, Weapon> weapons;

    private DatabaseManager databaseManager;
    private UIManager uiManager;
    private ShopManager shopManager;
    private FormationManager formationManager;
    private PlayerManager playerManager;
    private SoundManager soundManager;
    private GameStateFactory gameStateFactory;
    private final GameSession gameSession;
    private GameWorld gameWorld;

    // 사용자, 게임 데이터
    public GameState.Type nextState = null;
    private String message = "";
    private long messageEndTime = 0;

    private boolean logicRequiredThisLoop = false;
    private boolean showHitboxes = false;

    // 설정
    public final double moveSpeed = 300;
    public static final int ALIEN_SCORE = 10;

    private final boolean gameRunning = true;

    public GameManager() {
        this.gameSession = new GameSession();
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

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
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

    public void setUIManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public void setShopManager(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    public void setFormationManager(FormationManager formationManager) {
        this.formationManager = formationManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void setSoundManager(SoundManager soundManager) {
        this.soundManager = soundManager;
    }

    @Override
    public MainMenu getMainMenu() {
        return uiManager.getMainMenu();
    }

    @Override
    public PauseMenu getPauseMenu() {
        return uiManager.getPauseMenu();
    }

    @Override
    public GameOverMenu getGameOverMenu() {
        return uiManager.getGameOverMenu();
    }

    @Override
    public ConfirmDialog getConfirmDialog() {
        return uiManager.getConfirmDialog();
    }

    @Override
    public ShopMenu getShopMenu() {
        return uiManager.getShopMenu();
    }

    @Override
    public Sprite getStaticBackgroundSprite() {
        return uiManager.getStaticBackgroundSprite();
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
        uiManager.setShopMenu(new ShopMenu(shopManager.getAllUpgrades()));
    }

    //게임 시작, 메인루프 호출
    public void startGame() {
        uiManager.getGameWindow().setVisible(true);
        mainLoop();
    }

    //현재 상태 설정
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = gameStateFactory.create(stateType, this);
        gsm.setState(newState);
    }

    public void onWaveCleared() {
        getWaveManager().startNextWave();
    }

    @Override
    public void updatePlayingLogic(long delta) {
        gameWorld.update(delta);
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


            Graphics2D g = uiManager.getGameWindow().getGameCanvas().getGraphics2D();
            if (g != null) {
                gsm.render(g);
                g.dispose();
                uiManager.getGameWindow().getGameCanvas().showStrategy();
            }

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    // playingstate 정보
    @Override
    public void addEntity(Entity entity) { gameWorld.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { gameWorld.removeEntity(entity); }
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
        getEntityManager().decreaseAlienCount();

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
        getEntityManager().removeEntity(entity);
        getEntityManager().decreaseAlienCount();

    }

    @Override
    public void notifyMeteorDestroyed(int scoreValue) {
        playerManager.increaseScore(scoreValue);
    }

    @Override
    public java.util.List<Entity> getEntities() { return gameWorld.getEntities(); }

    @Override
    public ShipEntity getShip() { return gameWorld.getShip(); }

    public void startGameplay() {
        soundManager.stopSound("menubackground");
        playerManager.setGameStartTime(System.currentTimeMillis());
        playerManager.calculatePlayerStats();
        playerManager.resetScore();
        getWaveManager().startFirstWave();
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
    public EntityManager getEntityManager() { return gameWorld.getEntityManager(); }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerData getCurrentPlayer() { return playerManager.getCurrentPlayer(); }
    public PlayerStats getPlayerStats() { return playerManager.getPlayerStats(); }
    public GameStateManager getGsm() { return gsm; }
    public WaveManager getWaveManager() { return gameWorld.getWaveManager(); }
    public PlayerManager getPlayerManager() { return playerManager; }

    @Override
    public void notifyItemCollected() {
        gameSession.notifyItemCollected();
    }

    @Override
    public boolean hasCollectedAllItems() {
        return gameSession.hasCollectedAllItems();
    }

    @Override
    public void resetItemCollection() {
        gameSession.resetItemCollection();
    }

    @Override
    public boolean canPlayerAttack() {
        return gameSession.canPlayerAttack();
    }


    @Override
    public SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public Background getBackground() {
        return gameWorld.getBackground();
    }
}
