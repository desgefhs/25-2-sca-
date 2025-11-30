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

    /**
     * 히트박스 표시 여부를 반환합니다.
     * @return 히트박스를 표시할 경우 true, 그렇지 않으면 false
     */
    @Override
    public boolean getShowHitboxes() {
        return showHitboxes;
    }

    /**
     * 히트박스 표시 여부를 설정합니다.
     * @param show 히트박스를 표시할 경우 true, 그렇지 않으면 false
     */
    @Override
    public void setShowHitboxes(boolean show) {
        this.showHitboxes = show;
    }



    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 화면에 표시될 메시지를 설정합니다.
     * @param message 표시할 메시지 문자열
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 메시지가 화면에서 사라질 시간을 설정합니다.
     * @param time 메시지 종료 시간 (밀리초)
     */
    @Override
    public void setMessageEndTime(long time) {
        this.messageEndTime = time;
    }

    /**
     * 메시지가 화면에서 사라질 시간을 반환합니다.
     * @return 메시지 종료 시간 (밀리초)
     */
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

    /**
     * 현재 게임 상태를 즉시 변경합니다.
     * @param stateType 새로 설정할 게임 상태의 타입
     */
    @Override
    public void setCurrentState(GameState.Type stateType) {
        GameState newState = gameStateFactory.create(stateType, this);
        getGsm().setState(newState);
    }

    /**
     * 다음 게임 루프에서 전환될 게임 상태를 설정합니다.
     * @param stateType 다음 프레임에 설정될 게임 상태의 타입
     */
    @Override
    public void setNextState(GameState.Type stateType) {
        this.nextState = stateType;
    }

    /**
     * 현재 웨이브가 클리어되었을 때 호출됩니다.
     * 다음 웨이브를 시작하도록 웨이브 관리자에게 지시합니다.
     */
    @Override
    public void onWaveCleared() {
        getWaveManager().startNextWave();
    }

    /**
     * 게임 플레이 상태의 핵심 로직을 업데이트합니다.
     * @param delta 마지막 프레임 이후 경과 시간 (밀리초)
     */
    @Override
    public void updatePlayingLogic(long delta) {
        gameWorld.update(delta);
    }

    /**
     * 게임 월드에 엔티티를 추가합니다.
     * @param entity 추가할 엔티티
     */
    @Override
    public void addEntity(Entity entity) { gameWorld.addEntity(entity); }
    /**
     * 게임 월드에서 엔티티를 제거합니다.
     * @param entity 제거할 엔티티
     */
    @Override
    public void removeEntity(Entity entity) { gameWorld.removeEntity(entity); }


    /**
     * 게임 월드의 모든 엔티티 리스트를 반환합니다.
     * @return 엔티티 리스트
     */
    @Override
    public java.util.List<Entity> getEntities() { return gameWorld.getEntities(); }

    /**
     * 플레이어의 함선(Ship) 엔티티를 반환합니다.
     * @return 플레이어 함선 엔티티
     */
    @Override
    public ShipEntity getShip() { return gameWorld.getShip(); }

    /**
     * 실제 게임 플레이를 시작합니다.
     * 플레이어 관련 설정을 초기화하고 게임 플레이 상태로 전환합니다.
     */
    @Override
    public void startGameplay() {
        getPlayerManager().startGameplay();
    }

    /**
     * 게임의 입력 핸들러를 반환합니다.
     * @return 입력 핸들러
     */
    public InputHandler getInputHandler() { return gameContainer.getInputHandler(); }
    /**
     * 엔티티 매니저를 반환합니다.
     * @return 엔티티 매니저
     */
    public EntityManager getEntityManager() { return gameWorld.getEntityManager(); }
    /**
     * 현재 플레이어 데이터를 반환합니다.
     * @return 현재 플레이어 데이터
     */
    public PlayerData getCurrentPlayer() { return gameContainer.getPlayerManager().getCurrentPlayer(); }
    /**
     * 플레이어 통계 정보를 반환합니다.
     * @return 플레이어 통계
     */
    public PlayerStats getPlayerStats() { return gameContainer.getPlayerManager().getPlayerStats(); }
    /**
     * 게임 상태 관리자를 반환합니다.
     * @return 게임 상태 관리자
     */
    public GameStateManager getGsm() { return gameContainer.getGsm(); }
    /**
     * 웨이브 매니저를 반환합니다.
     * @return 웨이브 매니저
     */
    public WaveManager getWaveManager() { return gameWorld.getWaveManager(); }
    /**
     * 플레이어 매니저를 반환합니다.
     * @return 플레이어 매니저
     */
    public PlayerManager getPlayerManager() { return gameContainer.getPlayerManager(); }

    /**
     * 플레이어가 현재 스테이지의 모든 아이템을 수집했는지 확인합니다.
     * @return 모든 아이템을 수집한 경우 true, 그렇지 않으면 false
     */
    @Override
    public boolean hasCollectedAllItems() {
        return gameSession.hasCollectedAllItems();
    }

    /**
     * 아이템 수집 상태를 초기화합니다.
     */
    @Override
    public void resetItemCollection() {
        gameSession.resetItemCollection();
    }

    /**
     * 플레이어가 현재 공격할 수 있는 상태인지 확인합니다.
     * @return 공격 가능한 경우 true, 그렇지 않으면 false
     */
    @Override
    public boolean canPlayerAttack() {
        return gameSession.canPlayerAttack();
    }

    /**
     * 사운드 관리자를 반환합니다.
     * @return 사운드 관리자
     */
    public SoundManager getSoundManager() {
        return gameContainer.getSoundManager();
    }

    /**
     * 게임 월드의 배경 객체를 반환합니다.
     * @return 배경 객체
     */
    public Background getBackground() {
        return gameWorld.getBackground();
    }

    /**
     * 게임 내 이벤트 버스를 반환합니다.
     * @return 이벤트 버스
     */
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}