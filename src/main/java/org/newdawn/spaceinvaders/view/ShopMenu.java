package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.shop.Upgrade;

import java.util.List;

/**
 * 캐릭터 강화 상점의 메뉴 항목을 관리하는 모델 클래스.
 * 업그레이드 목록과 현재 선택된 항목에 대한 상태를 가집니다.
 */
public class ShopMenu {
    /** 상점에 표시될 업그레이드 항목 목록. */
    private final List<Upgrade> items;
    /** 현재 선택된 항목의 인덱스. */
    private int selectedItemIndex = 0;

    /**
     * ShopMenu 생성자.
     * @param items 상점에 표시할 업그레이드 목록
     */
    public ShopMenu(List<Upgrade> items) {
        this.items = items;
    }

    /**
     * 메뉴 선택을 위로 이동합니다.
     * 첫 번째 항목에서 위로 이동하면 마지막 항목으로 순환합니다.
     */
    public void moveUp() {
        if (items.isEmpty()) return;
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.size() - 1;
        }
    }

    /**
     * 메뉴 선택을 아래로 이동합니다.
     * 마지막 항목에서 아래로 이동하면 첫 번째 항목으로 순환합니다.
     */
    public void moveDown() {
        if (items.isEmpty()) return;
        selectedItemIndex++;
        if (selectedItemIndex >= items.size()) {
            selectedItemIndex = 0;
        }
    }

    /**
     * 현재 선택된 업그레이드 객체를 반환합니다.
     * @return 선택된 {@link Upgrade} 객체. 목록이 비어있으면 null.
     */
    public Upgrade getSelectedItem() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(selectedItemIndex);
    }

    /**
     * 모든 업그레이드 항목 목록을 반환합니다.
     * @return {@link Upgrade} 객체의 리스트
     */
    public List<Upgrade> getItems() {
        return items;
    }

    /**
     * 현재 선택된 항목의 인덱스를 반환합니다.
     * @return 선택된 항목의 인덱스
     */
    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
