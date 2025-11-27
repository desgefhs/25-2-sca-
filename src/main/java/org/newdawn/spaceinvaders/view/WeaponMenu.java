package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

import java.util.ArrayList;
import java.util.List;

public class WeaponMenu implements Menu {
    private List<String> weaponItems = new ArrayList<>();
    private int selectedItemIndex = 0;

    public WeaponMenu(List<String> availableWeapons) {
        this.weaponItems = availableWeapons;
    }

    @Override
    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = weaponItems.size() - 1;
        }
    }

    @Override
    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= weaponItems.size()) {
            selectedItemIndex = 0;
        }
    }

    @Override
    public void moveLeft() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void moveRight() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public String getSelectedItem() {
        if (weaponItems.isEmpty()) {
            return null;
        }
        return weaponItems.get(selectedItemIndex);
    }

    public List<String> getItems() {
        return weaponItems;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}