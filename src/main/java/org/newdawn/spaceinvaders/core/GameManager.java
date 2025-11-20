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

    private GameContainer gameContainer;
    private Map<String, Weapon> weapons;

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

    private GameEventHandler gameEventHandler;
    private GameLoop gameLoop;

    public GameManager() {
        this.gameSession = new GameSession();
        // Dependencies will be injected via setters
    }

    public void init() {
        this.gameEventHandler = new GameEventHandler(this, getPlayerManager(), getEntityManager(), getSoundManager());
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

    public long getMessageEndTime() {
        return messageEndTime;
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

    public void setGameContainer(GameContainer gameContainer) {
        this.gameContainer = gameContainer;
    }

    public void setWeapons(Map<String, Weapon> weapons) {
        this.weapons = weapons;
    }

    @Override
    public GameContainer getGameContainer() {
        return gameContainer;
    }

    public MainMenu getMainMenu() {
        return gameContainer.getUiManager().getMainMenu();
    }

    public PauseMenu getPauseMenu() {
        return gameContainer.getUiManager().getPauseMenu();
    }

    public GameOverMenu getGameOverMenu() {
        return gameContainer.getUiManager().getGameOverMenu();
    }

    public ConfirmDialog getConfirmDialog() {
        return gameContainer.getUiManager().getConfirmDialog();
    }

    public ShopMenu getShopMenu() {
        return gameContainer.getUiManager().getShopMenu();
    }

    public Sprite getStaticBackgroundSprite() {
        return gameContainer.getUiManager().getStaticBackgroundSprite();
    }

    public ShopManager getShopManager() {
        return gameContainer.getShopManager();
    }

    @Override
    public Map<String, Weapon> getWeapons() {
        return weapons;
    }

    //
    public void initializePlayer() {
        getPlayerManager().initializePlayer();
        gameContainer.getUiManager().setShopMenu(new ShopMenu(getShopManager().getAllUpgrades()));
    }

    //게임 시작, 메인루프 호출
    public void startGame() {
        gameContainer.getUiManager().getGameWindow().setVisible(true);
        gameLoop = new GameLoop(getGsm(), getInputHandler(), gameContainer.getUiManager().getGameWindow(), this);
        gameLoop.run();
    }

    //현재 상태 설정
    @Override
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = gameStateFactory.create(stateType, this);
        getGsm().setState(newState);
    }

    @Override
    public void setNextState(GameState.Type stateType) {
        this.nextState = stateType;
    }

    @Override
    public void onWaveCleared() {
        getWaveManager().startNextWave();
    }

    @Override
    public void updatePlayingLogic(long delta) {
        gameWorld.update(delta);
        this.setLogicRequiredThisLoop(true);
    }

    // playingstate 정보
    @Override
    public void addEntity(Entity entity) { gameWorld.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { gameWorld.removeEntity(entity); }
    @Override
    public void notifyDeath() {
        gameEventHandler.notifyDeath();
     }
    @Override
    public void notifyWin() {
        gameEventHandler.notifyWin();
    }

    //처치한 적 처리( 점수 처리 )
    @Override
    public void notifyAlienKilled() {
        gameEventHandler.notifyAlienKilled();
    }

    //화면 밖으로 나간 적 처리
    @Override
    public void notifyAlienEscaped(Entity entity) {
        gameEventHandler.notifyAlienEscaped(entity);
    }

    @Override
    public void notifyMeteorDestroyed(int scoreValue) {
        gameEventHandler.notifyMeteorDestroyed(scoreValue);
    }

    @Override
    public java.util.List<Entity> getEntities() { return gameWorld.getEntities(); }

    @Override
    public ShipEntity getShip() { return gameWorld.getShip(); }

    @Override
    public void startGameplay() {
        getPlayerManager().startGameplay();
    }

    public InputHandler getInputHandler() { return gameContainer.getInputHandler(); }
    public EntityManager getEntityManager() { return gameWorld.getEntityManager(); }
    public DatabaseManager getDatabaseManager() { return gameContainer.getDatabaseManager(); }
    public PlayerData getCurrentPlayer() { return gameContainer.getPlayerManager().getCurrentPlayer(); }
    public PlayerStats getPlayerStats() { return gameContainer.getPlayerManager().getPlayerStats(); }
    public GameStateManager getGsm() { return gameContainer.getGsm(); }
    public WaveManager getWaveManager() { return gameWorld.getWaveManager(); }
    public PlayerManager getPlayerManager() { return gameContainer.getPlayerManager(); }

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


    public SoundManager getSoundManager() {
        return gameContainer.getSoundManager();
    }

    public Background getBackground() {
        return gameWorld.getBackground();
    }
}
