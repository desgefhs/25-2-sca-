package org.newdawn.spaceinvaders.userinput;

/**
 * An interface for any menu that can be navigated.
 */
public interface Menu {
    void moveUp();
    void moveDown();
    void moveLeft();
    void moveRight();
    String getSelectedItem();
}