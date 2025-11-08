package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.ConfirmDialog;

import java.util.HashMap;
import java.util.Map;

public class ProcessConfirmDialogSelectionCommand implements Command {

    private final GameContext gameContext;
    private final Map<String, Command> selectionCommands = new HashMap<>();

    public ProcessConfirmDialogSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    private void mapSelectionCommands() {
        selectionCommands.put("Confirm", () -> System.exit(0));
        selectionCommands.put("Cancel", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    @Override
    public void execute() {
        ConfirmDialog menu = gameContext.getConfirmDialog();
        if (menu == null) {
            return;
        }
        String selectedItem = menu.getSelectedItem();
        
        Command command = selectionCommands.get(selectedItem);
        if (command != null) {
            gameContext.getSoundManager().playSound("buttonselect");
            command.execute();
        }
    }
}
