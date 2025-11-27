package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessPausedMenuSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 일시 정지 메뉴(PauseMenu)에서의 사용자 입력을 처리하는 클래스.
 * 상/하 방향키와 엔터 키 입력에 대한 커맨드를 실행합니다.
 */
public class PausedInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();

    /**
     * PausedInputHandler 생성자.
     * 키 입력에 따른 커맨드를 매핑합니다.
     * @param gameContext 게임 컨텍스트
     */
    public PausedInputHandler(GameContext gameContext) {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> gameContext.getGameContainer().getUiManager().getPauseMenu(), MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> gameContext.getGameContainer().getUiManager().getPauseMenu(), MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessPausedMenuSelectionCommand(gameContext));
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