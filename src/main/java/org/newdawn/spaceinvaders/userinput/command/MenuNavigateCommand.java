package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.userinput.Menu;
import java.util.function.Supplier;

public class MenuNavigateCommand implements Command {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private final Supplier<Menu> menuSupplier;
    private final Direction direction;

    public MenuNavigateCommand(Supplier<Menu> menuSupplier, Direction direction) {
        this.menuSupplier = menuSupplier;
        this.direction = direction;
    }

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
