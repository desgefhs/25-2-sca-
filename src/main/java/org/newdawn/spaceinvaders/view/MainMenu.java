package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

/**
 * 게임의 주 메뉴를 구현한 클래스.
 * 다양한 게임 상태로 전환할 수 있는 선택지를 제공하는 수평 메뉴입니다.
 */
public class MainMenu implements Menu {
    /** 메뉴에 표시될 항목들. */
    private final String[] items = {"1. 게임시작", "2. 랭킹", "3. 무기", "4. 펫", "5. 상점", "6. 설정"};
    /** 현재 선택된 항목의 인덱스. */
    private int selectedItemIndex = 0;

    /**
     * 메뉴 선택을 왼쪽으로 이동합니다.
     * 첫 번째 항목에서 왼쪽으로 이동하면 마지막 항목으로 순환합니다.
     */
    @Override
    public void moveLeft() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    /**
     * 메뉴 선택을 오른쪽으로 이동합니다.
     * 마지막 항목에서 오른쪽으로 이동하면 첫 번째 항목으로 순환합니다.
     */
    @Override
    public void moveRight() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
    }

    /**
     * 이 메뉴는 수평이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveUp() {
        throw new UnsupportedOperationException("moveUp is not supported in MainMenu");
    }

    /**
     * 이 메뉴는 수평이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveDown() {
        throw new UnsupportedOperationException("moveDown is not supported in MainMenu");
    }

    /**
     * 현재 선택된 메뉴 항목의 이름을 반환합니다.
     * @return 선택된 항목의 이름
     */
    @Override
    public String getSelectedItem() {
        return items[selectedItemIndex];
    }

    /**
     * 지정된 인덱스의 항목 이름을 반환합니다.
     * @param index 조회할 항목의 인덱스
     * @return 해당 인덱스의 항목 이름
     */
    public String getItem(int index) {
        return items[index];
    }

    /**
     * 메뉴 항목의 총 개수를 반환합니다.
     * @return 항목의 개수
     */
    public int getItemCount() {
        return items.length;
    }

    /**
     * 현재 선택된 항목의 인덱스를 반환합니다.
     * @return 선택된 항목의 인덱스
     */
    public int getSelectedIndex() {
        return selectedItemIndex;
    }
}
