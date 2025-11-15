package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

/**
 * 일시정지 메뉴 아이템이랑 동작 관련
 */
public class PauseMenu implements Menu {
    private final String[] items = {"재개하기", "메인메뉴로 나가기", "종료하기"};
    private int selectedItemIndex = 0;

    //메뉴 선택 관련 움직임 처리
    @Override
    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }
    
    @Override
    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
    }

    // Not used in PauseMenu, but required by Menu interface
    @Override
    public void moveLeft() {}

    @Override
    public void moveRight() {}

    @Override
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
