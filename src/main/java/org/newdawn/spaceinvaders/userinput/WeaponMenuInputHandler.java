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
        if (input.isUpPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_UP).execute();
        }
        if (input.isDownPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_DOWN).execute();
        }
        if (input.isEnterPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
        if (input.isUPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_U).execute();
        }
        if (input.isEscPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ESCAPE).execute();
        }
    }
}
