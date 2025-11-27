package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.GoToStateCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 랭킹 화면(RankingState)에서의 사용자 입력을 처리하는 클래스.
 * 특정 키 입력을 감지하여 그에 맞는 커맨드(Command)를 실행합니다.
 */
public class RankingInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();
    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * RankingInputHandler 생성자.
     * 엔터 키에 메인 메뉴로 돌아가는 커맨드를 매핑합니다.
     *
     * @param gameContext 게임 컨텍스트
     */
    public RankingInputHandler(GameContext gameContext) {
        this.gameContext = gameContext;
        commandMap.put(KeyEvent.VK_ENTER, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    /**
     * 입력을 받아 처리합니다.
     * 엔터 키가 눌렸는지 확인하고, 해당 커맨드를 실행합니다.
     *
     * @param input 게임의 주 입력 핸들러
     */
    public void handle(InputHandler input) {
        if (input.isPressedAndConsume(KeyEvent.VK_ENTER)) {
            gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
    }
}