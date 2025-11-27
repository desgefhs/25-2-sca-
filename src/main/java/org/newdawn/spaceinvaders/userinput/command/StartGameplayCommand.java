package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;

/**
 * 실제 게임 플레이를 시작하는 커맨드.
 */
public class StartGameplayCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * StartGameplayCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public StartGameplayCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * {@link GameContext#startGameplay()}를 호출하여 게임 플레이를 시작합니다.
     */
    @Override
    public void execute() {
        gameContext.startGameplay();
    }
}
