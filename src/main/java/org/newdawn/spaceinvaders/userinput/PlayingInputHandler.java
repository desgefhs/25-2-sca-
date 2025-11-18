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
        double moveSpeed = gameContext.getMoveSpeed();

        // Commands for continuous key presses (e.g., holding down a key)
        continuousPressCommands.put(KeyEvent.VK_LEFT, new MoveCommand(gameContext, -moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_RIGHT, new MoveCommand(gameContext, moveSpeed, 0));
        continuousPressCommands.put(KeyEvent.VK_UP, new MoveCommand(gameContext, 0, -moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_DOWN, new MoveCommand(gameContext, 0, moveSpeed));
        continuousPressCommands.put(KeyEvent.VK_SPACE, new FireCommand(gameContext));

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
        for (Map.Entry<Integer, Command> entry : continuousPressCommands.entrySet()) {
            if (input.isPressed(entry.getKey())) {
                entry.getValue().execute();
            }
        }

        // Process single-press commands
        for (Map.Entry<Integer, Command> entry : singlePressCommands.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}