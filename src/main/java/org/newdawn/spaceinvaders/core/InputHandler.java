package org.newdawn.spaceinvaders.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 키보드 입력을 감지하고, 현재 키 상태를 저장하는 책임을 가지는 클래스.
 */
public class InputHandler extends KeyAdapter {

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean firePressed = false;
    private boolean escPressed = false;

    // 키 한 번 누름을 처리하기 위한 플래그들
    private boolean leftKeyProcessed = false;
    private boolean rightKeyProcessed = false;
    private boolean upKeyProcessed = false;
    private boolean downKeyProcessed = false;
    private boolean fireKeyProcessed = false;
    private boolean escKeyProcessed = false;

    private final List<Character> typedChars = new ArrayList<>();

    // --- 연속 입력이 필요한 경우를 위한 Getter --- //
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isFirePressed() { return firePressed; }

    // --- 단일 입력 처리를 위한 "Consume" 메소드들 --- //

    public boolean isLeftPressedAndConsume() {
        if (leftPressed && !leftKeyProcessed) {
            leftKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isRightPressedAndConsume() {
        if (rightPressed && !rightKeyProcessed) {
            rightKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isUpPressedAndConsume() {
        if (upPressed && !upKeyProcessed) {
            upKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isDownPressedAndConsume() {
        if (downPressed && !downKeyProcessed) {
            downKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isFirePressedAndConsume() {
        if (firePressed && !fireKeyProcessed) {
            fireKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isEscPressedAndConsume() {
        if (escPressed && !escKeyProcessed) {
            escKeyProcessed = true;
            return true;
        }
        return false;
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
        // 키에서 손을 떼면, 다시 누를 수 있도록 처리
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            leftKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            rightKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_UP) {
            upKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER) {
            fireKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            escKeyProcessed = false;
        }
    }

    private void updateKeyState(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_LEFT) {
            leftPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            rightPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_UP) {
            upPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            downPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER) {
            firePressed = pressed;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
            escPressed = pressed;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (typedChars) {
            typedChars.add(e.getKeyChar());
        }
    }
}