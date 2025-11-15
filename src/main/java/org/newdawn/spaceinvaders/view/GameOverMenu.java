package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

//게임 오버 시 메뉴창
public class GameOverMenu implements Menu {
    private final String[] items = {"다시하기", "메인 메뉴로"};
    private int selectedItemIndex = 0;

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

    // Not used in GameOverMenu, but required by Menu interface
    @Override
    public void moveUp() {}

    @Override
    public void moveDown() {}

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