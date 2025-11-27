package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;

/**
 * 게임의 현재 상태를 다른 상태로 전환하는 커맨드.
 */
public class GoToStateCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 전환할 목표 게임 상태의 타입. */
    private final GameState.Type stateType;

    /**
     * GoToStateCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param stateType 전환할 게임 상태의 타입
     */
    public GoToStateCommand(GameContext gameContext, GameState.Type stateType) {
        this.gameContext = gameContext;
        this.stateType = stateType;
    }

    /**
     * {@link GameContext#setCurrentState}를 호출하여 게임 상태를 전환합니다.
     */
    @Override
    public void execute() {
        gameContext.setCurrentState(stateType);
    }
}
