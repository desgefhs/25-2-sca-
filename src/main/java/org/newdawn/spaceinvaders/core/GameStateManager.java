package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.gamestates.GameState;
import org.newdawn.spaceinvaders.gamestates.MainMenuState;
import org.newdawn.spaceinvaders.gamestates.PlayingState;

/**
 * 게임의 여러 상태를 관리하는 클래스
 *게임 상태 전환을 처리
 */
public class GameStateManager {

    private GameState currentState;
    private PlayingState playingState;
    private MainMenuState mainMenuState;

    /**
     * 게임 상태를 설정
     * 이전 상태의 종료 로직을 호출하고, 새로운 상태를 설정
     *
     * @param state 새로운 게임 상태
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


    public GameState getCurrentState() {
        return currentState;
    }

    public PlayingState getPlayingState() {
        return playingState;
    }

    public MainMenuState getMainMenuState() {
        return mainMenuState;
    }

    public void update(long delta) {
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    //  현재 게임 상태의 입력 처리
    public void handleInput(InputHandler input) {
        if (currentState != null) {
            currentState.handleInput(input);
        }
    }

    // 현재 게임 상태를 렌더링
    public void render(java.awt.Graphics2D g) {
        if (currentState != null) {
            currentState.render(g);
        }
    }
}
