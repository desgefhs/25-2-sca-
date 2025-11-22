package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

public class ItemDrawView implements Menu {
    private final String[] items = {"아이템 뽑기", "뒤로가기"};
    private int selectedItemIndex = 0;

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

    @Override
    public void moveLeft() {
        // 이 뷰에서는 사용하지 않음
    }

    @Override
    public void moveRight() {
        // 이 뷰에서는 사용하지 않음
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

    public void setSelectedIndex(int index) {
        this.selectedItemIndex = index;
    }
}
