package org.newdawn.spaceinvaders.userinput;

/**
 * 탐색(navigate)이 가능한 모든 메뉴 객체가 구현해야 하는 인터페이스.
 * 메뉴 항목 간의 이동 및 현재 선택된 항목을 가져오는 기능을 정의합니다.
 */
public interface Menu {
    /**
     * 메뉴 선택을 위로 이동합니다.
     */
    void moveUp();

    /**
     * 메뉴 선택을 아래로 이동합니다.
     */
    void moveDown();

    /**
     * 메뉴 선택을 왼쪽으로 이동합니다.
     */
    void moveLeft();

    /**
     * 메뉴 선택을 오른쪽으로 이동합니다.
     */
    void moveRight();

    /**
     * 현재 선택된 메뉴 항목의 문자열 표현을 반환합니다.
     * @return 선택된 항목의 이름
     */
    String getSelectedItem();
}