package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.userinput.Menu;

import java.util.List;

/**
 * 캐릭터 강화 상점 화면에 표시될 메뉴를 구현한 클래스.
 * 업그레이드 항목 목록을 표시하고 선택하는 수직 메뉴입니다.
 */
public class ShopView implements Menu {

    /** 상점에 표시될 업그레이드 항목 목록. */
    private final List<Upgrade> upgrades;
    /** 현재 선택된 항목의 인덱스. */
    private int selectedItemIndex = 0;

    /**
     * ShopView 생성자.
     * @param upgrades 상점에 표시할 업그레이드 목록
     */
    public ShopView(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    /**
     * 메뉴 선택을 위로 이동합니다.
     * 첫 번째 항목에서 위로 이동하면 마지막 항목으로 순환합니다.
     */
    @Override
    public void moveUp() {
        if (upgrades.isEmpty()) return;
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = upgrades.size() - 1;
        }
    }

    /**
     * 메뉴 선택을 아래로 이동합니다.
     * 마지막 항목에서 아래로 이동하면 첫 번째 항목으로 순환합니다.
     */
    @Override
    public void moveDown() {
        if (upgrades.isEmpty()) return;
        selectedItemIndex++;
        if (selectedItemIndex >= upgrades.size()) {
            selectedItemIndex = 0;
        }
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveLeft() {
        throw new UnsupportedOperationException("moveLeft is not supported in ShopView");
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveRight() {
        throw new UnsupportedOperationException("moveRight is not supported in ShopView");
    }

    /**
     * 현재 선택된 업그레이드의 ID를 반환합니다.
     * @return 선택된 업그레이드의 ID. 없으면 null.
     */
    @Override
    public String getSelectedItem() {
        Upgrade selected = getSelectedUpgrade();
        return (selected != null) ? selected.getId() : null;
    }

    /**
     * 현재 선택된 업그레이드 객체를 반환합니다.
     * @return 선택된 {@link Upgrade} 객체. 목록이 비어있으면 null.
     */
    public Upgrade getSelectedUpgrade() {
        if (upgrades.isEmpty() || selectedItemIndex < 0 || selectedItemIndex >= upgrades.size()) {
            return null;
        }
        return upgrades.get(selectedItemIndex);
    }

    /**
     * 모든 업그레이드 항목 목록을 반환합니다.
     * @return {@link Upgrade} 객체의 리스트
     */
    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    /**
     * 현재 선택된 항목의 인덱스를 반환합니다.
     * @return 선택된 항목의 인덱스
     */
    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
