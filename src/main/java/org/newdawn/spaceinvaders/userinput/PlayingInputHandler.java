package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.userinput.command.*;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class PlayingInputHandler {

    private final Map<Integer, Command> singlePressCommands = new HashMap<>();
    private final Map<Integer, Command> continuousPressCommands = new HashMap<>();
    private final GameContext gameContext;

    public PlayingInputHandler(GameContext gameContext) {
        this.gameContext = gameContext;
        mapCommands();
    }

    private void mapCommands() {
        ShipEntity ship = gameContext.getShip();
        double moveSpeed = gameContext.getMoveSpeed();

        // Commands for continuous key presses (e.g., holding down a key)
        continuousPressCommands.put(KeyEvent.VK_LEFT, new MoveCommand(ship, -moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_RIGHT, new MoveCommand(ship, moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_UP, new MoveCommand(ship, 0, -moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_DOWN, new MoveCommand(ship, 0, moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_SPACE, new FireCommand(ship));

        // Commands for single key presses (action happens once per press)
        // Using lambdas for simple, one-line actions
        singlePressCommands.put(KeyEvent.VK_ESCAPE, () -> gameContext.setCurrentState(GameState.Type.PAUSED));
        singlePressCommands.put(KeyEvent.VK_H, () -> gameContext.setShowHitboxes(!gameContext.getShowHitboxes()));
        singlePressCommands.put(KeyEvent.VK_K, () -> gameContext.getWaveManager().skipToNextBossWave());
        
        // Using dedicated classes for more complex or reusable actions
        singlePressCommands.put(KeyEvent.VK_1, new SwitchWeaponCommand(gameContext, "DefaultGun"));
        singlePressCommands.put(KeyEvent.VK_2, new SwitchWeaponCommand(gameContext, "Shotgun"));
        singlePressCommands.put(KeyEvent.VK_3, new SwitchWeaponCommand(gameContext, "Laser"));
    }

    public void handle(InputHandler input) {
        ShipEntity ship = gameContext.getShip();
        if (ship != null) {
            // Reset movement at the beginning of each frame to handle stopping.
            ship.setHorizontalMovement(0);
            ship.setVerticalMovement(0);
        }

        // Process continuous-press commands
        if (input.isLeftPressed()) continuousPressCommands.get(KeyEvent.VK_LEFT).execute();
        if (input.isRightPressed()) continuousPressCommands.get(KeyEvent.VK_RIGHT).execute();
        if (input.isUpPressed()) continuousPressCommands.get(KeyEvent.VK_UP).execute();
        if (input.isDownPressed()) continuousPressCommands.get(KeyEvent.VK_DOWN).execute();
        if (input.isFirePressed()) continuousPressCommands.get(KeyEvent.VK_SPACE).execute();

        // Process single-press commands
        if (input.isEscPressedAndConsume()) singlePressCommands.get(KeyEvent.VK_ESCAPE).execute();
        if (input.isHPressedAndConsume()) singlePressCommands.get(KeyEvent.VK_H).execute();
        if (input.isKPressedAndConsume()) singlePressCommands.get(KeyEvent.VK_K).execute();
        if (input.isOnePressedAndConsume()) singlePressCommands.get(KeyEvent.VK_1).execute();
        if (input.isTwoPressedAndConsume()) singlePressCommands.get(KeyEvent.VK_2).execute();
        if (input.isThreePressedAndConsume()) singlePressCommands.get(KeyEvent.VK_3).execute();
    }
}