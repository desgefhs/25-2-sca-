package org.newdawn.spaceinvaders.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 키보드 입력을 감지하며, 현재 키의 상태를 저장하고 관리하는 클래스입니다.
 * {@link KeyAdapter}를 상속받아 키 이벤트를 처리합니다.
 * 키를 계속 누르고 있는 상태와, 한 번만 처리되어야 하는 단일 입력 상태를 구분하여 관리합니다.
 */
public class InputHandler extends KeyAdapter {

    /** 현재 눌린 키의 상태를 저장하는 맵. (Key: KeyEvent 코드, Value: 눌림 여부) */
    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();
    /** 단일 입력으로 처리되어 "소비된" 키의 집합. 키를 뗄 때까지 중복 처리를 방지합니다. */
    private final Set<Integer> consumedKeys = new HashSet<>();
    /** 타이핑된 문자들을 저장하는 리스트. */
    private final List<Character> typedChars = new ArrayList<>();

    // --- 연속 입력이 필요한 경우를 위한 Getter --- //

    /**
     * 특정 키가 현재 눌려 있는지 확인합니다. (키를 누르고 있는 동안 계속 true)
     *
     * @param keyCode 확인할 키의 {@link KeyEvent} 코드
     * @return 키가 눌려있으면 true, 그렇지 않으면 false
     */
    public boolean isPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
    }

    public boolean isLeftPressed() { return isPressed(KeyEvent.VK_LEFT); }
    public boolean isRightPressed() { return isPressed(KeyEvent.VK_RIGHT); }
    public boolean isUpPressed() { return isPressed(KeyEvent.VK_UP); }
    public boolean isDownPressed() { return isPressed(KeyEvent.VK_DOWN); }
    public boolean isFirePressed() { return isPressed(KeyEvent.VK_SPACE); }

    // --- 단일 입력 처리를 위한 "Consume" 메소드들 --- //

    /**
     * 특정 키가 눌렸는지 확인하고, 눌렸다면 "소비"하여 다음 확인 전까지 true를 반환하지 않도록 합니다.
     * 메뉴 선택과 같이 한 번의 누름에 한 번의 액션만 필요할 때 사용됩니다.
     *
     * @param keyCode 확인할 키의 {@link KeyEvent} 코드
     * @return 키가 눌렸고 아직 소비되지 않았다면 true, 그렇지 않으면 false
     */
    public boolean isPressedAndConsume(int keyCode) {
        if (isPressed(keyCode) && !consumedKeys.contains(keyCode)) {
            consumedKeys.add(keyCode);
            return true;
        }
        return false;
    }

    // 이하 특정 키에 대한 isPressedAndConsume 헬퍼 메소드들
    public boolean isEnterPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_ENTER); }
    public boolean isKPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_K); }
    public boolean isHPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_H); }
    public boolean isBPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_B); }
    public boolean isLeftPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_LEFT); }
    public boolean isRightPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_RIGHT); }
    public boolean isUpPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_UP); }
    public boolean isDownPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_DOWN); }
    public boolean isFirePressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_SPACE); }
    public boolean isUPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_U); }
    public boolean isEscPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_ESCAPE); }
    public boolean isOnePressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_1); }
    public boolean isTwoPressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_2); }
    public boolean isThreePressedAndConsume() { return isPressedAndConsume(KeyEvent.VK_3); }

    /**
     * 타이핑된 문자 버퍼에서 문자를 하나 가져와 소비합니다.
     * @return 버퍼에 문자가 있으면 해당 문자, 없으면 0.
     */
    public char consumeTypedChar() {
        synchronized (typedChars) {
            if (typedChars.isEmpty()) {
                return 0;
            }
            return typedChars.remove(0);
        }
    }

    /**
     * 키가 눌렸을 때 호출되는 이벤트 리스너.
     * @param e 키 이벤트
     */
    @Override
    public void keyPressed(KeyEvent e) {
        updateKeyState(e.getKeyCode(), true);
    }

    /**
     * 키에서 손을 뗐을 때 호출되는 이벤트 리스너.
     * @param e 키 이벤트
     */
    @Override
    public void keyReleased(KeyEvent e) {
        updateKeyState(e.getKeyCode(), false);
    }

    /**
     * 키 상태를 업데이트하고, 키를 뗐을 경우 '소비된' 상태를 해제합니다.
     * @param keyCode 업데이트할 키 코드
     * @param pressed 눌림 상태
     */
    private void updateKeyState(int keyCode, boolean pressed) {
        pressedKeys.put(keyCode, pressed);
        if (!pressed) {
            consumedKeys.remove(keyCode);
        }
    }

    /**
     * 문자가 타이핑되었을 때 호출되는 이벤트 리스너.
     * @param e 키 이벤트
     */
    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (typedChars) {
            typedChars.add(e.getKeyChar());
        }
    }
}