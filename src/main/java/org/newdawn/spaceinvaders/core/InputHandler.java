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
    private boolean kPressed = false;
    private boolean hPressed = false;
    private boolean uPressed = false; // For Upgrade
    private boolean bPressed = false; // For Boss
    private boolean onePressed = false;
    private boolean twoPressed = false;
    private boolean threePressed = false;

    // 키 한 번 누름을 처리하기 위한 플래그들
    private boolean leftKeyProcessed = false;
    private boolean rightKeyProcessed = false;
    private boolean upKeyProcessed = false;
    private boolean downKeyProcessed = false;
    private boolean fireKeyProcessed = false;
    private boolean escKeyProcessed = false;
    private boolean kKeyProcessed = false;
    private boolean hKeyProcessed = false;
    private boolean uKeyProcessed = false; // For Upgrade
    private boolean bKeyProcessed = false; // For Boss
    private boolean oneKeyProcessed = false;
    private boolean twoKeyProcessed = false;
    private boolean threeKeyProcessed = false;

    private final List<Character> typedChars = new ArrayList<>();

    // --- 연속 입력이 필요한 경우를 위한 Getter --- //
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isFirePressed() { return firePressed; }

    // --- 단일 입력 처리를 위한 "Consume" 메소드들 --- //

    public boolean isKPressedAndConsume() {
        if (kPressed && !kKeyProcessed) {
            kKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isHPressedAndConsume() {
        if (hPressed && !hKeyProcessed) {
            hKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isBPressedAndConsume() {
        if (bPressed && !bKeyProcessed) {
            bKeyProcessed = true;
            return true;
        }
        return false;
    }

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

    public boolean isUPressedAndConsume() {
        if (uPressed && !uKeyProcessed) {
            uKeyProcessed = true;
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

    public boolean isOnePressedAndConsume() {
        if (onePressed && !oneKeyProcessed) {
            oneKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isTwoPressedAndConsume() {
        if (twoPressed && !twoKeyProcessed) {
            twoKeyProcessed = true;
            return true;
        }
        return false;
    }

    public boolean isThreePressedAndConsume() {
        if (threePressed && !threeKeyProcessed) {
            threeKeyProcessed = true;
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
        if (keyCode == KeyEvent.VK_K) {
            kKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_H) {
            hKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_U) {
            uKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_B) {
            bKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_1) {
            oneKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_2) {
            twoKeyProcessed = false;
        }
        if (keyCode == KeyEvent.VK_3) {
            threeKeyProcessed = false;
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
        if (keyCode == KeyEvent.VK_K) {
            kPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_H) {
            hPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_U) {
            uPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_B) {
            bPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_1) {
            onePressed = pressed;
        }
        if (keyCode == KeyEvent.VK_2) {
            twoPressed = pressed;
        }
        if (keyCode == KeyEvent.VK_3) {
            threePressed = pressed;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (typedChars) {
            typedChars.add(e.getKeyChar());
        }
    }
}