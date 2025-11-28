package org.newdawn.spaceinvaders.core;

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
import org.newdawn.spaceinvaders.wave.WaveManager;

import java.awt.Graphics2D;
import java.util.Map;

/**
 * 게임의 핵심 로직을 관리하고, 모든 하위 시스템(Manager)을 연결하는 중앙 허브 클래스.
 * GameContext 인터페이스를 구현하여 게임의 전반적인 상태와 데이터에 대한 접근점을 제공합니다.
 */
public class GameManager implements GameContext {

    /** 게임의 창, 입력, 사운드 등 전반적인 컨테이너. */
    private GameContainer gameContainer;
    /** 사용 가능한 모든 무기 정보. */
    private Map<String, Weapon> weapons;

    /** 게임 상태(GameState) 객체를 생성하는 팩토리. */
    private GameStateFactory gameStateFactory;
    /** 현재 게임 세션의 상태를 추적하는 객체. */
    private final GameSession gameSession;
    /** 게임 월드의 엔티티와 웨이브를 관리하는 객체. */
    private GameWorld gameWorld;

    /** 다음으로 전환될 게임 상태의 타입. */
    public GameState.Type nextState = null;
    /** 화면에 표시될 메시지. */
    private String message = "";
    /** 메시지가 화면에서 사라지는 시간. */
    private long messageEndTime = 0;

    /** 현재 루프에서 로직 업데이트가 필요한지 여부. */
    private boolean logicRequiredThisLoop = false;
    /** 히트박스 표시 여부. */
    private boolean showHitboxes = false;

    /** 기본 이동 속도. */
    public final double moveSpeed = 300;

    /** 게임 이벤트를 처리하는 핸들러. */
    private GameEventHandler gameEventHandler;
    /** 메인 게임 루프를 실행하는 객체. */
    private GameLoop gameLoop;
    /** 게임 내 이벤트를 발행하고 구독하는 이벤트 버스. */
    private final EventBus eventBus;

    /**
     * GameManager 생성자.
     * 의존성은 setter를 통해 주입됩니다.
     */
    public GameManager() {
        this.gameSession = new GameSession();
        this.eventBus = new EventBus();
    }

    /**
     * GameManager의 모든 의존성이 주입된 후 호출되어 초기 설정을 완료합니다.
     */
    public void init() {
        this.gameEventHandler = new GameEventHandler(this, getPlayerManager(), getEntityManager(), getSoundManager(), this.gameSession);
        getEventBus().register(this.gameEventHandler);
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

    /**
     * GameStateFactory 의존성을 주입합니다.
     * @param gameStateFactory 게임 상태 팩토리
     */
    public void setGameStateFactory(GameStateFactory gameStateFactory) {
        this.gameStateFactory = gameStateFactory;
    }

    /**
     * GameWorld 의존성을 주입합니다.
     * @param gameWorld 게임 월드
     */
    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    /**
     * GameContainer 의존성을 주입합니다.
     * @param gameContainer 게임 컨테이너
     */
    public void setGameContainer(GameContainer gameContainer) {
        this.gameContainer = gameContainer;
    }

    /**
     * 무기 정보를 주입합니다.
     * @param weapons 사용 가능한 무기 맵
     */
    public void setWeapons(Map<String, Weapon> weapons) {
        this.weapons = weapons;
    }

    @Override
    public GameContainer getGameContainer() {
        return gameContainer;
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

    /**
     * 플레이어 데이터를 기반으로 플레이어 관련 시스템을 초기화합니다.
     */
    public void initializePlayer() {
        getPlayerManager().initializePlayer();
        gameContainer.getUiManager().setShopMenu(new ShopMenu(getShopManager().getAllUpgrades()));
    }

    /**
     * 게임을 시작하고 메인 게임 루프를 실행합니다.
     */
    public void startGame() {
        gameContainer.getUiManager().getGameWindow().setVisible(true);
        gameLoop = new GameLoop(getGsm(), getInputHandler(), gameContainer.getUiManager().getGameWindow(), this);
        gameLoop.run();
    }

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
    }

    @Override
    public void addEntity(Entity entity) { gameWorld.addEntity(entity); }
    @Override
    public void removeEntity(Entity entity) { gameWorld.removeEntity(entity); }


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
    public PlayerData getCurrentPlayer() { return gameContainer.getPlayerManager().getCurrentPlayer(); }
    public PlayerStats getPlayerStats() { return gameContainer.getPlayerManager().getPlayerStats(); }
    public GameStateManager getGsm() { return gameContainer.getGsm(); }
    public WaveManager getWaveManager() { return gameWorld.getWaveManager(); }
    public PlayerManager getPlayerManager() { return gameContainer.getPlayerManager(); }

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

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}