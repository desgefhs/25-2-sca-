package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.shop.Upgrade;

import java.util.List;

/**
 * 상점 메뉴의 항목(업그레이드) 데이터와 선택 로직을 관리하는 클래스
 * 이 클래스는 렌더링을 직접 처리하지 않고, ShopState에서 이 클래스의 데이터를 사용해 UI를 그림
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

    /**
     * 현재 선택된 업그레이드 항목을 반환
     * @return 선택된 Upgrade 객체, 항목이 없으면 null
     */
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
