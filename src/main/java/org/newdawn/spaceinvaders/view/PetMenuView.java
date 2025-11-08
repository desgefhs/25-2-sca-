package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;
import java.util.ArrayList;
import java.util.List;

public class PetMenuView implements Menu {

    private List<String> ownedPetNames = new ArrayList<>();
    private int selectedItemIndex = 0;

    public PetMenuView(List<String> ownedPetNames) {
        this.ownedPetNames = ownedPetNames;
        if (this.ownedPetNames == null) {
            this.ownedPetNames = new ArrayList<>();
        }
    }

    @Override
    public void moveUp() {
        if (ownedPetNames.isEmpty()) return;
        selectedItemIndex = (selectedItemIndex - 1 + ownedPetNames.size()) % ownedPetNames.size();
    }

    @Override
    public void moveDown() {
        if (ownedPetNames.isEmpty()) return;
        selectedItemIndex = (selectedItemIndex + 1) % ownedPetNames.size();
    }

    @Override
    public void moveLeft() {}

    @Override
    public void moveRight() {}

    @Override
    public String getSelectedItem() {
        if (ownedPetNames.isEmpty() || selectedItemIndex < 0 || selectedItemIndex >= ownedPetNames.size()) {
            return null;
        }
        return ownedPetNames.get(selectedItemIndex);
    }

    public List<String> getOwnedPetNames() {
        return ownedPetNames;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < ownedPetNames.size()) {
            this.selectedItemIndex = index;
        }
    }
}
