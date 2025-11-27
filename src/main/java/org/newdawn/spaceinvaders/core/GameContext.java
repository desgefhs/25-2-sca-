package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.view.Background;

import java.util.List;
import java.util.Map;

/**
 * 게임의 다른 구성 요소들이 핵심 시스템과 상호작용할 수 있도록 하는 퍼사드(Facade) 인터페이스.
 * 이 인터페이스는 {@link GameManager}에 의해 구현되며, 게임의 다양한 부분에 대한
 * 중앙 접근점을 제공하여 컴포넌트 간의 결합도를 낮춥니다.
 */
public interface GameContext {

    /**
     * 모든 주요 관리자 객체를 포함하는 {@link GameContainer}를 반환합니다.
     * @return 게임 컨테이너
     */
    GameContainer getGameContainer();

    // --- 게임플레이 액션 ---

    /**
     * 게임 플레이를 시작합니다.
     */
    void startGameplay();

    /**
     * 현재 게임 상태를 즉시 변경합니다.
     * @param stateType 전환할 게임 상태의 타입
     */
    void setCurrentState(GameState.Type stateType);

    /**
     * 다음 게임 루프에서 전환될 상태를 예약합니다.
     * @param stateType 다음으로 전환할 게임 상태의 타입
     */
    void setNextState(GameState.Type stateType);

    /**
     * 현재 웨이브가 클리어되었을 때의 로직을 처리합니다.
     */
    void onWaveCleared();

    /**
     * '플레이 중' 상태의 핵심 로직을 업데이트합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    void updatePlayingLogic(long delta);

    // --- 엔티티 관리 ---

    /**
     * 게임 월드에 엔티티를 추가합니다.
     * @param entity 추가할 엔티티
     */
    void addEntity(Entity entity);

    /**
     * 게임 월드에서 엔티티를 제거합니다.
     * @param entity 제거할 엔티티
     */
    void removeEntity(Entity entity);

    /**
     * 현재 게임 월드에 있는 모든 엔티티의 목록을 반환합니다.
     * @return 엔티티 리스트
     */
    List<Entity> getEntities();

    /**
     * 플레이어의 함선 엔티티를 반환합니다.
     * @return 함선 엔티티
     */
    ShipEntity getShip();

    // --- 플레이어 상태 ---

    /**
     * 플레이어가 현재 공격할 수 있는 상태인지 확인합니다.
     * @return 공격 가능 시 true
     */
    boolean canPlayerAttack();

    // --- UI 및 렌더링 ---

    /**
     * 스크롤되는 배경 객체를 반환합니다.
     * @return 배경 객체
     */
    Background getBackground();

    /**
     * 정적 배경 이미지를 위한 스프라이트를 반환합니다.
     * @return 배경 스프라이트
     */
    Sprite getStaticBackgroundSprite();

    // --- 데이터 및 설정 ---

    /**
     * 사용 가능한 모든 무기의 맵을 반환합니다.
     * @return 무기 맵
     */
    Map<String, Weapon> getWeapons();

    /**
     * 플레이어의 기본 이동 속도를 반환합니다.
     * @return 이동 속도
     */
    double getMoveSpeed();

    /**
     * 히트박스 표시 여부를 반환합니다.
     * @return 히트박스 표시 여부
     */
    boolean getShowHitboxes();

    /**
     * 히트박스 표시 여부를 설정합니다.
     * @param show 히트박스를 표시하려면 true
     */
    void setShowHitboxes(boolean show);

    // --- 알림 및 메시지 ---

    /**
     * 화면에 표시될 현재 메시지를 반환합니다.
     * @return 메시지 문자열
     */
    String getMessage();

    /**
     * 화면에 표시할 메시지를 설정합니다.
     * @param message 표시할 메시지
     */
    void setMessage(String message);

    /**
     * 메시지가 사라질 시간을 설정합니다.
     * @param time 메시지가 사라질 시스템 시간 (타임스탬프)
     */
    void setMessageEndTime(long time);

    // --- 기타 ---

    /**
     * 플레이어가 모든 아이템을 수집했는지 확인합니다. (특정 게임 모드용)
     * @return 모든 아이템 수집 시 true
     */
    boolean hasCollectedAllItems();

    /**
     * 아이템 수집 상태를 리셋합니다.
     */
    void resetItemCollection();

    /**
     * 현재 게임 루프에서 로직 업데이트가 필요한지 설정합니다.
     * @param required 로직 업데이트가 필요하면 true
     */
    void setLogicRequiredThisLoop(boolean required);

    /**
     * 게임의 이벤트 버스를 반환합니다.
     * @return 이벤트 버스 인스턴스
     */
    EventBus getEventBus();
}