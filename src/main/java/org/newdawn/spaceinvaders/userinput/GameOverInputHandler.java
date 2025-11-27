package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessGameOverMenuSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임 오버 메뉴(GameOverMenu)에서의 사용자 입력을 처리하는 클래스.
 * 좌/우 방향키와 엔터 키 입력에 대한 커맨드를 실행합니다.
 */
public class GameOverInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();

    /**
     * GameOverInputHandler 생성자.
     * 방향키와 엔터 키에 각각 메뉴 탐색 및 선택 처리 커맨드를 매핑합니다.
     * @param gameContext 게임 컨텍스트
     */
    public GameOverInputHandler(GameContext gameContext) {
        commandMap.put(KeyEvent.VK_LEFT, new MenuNavigateCommand(() -> gameContext.getGameContainer().getUiManager().getGameOverMenu(), MenuNavigateCommand.Direction.LEFT));
        commandMap.put(KeyEvent.VK_RIGHT, new MenuNavigateCommand(() -> gameContext.getGameContainer().getUiManager().getGameOverMenu(), MenuNavigateCommand.Direction.RIGHT));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessGameOverMenuSelectionCommand(gameContext));
    }

    /**
     * 입력을 받아 처리합니다.
     * 매핑된 키가 눌렸는지 확인하고, 해당하는 커맨드를 실행합니다.
     * @param input 게임의 주 입력 핸들러
     */
    public void handle(InputHandler input) {
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}