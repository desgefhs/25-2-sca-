package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.userinput.Menu;
import java.util.function.Supplier;

/**
 * 메뉴의 선택 항목을 탐색(위, 아래, 왼쪽, 오른쪽)하는 커맨드.
 */
public class MenuNavigateCommand implements Command {

    /**
     * 메뉴 탐색 방향을 정의하는 열거형.
     */
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    /** 현재 메뉴 인스턴스를 제공하는 공급자(Supplier). */
    private final Supplier<Menu> menuSupplier;
    /** 탐색할 방향. */
    private final Direction direction;

    /**
     * MenuNavigateCommand 생성자.
     * @param menuSupplier 메뉴 인스턴스를 제공하는 공급자
     * @param direction 탐색할 방향
     */
    public MenuNavigateCommand(Supplier<Menu> menuSupplier, Direction direction) {
        this.menuSupplier = menuSupplier;
        this.direction = direction;
    }

    /**
     * 메뉴 공급자로부터 현재 메뉴를 가져와 지정된 방향으로 선택 항목을 이동시킵니다.
     */
    @Override
    public void execute() {
        Menu menu = menuSupplier.get();
        if (menu == null) {
            return;
        }
        
        switch (direction) {
            case UP:
                menu.moveUp();
                break;
            case DOWN:
                menu.moveDown();
                break;
            case LEFT:
                menu.moveLeft();
                break;
            case RIGHT:
                menu.moveRight();
                break;
        }
    }
}
