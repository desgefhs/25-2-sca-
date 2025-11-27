package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class MoveCommand implements Command {

    private final GameContext gameContext;
    private final double dx;
    private final double dy;

    public MoveCommand(GameContext gameContext, double dx, double dy) {
        this.gameContext = gameContext;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void execute() {
        // Fetch the ship at execution time, not creation time
        ShipEntity ship = gameContext.getShip();
        if (ship == null) {
            return;
        }
        // Note: This adds to the current movement.
        // The ship's movement should be reset to 0 at the start of each input handling cycle.
        ship.setHorizontalMovement(ship.getHorizontalMovement() + dx);
        ship.setVerticalMovement(ship.getVerticalMovement() + dy);
    }
}