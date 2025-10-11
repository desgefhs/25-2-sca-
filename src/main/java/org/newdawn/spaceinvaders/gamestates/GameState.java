package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.Graphics2D;

/**
 * 게임의 단일 상태를 나타내는 인터페이스
 * 메인 메뉴, 플레이 중, 게임 오버 등 각 상태는 이 인터페이스를 구현해야 함
 */
public interface GameState {

    /**
     * 게임의 모든 가능한 상태를 정의
     */
    enum Type {
        /** 메인 메뉴 */
        MAIN_MENU,
        /** 플레이 중 */
        PLAYING,
        /** 일시정지 */
        PAUSED,
        /** 게임 오버 */
        GAME_OVER,
        /** 게임 승리 */
        GAME_WON,
        /** 랭킹 표시 */
        RANKING,
        /** 일반 상점 */
        SHOP,
        /** 상점 메인 메뉴 */
        SHOP_MAIN_MENU,
        /** 아이템 뽑기 */
        ITEM_DRAW,
        /** 펫 메뉴 */
        PET_MENU,
        /** 무기 메뉴 */
        WEAPON_MENU,
        /** 종료 확인 */
        EXIT_CONFIRMATION,
        /** 웨이브 클리어 (다음 웨이브로 넘어가는 임시 상태) */
        WAVE_CLEARED
    }

    /**
     * 게임 상태를 초기화
     */
    void init();

    /**
     * 이 상태에 대한 사용자 입력을 처리
     *
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    void handleInput(InputHandler input);

    /**
     * 이 상태에 대한 게임 로직을 업데이트
     *
     * @param delta 마지막 업데이트 이후 경과된 시간 (밀리초)
     */
    void update(long delta);

    /**
     * 이 상태의 시각적 표현을 렌더링
     *
     * @param g 그림을 그릴 그래픽 컨텍스트
     */
    void render(Graphics2D g);

    /**
     * 이 상태로 진입할 때 호출
     */
    void onEnter();

    /**
     * 이 상태에서 벗어날 때 호출
     */
    void onExit();
}
