package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.gamestates.MainMenuState;
import org.newdawn.spaceinvaders.gamestates.PlayingState;

/**
 * 게임의 상태(GameState)를 관리하는 클래스 (상태 머신).
 * 현재 게임 상태를 추적하고, 상태 전환을 처리하며,
 * 게임 루프의 주요 호출(update, render, handleInput)을 현재 상태에 위임합니다.
 */
public class GameStateManager {

    /** 현재 활성화된 게임 상태. */
    private GameState currentState;
    /** '플레이 중' 상태의 인스턴스를 캐싱. */
    private PlayingState playingState;
    /** '메인 메뉴' 상태의 인스턴스를 캐싱. */
    private MainMenuState mainMenuState;

    /**
     * 게임의 현재 상태를 설정합니다.
     * 이전 상태의 onExit()을 호출하고, 새로운 상태의 init()과 onEnter()를 호출하여 상태 전환을 처리합니다.
     *
     * @param state 새로 설정할 게임 상태 객체
     */
    public void setState(GameState state) {
        if (currentState != null) {
            currentState.onExit();
        }
        currentState = state;
        if (currentState != null) {
            if (state instanceof PlayingState) {
                this.playingState = (PlayingState) state;
            }
            if (state instanceof MainMenuState) {
                this.mainMenuState = (MainMenuState) state;
            }
            currentState.init();
            currentState.onEnter();
        }
    }

    /**
     * 현재 게임 상태를 반환합니다.
     * @return 현재 활성화된 GameState
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * 캐시된 '플레이 중' 상태를 반환합니다.
     * @return PlayingState 인스턴스
     */
    public PlayingState getPlayingState() {
        return playingState;
    }

    /**
     * 캐시된 '메인 메뉴' 상태를 반환합니다.
     * @return MainMenuState 인스턴스
     */
    public MainMenuState getMainMenuState() {
        return mainMenuState;
    }

    /**
     * 현재 게임 상태의 업데이트 로직을 호출합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    public void update(long delta) {
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    /**
     * 현재 게임 상태의 입력 처리 로직을 호출합니다.
     * @param input 입력 핸들러
     */
    public void handleInput(InputHandler input) {
        if (currentState != null) {
            currentState.handleInput(input);
        }
    }

    /**
     * 현재 게임 상태의 렌더링 로직을 호출합니다.
     * @param g 그래픽 컨텍스트
     */
    public void render(java.awt.Graphics2D g) {
        if (currentState != null) {
            currentState.render(g);
        }
    }
}
