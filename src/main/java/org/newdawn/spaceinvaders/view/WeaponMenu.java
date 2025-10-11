package org.newdawn.spaceinvaders.view;

import java.util.ArrayList;
import java.util.List;

/**
 * 무기 선택 메뉴의 항목 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, WeaponMenuState에서 이 클래스의 데이터를 사용해 UI를 그림
 */
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
