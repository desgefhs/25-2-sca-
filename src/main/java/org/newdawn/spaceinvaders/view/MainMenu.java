package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

//메인메뉴 아이템이랑 동작관련
public class MainMenu implements Menu {
    private final String[] items = {"1. 게임시작", "2. 랭킹", "3. 무기", "4. 펫", "5. 상점", "6. 설정"};
    private int selectedItemIndex = 0;

    //메뉴 선택 관련 움직임 처리
    @Override
    public void moveLeft() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    @Override
    public void moveRight() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
    }

    @Override
    public void moveUp() {
        // Not used in MainMenu, but required by Menu interface
    }

    @Override
    public void moveDown() {
        // Not used in MainMenu, but required by Menu interface
    }

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
