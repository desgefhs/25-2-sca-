package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.*;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WeaponMenuInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public WeaponMenuInputHandler(GameContext gameContext, Supplier<WeaponMenu> weaponMenuSupplier) {
        // Pass the supplier itself to the commands, not the result of .get()
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> weaponMenuSupplier.get(), MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> weaponMenuSupplier.get(), MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new EquipWeaponCommand(gameContext, weaponMenuSupplier));
        commandMap.put(KeyEvent.VK_U, new UpgradeWeaponCommand(gameContext, weaponMenuSupplier));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    public void handle(InputHandler input) {
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}
