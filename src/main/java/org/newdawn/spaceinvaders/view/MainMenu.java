package org.newdawn.spaceinvaders.view;

/**
 * 메인 메뉴의 항목 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, MainMenuState에서 이 클래스의 데이터를 사용해 UI를 그림
 */
public class MainMenu {
    /** 메인 메뉴 항목들 */
    private String[] items = {"1. 게임시작", "2. 랭킹", "3. 무기", "4. 펫", "5. 상점", "6. 설정"};
    /** 현재 선택된 항목의 인덱스 */
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