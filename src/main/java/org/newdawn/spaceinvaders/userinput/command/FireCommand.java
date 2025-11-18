package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class FireCommand implements Command {

    private final GameContext gameContext;

    public FireCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void execute() {
        // Fetch the ship at execution time
        ShipEntity ship = gameContext.getShip();
        if (ship != null) {
            ship.tryToFire();
        }
    }
}