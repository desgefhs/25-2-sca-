package org.newdawn.spaceinvaders.view;

public class ConfirmDialog {
    private final String[] items = {"Confirm", "Cancel"};
    private int selectedItemIndex = 0;
    private final String message;

    public ConfirmDialog(String message) {
        this.message = message;
    }

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

    public String getMessage() {
        return message;
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
