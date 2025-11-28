package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.userinput.command.*;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임 플레이 상태(PlayingState)에서의 사용자 입력을 처리하는 클래스.
 * 키를 누르고 있는 동안 계속 실행되는 커맨드(예: 이동)와,
 * 한 번 눌렀을 때 한 번만 실행되는 커맨드(예: 일시정지)를 구분하여 처리합니다.
 */
public class PlayingInputHandler {

    /** 한 번 눌렀을 때 한 번만 실행되는 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> singlePressCommands = new HashMap<>();
    /** 키를 누르고 있는 동안 계속 실행되는 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> continuousPressCommands = new HashMap<>();
    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * PlayingInputHandler 생성자.
     * 게임 플레이 중에 사용될 모든 키 커맨드를 초기화하고 매핑합니다.
     * @param gameContext 게임 컨텍스트
     */
    public PlayingInputHandler(GameContext gameContext) {
        this.gameContext = gameContext;
        mapCommands();
    }

    /**
     * 지속적인 입력과 단일 입력에 대한 커맨드를 초기화하고 각각의 맵에 매핑합니다.
     */
    private void mapCommands() {
        double moveSpeed = gameContext.getMoveSpeed();

        // 키를 누르고 있는 동안 계속 실행되는 커맨드
        continuousPressCommands.put(KeyEvent.VK_LEFT, new MoveCommand(gameContext, -moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_RIGHT, new MoveCommand(gameContext, moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_UP, new MoveCommand(gameContext, 0, -moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_DOWN, new MoveCommand(gameContext, 0, moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_SPACE, new FireCommand(gameContext));

        // 키를 한 번 눌렀을 때 한 번만 실행되는 커맨드
        singlePressCommands.put(KeyEvent.VK_ESCAPE, () -> gameContext.setCurrentState(GameState.Type.PAUSED));
        singlePressCommands.put(KeyEvent.VK_H, () -> gameContext.setShowHitboxes(!gameContext.getShowHitboxes())); // 'H' 키는 히트박스 표시/숨기기
        singlePressCommands.put(KeyEvent.VK_K, () -> gameContext.getGameContainer().getWaveManager().skipToNextBossWave()); // 'K' 키는 현재 웨이브 건너뛰기
        
        singlePressCommands.put(KeyEvent.VK_1, new SwitchWeaponCommand(gameContext, "DefaultGun"));
        singlePressCommands.put(KeyEvent.VK_2, new SwitchWeaponCommand(gameContext, "Shotgun"));
        singlePressCommands.put(KeyEvent.VK_3, new SwitchWeaponCommand(gameContext, "Laser"));
    }

    /**
     * 입력을 받아 처리합니다.
     * 매 프레임 시작 시 함선의 이동량을 0으로 리셋한 후,
     * 지속적인 입력과 단일 입력을 각각 순회하며 해당하는 커맨드를 실행합니다.
     * @param input 게임의 주 입력 핸들러
     */
    public void handle(InputHandler input) {
        ShipEntity ship = gameContext.getShip();
        if (ship != null) {
            // 매 프레임 시작 시 이동량을 리셋하여 키를 뗐을 때 멈추도록 함.
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);
        }

        // 지속적인 입력 커맨드 처리
        for (Map.Entry<Integer, Command> entry : continuousPressCommands.entrySet()) {
            if (input.isPressed(entry.getKey())) {
                entry.getValue().execute();
            }
        }

        // 단일 입력 커맨드 처리
        for (Map.Entry<Integer, Command> entry : singlePressCommands.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}