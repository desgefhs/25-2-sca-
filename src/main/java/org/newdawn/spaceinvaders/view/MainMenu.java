package org.newdawn.spaceinvaders.view;

/**
 * 메인 메뉴의 데이터(아이템)와 동작(좌/우 이동)을 관리하는 클래스.
 */
public class MainMenu {
    private String[] items = {"1. 게임시작", "2. 장비", "3. 상점", "4. 설정"};
    private int selectedItemIndex = 0;

    /**
     * 메뉴 선택을 왼쪽으로 한 칸 이동합니다.
     */
    public void moveLeft() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    /**
     * 메뉴 선택을 오른쪽으로 한 칸 이동합니다.
     */
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