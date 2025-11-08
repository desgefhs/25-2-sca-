package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.entity.ShipEntity;

public class MoveCommand implements Command {

    private final ShipEntity ship;
    private final double dx;
    private final double dy;

    public MoveCommand(ShipEntity ship, double dx, double dy) {
        this.ship = ship;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        if (ship == null) {
            return;
        }
        // Note: This adds to the current movement. 
        // The ship's movement should be reset to 0 at the start of each input handling cycle.
        ship.setHorizontalMovement(ship.getHorizontalMovement() + dx);
        ship.setVerticalMovement(ship.getVerticalMovement() + dy);
    }
}
