package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.Graphics2D;

/**
 * 단일 게임 상태에 대한 인터페이스입니다.
 */
public interface GameState {

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
        WAVE_CLEARED // 이것은 일시적인 상태입니다
    }

    /**
     * 게임 상태를 초기화합니다.
     */
    void init();

    /**
     * 이 상태에 대한 사용자 입력을 처리합니다.
     *
     * @param input 현재 키 상태를 제공하는 입력 핸들러.
     */
    void handleInput(InputHandler input);

    /**
     * 이 상태에 대한 게임 로직을 업데이트합니다.
     *
     * @param delta 마지막 업데이트 이후 경과된 시간.
     */
    void update(long delta);

    /**
     * 이 상태의 시각적 표현을 렌더링합니다.
     *
     * @param g 그리기를 수행할 그래픽 컨텍스트.
     */
    void render(Graphics2D g);

    /**
     * 이 상태에 들어갈 때 호출됩니다.
     */
    void onEnter();

    /**
     * 이 상태에서 나갈 때 호출됩니다.
     */
    void onExit();
}