package org.newdawn.spaceinvaders.view;

/**
 * 게임 오버 화면의 메뉴 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, GameOverState에서 이 클래스의 데이터를 사용해 UI를 그림
 */
public class GameOverMenu {
    private String[] items = {"다시하기", "메인 메뉴로"};
    private int selectedItemIndex = 0;

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
