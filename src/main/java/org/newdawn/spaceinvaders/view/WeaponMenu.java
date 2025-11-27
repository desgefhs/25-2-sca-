package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용 가능한 무기 목록을 표시하고 선택하는 메뉴를 구현한 클래스.
 * 수직으로 탐색 가능한 메뉴입니다.
 */
public class WeaponMenu implements Menu {
    /** 메뉴에 표시될 무기 이름 목록. */
    private List<String> weaponItems = new ArrayList<>();
    /** 현재 선택된 항목의 인덱스. */
    private int selectedItemIndex = 0;

    /**
     * WeaponMenu 생성자.
     * @param availableWeapons 메뉴에 표시할 무기 이름 목록
     */
    public WeaponMenu(List<String> availableWeapons) {
        this.weaponItems = availableWeapons;
    }

    /**
     * 메뉴 선택을 위로 이동합니다.
     * 첫 번째 항목에서 위로 이동하면 마지막 항목으로 순환합니다.
     */
    @Override
    public void moveUp() {
        if (weaponItems.isEmpty()) return;
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = weaponItems.size() - 1;
        }
    }

    /**
     * 메뉴 선택을 아래로 이동합니다.
     * 마지막 항목에서 아래로 이동하면 첫 번째 항목으로 순환합니다.
     */
    @Override
    public void moveDown() {
        if (weaponItems.isEmpty()) return;
        selectedItemIndex++;
        if (selectedItemIndex >= weaponItems.size()) {
            selectedItemIndex = 0;
        }
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveLeft() {
        throw new UnsupportedOperationException("moveLeft is not supported in WeaponMenu");
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveRight() {
        throw new UnsupportedOperationException("moveRight is not supported in WeaponMenu");
    }

    /**
     * 현재 선택된 무기의 이름을 반환합니다.
     * @return 선택된 무기의 이름. 목록이 비어있으면 null.
     */
    @Override
    public String getSelectedItem() {
        if (weaponItems.isEmpty()) {
            return null;
        }
        return weaponItems.get(selectedItemIndex);
    }

    /**
     * 모든 무기 항목의 목록을 반환합니다.
     * @return 무기 이름의 리스트
     */
    public List<String> getItems() {
        return weaponItems;
    }

    /**
     * 현재 선택된 항목의 인덱스를 반환합니다.
     * @return 선택된 항목의 인덱스
     */
    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}