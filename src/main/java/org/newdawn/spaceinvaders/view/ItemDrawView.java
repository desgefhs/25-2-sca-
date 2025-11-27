package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.userinput.Menu;

/**
 * 아이템 뽑기 화면에 표시될 메뉴를 구현한 클래스.
 * "아이템 뽑기", "뒤로가기" 같은 선택지를 제공하는 수직 메뉴입니다.
 */
public class ItemDrawView implements Menu {
    /** 메뉴에 표시될 항목들. */
    private final String[] items = {"아이템 뽑기", "뒤로가기"};
    /** 현재 선택된 항목의 인덱스. */
    private int selectedItemIndex = 0;

    /**
     * 메뉴 선택을 위로 이동합니다.
     * 첫 번째 항목에서 위로 이동하면 마지막 항목으로 순환합니다.
     */
    @Override
    public void moveUp() {
        selectedItemIndex--;
        if (selectedItemIndex < 0) {
            selectedItemIndex = items.length - 1;
        }
    }

    /**
     * 메뉴 선택을 아래로 이동합니다.
     * 마지막 항목에서 아래로 이동하면 첫 번째 항목으로 순환합니다.
     */
    @Override
    public void moveDown() {
        selectedItemIndex++;
        if (selectedItemIndex >= items.length) {
            selectedItemIndex = 0;
        }
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveLeft() {
        throw new UnsupportedOperationException("moveLeft is not supported in ItemDrawView");
    }

    /**
     * 이 메뉴는 수직이므로 지원되지 않는 기능입니다.
     * @throws UnsupportedOperationException 항상 예외를 발생시킴
     */
    @Override
    public void moveRight() {
        throw new UnsupportedOperationException("moveRight is not supported in ItemDrawView");
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

    /**
     * 선택된 항목의 인덱스를 설정합니다.
     * @param index 설정할 인덱스
     */
    public void setSelectedIndex(int index) {
        this.selectedItemIndex = index;
    }
}
