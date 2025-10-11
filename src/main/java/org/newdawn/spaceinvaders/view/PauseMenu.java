package org.newdawn.spaceinvaders.view;

/**
 * 일시정지 메뉴의 항목 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, PausedState에서 이 클래스의 데이터를 사용해 UI를 그림
 */
public class PauseMenu {
    private String[] items = {"재개하기", "메인메뉴로 나가기", "종료하기"};
    private int selectedItemIndex = 0;

    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

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
