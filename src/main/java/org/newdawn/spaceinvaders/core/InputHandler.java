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
 * 키보드 입력을 감지하고, 현재 키 상태를 저장하는 책임을 가지는 클래스.
 */
public class InputHandler extends KeyAdapter {

    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();
    private final Set<Integer> consumedKeys = new HashSet<>();

    private final List<Character> typedChars = new ArrayList<>();

    // --- 연속 입력이 필요한 경우를 위한 Getter --- //
    public boolean isPressed(int keyCode) {
        return pressedKeys.getOrDefault(keyCode, false);
    }

    public boolean isLeftPressed() { return isPressed(KeyEvent.VK_LEFT); }
    public boolean isRightPressed() { return isPressed(KeyEvent.VK_RIGHT); }
    public boolean isUpPressed() { return isPressed(KeyEvent.VK_UP); }
    public boolean isDownPressed() { return isPressed(KeyEvent.VK_DOWN); }
    public boolean isFirePressed() { return isPressed(KeyEvent.VK_SPACE); }

    // --- 단일 입력 처리를 위한 "Consume" 메소드들 --- //

    public boolean isPressedAndConsume(int keyCode) {
        if (isPressed(keyCode) && !consumedKeys.contains(keyCode)) {
            consumedKeys.add(keyCode);
            return true;
        }
        return false;
    }

    public boolean isEnterPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_ENTER);
    }

    public boolean isKPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_K);
    }

    public boolean isHPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_H);
    }

    public boolean isBPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_B);
    }

    public boolean isLeftPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_LEFT);
    }

    public boolean isRightPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_RIGHT);
    }

    public boolean isUpPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_UP);
    }

    public boolean isDownPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_DOWN);
    }

    public boolean isFirePressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_SPACE);
    }

    public boolean isUPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_U);
    }

    public boolean isEscPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_ESCAPE);
    }

    public boolean isOnePressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_1);
    }

    public boolean isTwoPressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_2);
    }

    public boolean isThreePressedAndConsume() {
        return isPressedAndConsume(KeyEvent.VK_3);
    }

    public char consumeTypedChar() {
        synchronized (typedChars) {
            if (typedChars.isEmpty()) {
                return 0;
            }
            return typedChars.remove(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        updateKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateKeyState(e.getKeyCode(), false);
    }

    private void updateKeyState(int keyCode, boolean pressed) {
        pressedKeys.put(keyCode, pressed);
        if (!pressed) {
            consumedKeys.remove(keyCode);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (typedChars) {
            typedChars.add(e.getKeyChar());
        }
    }
}