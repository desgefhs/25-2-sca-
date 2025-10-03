package org.newdawn.spaceinvaders.view;

import java.util.ArrayList;
import java.util.List;

public class WeaponMenu {
    private List<String> weaponItems = new ArrayList<>();
    private int selectedItemIndex = 0;

    public WeaponMenu(List<String> availableWeapons) {
        this.weaponItems = availableWeapons;
    }

    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = weaponItems.size() - 1;
        }
    }

    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= weaponItems.size()) {
            selectedItemIndex = 0;
        }
    }

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
