package org.newdawn.spaceinvaders;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 키보드 입력을 감지하고, 현재 키 상태를 저장하는 책임을 가지는 클래스.
 * 눌린 키에 대한 상태(플래그)를 제공하여 다른 클래스에서 사용할 수 있게 합니다.
 */
public class InputHandler extends KeyAdapter {

    /** "Press any key" 메시지 후 첫 키 입력을 감지하기 위한 카운터 */
    private int pressCount = 1;

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean firePressed = false;
    private boolean waitingForKeyPress = true;

    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isFirePressed() { return firePressed; }
    public boolean isWaitingForKeyPress() { return waitingForKeyPress; }

    public void setWaitingForKeyPress(boolean waiting) {
        this.waitingForKeyPress = waiting;
        if (waiting) {
            pressCount = 1; // 대기 상태로 전환될 때 카운터 리셋
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (waitingForKeyPress) {
            return;
        }
        updateKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (waitingForKeyPress) {
            return;
        }
        updateKeyState(e.getKeyCode(), false);
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
        if (keyCode == KeyEvent.VK_SPACE) {
            firePressed = pressed;
        }
    }

    /**
     * "Press any key" 상태에서 키가 입력되었는지 확인하고, 게임 시작 신호를 반환합니다.
     * @return 게임을 시작해야 하면 true, 아니면 false
     */
    public boolean checkStartGameKey() {
        if (waitingForKeyPress) {
            if (pressCount == 1) {
                waitingForKeyPress = false;
                pressCount = 0;
                return true; // 게임 시작
            } else {
                pressCount++;
            }
        }
        return false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (checkStartGameKey()) {
            // 이 메소드는 이제 게임 시작 로직을 직접 호출하지 않습니다.
        }

        if (e.getKeyChar() == 27) {
            System.exit(0);
        }
    }
}
