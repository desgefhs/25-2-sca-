package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.userinput.Menu;

import java.util.List;

public class ShopView implements Menu {

    private final List<Upgrade> upgrades;
    private int selectedItemIndex = 0;

    public ShopView(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    @Override
    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = upgrades.size() - 1;
        }
    }

    @Override
    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= upgrades.size()) {
            selectedItemIndex = 0;
        }
    }

    @Override
    public void moveLeft() {}

    @Override
    public void moveRight() {}

    @Override
    public String getSelectedItem() {
        // In this menu, the object itself is more important than the name.
        // We can return the ID or name, but it's better for the command to get the object.
        return getSelectedUpgrade().getId();
    }

    public Upgrade getSelectedUpgrade() {
        if (upgrades.isEmpty() || selectedItemIndex < 0 || selectedItemIndex >= upgrades.size()) {
            return null;
        }
        return upgrades.get(selectedItemIndex);
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
