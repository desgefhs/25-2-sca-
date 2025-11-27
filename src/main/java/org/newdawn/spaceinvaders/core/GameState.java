package org.newdawn.spaceinvaders.core;

import java.awt.Graphics2D;

/**
 * 게임의 단일 상태(예: 메인 메뉴, 플레이 중, 게임 오버)를 나타내는 상태 패턴(State Pattern)의 인터페이스.
 * 모든 게임 상태 클래스는 이 인터페이스를 구현해야 합니다.
 */
public interface GameState {

    /**
     * 게임 내에 존재할 수 있는 모든 상태의 타입을 정의하는 열거형.
     */
    enum Type {
        MAIN_MENU,
        PLAYING,
        PAUSED,
        GAME_OVER,
        GAME_WON,
        RANKING,
        SHOP,
        SHOP_MAIN_MENU,
        ITEM_DRAW,
        PET_MENU,
        WEAPON_MENU,
        EXIT_CONFIRMATION,
        WAVE_CLEARED // 다음 웨이브로 넘어가기 전의 일시적인 상태
    }

    /**
     * 게임 상태를 초기화합니다.
     * 필요한 리소스를 로드하거나 변수를 초기화하는 데 사용됩니다.
     */
    void init();

    /**
     * 이 상태에서의 사용자 입력을 처리합니다.
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    void handleInput(InputHandler input);

    /**
     * 이 상태의 게임 로직을 업데이트합니다.
     * @param delta 마지막 업데이트 이후 경과된 시간 (밀리초)
     */
    void update(long delta);

    /**
     * 이 상태의 시각적 표현을 화면에 렌더링(그리기)합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    void render(Graphics2D g);

    /**
     * 이 상태로 처음 진입할 때 한 번 호출됩니다.
     */
    void onEnter();

    /**
     * 이 상태에서 다른 상태로 전환될 때 한 번 호출됩니다.
     */
    void onExit();
}