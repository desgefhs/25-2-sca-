package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

public class ConfirmDialog implements Menu {
    private final String[] items = {"Confirm", "Cancel"};
    private int selectedItemIndex = 0;
    private final String message;

    public ConfirmDialog(String message) {
        this.message = message;
    }

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
        // Not used in ConfirmDialog, but required by Menu interface
        throw new UnsupportedOperationException("moveUp is not supported in ConfirmDialog");
    }

    @Override
    public void moveDown() {
        // Not used in ConfirmDialog, but required by Menu interface
        throw new UnsupportedOperationException("moveDown is not supported in ConfirmDialog");
    }

    public String getMessage() {
        return message;
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