package org.newdawn.spaceinvaders.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 키보드 입력을 감지하고 현재 키 상태를 저장
 * 연속적인 키 입력과 한 번만 처리되어야 하는 키 입력을 구분하여 관리
 */
public class InputHandler extends KeyAdapter {

    // --- 키 눌림 상태 플래그 ---
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean firePressed = false;
    private boolean escPressed = false;
    private boolean kPressed = false;
    private boolean hPressed = false;
    private boolean uPressed = false;
    private boolean bPressed = false;
    private boolean onePressed = false;
    private boolean twoPressed = false;
    private boolean threePressed = false;
    private boolean enterPressed = false;

    // --- 키 입력 처리 완료 플래그 (단일 입력용) ---
    private boolean leftKeyProcessed = false;
    private boolean rightKeyProcessed = false;
    private boolean upKeyProcessed = false;
    private boolean downKeyProcessed = false;
    private boolean fireKeyProcessed = false;
    private boolean escKeyProcessed = false;
    private boolean kKeyProcessed = false;
    private boolean hKeyProcessed = false;
    private boolean uKeyProcessed = false;
    private boolean bKeyProcessed = false;
    private boolean oneKeyProcessed = false;
    private boolean twoKeyProcessed = false;
    private boolean threeKeyProcessed = false;
    private boolean enterKeyProcessed = false;

    /** 타이핑된 문자들을 저장하는 리스트 */
    private final List<Character> typedChars = new ArrayList<>();

    // --- 연속 입력이 필요한 경우를 위한 Getter --- //
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isFirePressed() { return firePressed; }

    // --- 단일 입력 처리를 위한 "Consume" 메소드 --- //

    public boolean isEnterPressedAndConsume() {
        if (enterPressed && !enterKeyProcessed) {
            enterKeyProcessed = true;
            return true;
        }
        return false;
    }

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


    /**
     * 키가 눌렸을 때 호출 해당 키의 상태를 '눌림'으로 업데이트
     */
    @Override
    public void keyPressed(KeyEvent e) {
        updateKeyState(e.getKeyCode(), true);
    }

    /**
     * 키에서 손을 뗐을 때 호출 해당 키의 상태를 '떼어짐'으로 업데이트
     */
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
        if (keyCode == KeyEvent.VK_SPACE) {
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
        if (keyCode == KeyEvent.VK_ENTER) {
            enterKeyProcessed = false;
        }
    }

    /**
     * 키 코드에 따라 해당 키의 눌림 상태를 업데이트
     * @param keyCode 업데이트할 키의 코드
     * @param pressed 눌림 상태 (true: 눌림, false: 떼어짐)
     */
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
        if (keyCode == KeyEvent.VK_SPACE) {
            firePressed = pressed;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
            enterPressed = pressed;
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

    /**
     * 키가 타이핑되었을 때(눌렀다 떼었을 때) 호출
     * 입력된 문자를 큐에 추가
     * @param e 키 이벤트
     */
    @Override
    public void keyTyped(KeyEvent e) {
        synchronized (typedChars) {
            typedChars.add(e.getKeyChar());
        }
    }
}