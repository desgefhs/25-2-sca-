package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.shop.Upgrade;

import java.util.List;

/**
 * Manages the data and navigation for the shop menu UI.
 */
public class ShopMenu {
    private final List<Upgrade> items;
    private int selectedItemIndex = 0;

    public ShopMenu(List<Upgrade> items) {
        this.items = items;
    }

    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.size() - 1;
        }
    }

    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.size()) {
            selectedItemIndex = 0;
        }
    }

    public Upgrade getSelectedItem() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(selectedItemIndex);
    }

    public List<Upgrade> getItems() {
        return items;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
