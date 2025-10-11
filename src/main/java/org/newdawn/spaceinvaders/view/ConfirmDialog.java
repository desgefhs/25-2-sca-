package org.newdawn.spaceinvaders.view;

/**
 * 확인 대화 상자의 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, 상태(예: ExitConfirmationState)에서 이 클래스의 데이터를 사용해 UI를 그림
 */
public class ConfirmDialog {
    private final String[] items = {"Confirm", "Cancel"};
    /** 현재 선택된 항목의 인덱스 */
    private int selectedItemIndex = 0;
    /** 대화 상자에 표시될 메시지 */
    private final String message;

    /**
     * 표시할 메시지와 함께 새로운 확인 대화 상자 데이터 객체를 생성
     *
     * @param message 대화 상자에 표시할 메시지
     */
    public ConfirmDialog(String message) {
        this.message = message;
    }

    public void moveLeft() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    public void moveRight() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
    }

    public String getMessage() {
        return message;
    }

    public String getSelectedItem() {
        return items[selectedItemIndex];
    }

    public String getItem(int index) {
        return items[index];
    }

    public int getItemCount() {
        return items.length;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
