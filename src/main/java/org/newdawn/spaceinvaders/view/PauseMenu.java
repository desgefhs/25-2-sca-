package org.newdawn.spaceinvaders.view;

/**
 * 일시정지 메뉴의 데이터와 동작을 관리하는 클래스.
 */
public class PauseMenu {
    private String[] items = {"재개하기", "메인메뉴로 나가기", "종료하기"};
    private int selectedItemIndex = 0;

    /**
     * 메뉴 선택을 위로 한 칸 이동합니다.
     */
    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    /**
     * 메뉴 선택을 아래로 한 칸 이동합니다.
     */
    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
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
