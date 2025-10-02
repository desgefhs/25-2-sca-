package org.newdawn.spaceinvaders.entity;

/**
 * An enum representing the different types of pets available in the game.
 */
public enum PetType {
    ATTACK("Attack Pet"),
    DEFENSE("Defense Pet"),
    HEAL("Heal Pet"),
    BUFF("Buff Pet");

    private final String displayName;

    PetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
