package org.newdawn.spaceinvaders.userinput.command;

/**
 * An interface representing an action to be executed.
 * This is the core of the Command Pattern.
 */
public interface Command {
    /**
     * Executes the action associated with this command.
     */
    void execute();
}
