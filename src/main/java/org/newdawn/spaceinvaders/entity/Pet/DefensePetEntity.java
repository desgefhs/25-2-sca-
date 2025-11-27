package org.newdawn.spaceinvaders.entity.Pet;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * A pet that grants a temporary shield to the player.
 */
public class DefensePetEntity extends PetEntity {

    private static final long BASE_COOLDOWN = 5000; // 5 seconds
    private static final long COOLDOWN_REDUCTION_PER_LEVEL = 200; // 0.2 seconds
    private static final String DEFENSE_PET_SPRITE = "sprites/pet/Defensepet.gif";

    /**
     * Create a new defense pet.
     *
     * @param game         The game context in which the pet exists
     * @param player       The player ship to follow
     * @param x            The initial x location
     * @param y            The initial y location
     * @param initialLevel The initial level of the pet
     */
    public DefensePetEntity(GameContext game, ShipEntity player, int x, int y, int initialLevel) {
        super(game, player, DEFENSE_PET_SPRITE, x, y, initialLevel);
        setScale(0.5);
        // Give the player an initial shield when the pet is created
        player.setShield(true, this::resetAbilityCooldown);
        this.resetAbilityCooldown();
    }

    @Override
    protected void updateStatsByLevel() {
        long reduction = this.level * COOLDOWN_REDUCTION_PER_LEVEL;
        this.abilityCooldown = Math.max(0, BASE_COOLDOWN - reduction); // Ensure cooldown doesn't go below zero
    }

    @Override
    public void activateAbility() {
        // Only grant a shield if the player doesn't already have one.
        if (!player.hasShield()) {
            player.setShield(true, this::resetAbilityCooldown);
        }
    }
}
