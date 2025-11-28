package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.*;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 펫 메뉴(PetMenuView)에서의 사용자 입력을 처리하는 클래스.
 * 메뉴 탐색, 펫 장착/해제, 펫 강화 등의 커맨드를 실행합니다.
 */
public class PetMenuInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();

    /**
     * PetMenuInputHandler 생성자.
     * 키 입력에 따른 커맨드를 매핑합니다.
     * @param gameContext 게임 컨텍스트
     * @param petMenuSupplier PetMenuView 인스턴스를 제공하는 공급자
     */
    public PetMenuInputHandler(GameContext gameContext, Supplier<PetMenuView> petMenuSupplier) {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> petMenuSupplier.get(), MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> petMenuSupplier.get(), MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new ToggleEquipPetCommand(gameContext, petMenuSupplier));
        commandMap.put(KeyEvent.VK_U, new UpgradePetCommand(gameContext, petMenuSupplier)); // 'U' 키는 업그레이드
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
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
