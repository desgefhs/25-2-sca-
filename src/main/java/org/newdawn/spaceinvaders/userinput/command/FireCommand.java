package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.entity.ShipEntity;

public class FireCommand implements Command {

    private final ShipEntity ship;

    public FireCommand(ShipEntity ship) {
        this.ship = ship;
    }

    @Override
    public void execute() {
        if (ship != null) {
            ship.tryToFire();
        }
    }
}
